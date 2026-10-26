$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $PSScriptRoot
$frontendPages = Join-Path (Split-Path -Parent $projectRoot) 'src\components\pages'
$javaRoot = Join-Path $projectRoot 'src\main\java\com\edufox\schoolerp'
$utf8 = New-Object System.Text.UTF8Encoding($false)

$moduleMap = @{
    Attendance = 'attendance'
    Canteen = 'canteen'
    Certificates = 'certificate'
    Communication = 'communication'
    Dashboard = 'dashboard'
    ExamMaster = 'examination'
    Fee = 'fee'
    Hostel = 'hostel'
    Leave = 'leave'
    Library = 'library'
    MasterSetting = 'master'
    PTM = 'ptm'
    Staff = 'staff'
    Student = 'student'
    StundentPages = 'studentportal'
    TimeTable = 'timetable'
    Transport = 'transport'
    Uploads = 'uploads'
    Visitor = 'visitor'
    WebSite = 'website'
}

$submoduleMap = @{
    Report = 'report'
    Reports = 'report'
    ReportAnalytics = 'report'
    Reportcard = 'report'
    'CBSE Result' = 'report'
    cumulative = 'report'
    Greensheet = 'report'
    MarkListReports = 'report'
    AllDashboard = 'report'
    Setup = 'setup'
    SettingBasic = 'setup'
    SettingPrimary = 'setup'
    SettingSecurity = 'setup'
    Update = 'update'
    delete = 'update'
    Work = 'work'
    marksEntry = 'work'
    marskEntryStatus = 'work'
    TapCard = 'work'
}

function Convert-Segment([string]$value) {
    $segment = ($value -replace '[^A-Za-z0-9_]', '_').ToLowerInvariant()
    if ([string]::IsNullOrWhiteSpace($segment)) { return 'page' }
    if ($segment -match '^[0-9]') { return "page_$segment" }
    return $segment
}

function Write-Utf8([string]$path, [string]$content) {
    $directory = Split-Path -Parent $path
    New-Item -ItemType Directory -Path $directory -Force | Out-Null
    [System.IO.File]::WriteAllText($path, $content, $utf8)
}

$generated = 0
$skipped = 0
$root = (Resolve-Path $frontendPages).Path
$files = Get-ChildItem $root -Recurse -Force -File | Where-Object { $_.Extension -eq '.jsx' -or $_.Extension -eq '.js' }

foreach ($file in $files) {
    $relative = $file.FullName.Substring($root.Length + 1)
    $parts = $relative -split '[\\/]'
    if ($parts.Count -lt 3) { $skipped++; continue }
    $moduleName = $parts[0]
    if (-not $moduleMap.ContainsKey($moduleName)) { $skipped++; continue }

    $pageName = [System.IO.Path]::GetFileNameWithoutExtension($file.Name)
    if ($pageName -match '(?i)(Common|Module|Page|Shared)$') { $skipped++; continue }

    $firstFolder = $parts[1]
    if ($submoduleMap.ContainsKey($firstFolder)) {
        $submodule = $submoduleMap[$firstFolder]
    } else {
        $submodule = 'work'
    }

    $pageParts = @($parts[1..($parts.Count - 2)]) + $pageName
    $pageSlug = (($pageParts | ForEach-Object { Convert-Segment $_ }) -join '_')
    $javaModule = $moduleMap[$moduleName]
    $packagePage = Convert-Segment $pageSlug
    $package = "com.edufox.schoolerp.$javaModule.$submodule.$packagePage"
    $packagePath = [System.String]::Join([System.IO.Path]::DirectorySeparatorChar, ($package -split '\.'))
    $javaDirectory = Join-Path $javaRoot $packagePath
    $className = ($pageName -replace '[^A-Za-z0-9_]', '_')
    if ($className -match '^[0-9]') { $className = "Page_$className" }
    $sourcePage = ($relative -replace '\\', '/')
    $routeSlug = (($moduleName, $submodule, $pageSlug | ForEach-Object { Convert-Segment $_ }) -join '/')
    $beanName = 'page_' + (($relative -replace '[^A-Za-z0-9]', '_').ToLowerInvariant())

    $repositoryPath = Join-Path $javaDirectory "${className}Repository.java"
    $servicePath = Join-Path $javaDirectory "${className}Service.java"
    $controllerPath = Join-Path $javaDirectory "${className}Controller.java"
    if ((Test-Path $repositoryPath) -or (Test-Path $servicePath) -or (Test-Path $controllerPath)) { $skipped++; continue }

    $repository = @"
package $package;

import com.edufox.schoolerp.common.page.PageArtifactRepository;
import org.springframework.stereotype.Repository;

/** Page-owned repository boundary for $sourcePage. */
@Repository("$beanName")
public class ${className}Repository extends PageArtifactRepository {
    public ${className}Repository() {
        super("$moduleName", "$submodule", "$pageName", "$sourcePage");
    }
}
"@
    $service = @"
package $package;

import com.edufox.schoolerp.common.page.PageArtifactService;
import org.springframework.stereotype.Service;

/** Page-owned workflow boundary for $sourcePage. */
@Service("$beanName")
public class ${className}Service extends PageArtifactService<${className}Repository> {
    public ${className}Service(${className}Repository repository) {
        super(repository);
    }
}
"@
    $controller = @"
package $package;

import com.edufox.schoolerp.common.page.PageArtifactController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Page-level HTTP boundary for $sourcePage. */
@RestController("$beanName")
@RequestMapping("/api/page-artifacts/$routeSlug")
public class ${className}Controller extends PageArtifactController<${className}Service> {
    public ${className}Controller(${className}Service service) {
        super(service);
    }

    @GetMapping("/descriptor")
    public Map<String, Object> pageDescriptor() {
        return descriptor();
    }
}
"@
    Write-Utf8 $repositoryPath $repository
    Write-Utf8 $servicePath $service
    Write-Utf8 $controllerPath $controller
    $generated++
}

Write-Output "Generated page boundaries: $generated; skipped: $skipped"
