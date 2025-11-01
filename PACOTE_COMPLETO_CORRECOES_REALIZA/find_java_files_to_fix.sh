#!/bin/bash

# Script para encontrar ficheiros Java que provavelmente precisam de correção

# Cores para a saída
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Termos de pesquisa
SEARCH_TERMS=(
  "name_contract"
  "name_branch"
  "name_client"
  "id_client"
)

# Diretório do projeto (ajuste se necessário)
PROJECT_DIR="."

echo -e "${GREEN}A procurar por ficheiros Java que contêm os termos incorretos...${NC}\n"

for term in "${SEARCH_TERMS[@]}"; do
  echo -e "${YELLOW}--- A procurar por: $term ---${NC}"
  grep -r --include='*.java' "$term" "$PROJECT_DIR"
  echo ""
done

echo -e "${GREEN}Pesquisa concluída. Os ficheiros listados acima são os principais candidatos a necessitar de correção.${NC}"
