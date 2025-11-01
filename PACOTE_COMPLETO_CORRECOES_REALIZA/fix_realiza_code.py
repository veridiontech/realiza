# 1. Ver as alterações feitas
git diff

# 2. Criar um novo branch
git checkout -b fix/database-column-mismatch

# 3. Adicionar as alterações
git add .

# 4. Fazer commit
git commit -m "FIX: Alinha entidades JPA com estrutura da BD"

# 5. Fazer push
git push origin fix/database-column-mismatch
