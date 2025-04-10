import { useEffect, useState } from "react";
import { Pagination } from "@/components/ui/pagination";
import { Table } from "@/components/ui/tableVanila";
import axios from "axios";
import { Oval, Puff } from "react-loader-spinner";
import { ip } from "@/utils/ip";
import { useClient } from "@/context/Client-Provider";
import { Eye, Search } from "lucide-react"; // Importando apenas o ícone Eye
import { Link } from "react-router-dom";
import { useUser } from "@/context/user-provider";
import { useBranch } from "@/context/Branch-provider";
import { propsBranch } from "@/types/interfaces";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { toast } from "sonner";
import { Label } from "@/components/ui/label";
import { ScrollArea } from "@/components/ui/scroll-area";
import { AddNewBranch } from "./modals/add-new-branch";

// interface BranchType {
//   idBranch: string;
//   name: string;
//   cnpj: string;
//   address: string;
//   actions: string;
// }

const newBranchFormSchema = z.object({
  cnpj: z.string(),
  name: z.string().min(1, "O nome da filial é obrigatório"),
  email: z.string().email("Insira um email válido"),
  cep: z.string().min(8, "O CEP deve ter pelo menos 8 caracteres."),
  country: z.string().min(1, "O país é obrigatório."),
  state: z.string().min(1, "O estado é obrigatório."),
  city: z.string().min(1, "A cidade é obrigatória."),
  address: z.string().min(1, "O endereço é obrigatório."),
  number: z.string().nonempty("Número é obrigatório"),
  telephone: z.string().nonempty("Insira um telefone"),
});

type NewBranchFormSchema = z.infer<typeof newBranchFormSchema>;
export function Branch() {
  const [branches, setBranches] = useState<propsBranch[]>([]);
  const [totalPages, setTotalPages] = useState(1);
  const [currentPage, setCurrentPage] = useState(1);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { client } = useClient();
  const { user } = useUser();
  const { setSelectedBranch } = useBranch();
  const [razaoSocial, setRazaoSocial] = useState<string | null>(null);
  const [cnpjValue, setCnpjValue] = useState("");

  const {
    register,
    handleSubmit,
    getValues,
    setValue,
    formState: { errors },
  } = useForm<NewBranchFormSchema>({
    resolver: zodResolver(newBranchFormSchema),
  });

  const formatCNPJ = (value: string) => {
    return value
      .replace(/\D/g, "")
      .replace(/(\d{2})(\d)/, "$1.$2")
      .replace(/(\d{3})(\d)/, "$1.$2")
      .replace(/(\d{3})(\d{4})$/, "$1/$2")
      .replace(/(\d{4})(\d{2})$/, "$1-$2");
  };


  const columns: {
    key: keyof propsBranch;
    label: string;
    render?: (value: any, row: propsBranch) => JSX.Element;
  }[] = [
      { key: "name", label: "Nome da Filial" },
      { key: "cnpj", label: "CNPJ" },
      { key: "address", label: "Endereço" },
      {
        key: "actions",
        label: "Ações",
        render: (_value, row: propsBranch) => (
          <div>
            {user?.role === "ROLE_CLIENT_RESPONSIBLE" ? (
              <Link to={`/cliente/profileBranch/${row.idBranch}`}>
                <Eye />
              </Link>
            ) : (
              <Link
                to={`/sistema/profileBranch/${row.idBranch}`}
                onClick={() => setSelectedBranch(row)}
              >
                <Eye />
              </Link>
            )}
          </div>
        ),
      },
    ];

  const fetchBranches = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await axios.get(
        `${ip}/branch/filtered-client?idSearch=${client?.idClient}`,
      );
      const { content, totalPages: total } = response.data;
      setBranches(content);
      setTotalPages(total);
    } catch (err) {
      console.error("Erro ao buscar filiais:", err);
      setError("Erro ao buscar filiais. Tente novamente.");
    } finally {
      setLoading(false);
    }
  };

  const handleCnpj = async () => {
    const cnpj = getValues("cnpj").replace(/\D/g, "");
    if (cnpj.length !== 14) {
      toast.error("CNPJ inválido");
      return;
    }
    try {
      const res = await axios.get(`https://open.cnpja.com/office/${cnpj}`);
      console.log("Dados da empresa:", res.data);
      const razao =
        res.data.company.name ||
        res.data.company.name ||
        "Razão social não encontrada";

      const cep = res.data.address.zip;

      const city = res.data.address.city;

      const address = res.data.address.street;

      const country = res.data.address.country.name;

      const state = res.data.address.state;
      const number = res.data.address.number;

      setValue("number", number);
      setValue("state", state);
      setValue("country", country);
      setValue("address", address);
      setValue("cep", cep);
      setValue("city", city);
      setValue("name", razao);
      setRazaoSocial(razao);
      toast.success("Sucesso ao buscar CNPJ");
    } catch (err) {
      setRazaoSocial(null);
      toast.error("Erro ao buscar CNPJ");
      console.error("Erro ao buscar CNPJ:", err);
    }
  };

  useEffect(() => {
    if (client?.idClient) {
      setBranches([]);
      setCurrentPage(1);
      fetchBranches();
    }
  }, [client?.idClient]);

  const handlePageChange = (page: number) => {
    if (page >= 1 && page <= totalPages) {
      setCurrentPage(page);
    }
  };

  const onSubmit = async (data: NewBranchFormSchema) => {
    const payload = {
      ...data,
      client: client?.idClient,
    };
    setLoading(true)
    try {
      console.log("enviando dados:", payload);
      await axios.post(`${ip}/branch`, payload);
      toast.success("Sucesso ao criar filial");
    } catch (err: any) {
      if (err.response && err.response.data) {
        const mensagemBackend =
          err.response.data.message ||
          err.response.data.error ||
          "Erro inesperado no servidor";
        console.log(mensagemBackend);

        toast.error(mensagemBackend);
      } else if (err.request) {
        toast.error("Não foi possível se conectar ao servidor.");
      } else {
        toast.error("Erro desconhecido ao processar requisição.");
      }

      console.error("Erro ao criar filial:", err);
    }finally {
      setLoading(false)
    }
  };

  return (
    <div>
      <div className="m-4 flex justify-center">
        <div className="flex w-[90rem] flex-col rounded-lg bg-white p-4 shadow-md">
          <div className="mb-6 flex items-center justify-between">
            <h1 className="m-8 text-2xl">Filiais</h1>
            <AddNewBranch />
          </div>
          {loading ? (
            <div className="flex w-[20vw] items-center justify-start rounded-md border p-2 dark:bg-white">
              <Puff
                visible={true}
                height="30"
                width="30"
                color="#34495D"
                ariaLabel="puff-loading"
              />
              <span className="ml-2 text-black">Carregando...</span>
            </div>
          ) : error ? (
            <div className="text-center text-red-500">{error}</div>
          ) : (
            <Table<propsBranch> data={branches} columns={columns} />
          )}
          <Pagination
            currentPage={currentPage}
            totalPages={totalPages}
            onPageChange={handlePageChange}
          />
        </div>
      </div>
    </div>
  );
}
