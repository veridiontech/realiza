import { Button } from "@/components/ui/button";
import { Puff } from "react-loader-spinner";
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

// 🛠 Regex mais seguro (exige hífen)
const cepRegex = /^\d{5}-\d{3}$/;

interface adressProps {
  city: string;
  state: string;
  adress: string;
}

async function validarCEPExiste(cep: string): Promise<boolean> {
  try {
    const cepLimpo = cep.replace(/\D/g, "");
    const res = await fetch(`https://viacep.com.br/ws/${cepLimpo}/json/`);
    if (!res.ok) return false;
    const data = await res.json();
    return !data.erro;
  } catch {
    return false;
  }
}

const editModalEnterpriseSchema = z.object({
  cnpj: z.string(),
  cep: z
    .string()
    .nonempty("CEP é obrigatório")
    .regex(cepRegex, "CEP inválido, use o formato 12345-678")
    .refine(validarCEPExiste, {
      message: "CEP não encontrado",
    }),
  corporateName: z.string(),
  tradeName: z.string(),
  email: z.string(),
  phone: z.string(),
  state: z.string(),
  city: z.string(),
  adress: z.string(),
  number: z.string(),
});

type EditModalEnterpriseSchema = z.infer<typeof editModalEnterpriseSchema>;

export function EditModalEnterprise() {
  const [cepValue, setCepValue] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const { client } = useClient();
  const [isOpen, setIsOpen] = useState(false);

  const {
    register,
    handleSubmit,
    setValue,
    getValues,
    formState: { errors },
  } = useForm<EditModalEnterpriseSchema>({
    resolver: zodResolver(editModalEnterpriseSchema),
  });

  useEffect(() => {
    const rawCEP = getValues("cep") || "";
    setCepValue(formatCEP(rawCEP));
  }, [getValues]);

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
      setValue("email", data.email || "");
      setValue("phone", data.phone || "");
      setValue("state", data.state || "");
      setValue("city", data.city || "");
      setValue("adress", data.adress || "");
      setValue("number", data.number || "");
    } catch (err) {
      console.error("Não foi possível recuperar os dados da empresa", err);
    }
  };

  const findCep = async () => {
    try {
      setIsLoading(true);
      const cepLimpo = cepValue.replace(/\D/g, "");
      const res = await axios.get(`https://viacep.com.br/ws/${cepLimpo}/json/`);
      if (res.data) {
        setValuesAdress({
          city: res.data.localidade,
          state: res.data.uf,
          adress: res.data.logradouro,
        });
      }
    } catch (err) {
      console.log("Não foi possível buscar o CEP", err);
    } finally {
      setIsLoading(false);
    }
  };

  const setValuesAdress = (data: adressProps) => {
    setValue("city", data.city);
    setValue("state", data.state);
    setValue("adress", data.adress);
  };

  const onSubmit = async (data: EditModalEnterpriseSchema) => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      await axios.put(`${ip}/client/${client?.idClient}`, data, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });
      toast.success("Sucesso ao atualizar cliente");
      setIsOpen(false);
      window.location.reload();
    } catch (err) {
      console.error("Erro ao atualizar cliente:", err);
      toast.error("Erro ao atualizar cliente, tente novamente");
    }
  };

  const formatCEP = (value: string) => {
    return value.replace(/\D/g, "").replace(/(\d{5})(\d)/, "$1-$2").slice(0, 9);
  };

  useEffect(() => {
    if (client) {
      getDatasEnterprise();
    }
  }, [client]);

  return (
    <Dialog open={isOpen} onOpenChange={setIsOpen}>
      <DialogTrigger asChild>
        <Button className="hidden md:block bg-realizaBlue">
          <Pencil />
        </Button>
      </DialogTrigger>
      <DialogTrigger asChild>
        <Button className="md:hidden bg-realizaBlue">Editar perfil empresarial</Button>
      </DialogTrigger>

      <DialogContent style={{ backgroundImage: `url(${bgModalRealiza})` }}>
        <DialogHeader>
          <DialogTitle className="text-white">Editar empresa</DialogTitle>
        </DialogHeader>

        <ScrollArea className="h-[60vh] pr-5">
          <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-4">
            <div className="text-white">
              <Label>CNPJ</Label>
              <div>{client?.cnpj}</div>
            </div>

            <div>
              <Label className="text-white">Nome da empresa</Label>
              <Input {...register("corporateName")} />
            </div>

            <div>
              <Label className="text-white">Nome fantasia</Label>
              <Input {...register("tradeName")} />
            </div>

            <div>
              <Label className="text-white">Email corporativo</Label>
              <Input {...register("email")} />
            </div>

            <div>
              <Label className="text-white">Telefone</Label>
              <Input {...register("phone")} />
            </div>

            <div>
              <Label className="text-white">CEP</Label>
              <Input
                value={cepValue}
                onChange={(e) => {
                  const formatted = formatCEP(e.target.value);
                  setCepValue(formatted);
                  setValue("cep", formatted, { shouldValidate: true });
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
              <Input {...register("state")} readOnly placeholder="Preencha o CEP" />
            </div>

            <div>
              <Label className="text-white">Cidade</Label>
              <Input {...register("city")} readOnly placeholder="Preencha o CEP" />
            </div>

            <div className="flex flex-col md:flex-row gap-3">
              <div className="w-full md:w-[80%]">
                <Label className="text-white">Endereço</Label>
                <Input {...register("adress")} readOnly placeholder="Preencha o CEP" />
              </div>
              <div className="w-full md:w-[20%]">
                <Label className="text-white">Número</Label>
                <Input {...register("number")} />
              </div>
            </div>

            <div>
              <Label className="text-white">Responsável pela unidade</Label>
              <Input />
            </div>

            <Button className="bg-realizaBlue w-full md:w-auto">
              Confirmar edição
            </Button>
          </form>
        </ScrollArea>
      </DialogContent>
    </Dialog>
  );
}
