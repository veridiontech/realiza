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

const modalSendEmailFormSchema = z.object({
  email: z.string(),
  company: z.string().default('CLIENT')
})

type ModalSendEmailFormSchema = z.infer<typeof modalSendEmailFormSchema>
export function ModalSendEmail() {

  const {
    register,
    handleSubmit
  } = useForm<ModalSendEmailFormSchema>({
    resolver: zodResolver(modalSendEmailFormSchema)
  })

  const createClient = async(data: ModalSendEmailFormSchema) => {
    console.log('teste');
    try{
      await axios.post(`${ip}/invite`, {
        email: data.email,
        company: data.company
      })
      console.log('sucesso');
      
    }catch(err) {
      console.log('erro ao enviar email para usuario', err);
      
    }
  }

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
          <form action="" onSubmit={handleSubmit(createClient)} className="flex flex-col gap-2">
            <div>
              <Label>Email</Label>
              <Input type="email" placeholder="Digite o email do novo cliente" {...register("email")} className="w-full"/>
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
