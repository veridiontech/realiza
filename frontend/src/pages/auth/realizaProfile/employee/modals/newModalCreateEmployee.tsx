import bgModalRealiza from "@/assets/modalBG.jpeg";

import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { ScrollArea } from "@/components/ui/scroll-area";
import { propsBranch, propsClient } from "@/types/interfaces";
import { ip } from "@/utils/ip";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";
import { Search } from "lucide-react";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { Oval } from "react-loader-spinner";
import { toast } from "sonner";
import { z } from "zod";

const createNewEmployeeFormSchema = z.object({
  contractType: z.string(),
  name: z.string(),
  surname: z.string(),
  email: z.string(),
  cpf: z.string().regex(/^(\d{3})(\d{3})(\d{3})(\d{2})$/),
  salary: z.string().regex(/^(\d+)(\d{3})*(\.\d{2})?$/),
  gender: z.string(),
  maritalStatus: z.string(),
  cep: z.string(),
  state: z.string(),
  city: z.string(),
  address: z.string(),
  phone: z.string().optional(),
  mobile: z.string(),
  position: z.string(),
  education: z.string(),
  cbo: z.string().optional(),
  platformAccess: z.string(),
  rg: z.string().regex(/^(\d{2})(\d{3})(\d{3})(\d{1})$/),
  admissionDate: z.string().nonempty("Data de admissão é obrigatória"),
  dob: z.string()
});

type CreateNewEmpoloyeeFormSchema = z.infer<typeof createNewEmployeeFormSchema>;
export function NewModalCreateEmployee() {
  const [clients, setClients] = useState<propsClient[]>([]);
  const [branches, setBranches] = useState([]);
  const [selectRole, setSelectRole] = useState("");
  const [selectedClient, setSelectedClient] = useState<string>("");
  const [seletedBranch, setSeletedBranch] = useState<string>("");
  const [isLoading, setIsLoading] = useState(false);

  const {
    register,
    handleSubmit,
    setValue,
    formState: { errors },
    getValues,
  } = useForm<CreateNewEmpoloyeeFormSchema>({
    resolver: zodResolver(createNewEmployeeFormSchema),
  });

  const formatCPF = (value: string) => {
    return value
      .replace(/\D/g, "")
      .replace(/(\d{3})(\d)/, "$1.$2")
      .replace(/(\d{3})(\d)/, "$1.$2")
      .replace(/(\d{3})(\d{2})$/, "$1-$2");
  };

  const formatRG = (value: string) => {
    return value
      .replace(/\D/g, "")
      .replace(/(\d{2})(\d)/, "$1.$2")
      .replace(/(\d{3})(\d)/, "$1.$2")
      .replace(/(\d{3})(\d{1})$/, "$1-$2");
  };

  const formatSalary = (value: string) => {
    return value
      .replace(/\D/g, "")
      .replace(/(\d)(?=(\d{3})+(?!\d))/g, "$1.")
      .replace(/\B(?=(\d{3})+(?!\d))/g, ",");
  };

  const onSubmit = async (data: CreateNewEmpoloyeeFormSchema) => {
    setIsLoading(true);
    const payload = {
      ...data,
      branch: seletedBranch,
    };
    console.log("Enviando dados:",payload);
    try {
      const response = await axios.post(`${ip}/employee/brazilian`, payload);
      console.log("Sucesso na requisição!", response.data);
      toast.success("Sucesso ao cadastrar novo usuário");
    } catch (err) {
      if (axios.isAxiosError(err)) {
        console.error("Erro Axios:", err.response?.status, err.response?.data);
        toast.error(`Erro ${err.response?.status}: ${err.response?.data?.message || "Erro ao cadastrar usuário"}`);
      } else {
        console.error("Erro desconhecido:", err);
        toast.error("Erro inesperado, tente novamente");
      }
    } finally {
      setIsLoading(false);
    }
  };

  const getClient = async () => {
    try {
      const firstRes = await axios.get(`${ip}/client`, {
        params: { page: 0, size: 100 },
      });
      const totalPages = firstRes.data.totalPages;
      const requests = Array.from({ length: totalPages - 1 }, (_, i) =>
        axios.get(`${ip}/client`, { params: { page: i + 1, size: 100 } }),
      );

      const responses = await Promise.all(requests);
      const allClients = [
        firstRes.data.content,
        ...responses.map((res) => res.data.content),
      ].flat();

      setClients(allClients);
    } catch (err) {
      console.error("Erro ao puxar clientes", err);
    }
  };

  useEffect(() => {
    if (selectRole === "branch") {
      getClient();
    }
  }, [selectRole]);

  useEffect(() => {
    if (selectedClient) {
      getBranch(selectedClient); 
    }
  }, [selectedClient]);

  const getBranch = async (idClient: string) => {
    console.log("idClient:", idClient);
    if (!idClient) return;
    try {
      const res = await axios.get(
        `${ip}/branch/filtered-client?idSearch=${idClient}`,
      );
      console.log("teste", res.data.content);

      setBranches(res.data.content);
    } catch (err) {
      console.log(err);
    }
  };

  return (
    <Dialog>
      <DialogTrigger asChild>
        <Button className="bg-realizaBlue">Cadastrar novo colaborador</Button>
      </DialogTrigger>
      <DialogContent
        style={{ backgroundImage: `url(${bgModalRealiza})` }}
        className="max-w-[45vw]"
      >
        <DialogHeader>
          <DialogTitle className="text-white">
            Cadastrar colaborador
          </DialogTitle>
          <ScrollArea className="h-[75vh]">
            <div>
              <form
                action=""
                className="flex flex-col gap-5"
                onSubmit={handleSubmit(onSubmit, (errors) => {
                  console.log("Erros detectados:", errors);
                })}
              >
                <div>
                  <Label className="text-white">Tipo de contrato</Label>
                  <select
                    {...register("contractType")}
                    className="flex flex-col rounded-md border p-2"
                  >
                    <option value="">Selecione um tipo de contrato</option>
                    <option value="Autônomo">Autônomo</option>
                    <option value="Avulso (Sindicato)">
                      Avulso (Sindicato)
                    </option>
                    <option value="CLT - Horista">CLT - Horista</option>
                    <option value="CLT - Tempo Determinado">
                      CLT - Tempo Determinado
                    </option>
                    <option value="CLT - Tempo Indeterminado">
                      CLT - Tempo Indeterminado
                    </option>
                    <option value="Cooperado">Cooperado</option>
                    <option value="Estágio / Bolsa">Estágio / Bolsa</option>
                    <option value="Estrangeiro - Imigrante">
                      Estrangeiro - Imigrante
                    </option>
                    <option value="Estrangeiro - Temporário">
                      Estrangeiro - Temporário
                    </option>
                    <option value="Intermitente">Intermitente</option>
                    <option value="Jovem Aprendiz">Jovem Aprendiz</option>
                    <option value="Sócio">Sócio</option>
                    <option value="Temporário">Temporário</option>
                  </select>
                </div>
                <div>
                  <Label className="text-white">Nome</Label>
                  <Input type="text" {...register("name")} />
                </div>
                <div>
                  <Label className="text-white">Sobrenome</Label>
                  <Input type="text" {...register("surname")} />
                </div>
                <div>
                  <Label className="text-white">Data de aniversário</Label>
                  <Input type="date" {...register("dob")} />
                </div>
                <div>
                  <Label className="text-white">Email</Label>
                  <Input type="text" {...register("email")} />
                </div>
                <div>
                  <Label className="text-white">CPF:</Label>
                  <Input
                    type="text"
                    value={getValues("cpf")} 
                    {...register("cpf")}
                    onChange={(e) => {
                      const formattedCPF = formatCPF(e.target.value);
                      setValue("cpf", formattedCPF);
                    }}
                    placeholder="000.000.000-00"
                    maxLength={14}
                  />
                  {errors.cpf && <span>{errors.cpf.message}</span>}
                </div>
                <div>
                  <Label className="text-white">RG:</Label>
                  <Input
                    type="text"
                    value={getValues("rg")}
                    {...register("rg")}
                    onChange={(e) => {
                      const formattedRG = formatRG(e.target.value);
                      setValue("rg", formattedRG);
                    }}
                    placeholder="00.000.000-0"
                    maxLength={12}
                  />
                  {errors.rg && <span>{errors.rg.message}</span>}
                </div>
                <div>
                  <Label className="text-white">Data de admissão</Label>
                  <Input type="date" {...register("admissionDate")} />
                </div>
                <div>
                  <Label className="text-white">Salário:</Label>
                  <Input
                    type="text"
                    value={getValues("salary")}
                    {...register("salary")}
                    onChange={(e) => {
                      const formattedSalary = formatSalary(e.target.value);
                      setValue("salary", formattedSalary);
                    }}
                    placeholder="000.000,00"
                  />
                  {errors.salary && <span>{errors.salary.message}</span>}
                </div>
                <div className="flex flex-col items-start gap-3">

                  <div className="flex items-start gap-2">
                    <Label className="text-white">Subcontratado</Label>
                    <input
                      type="radio"
                      checked={selectRole === "subcontractor"}
                      onChange={() => setSelectRole("subcontractor")}
                    />
                  </div>
                  <div className="flex items-start gap-2">
                    <Label className="text-white">Fornecedor</Label>
                    <input
                      type="radio"
                      checked={selectRole === "supplier"}
                      onChange={() => setSelectRole("supplier")}
                    />
                  </div>
                </div>
                <div>
                  {selectRole === "branch" && (
                    <div className="flex flex-col gap-3">
                      <select
                        defaultValue=""
                        className="rounded-md p-1"
                        onChange={(e) => {
                          const id = e.target.value;
                          setSelectedClient(id);
                          getBranch(id);
                        }}
                      >
                        <option value="" disabled>
                          Selecione um cliente
                        </option>
                        {clients.map((client: propsClient) => (
                          <option value={client.idClient} key={client.idClient}>
                            {client.tradeName}
                          </option>
                        ))}
                      </select>
                      <select
                        className="rounded-md p-1"
                        defaultValue=""
                        onChange={(e) => {
                          const id = e.target.value;
                          setSeletedBranch(id);
                        }}
                      >
                        <option value="">Selecione uma filial</option>
                        {branches.map((branch: propsBranch) => (
                          <option value={branch.idBranch} key={branch.idBranch}>
                            {branch.name}
                          </option>
                        ))}
                      </select>
                    </div>
                  )}
                  {selectRole === "subcontractor" && (
                    <div>
                      teste sasa
                      <select>{}</select>
                    </div>
                  )}
                  {selectRole === "supplier" && (
                    <div>
                      teste as1231
                      <select>{}</select>
                    </div>
                  )}
                </div>
                <div>
                  <Label className="text-white">Sexo</Label>
                  <select
                    {...register("gender")}
                    className="flex flex-col rounded-md border p-2"
                  >
                    <option value="">Selecione</option>
                    <option value="Masculino">Masculino</option>
                    <option value="Feminino">Feminino</option>
                  </select>
                  {errors.gender && (
                    <span className="text-red-600">
                      {errors.gender.message}
                    </span>
                  )}
                </div>
                <div>
                  <Label className="text-white">Estado civil</Label>
                  <select
                    {...register("maritalStatus")}
                    className="flex flex-col rounded-md border p-2"
                  >
                    <option value="">Selecione</option>
                    <option value="Masculino">Casado</option>
                    <option value="Feminino">Solteiro</option>
                  </select>
                </div>
                <div className="flex gap-1">
                  <div>
                    <Label className="text-white">CEP</Label>
                    <div className="flex gap-2">
                      <Input {...register("cep")} />
                      <Button className="bg-realizaBlue">
                        <Search />
                      </Button>
                    </div>
                  </div>
                </div>
                <div>
                  <Label className="text-white">Estado</Label>
                  <Input {...register("state")} />
                </div>
                <div>
                  <Label className="text-white">Cidade</Label>
                  <Input {...register("city")} />
                </div>
                <div>
                  <Label className="text-white">Adress</Label>
                  <Input {...register("address")} />
                </div>
                <div>
                  <Label className="text-white">Telefone</Label>
                  <Input {...register("phone")} />
                </div>
                <div>
                  <Label className="text-white">Celular</Label>
                  <Input {...register("mobile")} />
                </div>
                <div>
                  <Label className="text-white">Cargo</Label>
                  <Input {...register("position")} />
                </div>
                <div>
                  <Label className="text-white">cbo</Label>
                  <Input {...register("cbo")} />
                </div>
                <div className="flex flex-col gap-2">
                  <Label className="text-white">Graduação</Label>
                  <select
                {...register("education")}
                className="flex flex-col rounded-md border p-2"
              >
                <option value="">Selecione</option>
                <option value="Ensino Fundamental Incompleto">
                  Ensino Fundamental Incompleto
                </option>
                <option value="Ensino Fundamental Completo">
                  Ensino Fundamental Completo
                </option>
                <option value="Ensino Médio Incompleto">
                  Ensino Médio Incompleto
                </option>
                <option value="Ensino Médio Completo">
                  Ensino Médio Completo
                </option>
                <option value="Ensino Superior Incompleto">
                  Ensino Superior Incompleto
                </option>
                <option value="Ensino Superior Completo">
                  Ensino Superior Completo
                </option>
              </select>
                </div>
                <div>
                  <Label className="text-white">Acesso a plataforma</Label>
                  <select
                    {...register("platformAccess")}
                    className="flex flex-col rounded-md border p-2"
                  >
                    <option value="">Selecione</option>
                    <option value="Sim">Sim</option>
                    <option value="Não">Não</option>
                  </select>
                </div>
                <Button
                  type="submit"
                  className="bg-realizaBlue"
                  disabled={isLoading}
                >
                  {isLoading ? (
                    <Oval
                      visible={true}
                      height={20}
                      width={20}
                      color="#fff"
                      ariaLabel="oval-loading"
                    />
                  ) : (
                    "Cadastrar"
                  )}
                </Button>
              </form>
            </div>
          </ScrollArea>
        </DialogHeader>
      </DialogContent>
    </Dialog>
  );
}
