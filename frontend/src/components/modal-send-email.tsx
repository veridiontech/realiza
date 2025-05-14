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
import { useState } from "react";
import { Radio } from "react-loader-spinner";
import { toast } from "sonner";

const modalSendEmailFormSchema = z.object({
  email: z.string().email("Insira um email válido"),
  company: z.string().default("CLIENT"),
});

type ModalSendEmailFormSchema = z.infer<typeof modalSendEmailFormSchema>;

export function ModalSendEmail() {
  const [isLoading, setIsLoading] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ModalSendEmailFormSchema>({
    resolver: zodResolver(modalSendEmailFormSchema),
  });

  const createClient = async (data: ModalSendEmailFormSchema) => {
    console.log("teste");
    setIsLoading(true);
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      await axios.post(`${ip}/invite`, {
        email: data.email,
        company: data.company,
      },
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` }
        }
      );
      toast.success("Email enviado ao novo cliente");
    } catch (err) {
      toast.error("Erro ao enviar email. Tente novamente");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Dialog>
      <DialogTrigger asChild>
        <Button className="bg-realizaBlue mb-4">Cadastrar cliente</Button>
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
              <Label className="">Email</Label>
              <Input
                type="email"
                placeholder="Digite o email do novo cliente"
                {...register("email")}
                className="mt-2 w-full"
              />
              {errors.email && (
                <span className="text-red-600">{errors.email.message}</span>
              )}
            </div>
            <div className="flex justify-end">
              {isLoading ? (
                <Button className="bg-realizaBlue">
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
