import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useFormDataContext } from "@/context/formDataProvider";
import { ip } from "@/utils/ip";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";
import { Eye, EyeOff } from "lucide-react";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { Oval } from "react-loader-spinner";
import { useSearchParams } from "react-router-dom";
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
  const [searchParams] = useSearchParams();
  const findBranchId = searchParams.get("idBranch");
  const [branch, setBranch] = useState<any>(null);
  const {
    register,
    handleSubmit,
    formState: { errors, isValid },
  } = useForm<SignUpEmailFormSchema>({
    resolver: zodResolver(signUpEmailFormSchema),
    mode: "onChange",
  });
  

  if (!enterpriseData) {
    return <div>Dados da empresa não encontrados.</div>;
  }

  const getBranch = async () => {
    try {
      const res = await axios.get(`${ip}/branch/${findBranchId}`)
      console.log("aaa" , res.data);
      setBranch(res.data)
    } catch (err:any){
      console.log("Erro ao selecionar branch:" , err);
    }
  }

  useEffect (()=>{
    getBranch()
  },[])

  const onSubmit = async (data: SignUpEmailFormSchema) => {
    setIsLoading(true);
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      setUserData(data);
      const role =
        enterpriseData.company === "CLIENT"
          ? "ROLE_CLIENT_RESPONSIBLE"
          : enterpriseData.company === "SUPPLIER"
            ? "ROLE_SUPPLIER_RESPONSIBLE"
            : "ROLE_ADMIN";
      const allDatas = {
        ...enterpriseData,
        ...data,
        role,
        company: enterpriseData.company,
      };
      console.log("enviando dados:", allDatas);

      const response = await axios.post(`${ip}/sign-enterprise`, allDatas,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` }
        }
      );
      if (response.status === 200) {
        localStorage.removeItem("enterpriseForm");
        window.location.href = "https://realiza-1.onrender.com/";
      }

      window.location.href = "https://realiza-1.onrender.com/";
    } catch (err) {
      if (axios.isAxiosError(err) && err.response) {
        console.error("Erro na resposta da API:", err.response.data);
        const apiErrorMessage = err.response?.data?.message || "Erro desconhecido";
        console.log(`Erro: ${apiErrorMessage}`);
      } else {
        console.error("Erro desconhecido:", err);
      }
      console.log(err);
    } finally {
      setIsLoading(false);
    }
  };

  const togglePasswordVisibility = () => {
    setIsOpenEye(!isOpenEye);
  };

  const handleGoBack = () => {
    window.history.back();
  };

  return (
    <div>
      <div className="flex justify-center">
        <h1 className="text-[40px]">Cadastro</h1>
        <div>
          <p>Empresa: {branch?.name}</p>
        </div>
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
        <div>
          <Label>Confirme sua senha</Label>
          <div className="flex flex-col">
            <Input
              type={isOpenEye ? "text" : "password"}
              placeholder="Confirme sua senha"
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
        ) : (
          <Button className="bg-realizaBlue h-[5vh]" disabled={!isValid}>
            Cadastrar
          </Button>
        )}
        <div>
          <Button className="bg-realizaBlue h-[5vh]" onClick={handleGoBack}>
            Voltar
          </Button>
        </div>
      </form>
    </div>
  );
}
