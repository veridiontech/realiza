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
import { Pencil, Search } from "lucide-react";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { ScrollArea } from "@/components/ui/scroll-area";
import { useClient } from "@/context/Client-Provider";
import { toast } from "sonner";
import bgModalRealiza from "@/assets/modalBG.jpeg";

interface adressProps {
  city: string;
  state: string;
  adress: string;
}

const editModalEnterpriseSchema = z.object({
  cnpj: z.string(),
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
  const [cep, setCep] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const { client } = useClient();
  const [isOpen, setIsOpen] = useState(false);

  const {
    register,
    handleSubmit,
    setValue,
  } = useForm<EditModalEnterpriseSchema>({
    resolver: zodResolver(editModalEnterpriseSchema),
  });

  const getDatasEnterprise = async () => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res = await axios.get(`${ip}/client/${client?.idClient}`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` }
        }
      );
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
      const res = await axios.get(`https://viacep.com.br/ws/${cep}/json/`);
      if (res.data) {
        setValuesAdress({
          city: res.data.localidade,
          state: res.data.uf,
          adress: res.data.logradouro,
        });
      }
    } catch (err) {
      console.log("nao foi possivel buscar o cep", err);
    } finally {
      setIsLoading(false);
    }
  };

  const setValuesAdress = (data: adressProps) => {
    setValue("city", data.city),
      setValue("state", data.state),
      setValue("adress", data.adress);
  };

  const onSubmit = async (data: EditModalEnterpriseSchema) => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      await axios.put(`${ip}/client/${client?.idClient}`, data,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` }
        }
      );
      toast.success("Sucesso ao atualizar cliente");
      setIsOpen(false);
      window.location.reload();
    } catch (err) {
      console.log("erro ao atualizar cliente:", err);
      toast.error("Erro ao atualizar cliente, tente novamente");
    }
  };

  useEffect(() => {
    if (client) {
      getDatasEnterprise();
    }
  }, [client]);

  return (
    <Dialog open={isOpen} onOpenChange={setIsOpen}>
      <DialogTrigger asChild>
        <Button className="hidden md:block bg-realizaBlue"><Pencil /></Button>
      </DialogTrigger>
      <DialogTrigger asChild>
        <Button className="md:hidden bg-realizaBlue">Editar perfil empresarial</Button>
      </DialogTrigger>
      <DialogContent
        style={{ backgroundImage: `url(${bgModalRealiza})` }}>
        <DialogHeader>
          <DialogTitle className="text-white">Editar empresa</DialogTitle>
        </DialogHeader>
        <div>
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
                <Input className="w-full" {...register("corporateName")} />
              </div>
              <div>
                <Label className="text-white">Nome fantasia</Label>
                <Input className="w-full" {...register("tradeName")} />
              </div>
              <div>
                <Label className="text-white">Email corporativo</Label>
                <Input className="w-full" {...register("email")} />
              </div>
              <div>
                <Label className="text-white">Telefone</Label>
                <Input className="w-full" {...register("phone")} />
              </div>
              <div>
                <Label className="text-white">CEP</Label>
                <div className="flex gap-2 items-end">
                  <Input
                    className="w-2/3 sm:w-[200px]"
                    onChange={(e) => setCep(e.target.value)}
                  />
                  <Button
                    className="bg-realizaBlue px-4"
                    onClick={(e) => {
                      e.preventDefault();
                      findCep();
                    }}
                    disabled={isLoading}
                  >
                    {isLoading ? (
                      <Puff
                        visible={true}
                        height="20"
                        width="20"
                        color="white"
                        ariaLabel="puff-loading"
                      />
                    ) : (
                      <Search size={16} />
                    )}
                  </Button>
                </div>
              </div>

              <div>
                <Label className="text-white">Estado</Label>
                <Input
                  className="w-full"
                  {...register("state")}
                  readOnly
                  placeholder="Preencha o CEP"
                />
              </div>
              <div>
                <Label className="text-white">Cidade</Label>
                <Input
                  className="w-full"
                  {...register("city")}
                  readOnly
                  placeholder="Preencha o CEP"
                />
              </div>
              <div className="flex flex-col md:flex-row gap-3">
                <div className="w-full md:w-[80%]">
                  <Label className="text-white">Endereço</Label>
                  <Input
                    className="w-full"
                    {...register("adress")}
                    readOnly
                    placeholder="Preencha o CEP"
                  />
                </div>
                <div className="w-full md:w-[20%]">
                  <Label className="text-white">Número</Label>
                  <Input className="w-full" {...register("number")} />
                </div>
              </div>
              <div>
                <Label className="text-white">Responsável pela unidade</Label>
                <Input className="w-full" />
              </div>
              <Button className="bg-realizaBlue w-full md:w-auto">Confirmar edição</Button>
            </form>
          </ScrollArea>
        </div>
      </DialogContent>
    </Dialog>
  );
}  
