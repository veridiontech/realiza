import { motion } from "framer-motion";

export function AtualizationPage() {
  return (
    <div className="flex flex-col items-start p-28">
      <div className="flex flex-col gap-8">
        <motion.div
          initial={{ opacity: 0 }}
          whileInView={{ opacity: 1 }}
          transition={{ duration: 1.5 }}
          viewport={{ once: true }}
          className="flex flex-col"
        >
          <h1 className="text-[30px]">Nova atualização: Versão 1.0.2</h1>
          <span className="text-[14px] text-gray-600 dark:text-gray-400">
            Realiza sistema
          </span>
        </motion.div>
        <motion.div
          initial={{ opacity: 0 }}
          whileInView={{ opacity: 1 }}
          transition={{ duration: 1.5 }}
          viewport={{ once: true }}
        >
          <p className="w-[40vw]">
            Estamos sempre trabalhando para melhorar sua experiência! Confira as
            novas funcionalidades disponíveis na última atualização do sistema:
          </p>
        </motion.div>
        <motion.ul
          initial={{ opacity: 0 }}
          whileInView={{ opacity: 1 }}
          transition={{ duration: 1.5 }}
          viewport={{ once: true }}
          className="flex flex-col gap-5"
        >
          <div className="flex flex-col gap-1">
            <h2>Cadastro de Fornecedores</h2>
            <div className="list-disc px-2 text-[14px]">
              <li>Adicionados os campos CAPEX e OPEX no cadastro.</li>
              <li>Adicionados os campos filiais atendidas/bases/CNPJ.</li>
            </div>
          </div>
          <div className="flex flex-col gap-1">
            <h2>Documentos e Contratos</h2>
            <div className="list-disc px-2 text-[14px]">
              <li>Implementada a funcionalidade de check manual de documentos.</li>
              <li>Adicionados os campos gestor e fiscal do contrato.</li>
              <li>Implementado o recurso de busca para download de documentos.</li>
            </div>
          </div>
          <div className="flex flex-col gap-1">
            <h2>Colaboradores</h2>
            <div className="list-disc px-2 text-[14px]">
              <li>Renomeado o termo "Funcionário" para "Colaborador" em todo o sistema.</li>
              <li>Diferenciados colaboradores e acessos ao sistema com menos campos, separando funcionalidades.</li>
            </div>
          </div>
          <div className="flex flex-col gap-1">
            <h2>Gestão de Acessos</h2>
            <div className="list-disc px-2 text-[14px]">
              <li>Criados usuários "realiza" com a capacidade de criar outros usuários.</li>
            </div>
          </div>
          <div className="flex flex-col gap-1">
            <h2>Interface de Cadastro</h2>
            <div className="list-disc px-2 text-[14px]">
              <li>Alterado o campo de "descrição do serviço" para ser maior e reposicionado na tela.</li>
            </div>
          </div>
        </motion.ul>
      </div>
    </div>
  );
}
