import { Modal } from "@/components/modal";

interface StepTwoServiceProvidersProps {
  onClose: () => void;
  onSubmit: (data: Record<string, any>) => void;
}

export function StepTwoServiceProviders({
  onClose,
  onSubmit,
}: StepTwoServiceProvidersProps) {
  return (
    <Modal
      title="Solicitar Serviço"
      onClose={onClose}
      onSubmit={onSubmit}
      fields={[
        {
          name: "subcontratacao",
          label: "Subcontratação?",
          type: "custom",
          render: ({ value, onChange }) => (
            <div className="flex items-center space-x-4">
              <label className="flex items-center space-x-2">
                <input
                  type="radio"
                  name="subcontratacao"
                  value="Não"
                  checked={value === "Não"}
                  onChange={() => onChange("Não")}
                  className="h-5 w-5 text-red-600"
                />
                <span>Não</span>
              </label>
              <label className="flex items-center space-x-2">
                <input
                  type="radio"
                  name="subcontratacao"
                  value="Sim"
                  checked={value === "Sim"}
                  onChange={() => onChange("Sim")}
                  className="h-5 w-5 text-green-600"
                />
                <span>Sim</span>
              </label>
            </div>
          ),
          defaultValue: "Não",
        },
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
          name: "descricao",
          label: "Descrição da solicitação (opcional)",
          type: "text",
          placeholder: "Descrição da solicitação (opcional)",
        },
      ]}
    />
  );
}
