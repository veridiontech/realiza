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
import { useClient } from "@/context/Client-Provider";
import { ip } from "@/utils/ip";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";
import { Search } from "lucide-react";
import { useState } from "react";
import { useForm } from "react-hook-form";
import { Oval } from "react-loader-spinner";
import { toast } from "sonner";
import { z } from "zod";
import bgModalRealiza from "@/assets/modalBG.jpeg";

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
export function AddNewBranch() {
  const { client } = useClient();
  const [loading, setLoading] = useState(false);
  const [cnpjValue, setCnpjValue] = useState("");
  const [razaoSocial, setRazaoSocial] = useState<string | null>(null);

  const {
    register,
    handleSubmit,
    getValues,
    setValue,
    formState: { errors },
  } = useForm<NewBranchFormSchema>({
    resolver: zodResolver(newBranchFormSchema),
  });

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

  const onSubmit = async (data: NewBranchFormSchema) => {
    const payload = {
      ...data,
      client: client?.idClient,
    };
    setLoading(true);
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
    } finally {
      setLoading(false);
    }
  };

  const formatCNPJ = (value: string) => {
    return value
      .replace(/\D/g, "")
      .replace(/(\d{2})(\d)/, "$1.$2")
      .replace(/(\d{3})(\d)/, "$1.$2")
      .replace(/(\d{3})(\d{4})$/, "$1/$2")
      .replace(/(\d{4})(\d{2})$/, "$1-$2");
  };

  return (
    <Dialog>
      <DialogTrigger asChild>
        <Button className="bg-realizaBlue hidden md:block">Adicionar Filial</Button>
      </DialogTrigger>
      <DialogTrigger asChild>
        <Button className="bg-realizaBlue md:hidden">+</Button>
      </DialogTrigger>
      <DialogContent
      style={{ backgroundImage: `url(${bgModalRealiza})` }}>
        <DialogHeader>
          <DialogTitle className="text-white">Cadastro de filial</DialogTitle>
        </DialogHeader>
        <ScrollArea className="h-[40vh] p-3">
          <form
            onSubmit={handleSubmit(onSubmit)}
            className="m-2 flex flex-col gap-5"
          >
            <div className="flex flex-col gap-2">
              <Label className="text-white">CNPJ</Label>
              <div className="flex items-center gap-2">
                <Input 
                  type="text"
                  value={cnpjValue}
                  onChange={(e) => {
                    const formattedCNPJ = formatCNPJ(e.target.value);
                    setCnpjValue(formattedCNPJ);
                    setValue("cnpj", formattedCNPJ);
                  }}
                  placeholder="00.000.000/0000-00"
                  maxLength={18}
                />
                <div
                  onClick={handleCnpj}
                  className="bg-realizaBlue cursor-pointer rounded-lg p-2 hover:bg-gray-500"
                >
                  <Search className="text-white" />
                </div>
              </div>
              {errors.cnpj && (
                <span className="text-sm text-red-600">
                  {errors.cnpj.message}
                </span>
              )}
              {errors.cnpj && (
                <span className="text-sm text-red-600">
                  {errors.cnpj.message}
                </span>
              )}
              {razaoSocial && (
                <p className="mt-1 text-sm text-gray-700">
                  Razão social: <strong>{razaoSocial}</strong>
                </p>
              )}
            </div>

            <div>
              <Label className="text-white">Email</Label>
              <Input type="email" {...register("email")} />
              {errors.email && (
                <span className="text-sm text-red-600">
                  {errors.email.message}
                </span>
              )}
            </div>

            <div>
              <Label className="text-white">Nome da filial</Label>
              <Input type="text" {...register("name")} />
              {errors.name && (
                <span className="text-sm text-red-600">
                  {errors.name.message}
                </span>
              )}
            </div>

            <div>
              <Label className="text-white">Razão social</Label>
              <Input type="text" {...register("name")} />
              {errors.name && (
                <span className="text-sm text-red-600">
                  {errors.name.message}
                </span>
              )}
            </div>

            <div>
              <Label className="text-white">CEP</Label>
              <Input type="text" {...register("cep")} />
              {errors.cep && (
                <span className="text-sm text-red-600">
                  {errors.cep.message}
                </span>
              )}
            </div>

            <div>
              <Label className="text-white">Cidade</Label>
              <Input type="text" {...register("city")} />
              {errors.city && (
                <span className="text-sm text-red-600">
                  {errors.city.message}
                </span>
              )}
            </div>

            <div>
              <Label className="text-white">Endereço</Label>
              <Input type="text" {...register("address")} />
              {errors.address && (
                <span className="text-sm text-red-600">
                  {errors.address.message}
                </span>
              )}
            </div>

            <div>
              <Label className="text-white">Número</Label>
              <Input type="text" {...register("number")} />
              {errors.number && (
                <span className="text-sm text-red-600">
                  {errors.number.message}
                </span>
              )}
            </div>

            <div>
              <Label className="text-white">País</Label>
              <Input type="text" {...register("country")} />
              {errors.country && (
                <span className="text-sm text-red-600">
                  {errors.country.message}
                </span>
              )}
            </div>

            <div>
              <Label className="text-white">Estado</Label>
              <Input type="text" {...register("state")} />
              {errors.state && (
                <span className="text-sm text-red-600">
                  {errors.state.message}
                </span>
              )}
            </div>

            <div>
              <Label className="text-white">Telefone</Label>
              <Input type="text" {...register("telephone")} />
              {errors.telephone && (
                <span className="text-sm text-red-600">
                  {errors.telephone.message}
                </span>
              )}
            </div>

            <div className="flex justify-end">
              {loading ? (
                <Button type="submit" className="bg-realizaBlue">
                  {" "}
                  <Oval
                    visible={true}
                    height="30"
                    width="30"
                    color="#34495D"
                    ariaLabel="puff-loading"
                  />
                </Button>
              ) : (
                <Button type="submit" className="bg-realizaBlue">
                  Cadastrar
                </Button>
              )}
            </div>
          </form>
        </ScrollArea>
      </DialogContent>
    </Dialog>
  );
}
