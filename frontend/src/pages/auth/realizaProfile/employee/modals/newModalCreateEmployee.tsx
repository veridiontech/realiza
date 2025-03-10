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
import { zodResolver } from "@hookform/resolvers/zod";
import { Search } from "lucide-react";
import { useForm } from "react-hook-form";
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
    } catch (err) {
      console.log("erro ao enviar dados do novo colaborador:", err);
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
          <DialogTitle>Cadastrar colaborador</DialogTitle>
          <ScrollArea className="h-[75vh]">
            <div>
              <form action="" className="flex flex-col gap-5">
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
                  <Input type="text" />
                </div>
                <div>
                  <Label className="text-white">Cpf</Label>
                  <Input type="text" />
                </div>
                <div>
                  <Label className="text-white">salary</Label>
                  <Input type="text" />
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
                    {...register("gender")}
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
                      <Input />
                      <Button className="bg-realizaBlue">
                        <Search />
                      </Button>
                    </div>
                  </div>
                </div>
                <div>
                  <Label className="text-white">Estado</Label>
                  <Input />
                </div>
                <div>
                  <Label className="text-white">Cidade</Label>
                  <Input />
                </div>
                <div>
                  <Label className="text-white">Adress</Label>
                  <Input />
                </div>
                <div>
                  <Label className="text-white">Telefone</Label>
                  <Input />
                </div>
                <div>
                  <Label className="text-white">Celular</Label>
                  <Input />
                </div>
                <div>
                  <Label className="text-white">Cargo</Label>
                  <Input />
                </div>
                <div>
                  <Label className="text-white">cbo</Label>
                  <Input />
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
              </form>
            </div>
          </ScrollArea>
        </DialogHeader>
      </DialogContent>
    </Dialog>
  );
}
