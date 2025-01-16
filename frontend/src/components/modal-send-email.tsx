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

const modalSendEmail = z.object({
  email: z.string().email(''),
  
})


export function ModalSendEmail() {


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
          <form action="">
            <div>
              <Label>Email</Label>
              <Input type="email" placeholder="Digite o email do novo cliente"/>
            </div>
          </form>
        </div>
      </DialogContent>
    </Dialog>
  );
}
