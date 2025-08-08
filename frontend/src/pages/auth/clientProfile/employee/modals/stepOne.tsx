import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import axios from "axios";
import { ip } from "@/utils/ip";
import { useEffect, useState } from "react";
import { propsBranch, propsClient } from "@/types/interfaces";
import { useClient } from "@/context/Client-Provider";
import bgModalRealiza from "@/assets/modalBG.jpeg";
import { ScrollArea } from "@/components/ui/scroll-area";
import { toast } from "sonner";

const employeeFormSchema = z.object({
  clientSelect: z.string().nonempty("Selecione um cliente"),
  id_branch: z.string().nonempty("Selecione uma filial"),
  contractType: z.string().nonempty("Tipo de contrato é obrigatório"),
  name: z.string().nonempty("Nome completo é obrigatório"),
  cpf: z.string().nonempty("CPF é obrigatório"),
  salary: z.string(),
  gender: z.string().nonempty("Sexo é obrigatório"),
  maritalStatus: z.string().nonempty("Estado Civil é obrigatório"),
  dob: z.string().nonempty("Data de nascimento é obrigatória"),
  cep: z.string().nonempty("CEP é obrigatório"),
  state: z.string().nonempty("Estado é obrigatório"),
  city: z.string().nonempty("Cidade é obrigatória"),
  address: z.string().nonempty("Endereço é obrigatório"),
  phone: z.string().optional(),
  mobile: z.string(),
  admissionDate: z.string().nonempty("Data de admissão é obrigatória"),
  role: z.string().nonempty("Cargo é obrigatório"),
  education: z.string().nonempty("Grau de instrução é obrigatório"),
  cboId: z.string().optional(),
  platformAccess: z.string().nonempty("Acesso à plataforma é obrigatório"),
});

type EmployeeFormSchema = z.infer<typeof employeeFormSchema>;

export function StepOneEmployee({
  onSubmit,
}: {
  onClose: () => void;
  onSubmit: (data: any) => void;
}) {
  const { client } = useClient();
  const [clients, setClients] = useState<propsClient[]>([]);
  const [branches, setBranches] = useState<propsBranch[]>([]);
  // const [isOpen, setIsOpen] = useState(false);
  const [idbranch, setIdBranch] = useState<string | null>(null);
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<EmployeeFormSchema>({
    resolver: zodResolver(employeeFormSchema),
  });
  const [isOpen, setIsOpen] = useState(false);

  useEffect(() => {
    const getAllClients = async () => {
      try {
        const tokenFromStorage = localStorage.getItem("tokenClient");
        const firstRes = await axios.get(`${ip}/client`, {
          params: { page: 0, size: 100 },
          headers: { Authorization: `Bearer ${tokenFromStorage}` }
        });
        const totalPages = firstRes.data.totalPaages;
        const requests = Array.from({ length: totalPages - 1 }, (_, i) =>
          axios.get(`${ip}/client`, { params: { page: i + 1, size: 100 }, headers: { Authorization: `Bearer ${tokenFromStorage}` } }),
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

    getAllClients();
  }, []);

  const getBranches = async (clientId: string) => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      // Faz a primeira requisição para obter o número total de páginas
      const firstRes = await axios.get(
        `${ip}/branch/filtered-client?idSearch=${clientId}`,
        { params: { page: 0, size: 100 }, headers: { Authorization: `Bearer ${tokenFromStorage}` } },
      );
      const totalPages = firstRes.data.totalPages; // Obtém o total de páginas disponíveis
      const requests = Array.from({ length: totalPages - 1 }, (_, i) =>
        axios.get(`${ip}/branch/filtered-client?idSearch=${clientId}`, {
          params: { page: i + 1, size: 100 },
          headers: { Authorization: `Bearer ${tokenFromStorage}` }
        }),
      );

      // Aguarda todas as requisições assíncronas
      const responses = await Promise.all(requests);

      // Combina todas as respostas
      const allBranches = [
        firstRes.data.content,
        ...responses.map((res) => res.data.content),
      ].flat();

      setBranches(allBranches);
    } catch (err) {
      console.error("Erro ao buscar filiais", err);
    }
  };

  const handleClientChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    getBranches(e.target.value);
  };

  const getIdBranch = (id_branch: string) => {
    setIdBranch(id_branch);
  };

  const onFormSubmit = async (data: EmployeeFormSchema) => {
    const filterIdClient = client?.idClient;
    console.log("id da branch teste:", idbranch);

    const payload = { ...data, client: filterIdClient, id_branch: idbranch };
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const response = await axios.post(`${ip}/employee/brazilian`, payload,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` }
        }
      );
      onSubmit(response.data);
      toast.success("Sucesso ao criar colaborador");
      setIsOpen(false);
    } catch (error: any) {
      console.error(error.response?.data || error.message);
      toast.error("Erro ao criar colaborador, tente novamente");
      setIsOpen(false);
    }
  };

  return (
    <Dialog open={isOpen} onOpenChange={setIsOpen}>
      <DialogTrigger asChild>
        <Button className="bg-realizaBlue">Cadastrar colaborador</Button>
      </DialogTrigger>
      <DialogContent
        style={{ backgroundImage: `url(${bgModalRealiza})` }}
        className="max-w-[45vw]"
      >
        <DialogHeader>
          <DialogTitle className="text-white">
            Cadastrar colaborador
          </DialogTitle>
        </DialogHeader>
        <ScrollArea className="h-[70vh]">
          <form
            onSubmit={handleSubmit(onFormSubmit)}
            className="flex flex-col gap-4"
          >
            <div>
              <Label className="text-white">Selecione um cliente</Label>
              <select
                {...register("clientSelect")}
                onChange={(e) => {
                  register("clientSelect").onChange(e);
                  handleClientChange(e);
                }}
                className="flex flex-col rounded-md border p-2"
              >
                <option value="">Selecione um cliente</option>
                {clients.map((client) => (
                  <option key={client.idClient} value={client.idClient}>
                    {client.tradeName}
                  </option>
                ))}
              </select>
              {errors.clientSelect && (
                <span className="text-red-600">
                  {errors.clientSelect.message}
                </span>
              )}
            </div>
            <div>
              <Label className="text-white">Filiais do cliente</Label>
              <select
                className="flex flex-col rounded-md border p-2"
                onChange={(e) => setIdBranch(e.target.value)}
              >
                <option value="">Selecione uma filial</option>
                {branches.map((branch: any) => (
                  <option key={branch.id_branch} value={branch.id_branch} onClick={() => getIdBranch(branch.id_branch)}>
                    {branch.name}
                  </option>
                ))}
              </select>

              {errors.id_branch && (
                <span className="text-red-600">{errors.id_branch.message}</span>
              )}
            </div>
            <div>
              <Label className="text-white">Tipo de Contrato</Label>
              <select
                {...register("contractType")}
                className="flex flex-col rounded-md border p-2"
              >
                <option value="">Selecione um tipo</option>
                <option value="AUTONOMO">Autônomo</option>
                <option value="AVULSO_SINDICATO">Avulso (Sindicato)</option>
                <option value="CLT_HORISTA">CLT - Horista</option>
                <option value="CLT_TEMPO_DETERMINADO">
                  CLT - Tempo Determinado
                </option>
                <option value="CLT_TEMPO_INDETERMINADO">
                  CLT - Tempo Indeterminado
                </option>
                <option value="COOPERADO">Cooperado</option>
                <option value="ESTAGIO_BOLSA">Estágio / Bolsa</option>
                <option value="ESTRANGEIRO_IMIGRANTE">
                  Estrangeiro - Imigrante
                </option>
                <option value="ESTRANGEIRO_TEMPORARIO">
                  Estrangeiro - Temporário
                </option>
                <option value="INTERMITENTE">Intermitente</option>
                <option value="JOVEM_APRENDIZ">Jovem Aprendiz</option>
                <option value="SOCIO">Sócio</option>
                <option value="TEMPORARIO">Temporário</option>
              </select>
              {errors.contractType && (
                <span className="text-red-600">
                  {errors.contractType.message}
                </span>
              )}
            </div>
            <div>
              <Label className="text-white">Nome completo</Label>
              <Input
                type="text"
                placeholder="Digite seu nome completo..."
                {...register("name")}
              />
              {errors.name && (
                <span className="text-red-600">{errors.name.message}</span>
              )}
            </div>
            <div>
              <Label className="text-white">CPF</Label>
              <Input
                type="text"
                placeholder="000.000.000-00"
                {...register("cpf")}
              />
              {errors.cpf && (
                <span className="text-red-600">{errors.cpf.message}</span>
              )}
            </div>
            <div>
              <Label className="text-white">Salário R$</Label>
              <Input type="number" placeholder="0.00" {...register("salary")} />
              {errors.salary && (
                <span className="text-red-600">{errors.salary.message}</span>
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
                <span className="text-red-600">{errors.gender.message}</span>
              )}
            </div>
            <div>
              <Label className="text-white">Estado Civil</Label>
              <select
                {...register("maritalStatus")}
                className="flex flex-col rounded-md border p-2"
              >
                <option value="">Selecione</option>
                <option value="SOLTEIRO">Solteiro(a)</option>
                <option value="CASADO">Casado(a)</option>
                <option value="DIVORCIADO">Divorciado(a)</option>
                <option value="VIUVO">Viúvo(a)</option>
                <option value="SEPARADO_JUDICIALMENTE">Separado judicialmente </option>
                <option value="UNIAO_ESTAVEL">União estável</option>
              </select>
              {errors.maritalStatus && (
                <span className="text-red-600">
                  {errors.maritalStatus.message}
                </span>
              )}
            </div>
            <div>
              <Label className="text-white">Data de Nascimento</Label>
              <Input type="date" {...register("dob")} />
              {errors.dob && (
                <span className="text-red-600">{errors.dob.message}</span>
              )}
            </div>
            <div>
              <Label className="text-white">CEP</Label>
              <Input type="text" placeholder="00000-000" {...register("cep")} />
              {errors.cep && (
                <span className="text-red-600">{errors.cep.message}</span>
              )}
            </div>
            <div>
              <Label className="text-white">Estado</Label>
              <Input type="text" placeholder="Estado" {...register("state")} />
              {errors.state && (
                <span className="text-red-600">{errors.state.message}</span>
              )}
            </div>
            <div>
              <Label className="text-white">Cidade</Label>
              <Input type="text" placeholder="Cidade" {...register("city")} />
              {errors.city && (
                <span className="text-red-600">{errors.city.message}</span>
              )}
            </div>
            <div>
              <Label className="text-white">Endereço</Label>
              <Input
                type="text"
                placeholder="Endereço"
                {...register("address")}
              />
              {errors.address && (
                <span className="text-red-600">{errors.address.message}</span>
              )}
            </div>
            <div>
              <Label className="text-white">Telefone</Label>
              <Input
                type="telephone"
                placeholder="(XX) XXXX-XXXX"
                {...register("phone")}
              />
              {errors.phone && (
                <span className="text-red-600">{errors.phone.message}</span>
              )}
            </div>
            <div>
              <Label className="text-white">Celular</Label>
              <Input
                type="telephone"
                placeholder="(XX) XXXXX-XXXX"
                {...register("mobile")}
              />
              {errors.mobile && (
                <span className="text-red-600">{errors.mobile.message}</span>
              )}
            </div>
            <div>
              <Label className="text-white">Data de Admissão</Label>
              <Input type="date" {...register("admissionDate")} />
              {errors.admissionDate && (
                <span className="text-red-600">
                  {errors.admissionDate.message}
                </span>
              )}
            </div>
            <div>
              <Label className="text-white">Cargo</Label>
              <Input type="text" placeholder="Cargo" {...register("role")} />
              {errors.role && (
                <span className="text-red-600">{errors.role.message}</span>
              )}
            </div>
            <div>
              <Label className="text-white">Grau de Instrução</Label>
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
                <option value="Fundamental I incompleto">
                  Ensino Fundamental I incompleto
                </option>
                <option value="Fundamental I completo">
                  Ensino Fundamental I completo
                </option>
                <option value="Fundamental II incompleto">
                  Ensino Fundamental II incompleto
                </option>
                <option value="Fundamental II completo">
                  Ensino Fundamental II completo
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
                <option value="Pós-graduação">
                  Pós-graduação
                </option>
                <option value="Mestrado">
                  Mestrado
                </option>
                <option value="Doutorado">
                  Doutorado
                </option>
                <option value="Ph.D">
                  Ph.D
                </option>
              </select>
              {errors.education && (
                <span className="text-red-600">{errors.education.message}</span>
              )}
            </div>
            <div>
              <Label className="text-white">CBO</Label>
              <Input type="text" placeholder="CBO" {...register("cboId")} />
              {errors.cboId && (
                <span className="text-red-600">{errors.cboId.message}</span>
              )}
            </div>
            <div>
              <Label className="text-white">
                Usuário com acesso na plataforma?
              </Label>
              <select
                {...register("platformAccess")}
                className="flex flex-col rounded-md border p-2"
              >
                <option value="">Selecione</option>
                <option value="Sim">Sim</option>
                <option value="Não">Não</option>
              </select>
              {errors.platformAccess && (
                <span className="text-red-600">
                  {errors.platformAccess.message}
                </span>
              )}
            </div>
            <div className="flex justify-end">
              <Button className="bg-realizaBlue" type="submit">
                Enviar
              </Button>
            </div>
          </form>
        </ScrollArea>
      </DialogContent>
    </Dialog>
  );
}
