import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useFormDataContext } from "@/context/formDataProvider";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
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

  const {
    register,
    handleSubmit,
    formState: { errors, isValid },
  } = useForm<EnterprisePageEmailFormSchema>({
    resolver: zodResolver(enterprisePageEmailFormSchema),
    mode: "onChange"
  });

  const onSubmit = (data: EnterprisePageEmailFormSchema) => {
    setEnterpriseData(data)
    localStorage.setItem('enterpriseData', JSON.stringify(data))
    console.log("Dados enviados", data);
    navigate(`/email/Sign-up`)
  };

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
