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
import { set, useForm } from "react-hook-form";
import axios from "axios";
import { ip } from "@/utils/ip";
import { useEffect, useState } from "react";

interface Client {
  id: string;
  name: string;
}

const modalSendEmailSupplierFormSchema = z.object({
  email: z.string(),
  id_client: z.string(),
  company: z.string().default("SUPPLIER"),
});

type ModalSendEmailFormSchema = z.infer<
  typeof modalSendEmailSupplierFormSchema
>;
export function ModalSendEmailSupplier() {
  const [idClient, setIdClient] = useState<Client | undefined>(undefined);

  const { register, handleSubmit } = useForm<ModalSendEmailFormSchema>({
    resolver: zodResolver(modalSendEmailSupplierFormSchema),
  });

  const getClient = async () => {
    try {
      const getIdClient = await axios.get(`${ip}/client`);
      setIdClient(getIdClient.data.id);
      console.log(getIdClient.data.id);
    } catch (err) {
      console.log("erro ao buscar id do cliente", err);
    } finally {
    }
  };

  const createClient = async (data: ModalSendEmailFormSchema) => {
    console.log("teste");
    try {
      await axios.post(`${ip}/invite`, {
        email: data.email,
        id_client: idClient,
        company: data.company,
      });
      console.log("sucesso");
    } catch (err) {
      console.log("erro ao enviar email para usuario", err);
    }
  };

  useEffect(() => {
    getClient();
  }, []);

  return (
    <Dialog>
      <DialogTrigger asChild>
        <Button className="bg-sky-700">Cadastrar cliente</Button>
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Cadastrar novo cliente</DialogTitle>
        </DialogHeader>
        <div>
          <form
            action=""
            onSubmit={handleSubmit(createClient)}
            className="flex flex-col gap-2"
          >
            <div>
              <Label>Email</Label>
              <Input
                type="email"
                placeholder="Digite o email do novo cliente"
                {...register("email")}
                className="w-full"
              />
            </div>
            <div className="flex justify-end">
              <Button className="bg-realizaBlue">Enviar</Button>
            </div>
          </form>
        </div>
      </DialogContent>
    </Dialog>
  );
}
