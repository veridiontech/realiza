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
          <h1 className="text-[30px]">Nova atualização: Versão 1.0.0</h1>
          <span className="text-[14px] text-gray-600 dark:text-gray-400">Realiza sistema</span>
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
            <h2>Perfil de empresa</h2>
            <div className="list-disc px-2 text-[14px]">
              <li className="">
                Perfil da empresa recebendo suas informações de acordo com o
                cadastro no email
              </li>
              <li>Remoção dos campos de endereços e descrição.</li>
            </div>
          </div>
          <div className="flex flex-col gap-1">
            <h2>Página de Colaboradores</h2>
            <div className="list-disc px-2 text-[14px]">
              <li className="">
                Renomeação da página de "Funcionários" para "Colaboradores
              </li>
              <li>
                Nome e sobrenome agora estão unificados em um único campo.
              </li>
              <li>Remoção dos campos "PJ" e "e-mail.</li>
              <li>
                Avaliação da possibilidade de incluir ficha de registro para
                colaboradores
              </li>
            </div>
          </div>
          <div className="flex flex-col gap-1">
            <h2>Cadastro de Fornecedores e Subcontratados</h2>
            <div className="list-disc px-2 text-[14px]">
              <li className="">
                Novo botão para alternar entre "Fornecedor" e "Subcontratado"
                durante o cadastro
              </li>
              <li>
                Envio automático de e-mail de convite para fornecedores e
                subcontratados..
              </li>
            </div>
          </div>
        </motion.ul>
      </div>
    </div>
  );
}
