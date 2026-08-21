$ErrorActionPreference = 'Stop'

$templatePath = '.env.template'
$envPath = '.env'

# .env file doesn't exist.
if (-not (Test-Path $envPath)) {
    Copy-Item $templatePath $envPath
    Write-Host 'Environment has been initialized. Update .env before starting the stack.'
    exit 0
}

# Checking that all env vars exist.
$templateKeys = @{}
foreach ($line in (Get-Content $templatePath)) {
    if ([string]::IsNullOrWhiteSpace($line) -or $line.TrimStart().StartsWith('#')) {
        continue
    }

    $parts = $line.Split('=', 2)
    if ($parts.Count -ge 2) {
        $templateKeys[$parts[0].Trim()] = $true
    }
}

$envKeys = @{}
foreach ($line in (Get-Content $envPath)) {
    if ([string]::IsNullOrWhiteSpace($line) -or $line.TrimStart().StartsWith('#')) {
        continue
    }

    $parts = $line.Split('=', 2)
    if ($parts.Count -ge 2) {
        $envKeys[$parts[0].Trim()] = $true
    }
}

$missingKeys = @($templateKeys.Keys | Where-Object { -not $envKeys.ContainsKey($_) })

if ($missingKeys.Count -eq 0) {
    Write-Host 'updated .env file exists. Exiting.'
    exit 0
}

Copy-Item $templatePath $envPath -Force
Write-Host 'outdated .env file exists, overriding with a newer version'
