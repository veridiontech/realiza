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
import { Search } from "lucide-react";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { ScrollArea } from "@/components/ui/scroll-area";
import { useClient } from "@/context/Client-Provider";
import { toast } from "sonner";

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
  // const [enterpriseDatas, setEnterpriseDatas] = useState<
  //   EditModalEnterpriseSchema | undefined
  // >(undefined);
  const [cep, setCep] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const {client} = useClient()

  const {
    register,
    handleSubmit,
    setValue,
    // formState = { }
  } = useForm<EditModalEnterpriseSchema>({
    resolver: zodResolver(editModalEnterpriseSchema),
  });

  console.log("cliente selecionado:", client);
  

  const getDatasEnterprise = async () => {
    try {
      const res = await axios.get(`${ip}/client/${client?.idClient}`);
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

  const onSubmit = async () => {
    try {
      await axios.put(`${ip}/client/${client?.idClient}`)
      toast.success("Sucesso ao atualizar cliente")
    } catch (err) {
      console.log("erro ao atualizar cliente:", err);
      toast.error("Erro ao atualizar cliente, tente novamente")
    }
  };

  useEffect(() => {
    getDatasEnterprise();
  }, []);

  return (
    <Dialog>
      <DialogTrigger asChild>
        <Button className="bg-realizaBlue">Editar perfil</Button>
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Editar empresa</DialogTitle>
        </DialogHeader>
        <div className="">
          <ScrollArea className="h-[60vh] pr-5">
            <form
              action=""
              onSubmit={handleSubmit(onSubmit)}
              className="flex flex-col gap-2"
            >
              <div>
                <Label>CNPJ</Label>
                <Input
                  placeholder="CNPJ: __.___.___/____-__"
                  {...register("cnpj")}
                />
              </div>
              <div className="flex items-center gap-1 ">
                <div className="w-auto">
                  <Label>Nome da empresa</Label>
                  <Input className="w-full" {...register("corporateName")} />
                </div>
                <div className="w-auto">
                  <Label>Nome fantasia</Label>
                  <Input className="w-auto" {...register("tradeName")} />
                </div>
              </div>
              <div>
                <Label>Email corporativo</Label>
                <Input {...register("email")} />
              </div>
              <div>
                <Label>Telefone</Label>
                <Input {...register("phone")} />
              </div>
              <div className="flex items-end gap-3">
                <div>
                  <Label>CEP</Label>
                  <Input
                    className="w-[21vw]"
                    onChange={(e) => setCep(e.target.value)}
                  />
                </div>
                <Button
                  className="bg-realizaBlue"
                  onClick={(e) => {
                    e.preventDefault();
                    findCep();
                  }}
                  disabled={isLoading}
                >
                  {isLoading ? (
                    <Puff
                      visible={true}
                      height="80"
                      width="80"
                      color="white"
                      ariaLabel="puff-loading"
                    />
                  ) : (
                    <Search />
                  )}
                </Button>
              </div>
              <div>
                <Label>Estado</Label>
                <Input
                  {...register("state")}
                  readOnly
                  placeholder="Preencha o CEP"
                />
              </div>
              <div>
                <Label>Cidade</Label>
                <Input
                  {...register("city")}
                  readOnly
                  placeholder="Preencha o CEP"
                />
              </div>
              <div className="flex items-center gap-3">
                <div>
                  <Label>Endereço</Label>
                  <Input
                    {...register("adress")}
                    readOnly
                    placeholder="Preencha o CEP"
                    className="w-[20.6vw]"
                  />
                </div>
                <div>
                  <Label>Número</Label>
                  <Input className="w-[3vw]" {...register("number")} />
                </div>
              </div>
              <div>
                <Label>Responsável pela unidade</Label>
                <Input />
              </div>
              <Button className="bg-realizaBlue">Confirmar edição</Button>
            </form>
          </ScrollArea>
        </div>
      </DialogContent>
    </Dialog>
  );
}
