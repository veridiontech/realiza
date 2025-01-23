import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { ip } from "@/utils/ip";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";
import { User } from "lucide-react";
import { useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";

const createUserRealizaSchema = z.object({
  firstName: z.string(),
  surname: z.string(),
  email: z.string().email("Insira um email valido"),
  cpf: z.string(),
  telephone: z.string(),
  // profilePicture: z.string().optional(),
});

type CreateUserRealizaSchema = z.infer<typeof createUserRealizaSchema>;
export function CreateUserRealiza() {
  const [nameTyping, setNameTyping] = useState("");
  const [surnameTyping, setSurnameTyping] = useState(""); 
  const [ emailTyping, setEmailTyping ] = useState("")

  const {
    register,
    handleSubmit,
  } = useForm<CreateUserRealizaSchema>({
    resolver: zodResolver(createUserRealizaSchema),
  });

  const createUser = async (data: CreateUserRealizaSchema) => {
    const payload = {
      ...data,
      role: "ROLE_MANAGER",
      password: "teste123",
    };
    console.log("Função createUser chamada com os dados:", payload);
    try {
      await axios.post(`${ip}/user/manager`, payload);

      console.log("Usuário criado com sucesso");
    } catch (err) {
      console.error("Erro ao criar novo usuário", err);
    }
  };

  return (
    <div className="dark:bg-primary m-16 flex flex-col gap-8 rounded-md bg-white p-10 shadow-md">
      <div className="flex justify-start">
        <h1 className="text-[24px]">Crie um novo usuário para realiza</h1>
      </div>
      <div className="flex justify-center">
        <form action="" onSubmit={handleSubmit(createUser)}>
          <div className="flex h-[43vh] w-[70vw] items-center justify-between rounded-md border p-10 shadow-md">
            <div className="flex flex-col gap-3">
              <h2>Informações pessoais</h2>
              <div className="flex flex-col gap-6">
                <div className="flex items-center gap-6">
                  <div className="w-[20vw]">
                    <Label>Nome</Label>
                    <Input
                      type="text"
                      {...register("firstName")}
                      onChange={(e) => setNameTyping(e.target.value)}
                    />
                  </div>
                  <div className="w-[20vw]">
                    <Label>Sobrenome</Label>
                    <Input
                      type="text"
                      {...register("surname")}
                      onChange={(e) => setSurnameTyping(e.target.value)}
                    />
                  </div>
                </div>
                <div className="w-[41.2vw]">
                  <Label>Cpf</Label>
                  <Input type="text" {...register("cpf")} onChange={(e) => setEmailTyping(e.target.value)}/>
                </div>
                <div className="flex items-center gap-6">
                  <div className="w-[20vw]">
                    <Label>Email</Label>
                    <Input type="email" {...register("email")} />
                  </div>
                  <div className="w-[20vw]">
                    <Label>Celular</Label>
                    <Input type="text" {...register("telephone")} />
                  </div>
                </div>
                <div className="flex justify-end">
                  <Button className="bg-realizaBlue" type="submit">
                    Criar Usuário
                  </Button>
                </div>
              </div>
            </div>
            <div className="flex h-[40vh] w-[22vw] flex-col justify-center gap-2 rounded-md border p-14 shadow-md">
              <div className="flex h-[50vh] items-center justify-center rounded-md bg-gray-300 px-5 py-2">
                <User size={150} />
              </div>
              <div className="flex flex-col gap-2">
                <div className="flex items-center gap-1">
                  <p>Nome:</p>
                  <div className="flex items-center gap-1">
                    <p>{nameTyping}</p>
                    <p>{surnameTyping}</p>
                  </div>
                </div>
                <div>
                  <p>Email:</p>
                  <p>{emailTyping}</p>
                </div>
              </div>
            </div>
          </div>
        </form>
      </div>
    </div>
  );
}
