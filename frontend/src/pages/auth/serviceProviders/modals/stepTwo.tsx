import { Modal } from "@/components/modal";
import * as z from "zod";
import { useActivities, Activity } from "@/hooks/gets/useActivities";
import { useRequirements, Requirement } from "@/hooks/gets/useRequirements";
import axios from "axios";

interface StepTwoServiceProvidersProps {
  onClose: () => void;
  onSubmit: (data: Record<string, any>) => void;
}

// Definindo o esquema Zod
const stepTwoSchema = z.object({
  serviceType: z.enum(["Tipo 1", "Tipo 2", "Tipo 3"], {
    errorMap: () => ({ message: "Selecione um tipo de serviço válido." }),
  }),
  serviceDuration: z.string().min(1, "A duração do serviço é obrigatória."),
  serviceName: z
    .string()
    .min(1, "O nome do serviço é obrigatório.")
    .max(300, "O nome do serviço não pode exceder 300 caracteres."),
  description: z.string().optional(),
  allocatedLimit: z.number().positive("O limite alocado deve ser positivo."),
  startDate: z.string().refine((date) => !isNaN(new Date(date).getTime()), {
    message: "Data de início inválida.",
  }),
  endDate: z
    .string()
    .optional()
    .refine((date) => !date || !isNaN(new Date(date).getTime()), {
      message: "Data de término inválida.",
    }),
  activities: z.array(z.string()).nonempty("Selecione ao menos uma atividade."),
  requirements: z
    .array(z.string())
    .nonempty("Selecione ao menos um requisito."),
  providerSupplier: z.string().uuid("Selecione um fornecedor válido."),
});

export function StepTwoServiceProviders({
  onClose,
  onSubmit,
}: StepTwoServiceProvidersProps) {
  const {
    activities,
    loading: activitiesLoading,
    error: activitiesError,
  } = useActivities();
  const {
    requirements,
    loading: requirementsLoading,
    error: requirementsError,
  } = useRequirements();

  const handleSubmit = async (data: Record<string, any>) => {
    try {
      const validatedData = stepTwoSchema.parse({
        ...data,
        allocatedLimit: Number(data.allocatedLimit),
      });

      const response = await axios.post(
        "https://realiza.onrender.com/contract/supplier",
        validatedData,
      );

      onSubmit(response.data);
    } catch (error) {
      if (error instanceof z.ZodError) {
        alert(error.errors[0]?.message || "Erro de validação.");
      } else if (axios.isAxiosError(error)) {
        alert(
          error.response?.data?.message ||
            "Erro ao enviar os dados. Verifique sua conexão ou tente novamente.",
        );
      }
    }
  };

  if (activitiesLoading || requirementsLoading) {
    return <p>Carregando dados...</p>;
  }

  if (activitiesError || requirementsError) {
    return <p>Erro ao carregar atividades ou requisitos.</p>;
  }

  return (
    <Modal
      title="Solicitar Serviço"
      onClose={onClose}
      onSubmit={handleSubmit}
      fields={[
        {
          name: "confirmation",
          label: "* É uma Subcontratação?",
          type: "radio",
          options: [
            { label: "Sim", value: "yes" },
            { label: "Não", value: "no" },
          ],
          required: true,
        },
        {
          name: "serviceReference",
          label: "* Referência do Serviço",
          type: "text",
          placeholder: "Exemplo: 0001",
          required: true,
        },
        {
          name: "serviceType",
          label: "* Tipo do Serviço",
          type: "select",
          options: ["Tipo 1", "Tipo 2", "Tipo 3"],
          required: true,
        },
        {
          name: "serviceTypeExpense",
          label: "* Tipo de Despesa",
          type: "select",
          options: ["CAPEX", "OPEX"],
          required: true,
        },
        {
          name: "serviceDuration",
          label: "* Duração do Serviço",
          type: "text",
          placeholder: "Exemplo: 6 months",
          required: true,
        },
        {
          name: "serviceName",
          label: "* Nome do Serviço",
          type: "text",
          placeholder: "Máximo 300 caracteres",
          required: true,
        },
        {
          name: "description",
          label: "Escopo do Serviço",
          type: "text",
          placeholder: "Descrição detalhada do serviço",
          required: false,
        },
        {
          name: "allocatedLimit",
          label: "Número Máximo de Empregados Alocados",
          type: "number",
          placeholder: "Exemplo: 50000",
          required: false,
        },
        {
          name: "startDate",
          label: "* Data de Início",
          type: "date",
          required: true,
        },
        {
          name: "endDate",
          label: "Data de Término",
          type: "date",
        },
        {
          name: "activities",
          label: "Atividades",
          type: "multiselect",
          options: activities.map((activity: Activity) => ({
            label: activity.title,
            value: activity.idActivity,
          })),
          required: false,
        },
        {
          name: "requirements",
          label: "Requisitos",
          type: "multiselect",
          options: requirements.map((requirement: Requirement) => ({
            label: requirement.title,
            value: requirement.idRequirement,
          })),
          required: false,
        },
      ]}
    />
  );
}
