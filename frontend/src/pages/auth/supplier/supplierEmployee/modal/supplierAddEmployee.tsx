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
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const response = await axios.post(`${ip}/employee/brazilian`, payload,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` }
        }
      );
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
            "AUTONOMO",
            "AVULSO_SINDICATO",
            "CLT_HORISTA",
            "CLT_TEMPO_DETERMINADO",
            "CLT_TEMPO_INDETERMINADO",
            "COOPERADO",
            "ESTAGIO_BOLSA",
            "ESTRANGEIRO_IMIGRANTE",
            "ESTRANGEIRO_TEMPORARIO",
            "INTERMITENTE",
            "JOVEM_APRENDIZ",
            "SOCIO",
            "TEMPORARIO",
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
          options: ["SOLTEIRO", "CASADO", "DIVORCIADO", "VIUVO", "SEPARADO_JUDICIALMENTE", "UNIAO_ESTAVEL"],
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
            "Fundamental I incompleto", 
            "Fundamental I completo", 
            "Fundamental II incompleto", 
            "Fundamental II completo", 
            "Médio incompleto", 
            "Médio completo", 
            "Superior incompleto", 
            "Superior completo", 
            "Pós-graduação", 
            "Mestrado", 
            "Doutorado", 
            "Ph.D",
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
