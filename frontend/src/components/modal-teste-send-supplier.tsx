import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Button } from "./ui/button";
import { Label } from "./ui/label";
import { Input } from "./ui/input";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import axios from "axios";
import { ip } from "@/utils/ip";
import { useEffect, useState } from "react";
import { Radio } from "react-loader-spinner";
import { propsClient } from "@/types/interfaces";
import { toast } from "sonner";

const modalSendEmailFormSchema = z.object({
  email: z.string().email("Insira um email valido"),
  company: z.string().default("SUPPLIER"),
  idCompany: z.string().nonempty("Selecione um cliente"),
});

type ModalSendEmailFormSchema = z.infer<typeof modalSendEmailFormSchema>;
export function ModalTesteSendSupplier() {
  const [clients, setClients] = useState<propsClient[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const { register, handleSubmit, formState : { errors } } = useForm<ModalSendEmailFormSchema>({
    resolver: zodResolver(modalSendEmailFormSchema),
  });

  const getClients = async () => {
    try {
      const res = await axios.get(`${ip}/client`);
      setClients(res.data.content);
    } catch (err) {
      console.log("erro ao puxar clientes", err);
    }
  };

  const createClient = async (data: ModalSendEmailFormSchema) => {
    console.log('enviando dados:',data);
    setIsLoading(true)
    try {
      await axios.post(`${ip}/invite`, {
        email: data.email,
        idCompany: data.idCompany,
        company: data.company
      });
      toast.success("Email de cadastro enviado para novo prestador")
    } catch (err) {
      console.log("erro ao enviar email para usuario", err);
      toast.error('Erro ao enviar email. Tente novamente')
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    getClients();
  }, []);

  return (
    <Dialog>
      <DialogTrigger asChild>
        <Button className="bg-sky-700">Cadastrar novo prestador</Button>
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Cadastrar novo prestador</DialogTitle>
        </DialogHeader>
        <div>
          <form
            action=""
            onSubmit={handleSubmit(createClient)}
            className="flex flex-col gap-4"
          >
            <div>
              <Label>Email</Label>
              <Input
                type="email"
                placeholder="Digite o email do novo cliente"
                {...register("email")}
                className="w-full"
              />
              {errors.email && <span className="text-red-600">{errors.email.message}</span>}
            </div>
            <div className="flex flex-col gap-1">
              <Label>Selecione um cliente</Label>
              <select
                className="rounded-md border p-2"
                defaultValue=""
                {...register("idCompany")}
              >
                <option value="" disabled>
                  Selecione um cliente
                </option>
                {clients.map((client) => (
                  <option key={client.idClient} value={client.idClient}>
                    {client.companyName}
                  </option>
                ))}
              </select>
              {errors.idCompany && <span className="text-red-600">{errors.idCompany.message}</span>}
            </div>
            <div className="flex justify-end">
              {isLoading ? (
                <Button>
                  <Radio
                    visible={true}
                    height="80"
                    width="80"
                    ariaLabel="radio-loading"
                    wrapperStyle={{}}
                    wrapperClass=""
                  />
                </Button>
              ) : (
                <Button className="bg-realizaBlue">Enviar</Button>
              )}
            </div>
          </form>
        </div>
      </DialogContent>
    </Dialog>
  );
}
