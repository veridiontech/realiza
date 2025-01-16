import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";

const editModalEnterpriseSchema = z.object({
  cnpj: z.string(),
  nameEnterprise: z.string(),
  fantasyName: z.string(),
  socialReason: z.string(),
  email: z.string(),
  phone: z.string(),
});

type EditModalEnterpriseSchema = z.infer<typeof editModalEnterpriseSchema>
export function EditModalEnterprise() {

    const {
        register, 
        handleSubmit, 
        formState = { errors }
    } = useForm<EditModalEnterpriseSchema>({
        resolver: zodResolver(editModalEnterpriseSchema)
    })

    const onSubmit = async() => {
        try{

        }catch(err) {
            
        }
    }

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
            <form action="" onSubmit={handleSubmit()}>
                <div>
                    <Label>Cnpj</Label>
                    <Input />
                </div>
                <div>
                    <Label></Label>
                    <Input />
                </div>
                <div>
                    <Label></Label>
                    <Input />
                </div>
                <div>
                    <Label></Label>
                    <Input />
                </div>
                <div>
                    <Label></Label>
                    <Input />
                </div>
                <div>
                    <Label></Label>
                    <Input />
                </div>
                <div>
                    <Label></Label>
                    <Input />
                </div>
                <div>
                    <Label></Label>
                    <Input />
                </div>
                <div>
                    <Label></Label>
                    <Input />
                </div>
            </form>
        </div>
      </DialogContent>
    </Dialog>
  );
}
