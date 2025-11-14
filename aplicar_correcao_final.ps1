# Script para aplicar correção final no sistema Realiza
# Execute este script no diretório raiz do projeto

Write-Host "=== Aplicando Correção Final ===" -ForegroundColor Green

# 1. Fazer rollback para o commit que funcionava
Write-Host "`n[1/4] Fazendo rollback para versão limpa..." -ForegroundColor Yellow
git checkout 0f94622e -- api/src/main/java/bl/tech/realiza/gateways/controllers/impl/documents/provider/DocumentProviderSupplierControllerImpl.java

# 2. Aplicar apenas a melhoria de logs
Write-Host "`n[2/4] Aplicando melhorias de logs..." -ForegroundColor Yellow

$filePath = "api\src\main\java\bl\tech\realiza\gateways\controllers\impl\documents\provider\DocumentProviderSupplierControllerImpl.java"
$content = Get-Content $filePath -Raw

# Substituir o catch simples por um com logs detalhados
$oldCatch = @"
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
"@

$newCatch = @"
        } catch (IOException e) {
            System.err.println("[ERROR] Falha ao buscar documento " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        } catch (Exception e) {
            System.err.println("[ERROR] Erro inesperado ao buscar documento " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
"@

$content = $content -replace [regex]::Escape($oldCatch), $newCatch
Set-Content -Path $filePath -Value $content -NoNewline

Write-Host "Correção aplicada com sucesso!" -ForegroundColor Green

# 3. Fazer commit
Write-Host "`n[3/4] Fazendo commit..." -ForegroundColor Yellow
git add .
git commit -m "fix: melhorar logs de erro no endpoint de visualização de documentos"

# 4. Fazer push
Write-Host "`n[4/4] Fazendo push para o GitHub..." -ForegroundColor Yellow
git push origin main

Write-Host "`n=== Correção Concluída! ===" -ForegroundColor Green
Write-Host "Aguarde 5-10 minutos para o deploy no Render.com" -ForegroundColor Cyan
