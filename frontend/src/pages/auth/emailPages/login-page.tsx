import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { zodResolver } from "@hookform/resolvers/zod";
import { Eye, EyeOff } from "lucide-react";
import { useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";

const loginPageEmailFormSchema = z.object({
    emailUser: z.string(),
    password: z.string(),
    confirmPassword: z.string()
})

type LoginPageEmailFormSchema = z.infer<typeof loginPageEmailFormSchema>
export function LoginPageEmail() {
    const [ isOpenEye, setIsOpenEye ] = useState(false) 

    const {
        register,
        handleSubmit,
        formState: { errors }
    } = useForm<LoginPageEmailFormSchema>({
        resolver: zodResolver(loginPageEmailFormSchema)
    })

    const onSubmit = (data: LoginPageEmailFormSchema) => {
        console.log('dados enviados:',data);
        
    }

    const togglePasswordVisibility = () => {
        setIsOpenEye(!isOpenEye)
    }

  return (
    <div className="flex flex-col gap-5">
      <div className="flex justify-center">
        <h1 className="text-[40px]">Crie seu login</h1>
      </div>
      <form action="" className="flex flex-col gap-5" onSubmit={handleSubmit(onSubmit)}>
        <div>
          <Label>Email</Label>
          <Input type="email" className="w-[27vw]" {...register('emailUser')}/>
        </div>
        <div>
          <Label>Senha</Label>
          <div className="flex items-center border border-gray-300 rounded w-[27vw]">
            <Input
              type={isOpenEye ? "text": "password"}
              className="flex-1 border-none focus:ring-0"
              placeholder="Digite sua senha"
              {...register('password')}
            />
            {isOpenEye ? (
              <Eye className="mx-3 text-gray-400 cursor-pointer" onClick={togglePasswordVisibility} />
            ) : (
              <EyeOff className="mx-3 text-gray-400 cursor-pointer" onClick={togglePasswordVisibility} />
            )}
          </div>
        </div>
        <div>
          <Label>Confirme sua senha</Label>
          <div className="flex w-[27vw] items-center rounded border border-gray-300">
            <Input
              type={isOpenEye ? "text": "password"}
              className="flex-1 border-none focus:border-transparent focus:outline-none"
              placeholder="Confirme sua senha "
              {...register('confirmPassword')}
            />
          </div>
        </div>
        <Button className="bg-realizaBlue h-[5vh]" type="submit">Criar</Button>
      </form>
    </div>
  );
}
