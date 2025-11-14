# Script PowerShell para aplicar a correção do problema de visualização de documentos
# Execute este script no diretório raiz do projeto Realiza

Write-Host "=== Aplicando Correção de Visualização de Documentos ===" -ForegroundColor Green
Write-Host ""

# Verificar se estamos no diretório correto
if (-not (Test-Path "api/src/main/java")) {
    Write-Host "ERRO: Execute este script no diretório raiz do projeto Realiza!" -ForegroundColor Red
    exit 1
}

$arquivoPath = "api/src/main/java/bl/tech/realiza/gateways/controllers/impl/documents/provider/DocumentProviderSupplierControllerImpl.java"

Write-Host "1. Fazendo backup do arquivo original..." -ForegroundColor Yellow
Copy-Item $arquivoPath "$arquivoPath.backup" -Force
Write-Host "   Backup criado: $arquivoPath.backup" -ForegroundColor Gray
Write-Host ""

Write-Host "2. Baixando arquivo corrigido..." -ForegroundColor Yellow
# O usuário deve ter baixado o arquivo DocumentProviderSupplierControllerImpl_CORRIGIDO.java
$arquivoCorrigido = "DocumentProviderSupplierControllerImpl_CORRIGIDO.java"

if (-not (Test-Path $arquivoCorrigido)) {
    Write-Host "   ERRO: Arquivo $arquivoCorrigido não encontrado!" -ForegroundColor Red
    Write-Host "   Por favor, baixe o arquivo corrigido primeiro e coloque-o no diretório raiz do projeto." -ForegroundColor Red
    exit 1
}

Write-Host "3. Aplicando correção..." -ForegroundColor Yellow
Copy-Item $arquivoCorrigido $arquivoPath -Force
Write-Host "   Arquivo atualizado com sucesso!" -ForegroundColor Gray
Write-Host ""

Write-Host "4. Fazendo commit..." -ForegroundColor Yellow
git add .
git commit -m "fix: corrigir visualização de documentos - regenerar URLs assinadas automaticamente"
Write-Host ""

Write-Host "5. Fazendo push..." -ForegroundColor Yellow
git push origin main
Write-Host ""

Write-Host "=== Correção Aplicada com Sucesso! ===" -ForegroundColor Green
Write-Host ""
Write-Host "Próximos passos:" -ForegroundColor Cyan
Write-Host "1. Aguarde 5-10 minutos para o deploy no Render.com" -ForegroundColor White
Write-Host "2. Teste a visualização de documentos no sistema" -ForegroundColor White
Write-Host "3. Se houver problemas, restaure o backup: Copy-Item $arquivoPath.backup $arquivoPath" -ForegroundColor White
