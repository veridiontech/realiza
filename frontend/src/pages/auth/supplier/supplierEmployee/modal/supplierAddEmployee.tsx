import { Modal } from "@/components/modal";
import axios from "axios";
import { ip } from "@/utils/ip";
import { useClient } from "@/context/Client-Provider";

interface SupplierAddEmployeeProps {
  onClose: () => void;
  onSubmit: (data: Record<string, any>) => void;
}

export function SupplierAddEmployee({
  onClose,
  onSubmit,
}: SupplierAddEmployeeProps) {
  const { client } = useClient();

  const handleSubmit = async (formData: Record<string, any>) => {
    const filterIdClient = client?.idClient;
    console.log(filterIdClient);

    const payload = {
      ...formData,
      client: filterIdClient,
    };
    try {
      const response = await axios.post(`${ip}/employee/brazilian`, payload);
      console.log("Colaborador criado com sucesso:", response.data);
      onSubmit(response.data);
    } catch (error: any) {
      console.error(
        "Erro ao criar Colaborador:",
        error.response?.data || error.message,
      );
      alert("Erro ao criar colaborador. Verifique os dados e tente novamente.");
    }
  };

  return (
    <Modal
      title="Cadastrar colaborador"
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
          ],
          required: true,
        },
        {
          name: "name",
          label: "Nome completo",
          type: "text",
          placeholder: "Digite seu nome completo...",
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
