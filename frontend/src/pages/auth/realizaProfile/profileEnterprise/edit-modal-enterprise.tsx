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
import { ip } from "@/utils/ip";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";
import { Pencil } from "lucide-react";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { ScrollArea } from "@/components/ui/scroll-area";
import { useClient } from "@/context/Client-Provider";
import { toast } from "sonner";
import bgModalRealiza from "@/assets/modalBG.jpeg";

const editModalEnterpriseSchema = z.object({
  cnpj: z.string(),
  cep: z.string().nonempty("CEP é obrigatório"),
  corporateName: z.string(),
  tradeName: z.string(),
  email: z.string(),
  telephone: z.string().nonempty("Celular é obrigatório"),
  state: z.string(),
  city: z.string(),
  address: z.string(),
  number: z.string(),
});

type EditModalEnterpriseSchema = z.infer<typeof editModalEnterpriseSchema>;

export function EditModalEnterprise() {
  const [cepValue, setCepValue] = useState("");
  const { client, setClient, refreshClient, refreshClients } = useClient();
  const [isOpen, setIsOpen] = useState(false);
  const [phoneValue, setPhoneValue] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [editHistory, setEditHistory] = useState<any[]>([]);

  const {
    register,
    handleSubmit,
    setValue,
    getValues,
    formState: { errors },
  } = useForm<EditModalEnterpriseSchema>({
    resolver: zodResolver(editModalEnterpriseSchema),
  });

  // Função para formatar o CEP
  const formatCEP = (value: string) => {
    return value
      .replace(/\D/g, "") // Remove tudo que não for número
      .replace(/(\d{5})(\d)/, "$1-$2") // Formata para o padrão "XXXXX-XXX"
      .slice(0, 9); // Limita o comprimento a 9 caracteres (CEP com o hífen)
  };

  const getDatasEnterprise = async () => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res = await axios.get(`${ip}/client/${client?.idClient}`, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });

      const data = res.data;
      setValue("cnpj", data.cnpj || "");
      setValue("corporateName", data.corporateName || "");
      setValue("tradeName", data.tradeName || "");
      setValue("cep", data.cep || "");
      setValue("email", data.email || "");
      setValue("telephone", data.telephone || "");
      setValue("state", data.state || "");
      setValue("city", data.city || "");
      setValue("address", data.adress || "");
      setValue("number", data.number || "");
      setValue("address", data.address || "");
    } catch (err) {
      console.error("Não foi possível recuperar os dados da empresa", err);
    }
  };

  const getEditHistory = async () => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res = await axios.get(`${ip}/client/${client?.idClient}/history`, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });
      setEditHistory(res.data);
    } catch (err) {
      console.error("Erro ao buscar histórico de edições", err);
    }
  };

  const onSubmit = async (data: EditModalEnterpriseSchema) => {
    setIsLoading(true);
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      await axios.put(`${ip}/client/${client?.idClient}`, data, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });
      const res = await axios.get(`${ip}/client/${client?.idClient}`, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });

      if (client) {
        await refreshClient(client.idClient);
        await refreshClients();
      }
      setClient(res.data);
      toast.success("Sucesso ao atualizar cliente");
      setIsOpen(false);
    } catch (err) {
      console.error("Erro ao atualizar cliente:", err);
      toast.error("Erro ao atualizar cliente, tente novamente");
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (client) {
      getDatasEnterprise();
      getEditHistory();
    }
  }, [isOpen, client]);

  useEffect(() => {
    // Usando o getValues para pegar o valor do campo 'cep' e formatá-lo
    const rawCEP = getValues("cep") || ""; // Acessando o valor do campo "cep"
    setCepValue(formatCEP(rawCEP)); // Usando o valor do campo para atualizar o estado
  }, [getValues]);

  return (
    <Dialog open={isOpen} onOpenChange={setIsOpen}>
      <DialogTrigger asChild>
        <Button className="hidden md:block bg-realizaBlue">
          <Pencil />
        </Button>
      </DialogTrigger>
      <DialogTrigger asChild>
        <Button className="md:hidden bg-realizaBlue">
          Editar perfil empresarial
        </Button>
      </DialogTrigger>

      <DialogContent style={{ backgroundImage: `url(${bgModalRealiza})` }}>
        <DialogHeader>
          <DialogTitle className="text-white">Editar empresa</DialogTitle>
        </DialogHeader>

        <ScrollArea className="h-[60vh] pr-5">
          <form
            onSubmit={handleSubmit(onSubmit)}
            className="flex flex-col gap-4"
          >
            <div className="text-white">
              <Label>CNPJ</Label>
              <div>{client?.cnpj}</div>
            </div>

            <div>
              <Label className="text-white">Nome da empresa</Label>
              <Input
                placeholder="Digite o nome da empresa"
                {...register("corporateName")}
              />
            </div>

            <div>
              <Label className="text-white">Nome fantasia</Label>
              <Input
                placeholder="Digite o nome fantasia"
                {...register("tradeName")}
              />
            </div>

            <div>
              <Label className="text-white">Email corporativo</Label>
              <Input
                placeholder="Digite o email corporativo"
                {...register("email")}
              />
            </div>

            <div className="flex flex-col gap-2">
              <Label className="text-white">Telefone</Label>
              <Input
                type="text"
                value={phoneValue}
                {...register("telephone")}
                onChange={(e) => {
                  setPhoneValue(e.target.value);
                }}
                placeholder="(00) 00000-0000"
                maxLength={15}
              />
              {errors.telephone && (
                <span className="text-sm text-red-600">
                  {errors.telephone.message}
                </span>
              )}
            </div>

            <div>
              <Label className="text-white">CEP</Label>
              <Input
                value={cepValue}
                {...register("cep")}
                onChange={(e) => {
                  setCepValue(e.target.value);
                }}
                placeholder="00000-000"
                maxLength={9}
              />
              {errors.cep && (
                <span className="text-sm text-red-600">
                  {errors.cep.message}
                </span>
              )}
            </div>

            <div>
              <Label className="text-white">Estado</Label>
              <Input
                {...register("state")}
                readOnly
                placeholder="Digite o estado"
              />
            </div>

            <div>
              <Label className="text-white">Cidade</Label>
              <Input
                {...register("city")}
                readOnly
                placeholder="Digite a cidade"
              />
            </div>

            <div className="flex flex-col md:flex-row gap-3">
              <div className="w-full md:w-[80%]">
                <Label className="text-white">Endereço</Label>
                <Input
                  {...register("address")}
                  readOnly
                  placeholder="Digite o endereço"
                />
              </div>
              <div className="w-full md:w-[20%]">
                <Label className="text-white">Número</Label>
                <Input {...register("number")} />
              </div>
            </div>
            <Button className="bg-realizaBlue w-full md:w-auto" type="submit">
              {isLoading ? "Carregando..." : "Confirmar edição"}
            </Button>
          </form>

          {/* Seção de Histórico de Edições */}
          <div className="mt-6 text-white">
            <h4 className="text-lg font-semibold">Histórico de Edições</h4>
            <div className="overflow-y-auto max-h-[300px]">
              {editHistory.length > 0 ? (
                <ul>
                  {editHistory.map((entry, index) => (
                    <li key={index} className="border-b py-2">
                      <strong>{entry.date}</strong>
                      <p>
                        {entry.field}: {entry.oldValue} → {entry.newValue}
                      </p>
                    </li>
                  ))}
                </ul>
              ) : (
                <p>Nenhuma edição registrada.</p>
              )}
            </div>
          </div>
        </ScrollArea>
      </DialogContent>
    </Dialog>
  );
}
