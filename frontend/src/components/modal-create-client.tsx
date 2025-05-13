import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Button } from "./ui/button";
import { Plus, Search } from "lucide-react";
import { Label } from "./ui/label";
import { Input } from "./ui/input";
import { toast } from "sonner";
import { useEffect, useState } from "react";
import axios from "axios";
import { propsCompanyData } from "@/types/interfaces";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { ip } from "@/utils/ip";
import { Oval } from "react-loader-spinner";

const createClienteFormSchema = z.object({
  tradeName: z.string(),
  corporateName: z.string(),
  email: z.string(),
  telephone: z.string(),
  cep: z.string(),
  state: z.string(),
  city: z.string(),
  address: z.string(),
  number: z.string(),
});

type CreateClientFormSchema = z.infer<typeof createClienteFormSchema>;
export function ModalCreateCliente() {
  const [cnpj, setCnpj] = useState("");
  const [showSecondModal, setShowSecondModal] = useState(false);
  const [cnpjData, setCnpjData] = useState<propsCompanyData | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const sanitizedCnpj = typeof cnpj === "string" ? cnpj.replace(/\D/g, "") : "";

  const getCnpj = async () => {
    try {
      const res = await axios.get(
        `https://open.cnpja.com/office/${sanitizedCnpj}`,
      );
      setCnpjData(res.data);
      console.log(res.data);
      setShowSecondModal(true);
    } catch (err: any) {
      console.log("Erro ao requisitar cnpj", err);
      toast.error("Erro ao buscar cnpj", err);
    }
  };
  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setCnpj(event.target.value);
  };

  const {
    register,
    handleSubmit,
    setValue,
  } = useForm<CreateClientFormSchema>({
    resolver: zodResolver(createClienteFormSchema),
  });

  useEffect(() => {
    if (cnpjData) {
      setValue("corporateName", cnpjData.company.name || "");
      setValue("tradeName", cnpjData.alias || "");
      setValue("email", cnpjData.emails?.[0]?.address || "");
      setValue("telephone", cnpjData.phones?.[0]?.number || "");
      setValue("cep", cnpjData.address.zip || "");
      setValue("state", cnpjData.address.state || "");
      setValue("city", cnpjData.address.city || "");
      setValue("address", cnpjData.address.street || "");
      setValue("number", cnpjData.address.number || "");
    }
  }, [cnpjData, setValue]);

  const createCliente = async (data: CreateClientFormSchema) => {
    const token = localStorage.getItem("tokenClient");
    const payload = {
      ...data,
      cnpj: sanitizedCnpj,
    };
    setIsLoading(true);
    try {
      await axios.post(`${ip}/client`, payload, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      toast.success("Sucesso ao criar cliente ");
    } catch (err: any) {
      if (err.response.status === 422) {
        toast.warning("CNPJ já cadastrado");
      } else {
        console.log(err);
        toast.error("Erro ao criar cliente");
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div>
      <Dialog>
        <DialogTrigger>
          <Button className="bg-realizaBlue w-[2.1vw] rounded-full">
            <Plus size={24} className="dark:text-white" />
          </Button>
        </DialogTrigger>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Cadastrar novo cliente</DialogTitle>
          </DialogHeader>
          <div>
            <form>
              <div>
                <Label>CNPJ</Label>
                <div className="flex items-center gap-1">
                  <Input
                    type="text"
                    placeholder="Insira o cnpj do cliente"
                    value={cnpj}
                    onChange={handleChange}
                  />
                  <div
                    className="bg-realizaBlue cursor-pointer rounded-md p-2 text-white hover:bg-neutral-700"
                    onClick={getCnpj}
                  >
                    <Search />
                  </div>
                </div>
              </div>
            </form>
          </div>
        </DialogContent>
      </Dialog>
      <Dialog open={showSecondModal} onOpenChange={setShowSecondModal}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>{cnpjData?.company.name}</DialogTitle>
          </DialogHeader>
          <div>
            <form
              onSubmit={handleSubmit(createCliente)}
              className="flex flex-col gap-2"
            >
              <div>
                <Label>Nome fantasia</Label>
                <Input {...register("tradeName")} />
              </div>
              <div>
                <Label>Razão social</Label>
                <Input {...register("corporateName")} />
              </div>
              <div>
                <Label>Email</Label>
                <Input {...register("email")} />
              </div>
              <div>
                <Label>Telefone</Label>
                <Input {...register("telephone")} />
              </div>
              <div>
                <Label>CEP</Label>
                <Input {...register("cep")} />
              </div>
              <div>
                <Label>Estado</Label>
                <Input {...register("state")} />
              </div>
              <div>
                <Label>Cidade</Label>
                <Input {...register("city")} />
              </div>
              <div>
                <Label>Endereço</Label>
                <Input {...register("address")} />
              </div>
              <div>
                <Label>Número</Label>
                <Input {...register("number")} />
              </div>
              {isLoading ? (
                <Button className="bg-realizaBlue w-full">
                  <Oval
                    visible={true}
                    height="80"
                    width="80"
                    color="#4fa94d"
                    ariaLabel="oval-loading"
                    wrapperStyle={{}}
                    wrapperClass=""
                  />
                </Button>
              ) : (
                <Button className="bg-realizaBlue w-full">
                  Cadastrar cliente
                </Button>
              )}
            </form>
          </div>
        </DialogContent>
      </Dialog>
    </div>
  );
}
