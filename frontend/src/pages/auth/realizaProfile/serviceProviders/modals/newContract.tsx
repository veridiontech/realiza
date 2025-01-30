import { Modal } from "@/components/modal";

interface NewContractProps {
  onClose: () => void;
  onSubmit: (data: Record<string, any>) => void;
}

export function NewContract({ onClose, onSubmit }: NewContractProps) {
  return (
    <Modal
      title="Solicitar Serviço"
      onClose={onClose}
      onSubmit={onSubmit}
      fields={[
        {
          name: "nome_servico",
          label: "* Nome do Serviço",
          type: "text",
          placeholder: "Máximo 300 caracteres",
          required: true,
        },
        {
          name: "ref_contrato",
          label: "Ref. do contrato",
          type: "text",
          placeholder: "Referência Contrato",
        },
        {
          name: "limite_alocados",
          label: "Limite de Alocados",
          type: "number",
        },
        {
          name: "data_inicio",
          label: "* Data de Início",
          type: "date",
          required: true,
        },
        {
          name: "data_termino",
          label: "Data de Término do contrato",
          type: "date",
        },
        {
          name: "tipo_servico",
          label: "* Tipo do Serviço",
          type: "select",
          options: ["Tipo 1", "Tipo 2", "Tipo 3"],
          required: true,
        },
        {
          name: "atividades",
          label: "Atividades",
          type: "select",
          options: ["Atividade 1", "Atividade 2", "Atividade 3"],
        },
        {
          name: "exigencias",
          label: "Outras exigências",
          type: "select",
          options: ["Exigência 1", "Exigência 2", "Exigência 3"],
        },
        {
          name: "unidades",
          label: "Unidades",
          type: "select",
          options: ["Unidade 1", "Unidade 2", "Unidade 3"],
        },
        {
          name: "gestor",
          label: "Gestor",
          type: "select",
          options: ["Gestor 1", "Gestor 2", "Gestor 3"],
        },
        {
          name: "fornecedor_responsavel",
          label: "Fornecedor Responsável",
          type: "select",
          options: ["Fornecedor 1", "Fornecedor 2", "Fornecedor 3"],
        },
        {
          name: "descricao",
          label: "Descrição da solicitação (opcional)",
          type: "text",
          placeholder: "Descrição da solicitação (opcional)",
        },
      ]}
    />
  );
}
