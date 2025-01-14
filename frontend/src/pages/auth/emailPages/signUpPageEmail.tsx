import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import { z } from "zod";

const signUpEmailFormSchema = z.object({
    name: z.string().nonempty('Nome é obrigatório'),
    surname: z.string().nonempty('Sobrenome é obrigatório'),
    phone: z.string().nonempty('Celular é obrigatório'),
    cpf:z.string().nonempty('Cpf é obrigatório'),
    email: z.string().nonempty('Email é obrigatório obrigatório'),
    position: z.string().nonempty('Seu cargo é obrigatório'),
    role:z.string().default('ROLE_ADMIN')
})

type SignUpEmailFormSchema = z.infer<typeof signUpEmailFormSchema> 
export function SignUpPageEmail() {
    const navigate = useNavigate()

    const {
        register,
        handleSubmit,
        formState: {errors, isValid},
    } = useForm<SignUpEmailFormSchema>({
        resolver: zodResolver(signUpEmailFormSchema),
        mode: "onChange"
    })

    const onSubmit = (data: SignUpEmailFormSchema) => {
        console.log('Dados enviados',data);
        navigate('/email/Enterprise-sign-up')
    }

  return (
    <div>
      <div className="flex justify-center">
        <h1 className="text-[40px]">Cadastro</h1>
      </div>
      <form className="flex flex-col gap-5" onSubmit={handleSubmit(onSubmit)}>
        <div className="flex items-center gap-5">
          <div>
            <Label>Nome</Label>
            <Input
              type="text"
              placeholder="Nome"
              className="w-[13vw]"
              {...register("name")}
              
            />
            {errors.name && <span className="text-red-600">{errors.name.message}</span>}
          </div>
          <div>
            <Label>Sobrenome</Label>
            <Input
              type="text"
              placeholder="Sobrenome"
              className="w-[13vw]"
              {...register("surname")}
            />
            {errors.surname && <span className="text-red-600">{errors.surname.message}</span>}
          </div>
        </div>
        <div>
          <Label>Celular</Label>
          <Input placeholder="Digite seu celular" className="w-[27vw]"  {...register("phone")}/>
          {errors.phone && <span className="text-red-600">{errors.phone.message}</span>}
        </div>
        <div>
          <Label>CPF</Label>
          <Input placeholder="CPF: ___.___.___-__" className="w-[27vw]"  {...register("cpf")}/>
          {errors.cpf && <span className="text-red-600">{errors.cpf.message}</span>}
        </div>
        <div className="flex items-center gap-5">
          <div>
            <Label>Email</Label>
            <Input
              type="email"
              placeholder="Digite seu email"
              className="w-[13vw]"
              {...register("email")}
            />
            {errors.email && <span className="text-red-600">{errors.email.message}</span>}
          </div>
          <div>
            <Label>Seu cargo</Label>
            <Input
              type="text"
              placeholder="Qual seu cargo na empresa?"
              className="w-[13vw]"
              {...register("position")}
            />
            {errors.position && <span className="text-red-600">{errors.position.message}</span>}
          </div>
        </div>
        <Button className="h-[5vh] bg-realizaBlue" disabled={!isValid}>Cadastrar</Button>
      </form>
    </div>
  );
}
