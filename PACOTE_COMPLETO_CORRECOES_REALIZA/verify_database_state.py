import pymysql

# Credenciais da base de dados
DB_CONFIG = {
    'host': '177.170.30.9',
    'port': 8004,
    'user': 'veridion_user',
    'password': 'SenhaSegura123!',
    'database': 'dbrealiza'
}

def verify_database():
    try:
        connection = pymysql.connect(**DB_CONFIG)
        cursor = connection.cursor()
        
        print("=" * 60)
        print("VERIFICAÇÃO DO ESTADO DA BASE DE DADOS - SISTEMA REALIZA")
        print("=" * 60)
        print()
        
        # 1. Verificar total de contratos
        cursor.execute("SELECT COUNT(*) FROM contract")
        total_contracts = cursor.fetchone()[0]
        print(f"✓ Total de contratos: {total_contracts}")
        
        # 2. Verificar contratos órfãos (sem id_branch)
        cursor.execute("SELECT COUNT(*) FROM contract WHERE id_branch IS NULL")
        orphan_contracts = cursor.fetchone()[0]
        print(f"✓ Contratos órfãos (id_branch = NULL): {orphan_contracts}")
        
        # 3. Verificar contratos válidos
        valid_contracts = total_contracts - orphan_contracts
        print(f"✓ Contratos válidos (com id_branch): {valid_contracts}")
        
        # 4. Verificar estrutura da tabela branch
        cursor.execute("DESCRIBE branch")
        branch_columns = [row[0] for row in cursor.fetchall()]
        print(f"\n✓ Colunas da tabela 'branch': {', '.join(branch_columns)}")
        
        # Verificar se 'name_branch' existe
        if 'name_branch' in branch_columns:
            print("  ⚠️  ATENÇÃO: Coluna 'name_branch' existe (esperado: 'name')")
        elif 'name' in branch_columns:
            print("  ✓ Coluna 'name' existe (correto)")
        
        # 5. Verificar estrutura da tabela contract
        cursor.execute("DESCRIBE contract")
        contract_columns = [row[0] for row in cursor.fetchall()]
        print(f"\n✓ Colunas da tabela 'contract': {', '.join(contract_columns)}")
        
        # Verificar se 'name_contract' existe
        if 'name_contract' in contract_columns:
            print("  ⚠️  ATENÇÃO: Coluna 'name_contract' existe (esperado: 'service_name')")
        elif 'service_name' in contract_columns:
            print("  ✓ Coluna 'service_name' existe (correto)")
        
        # Verificar se 'id_client' existe
        if 'id_client' in contract_columns:
            print("  ⚠️  ATENÇÃO: Coluna 'id_client' existe (deve ser removida do código)")
        else:
            print("  ✓ Coluna 'id_client' NÃO existe (correto)")
        
        # 6. Verificar estrutura da tabela client
        cursor.execute("DESCRIBE client")
        client_columns = [row[0] for row in cursor.fetchall()]
        print(f"\n✓ Colunas da tabela 'client': {', '.join(client_columns)}")
        
        # Verificar se 'name_client' existe
        if 'name_client' in client_columns:
            print("  ⚠️  ATENÇÃO: Coluna 'name_client' existe (esperado: 'trade_name')")
        elif 'trade_name' in client_columns:
            print("  ✓ Coluna 'trade_name' existe (correto)")
        
        # 7. Resumo final
        print("\n" + "=" * 60)
        print("RESUMO")
        print("=" * 60)
        
        if orphan_contracts == 0:
            print("✅ Todos os contratos têm filial associada (id_branch)")
        else:
            print(f"❌ Ainda existem {orphan_contracts} contratos órfãos")
        
        print("\nEstrutura da Base de Dados:")
        print(f"  - Tabela 'branch': {'name' if 'name' in branch_columns else 'name_branch (INCORRETO)'}")
        print(f"  - Tabela 'contract': {'service_name' if 'service_name' in contract_columns else 'name_contract (INCORRETO)'}")
        print(f"  - Tabela 'client': {'trade_name' if 'trade_name' in client_columns else 'name_client (INCORRETO)'}")
        
        cursor.close()
        connection.close()
        
    except Exception as e:
        print(f"❌ Erro ao conectar à base de dados: {e}")

if __name__ == "__main__":
    verify_database()
