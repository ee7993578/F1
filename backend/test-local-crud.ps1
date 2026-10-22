param([Parameter(Mandatory=$true)][string]$Token)
$ErrorActionPreference = 'Stop'
$listener = Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction Stop | Select-Object -First 1
$backend = Get-CimInstance Win32_Process -Filter "ProcessId=$($listener.OwningProcess)"
if (-not ($backend.CommandLine -match '--spring\.datasource\.url=jdbc:sqlserver://(localhost|127\.0\.0\.1):1433;databaseName=dpsbsr_edufox_net;')) {
    throw 'Refusing write test: port 8080 is not pinned to the authorized local database. Use start-local.ps1.'
}
$headers = @{ Authorization = "Bearer $Token" }
$user = (Invoke-RestMethod 'http://localhost:8080/api/auth/me' -Headers $headers -TimeoutSec 15).user
$connection = New-Object System.Data.SqlClient.SqlConnection('Data Source=127.0.0.1;Initial Catalog=dpsbsr_edufox_net;Integrated Security=True;Encrypt=True;TrustServerCertificate=True;Connect Timeout=5;')
$connection.Open()
$query = $connection.CreateCommand()
$marker = 'LOCAL_SMOKE_' + [guid]::NewGuid().ToString('N')
$testId = $null
$url = 'http://localhost:8080/api/student-settings/houses'
try {
    $query.CommandText = 'SELECT TOP 1 f_id FROM TB_FacultyMaster WHERE school_id=@school AND staff_u_id=@staff'
    [void]$query.Parameters.AddWithValue('@school', $user.schoolId)
    [void]$query.Parameters.AddWithValue('@staff', $user.staffUid)
    $incharge = $query.ExecuteScalar()
    if ($null -eq $incharge -or $incharge -is [DBNull]) { throw 'No local faculty found for this authenticated user.' }
    $body = @{ houseName=$marker; inchargeId=[int]$incharge; color='blue' }
    Invoke-RestMethod $url -Headers $headers -Method Post -ContentType 'application/json' -Body ($body|ConvertTo-Json) -TimeoutSec 15 | Out-Null
    $rows = Invoke-RestMethod $url -Headers $headers -TimeoutSec 15
    $created = @($rows | Where-Object houseName -eq $marker)
    if ($created.Count -ne 1) { throw 'Inserted test row was not uniquely returned.' }
    $testId = [int]$created[0].id
    Write-Output 'INSERT and READ through backend API: passed'
    $body.houseName = $marker + '_UPDATED'
    Invoke-RestMethod "$url/$testId" -Headers $headers -Method Put -ContentType 'application/json' -Body ($body|ConvertTo-Json) -TimeoutSec 15 | Out-Null
    $rows = Invoke-RestMethod $url -Headers $headers -TimeoutSec 15
    if (-not ($rows | Where-Object { $_.id -eq $testId -and $_.houseName -eq $body.houseName })) { throw 'Update verification failed.' }
    Write-Output 'UPDATE through backend API: passed'
    Invoke-RestMethod "$url/$testId" -Headers $headers -Method Delete -TimeoutSec 15 | Out-Null
    $rows = Invoke-RestMethod $url -Headers $headers -TimeoutSec 15
    if ($rows | Where-Object id -eq $testId) { throw 'Delete verification failed.' }
    $query.Parameters.Clear()
    $query.CommandText = 'SELECT status FROM HouseMaster WHERE id=@id AND school_id=@school AND H_Name=@name'
    [void]$query.Parameters.AddWithValue('@id', $testId)
    [void]$query.Parameters.AddWithValue('@school', $user.schoolId)
    [void]$query.Parameters.AddWithValue('@name', $body.houseName)
    $status = $query.ExecuteScalar()
    if ($null -eq $status -or [int]$status -ne 0) { throw 'Local database did not reflect API soft-delete.' }
    Write-Output 'DELETE through backend API (soft-delete), confirmed in localhost database: passed'
} finally {
    # Only remove this test's uniquely named records; never existing school records.
    $query.Parameters.Clear()
    $query.CommandText = 'DELETE FROM HouseMaster WHERE school_id=@school AND (H_Name=@name OR H_Name=@updated)'
    [void]$query.Parameters.AddWithValue('@school', $user.schoolId)
    [void]$query.Parameters.AddWithValue('@name', $marker)
    [void]$query.Parameters.AddWithValue('@updated', ($marker + '_UPDATED'))
    $removed = $query.ExecuteNonQuery()
    Write-Output "Isolated test records removed: $removed; no existing records removed."
    $connection.Close()
}
