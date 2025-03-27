import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { Oval } from "react-loader-spinner";
import { useNavigate, useSearchParams } from "react-router-dom";
import { z } from "zod";
import { useUser } from "@/context/user-provider";
// import { ip } from "@/utils/ip";
import { useFormDataContext } from "@/context/formDataProvider";
import { useDataSendEmailContext } from "@/context/dataSendEmail-Provider";

const enterprisePageEmailFormSchema = z.object({
  tradeName: z.string().optional(),
  corporateName: z.string().nonempty("A razão social é obrigatória"),
  email: z.string().nonempty("O email é obrigatório"),
  phone: z.string().nonempty("O telefone é obrigatório"),
  company: z.string().nullable().optional(),

});

type EnterprisePageEmailFormSchema = z.infer<
  typeof enterprisePageEmailFormSchema
>;

export function EnterprisePageEmail() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const tokenFromUrl = searchParams.get("token");
  const { token } = useUser();
  const { setEnterpriseData } = useFormDataContext();
  const [isValidToken, setIsValidToken] = useState(false);
  const findIdCompany = searchParams.get("id");
  const findCompany = searchParams.get("company");
  const findBranchId = searchParams.get("idBranch")
  const [isLoading, setIsLoading] = useState(false);
  const {datasSender} = useDataSendEmailContext()
  // const {userData} = useFormDataContext()

  useEffect(() => {
    const validateToken = async () => {
      try {
        const res = await axios.get(
          `https://realiza-1.onrender.com/email/Enterprise-sign-up/validate?token=${tokenFromUrl}`,
        );
        if (res.status === 200) {
          setIsValidToken(true);
        } else {
          console.log("Erro ao validar token.");
        }
      } catch (err) {
        console.log("Não foi possível validar o token", err);
        setIsValidToken(false);
      }
    };

    if (tokenFromUrl) {
      validateToken();
    } else {
      console.log("Token não encontrado.");
    }
  }, [tokenFromUrl]);


  const {
    register,
    handleSubmit,
    setValue,
    // getValues,
    formState: { isValid },
  } = useForm<EnterprisePageEmailFormSchema>({
    resolver: zodResolver(enterprisePageEmailFormSchema),
    mode: "onChange",
  });

  const onSubmit = async (data: EnterprisePageEmailFormSchema) => {
    setIsLoading(true);
    let payload;
    switch (findCompany) {
      case "SUBCONTRACTOR":
        payload = {
          ...data,
          idCompany: findIdCompany || "",
          company: findCompany || "",
          fantasyName: data.tradeName || "",
          socialReason: data.corporateName,
          role: "ROLE_SUPPLIER_RESPONSIBLE",
        };
        break;
      case "CLIENT":
        payload = {
          ...data,
          idCompany: findIdCompany || "",
          company: findCompany || "",
          fantasyName: data.tradeName || "",
          socialReason: data.corporateName,
          role: "ROLE_CLIENT_RESPONSIBLE",
        };
        break;
      case "SUPPLIER":
        payload = {
          ...data,
                    idBranch: findBranchId || "",
          idCompany: findIdCompany || "",
          company: findCompany || "",
          fantasyName: data.tradeName || "",
          socialReason: data.corporateName,
        };
        break;
      default:
        payload = {
          ...data,
          idCompany: findIdCompany || "",
          company: findCompany || "",
          fantasyName: data.tradeName || "",
          socialReason: data.corporateName,
        };
        break;
    }
    setEnterpriseData(payload);
    navigate(`/email/Sign-Up?token=${token}`);
    setIsLoading(false);
  };

  if (!isValidToken) {
    return (
      <div className="text-red-600">
        Token inválido ou expirado. Por favor, solicite um novo convite.
      </div>
    );
  }

  
useEffect(() => {
  if (datasSender) {
    if (datasSender.email) setValue("email", datasSender.email);
    if (datasSender.phone) setValue("phone", datasSender.phone);
    if (datasSender.tradeName) setValue("tradeName", datasSender.tradeName);
    if (datasSender.corporateName) setValue("corporateName", datasSender.corporateName);
  }
}, [datasSender, setValue]);

  useEffect(() => {

  }, )

  return (
    <div className="flex flex-col gap-4">
      <div className="flex justify-center">
        <h1 className="text-[40px]">Cadastrar Empresa</h1>
      </div>
      <div>
        <form className="flex flex-col gap-6" onSubmit={handleSubmit(onSubmit)}>
          <div className="flex items-center gap-5">
          </div>
          <div>
            <Label>Email corporativo</Label>
            <Input
              type="email"
              placeholder="Digite o seu email"
              className="w-[27vw]"
              {...register("email")}
            />
          </div>
          <div>
            <Label>Telefone</Label>
            <Input
              type="text"
              placeholder="Digite o telefone"
              className="w-[27vw]"
              {...register("phone")}
            />
          </div>
          <div className="flex items-center gap-5">
            <div>
              <Label>Nome fantasia</Label>
              <Input
                type="text"
                placeholder="Nome Fantasia"
                className="w-[13vw]"
                {...register("tradeName")}
              />
            </div>
            <div>
              <Label>Razão social</Label>
              <Input
                type="text"
                placeholder="*Razão social"
                className="w-[13vw]"
                {...register("corporateName")}
              />
            </div>
          </div>
          {isLoading ? (
            <Button className="bg-realizaBlue h-[5vh]">
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
            <Button
              className="bg-realizaBlue h-[5vh]"
              type="submit"
              disabled={!isValid}
            >
              Cadastrar empresa
            </Button>
          )}
        </form>
      </div>
    </div>
  );
}
