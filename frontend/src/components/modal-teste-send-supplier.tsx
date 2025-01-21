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
import { useClient } from "@/context/Client-Provider";
  
  const modalSendEmailFormSchema = z.object({
    email: z.string(),
    company: z.string().default('SUPPLIER')
  })
  
  type ModalSendEmailFormSchema = z.infer<typeof modalSendEmailFormSchema>
  export function ModalTesteSendSupplier() {
    const { client } = useClient()
  
    const {
      register,
      handleSubmit
    } = useForm<ModalSendEmailFormSchema>({
      resolver: zodResolver(modalSendEmailFormSchema)
    })
  
    const getIdUniqueClient = client?.idClient
    

    const createClient = async(data: ModalSendEmailFormSchema) => {
      const payload = {
        ...data,
        idCompany: getIdUniqueClient
      }
      try{
        await axios.post(`${ip}/invite`, payload)
        console.log(payload);
        
        console.log('sucesso');
        
      }catch(err) {
        console.log('erro ao enviar email para usuario', err);
        
      }
    }
  
    return (
      <Dialog>
        <DialogTrigger asChild> 
          <Button className="bg-sky-700">Cadastrar novo prestador</Button>
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
  