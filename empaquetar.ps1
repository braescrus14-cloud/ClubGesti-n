$ErrorActionPreference = 'Stop'
Push-Location $PSScriptRoot
try {
    & .\mvnw.cmd package
    if ($LASTEXITCODE -ne 0) { throw 'Maven no pudo generar el paquete. Revisa los errores anteriores.' }
    Write-Host 'Empaquetado completo. Los archivos JAR están en las carpetas target de los proyectos.'
} finally {
    Pop-Location
}
