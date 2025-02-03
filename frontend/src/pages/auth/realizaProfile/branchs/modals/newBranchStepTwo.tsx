import { Modal } from "@/components/modal";
import * as z from "zod";
import axios from "axios";

interface NewBranchModalProps {
  onClose: () => void;
  onSubmit: (data: Record<string, any>) => void;
}

const newBranchSchema = z.object({
  branchName: z.string().min(1, "O nome fantasia é obrigatório."),
  cep: z.string().min(8, "O CEP deve ter pelo menos 8 caracteres."),
  country: z.string().min(1, "O país é obrigatório."),
  state: z.string().min(1, "O estado é obrigatório."),
  city: z.string().min(1, "A cidade é obrigatória."),
  address: z.string().min(1, "O endereço é obrigatório."),
  responsiblePerson: z.string().min(1, "O responsável é obrigatório."),
  phone1: z.string().min(1, "O telefone principal é obrigatório."),
  phone2: z.string().optional(),
  logo: z.any().optional(),
});

export function NewBranchModal({ onClose, onSubmit }: NewBranchModalProps) {
  const handleSubmit = async (data: Record<string, any>) => {
    try {
      const validatedData = newBranchSchema.parse(data);
      const formData = new FormData();

      Object.entries(validatedData).forEach(([key, value]) => {
        if (value) {
          formData.append(key, value as string);
        }
      });

      const response = await axios.post(
        "https://api.example.com/branches",
        formData,
        {
          headers: { "Content-Type": "multipart/form-data" },
        },
      );

      onSubmit(response.data);
    } catch (error) {
      if (error instanceof z.ZodError) {
        alert(error.errors[0]?.message || "Erro de validação.");
      } else if (axios.isAxiosError(error)) {
        alert(error.response?.data?.message || "Erro ao enviar os dados.");
      }
    }
  };

  return (
    <Modal
      title="Nova Filial"
      onClose={onClose}
      onSubmit={handleSubmit}
      fields={[
        {
          name: "branchName",
          label: "* Nome Fantasia da Unidade",
          type: "text",
          placeholder: "Exemplo: Matriz",
          required: true,
        },
        {
          name: "cep",
          label: "* CEP",
          type: "text",
          placeholder: "Exemplo: 12345-678",
          required: true,
        },
        {
          name: "country",
          label: "* País",
          type: "text",
          defaultValue: "Brazil",
          required: true,
        },
        {
          name: "state",
          label: "* Estado",
          type: "text",
          placeholder: "Exemplo: São Paulo",
          required: true,
        },
        {
          name: "city",
          label: "* Cidade",
          type: "text",
          placeholder: "Exemplo: São Paulo",
          required: true,
        },
        {
          name: "address",
          label: "* Endereço",
          type: "text",
          placeholder: "Exemplo: Rua Principal, 123",
          required: true,
        },
        {
          name: "responsiblePerson",
          label: "* Responsável pela Unidade",
          type: "text",
          placeholder: "Nome completo do responsável",
          required: true,
        },
        {
          name: "phone1",
          label: "* Telefone",
          type: "text",
          placeholder: "(XX) XXXX-XXXX",
          required: true,
        },
        {
          name: "phone2",
          label: "Telefone 2",
          type: "text",
          placeholder: "(XX) XXXX-XXXX",
        },
        {
          name: "logo",
          label: "Logo",
          type: "file",
          accept: ".png,.jpg,.jpeg",
        },
      ]}
    />
  );
}
