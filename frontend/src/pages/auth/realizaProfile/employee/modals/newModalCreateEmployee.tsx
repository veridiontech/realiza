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
import { propsClient } from "@/types/interfaces";
import { ip } from "@/utils/ip";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";
import { Search } from "lucide-react";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { toast } from "sonner";
import { z } from "zod";

const createNewEmployeeFormSchema = z.object({
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
  cbo: z.string().optional(),
  platformAccess: z.string().nonempty("Acesso à plataforma é obrigatório"),
});

type CreateNewEmpoloyeeFormSchema = z.infer<typeof createNewEmployeeFormSchema>;
export function NewModalCreateEmployee() {
  const [clients, setClients] = useState<propsClient[]>([]);
  const [selectRole, setSelectRole] = useState("");

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<CreateNewEmpoloyeeFormSchema>({
    resolver: zodResolver(createNewEmployeeFormSchema),
  });

  const onSubmit = async (data: CreateNewEmpoloyeeFormSchema) => {
    const payload = {
      ...data,
    };
    try {
      await axios.post(`${ip}/employee/brazilian`, payload);
      toast.success("Sucesso ao cadastrar novo usuário");
    } catch (err) {
      console.log("erro ao enviar dados do novo colaborador:", err);
      toast.error("Erro ao cadastrar novo usuário, tente novamente");
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
                onSubmit={handleSubmit(onSubmit)}
              >
                <div>
                  <Label className="text-white">Tipo de contrato</Label>
                  <select
                    {...register("contractType")}
                    className="flex flex-col rounded-md border p-2"
                    {...register("contractType")}
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
                  <Label className="text-white">Cpf</Label>
                  <Input type="text" {...register("cpf")} />
                </div>
                <div>
                  <Label className="text-white">salário</Label>
                  <Input type="text" {...register("salary")} />
                </div>
                <div className="flex flex-col items-start gap-3">
                  <div className="flex items-start gap-2">
                    <Label className="text-white">
                      {" "}
                      Usuário para filial do cliente
                    </Label>
                    <input
                      type="radio"
                      checked={selectRole === "branch"}
                      onChange={() => setSelectRole("branch")}
                    />
                  </div>
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
                      <select defaultValue="" className="rounded-md p-1">
                        <option value="" disabled>
                          Selecione um cliente
                        </option>
                        {clients.map((client: propsClient) => (
                          <option value="" key={client.idClient}>
                            {client.tradeName}
                          </option>
                        ))}
                      </select>
                      <select className="rounded-md p-1">
                        <option value="">Selecione uma filial</option>
                        {}
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
                    <option value="Masculino">Masculino</option>
                    <option value="Feminino">Feminino</option>
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
                  <Label className="text-white" {...register("state")}>
                    Estado
                  </Label>
                  <Input />
                </div>
                <div>
                  <Label className="text-white" {...register("city")}>
                    Cidade
                  </Label>
                  <Input />
                </div>
                <div>
                  <Label className="text-white" {...register("address")}>
                    Adress
                  </Label>
                  <Input />
                </div>
                <div>
                  <Label className="text-white" {...register("phone")}>
                    Telefone
                  </Label>
                  <Input />
                </div>
                <div>
                  <Label className="text-white" {...register("mobile")}>
                    Celular
                  </Label>
                  <Input />
                </div>
                <div>
                  <Label className="text-white" {...register("role")}>
                    Cargo
                  </Label>
                  <Input />
                </div>
                <div>
                  <Label className="text-white" {...register("cbo")}>
                    cbo
                  </Label>
                  <Input />
                </div>
                <div>
                  <Label className="text-white" {...register("platformAccess")}>
                    Acesso a plataforma
                  </Label>
                  <select
                    {...register("platformAccess")}
                    className="flex flex-col rounded-md border p-2"
                  >
                    <option value="">Selecione</option>
                    <option value="Sim">Sim</option>
                    <option value="Não">Não</option>
                  </select>
                </div>
                <Button type="submit" className="bg-realizaBlue">
                  Cadastrar
                </Button>
              </form>
            </div>
          </ScrollArea>
        </DialogHeader>
      </DialogContent>
    </Dialog>
  );
}
