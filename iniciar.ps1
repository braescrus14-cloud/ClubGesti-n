param(
    [ValidateSet('gestion-unificada')]
    [string]$Modulo = 'gestion-unificada',
    [int]$Puerto = 0
)
$ErrorActionPreference = 'Stop'
$previousUri = $env:MONGODB_URI
$previousPort = $env:PORT
$uriPointer = [IntPtr]::Zero
Push-Location $PSScriptRoot
try {
    if ($Puerto -eq 0) { $Puerto = 8092 }
    $env:PORT = [string]$Puerto
    if ([string]::IsNullOrWhiteSpace($env:MONGODB_URI)) {
        $secretUri = Read-Host 'Pega tu URI completa de MongoDB Atlas' -AsSecureString
        $uriPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secretUri)
        $env:MONGODB_URI = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($uriPointer)
    }
    $jar = Join-Path $PSScriptRoot "$Modulo\target\$Modulo-1.0.0.jar"
    if (-not (Test-Path -LiteralPath $jar)) {
        & .\mvnw.cmd -pl $Modulo -am package
        if ($LASTEXITCODE -ne 0) { throw 'No se pudo generar el JAR.' }
    }
    Write-Host "Abriendo $Modulo en http://localhost:$Puerto"
    & java -jar $jar
} finally {
    if ($uriPointer -ne [IntPtr]::Zero) { [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($uriPointer) }
    $env:MONGODB_URI = $previousUri
    $env:PORT = $previousPort
    Pop-Location
}
