import { Button } from "@/components/ui/button";
import { Puff } from "react-loader-spinner";
import {
    Dialog,
    DialogContent, DialogHeader,
    DialogTitle,
    DialogTrigger
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

interface adressProps {
  city: string;
  state: string;
  adress: string;
}

const editModalEnterpriseSchema = z.object({
  cnpj: z.string(),
  nameEnterprise: z.string(),
  fantasyName: z.string(),
  socialReason: z.string(),
  email: z.string(),
  phone: z.string(),
  cep: z.string(),
  state: z.string(),
  city: z.string(),
  adress: z.string(),
  number: z.string(),
});

type EditModalEnterpriseSchema = z.infer<typeof editModalEnterpriseSchema>;
export function EditModalEnterprise() {
  const [enterpriseDatas, setEnterpriseDatas] = useState<
    EditModalEnterpriseSchema | undefined
  >(undefined);
  const [cep, setCep] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  const {
    register,
    handleSubmit,
    setValue,
    // formState = { }
  } = useForm<EditModalEnterpriseSchema>({
    resolver: zodResolver(editModalEnterpriseSchema),
  });

  const getDatasEnterprise = async (id: string) => {
    try {
      const res = await axios.get(`${ip}/client/${id}`);
      console.log(res.data);
      setEnterpriseDatas(res.data);
    } catch (err) {
      console.log("nao foi possivel recuperar os dados da empresa", err);
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
    } catch (err) {}
  };

  useEffect(() => {
    getDatasEnterprise();
  }, []);

  return (
    <Dialog>
      <DialogTrigger asChild>
        <Button className="bg-blue-700">Editar perfil</Button>
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Editar empresa</DialogTitle>
        </DialogHeader>
        <div>
          <form
            action=""
            onSubmit={handleSubmit(onSubmit)}
            className="flex flex-col gap-2"
          >
            <div>
              <Label>Cnpj</Label>
              <Input placeholder="CNPJ: __.___.___/____-__"/>
            </div>
            <div className="flex items-center gap-1">
              <div>
                <Label>Nome da empresa</Label>
                <Input className="w-[12vw]" />
              </div>
              <div>
                <Label>Nome fanstasia</Label>
                <Input className="w-[12vw]" />
              </div>
            </div>
            <div>
              <Label>Razão social</Label>
              <Input />
            </div>
            <div>
              <Label>email corporativo</Label>
              <Input />
            </div>
            <div>
              <Label>Telefone</Label>
              <Input />
            </div>
            <div className="flex items-end gap-3">
              <div>
                <Label>Cep</Label>
                <Input
                  className="w-[21vw]"
                  {...register("cep")}
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
                  wrapperStyle={{}}
                  wrapperClass=""
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
                placeholder="Preencha o cep"
              />
            </div>
            <div>
              <Label>Cidade</Label>
              <Input
                {...register("city")}
                readOnly
                placeholder="Preencha o cep"
              />
            </div>
            <div className="flex items-center gap-3">
              <div>
                <Label>Endereço</Label>
                <Input
                  {...register("adress")}
                  readOnly
                  placeholder="Preencha o cep"
                  className="w-[20.6vw]"
                />
              </div>
              <div>
                <Label>Numero</Label>
                <Input
                  className="w-[3vw]"
                  placeholder=""  
                />
              </div>
            </div>
            <div>
              <Label>Responsável pela unidadee</Label>
              <Input />
            </div>
            <Button className="bg-realizaBlue">Confirmar edição</Button>
          </form>
        </div>
      </DialogContent>
    </Dialog>
  );
}
