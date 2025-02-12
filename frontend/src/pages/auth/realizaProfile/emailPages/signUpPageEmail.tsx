import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useFormDataContext } from "@/context/formDataProvider";
import { ip } from "@/utils/ip";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";
import { Eye, EyeOff } from "lucide-react";
import { useState } from "react";
import { useForm } from "react-hook-form";
import { Oval } from "react-loader-spinner";
import { useNavigate } from "react-router-dom";
import { z } from "zod";

const signUpEmailFormSchema = z
  .object({
    name: z.string().nonempty("Nome é obrigatório"),
    surname: z.string().nonempty("Sobrenome é obrigatório"),
    phone: z.string().nonempty("Celular é obrigatório"),
    cpf: z.string().nonempty("Cpf é obrigatório"),
    email: z
      .string()
      .email("Formato de email inválido")
      .nonempty("Email é obrigatório"),
    position: z.string().nonempty("Seu cargo é obrigatório"),
    password: z.string().min(6, "A senha deve ter pelo menos 6 caracteres"),
    confirmPassword: z
      .string()
      .min(6, "Confirmação de senha deve ter pelo menos 6 caracteres"),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: "As senhas não coincidem",
    path: ["confirmPassword"],
  });

type SignUpEmailFormSchema = z.infer<typeof signUpEmailFormSchema>;
export function SignUpPageEmail() {
  const { enterpriseData, setUserData } = useFormDataContext();
  const [isOpenEye, setIsOpenEye] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    formState: { errors, isValid },
  } = useForm<SignUpEmailFormSchema>({
    resolver: zodResolver(signUpEmailFormSchema),
    mode: "onChange",
  });

  const onSubmit = async (data: SignUpEmailFormSchema) => {
    setIsLoading(true);
    try {
      setUserData(data);
      const allDatas = {
        ...enterpriseData,
        ...data,
      };
      console.log("enviando dados:", allDatas);
      await axios.post(`${ip}/sign-enterprise`, allDatas);
      navigate("/");
    } catch (err) {
      console.log(err);
    } finally {
      setIsLoading(false);
    }
  };

  const togglePasswordVisibility = () => {
    setIsOpenEye(!isOpenEye);
  };

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
            {errors.name && (
              <span className="text-red-600">{errors.name.message}</span>
            )}
          </div>
          <div>
            <Label>Sobrenome</Label>
            <Input
              type="text"
              placeholder="Sobrenome"
              className="w-[13vw]"
              {...register("surname")}
            />
            {errors.surname && (
              <span className="text-red-600">{errors.surname.message}</span>
            )}
          </div>
        </div>
        <div>
          <Label>Celular</Label>
          <Input
            placeholder="Digite seu celular"
            className="w-[27vw]"
            {...register("phone")}
          />
          {errors.phone && (
            <span className="text-red-600">{errors.phone.message}</span>
          )}
        </div>
        <div>
          <Label>CPF</Label>
          <Input
            placeholder="CPF: ___.___.___-__"
            className="w-[27vw]"
            {...register("cpf")}
          />
          {errors.cpf && (
            <span className="text-red-600">{errors.cpf.message}</span>
          )}
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
            {errors.email && (
              <span className="text-red-600">{errors.email.message}</span>
            )}
          </div>
          <div>
            <Label>Seu cargo</Label>
            <Input
              type="text"
              placeholder="Qual seu cargo na empresa?"
              className="w-[13vw]"
              {...register("position")}
            />
            {errors.position && (
              <span className="text-red-600">{errors.position.message}</span>
            )}
          </div>
        </div>
        <div>
          <Label>Senha</Label>
          <div>
            <div className="flex w-[27vw] items-center rounded border border-gray-300">
              <Input
                type={isOpenEye ? "text" : "password"}
                className="flex-1 border-none focus:ring-0"
                placeholder="Digite sua senha"
                {...register("password")}
              />
              {isOpenEye ? (
                <Eye
                  className="mx-3 cursor-pointer text-gray-400"
                  onClick={togglePasswordVisibility}
                />
              ) : (
                <EyeOff
                  className="mx-3 cursor-pointer text-gray-400"
                  onClick={togglePasswordVisibility}
                />
              )}
            </div>
            {errors.password && (
              <span className="text-red-600">{errors.password.message}</span>
            )}
          </div>
        </div>
        <div>
          <Label>Confirme sua senha</Label>
          <div className="flex flex-col">
            <Input
              type={isOpenEye ? "text" : "password"}
              className=""
              placeholder="Confirme sua senha "
              {...register("confirmPassword")}
            />
            {errors.confirmPassword && (
              <span className="text-red-600">
                {errors.confirmPassword.message}
              </span>
            )}
          </div>
        </div>
        {isLoading ? (
          <Button className="bg-realizaBlue h-[5vh]" disabled={!isValid}>
            Cadastrar
          </Button>
        ) : (
          <Button className="bg-realizaBlue h-[5vh]" disabled={!isValid}>
            <Oval
            visible={true}
            height="80"
            width="80"
            color="#4fa94d"
            ariaLabel="oval-loading"
            wrapperStyle={{}}
            wrapperClass=""
          />
          </Button>
          
        )}
      </form>
    </div>
  );
}
