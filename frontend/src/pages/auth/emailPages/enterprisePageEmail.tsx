import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useFormDataContext } from "@/context/formDataProvider";
import { ip } from "@/utils/ip";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { useNavigate, useSearchParams } from "react-router-dom";
import { z } from "zod";

const enterprisePageEmailFormSchema = z.object({
  cnpj: z.string().nonempty("O CNPJ é obrigatório"),
  nameEnterprise: z.string().nonempty("O nome da empresa obrigatório"),
  fantasyName: z.string().nonempty("O nome fantasia é obrigatório"),
  socialReason: z.string().nonempty("A razão social é obrigatória"),
  email: z.string().nonempty("O email é obrigatório"),
  phone: z.string().nonempty("O telefone é obrigatório"),
});

type EnterprisePageEmailFormSchema = z.infer<typeof enterprisePageEmailFormSchema>;
export function EnterprisePageEmail() {
    const {setEnterpriseData} = useFormDataContext()
    const navigate = useNavigate()
    const [searchParams] = useSearchParams();
    const token = searchParams.get("token");
    const [ isValidToken, setIsValidToken ] = useState(false)


  useEffect(() => {
    const validateToken = async() => {
      try{ 
        const res = await axios.get(`${ip}/email/Enterprise-sign-up/validate?token=${token}`)
        if(res.status === 200) {
          setIsValidToken(true)
        }
      }catch(err) {
        console.log("Nao foi possivel validar o token", err);
        setIsValidToken(false)
      }
    }

    if(token) {
      validateToken()
    } else {
      console.log("nao foi possivel verificar o token");
    }
  }, [token])

  const {
    register,
    handleSubmit,
    formState: { isValid },
  } = useForm<EnterprisePageEmailFormSchema>({
    resolver: zodResolver(enterprisePageEmailFormSchema),
    mode: "onChange"
  });

  const onSubmit = async (data: EnterprisePageEmailFormSchema) => {
    try {
      const response = await axios.post(`${ip}/clients/user`, { ...data, token });
      setEnterpriseData(data);
      localStorage.setItem("enterpriseData", JSON.stringify(data));
      console.log("Cadastro realizado:", response.data);
      navigate(`/email/success`);
    } catch (err) {
      console.error("Erro ao enviar os dados:", err);
    }
  };

  if (!isValidToken) {
    return <div className="text-red-600">Token inválido ou expirado. Por favor, solicite um novo convite.</div>;
  }

  return (
    <div className="flex flex-col gap-4">
      <div className="flex justify-center">
        <h1 className="text-[40px]">Cadastrar Empresa</h1>
      </div>
      <div>
        <form className="flex flex-col gap-6" onSubmit={handleSubmit(onSubmit)}>
          <div className="flex items-center gap-5">
            <div>
              <Label>Nome da empresa</Label>
              <Input
                type="text"
                placeholder="Nome Empresa / Grupo terceiro"
                className="w-[13vw]"
                {...register('nameEnterprise')}
              />
            </div>
            <div>
              <Label>Nome fantasia</Label>
              <Input
                type="text"
                placeholder="Nome Fantasia"
                className="w-[13vw]"
                {...register('fantasyName')}
              />
            </div>
          </div>
          <div>
            <Label>Email corporativo</Label>
            <Input
              type="email"
              placeholder="Digite o seu email"
              className="w-[27vw]"
              {...register('email')}
            />
          </div>
          <div>
            <Label>Razão social</Label>
            <Input
              type="text"
              placeholder="*Razão social"
              className="w-[27vw]"
              {...register('socialReason')}
            />
          </div>
          <div className="flex items-center gap-5">
            <div>
              <Label>Telefone</Label>
              <Input type="text" placeholder="Telefone" className="w-[13vw]" 
              {...register('phone')}/>
            </div>
            <div>
              <Label>Cnpj</Label>
              <Input
                type="text"
                placeholder="CNPJ: __.___.___/____-__"
                className="w-[13vw]"
                {...register('cnpj')}
              />
            </div>
          </div>
          <Button className="bg-realizaBlue h-[5vh]" type="submit" disabled={!isValid}>Cadastrar empresa</Button>
        </form>
      </div>
    </div>
  );
}
