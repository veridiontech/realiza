import { Modal } from "@/components/modal";
import axios from "axios";
import { ip } from "@/utils/ip";

interface StepOneEmployeeProps {
  onClose: () => void;
  onSubmit: (data: Record<string, any>) => void;
}

export function StepOneEmployee({ onClose, onSubmit }: StepOneEmployeeProps) {
  const handleSubmit = async (formData: Record<string, any>) => {
    try {
      const response = await axios.post(
        `${ip}/employee/brazilian`, // Altere para o endpoint correto
        formData,
      );
      console.log("Funcionário criado com sucesso:", response.data);
      onSubmit(response.data); // Retorna os dados para o componente pai
    } catch (error: any) {
      console.error(
        "Erro ao criar funcionário:",
        error.response?.data || error.message,
      );
      alert("Erro ao criar funcionário. Verifique os dados e tente novamente.");
    }
  };

  return (
    <Modal
      title="Cadastrar Funcionário"
      fields={[
        {
          name: "contractType",
          label: "Tipo de Contrato",
          type: "select",
          options: [
            "Autônomo",
            "Avulso (Sindicato)",
            "CLT - Horista",
            "CLT - Tempo Determinado",
            "CLT - Tempo Indeterminado",
            "Cooperado",
            "Estágio / Bolsa",
            "Estrangeiro - Imigrante",
            "Estrangeiro - Temporário",
            "Intermitente",
            "Jovem Aprendiz",
            "Sócio",
            "Temporário",
            "PJ",
          ],
          required: true,
        },
        {
          name: "name",
          label: "Nome",
          type: "text",
          placeholder: "Nome",
          required: true,
        },
        {
          name: "surname",
          label: "Sobrenome",
          type: "text",
          placeholder: "Sobrenome",
          required: true,
        },
        {
          name: "cpf",
          label: "CPF",
          type: "text",
          placeholder: "000.000.000-00",
          required: true,
        },
        {
          name: "rg",
          label: "RG",
          type: "text",
          placeholder: "RG",
          required: true,
        },
        {
          name: "pis",
          label: "PIS",
          type: "text",
          placeholder: "PIS",
        },
        {
          name: "email",
          label: "E-mail",
          type: "email",
          placeholder: "exemplo@email.com",
          required: true,
        },
        {
          name: "salary",
          label: "Salário R$",
          type: "number",
          placeholder: "0.00",
          required: true,
        },
        {
          name: "sex",
          label: "Sexo",
          type: "select",
          options: ["Masculino", "Feminino", "Outro"],
          required: true,
        },
        {
          name: "maritalStatus",
          label: "Estado Civil",
          type: "select",
          options: ["Solteiro(a)", "Casado(a)", "Divorciado(a)", "Viúvo(a)"],
          required: true,
        },
        {
          name: "dob",
          label: "Data de Nascimento",
          type: "date",
          required: true,
        },
        {
          name: "cep",
          label: "CEP",
          type: "text",
          placeholder: "00000-000",
          required: true,
        },
        {
          name: "state",
          label: "Estado",
          type: "text",
          placeholder: "Estado",
          required: true,
        },
        {
          name: "city",
          label: "Cidade",
          type: "text",
          placeholder: "Cidade",
          required: true,
        },
        {
          name: "address",
          label: "Endereço",
          type: "text",
          placeholder: "Endereço",
          required: true,
        },
        {
          name: "phone",
          label: "Telefone",
          type: "telephone",
          placeholder: "(XX) XXXX-XXXX",
        },
        {
          name: "mobile",
          label: "Celular",
          type: "telephone",
          placeholder: "(XX) XXXXX-XXXX",
          required: true,
        },
        {
          name: "admissionDate",
          label: "Data de Admissão",
          type: "date",
          required: true,
        },
        {
          name: "role",
          label: "Cargo",
          type: "text",
          placeholder: "Cargo",
          required: true,
        },
        {
          name: "education",
          label: "Grau de Instrução",
          type: "select",
          options: [
            "Ensino Fundamental Incompleto",
            "Ensino Fundamental Completo",
            "Ensino Médio Incompleto",
            "Ensino Médio Completo",
            "Ensino Superior Incompleto",
            "Ensino Superior Completo",
          ],
          required: true,
        },
        {
          name: "cbo",
          label: "CBO",
          type: "text",
          placeholder: "CBO",
        },
        {
          name: "platformAccess",
          label: "Usuário com acesso na plataforma?",
          type: "select",
          options: ["Sim", "Não"],
          required: true,
        },
      ]}
      onSubmit={handleSubmit}
      onClose={onClose}
    />
  );
}
