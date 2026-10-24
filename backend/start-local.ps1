param([switch]$SkipBuild, [switch]$Offline)
$ErrorActionPreference = 'Stop'
$backendRoot = $PSScriptRoot
$javaExe = (Get-Command java.exe -ErrorAction Stop).Source
$jar = Join-Path $backendRoot 'target\school-erp-backend-0.1.0-SNAPSHOT.jar'
$nativeDir = Join-Path $backendRoot '.local\native'
$dll = Join-Path $nativeDir 'mssql-jdbc_auth-12.6.4.x64.dll'

if (Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue) {
    throw 'Port 8080 is occupied. Identify and stop the existing project backend before starting this one.'
}

if (-not $SkipBuild) {
    $maven = Get-Command mvn.cmd -ErrorAction SilentlyContinue
    $mavenPath = if ($maven) { $maven.Source } else {
        Get-ChildItem -LiteralPath (Join-Path $env:USERPROFILE '.m2\wrapper\dists') -Filter mvn.cmd -Recurse -ErrorAction SilentlyContinue |
            Select-Object -First 1 -ExpandProperty FullName
    }
    if (-not $mavenPath) { throw 'Maven is required to build the backend.' }
    $env:JAVA_HOME = Split-Path (Split-Path $javaExe -Parent) -Parent
    Push-Location $backendRoot
    try {
        $buildArgs = @('-q', "-Dmaven.repo.local=$(Join-Path $env:USERPROFILE '.m2\repository')", 'package')
        if ($Offline) { $buildArgs = @('-o') + $buildArgs }
        & $mavenPath @buildArgs
        if ($LASTEXITCODE -ne 0) { throw 'Backend build/tests failed.' }
    } finally { Pop-Location }
}
if (-not (Test-Path -LiteralPath $jar)) { throw 'Backend JAR not found. Run without -SkipBuild.' }

if (-not (Test-Path -LiteralPath $dll)) {
    New-Item -ItemType Directory -Path $nativeDir -Force | Out-Null
    Invoke-WebRequest -UseBasicParsing -Uri 'https://repo.maven.apache.org/maven2/com/microsoft/sqlserver/mssql-jdbc_auth/12.6.4.x64/mssql-jdbc_auth-12.6.4.x64.dll' -OutFile $dll -TimeoutSec 30
}
$signature = Get-AuthenticodeSignature -LiteralPath $dll
if ($signature.Status -ne 'Valid' -or $signature.SignerCertificate.Subject -notmatch 'O=Microsoft Corporation') {
    throw 'The JDBC Windows authentication DLL must have a valid Microsoft signature.'
}

# Use process arguments to override stale beta DB/profile/write-mode environment settings.
$url = 'jdbc:sqlserver://127.0.0.1:1433;databaseName=dpsbsr_edufox_net;integratedSecurity=true;authenticationScheme=NativeAuthentication;encrypt=true;trustServerCertificate=true;loginTimeout=5'
if (-not $env:JWT_SECRET) {
    $bytes = New-Object byte[] 48
    $rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
    try { $rng.GetBytes($bytes); $env:JWT_SECRET = [Convert]::ToBase64String($bytes) } finally { $rng.Dispose() }
}
$arguments = @(
    "-Djava.library.path=$nativeDir", '-jar', $jar,
    '--spring.profiles.active=local-sqlserver', '--server.port=8080',
    "--spring.datasource.url=$url", '--spring.datasource.username=', '--spring.datasource.password=',
    '--spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver',
    '--spring.datasource.hikari.read-only=false', '--app.auth.database-read-only=false',
    '--app.master-settings.write-enabled=true', '--app.auth.otp-enabled=false'
)
Write-Output 'Starting backend: target=127.0.0.1/dpsbsr_edufox_net; Windows authentication; read/write enabled.'
Write-Output 'Check http://localhost:8080/actuator/health. If a new JWT key was generated, log in again.'
# Keep the current Windows logon context for native SQL authentication.
# Detached Start-Process launches can lack usable SSPI credentials in some environments.
Push-Location $backendRoot
try { & $javaExe @arguments } finally { Pop-Location }
