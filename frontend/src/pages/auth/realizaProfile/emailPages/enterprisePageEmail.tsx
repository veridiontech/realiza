import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useFormDataContext } from "@/context/formDataProvider";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";
import { Search } from "lucide-react";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { Oval } from "react-loader-spinner";
import { useNavigate, useSearchParams } from "react-router-dom";
import { z } from "zod";

const enterprisePageEmailFormSchema = z.object({
  cnpj: z.string().nonempty("O CNPJ é obrigatório"),
  fantasyName: z.string().nonempty("O nome fantasia é obrigatório"),
  socialReason: z.string().nonempty("A razão social é obrigatória"),
  email: z.string().nonempty("O email é obrigatório"),
  phone: z.string().nonempty("O telefone é obrigatório"),
});

type EnterprisePageEmailFormSchema = z.infer<
  typeof enterprisePageEmailFormSchema
>;

export function EnterprisePageEmail() {
  const { setEnterpriseData } = useFormDataContext();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const token = searchParams.get("token");
  const [isValidToken, setIsValidToken] = useState(false);
  const findIdCompany = searchParams.get("id");
  const findCompany = searchParams.get("company");
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    const validateToken = async () => {
      try {
        const res = await axios.get(
          `https://realiza-1.onrender.com/email/Enterprise-sign-up/validate?token=${token}`,
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
    if (token) {
      validateToken();
    } else {
      console.log("Token não encontrado.");
    }
  }, [token]);

  const {
    register,
    handleSubmit,
    setValue,
    getValues,
    formState: { isValid },
  } = useForm<EnterprisePageEmailFormSchema>({
    resolver: zodResolver(enterprisePageEmailFormSchema),
    mode: "onChange",
  });

  const validateCnpj = async () => {
    setIsLoading(true);
    const cnpj = getValues("cnpj").replace(/\D/g, "");
    if (cnpj.length !== 14) return;

    try {
      const res = await axios.get(
        `https://www.receitaws.com.br/v1/cnpj/${cnpj}`,
      );

      console.log(res.data);

      if (res.data) {
        setValue("socialReason", res.data.nome);
        setValue("fantasyName", res.data.fantasia || "Sem nome fantasia");
        setValue("email", res.data.email);
        setValue("phone", res.data.telefone);
      }
    } catch (err) {
      console.log("Erro ao buscar CNPJ:", err);
    } finally {
      setIsLoading(false);
    }
  };

  const onSubmit = async (data: EnterprisePageEmailFormSchema) => {
    setIsLoading(true);
    try {
      const payload = {
        ...data,
        idCompany: findIdCompany || "",
        company: findCompany,
      };
      // if(findCompany === "SUPPLIER") {
      //   const payloadCompanySupplier = {
      //     ...data,
      //     branches: {
      //       idCompany: findIdCompany || "",
      //     },
      //   }
      // }
      console.log("idCompany:", findCompany);
      
      console.log("Enviando dados:", payload);

      setEnterpriseData(payload);
      navigate(`/email/Sign-up`);
    } catch (err) {
      console.error("Erro ao enviar os dados:", err);
    } finally {
      setIsLoading(false);
    }
  };

  if (!isValidToken) {
    return (
      <div className="text-red-600">
        Token inválido ou expirado. Por favor, solicite um novo convite.
      </div>
    );
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
              <Label>CNPJ</Label>
              <div className="flex items-center gap-2">
                <Input
                  type="text"
                  placeholder="CNPJ: __.___.___/____-__"
                  className="w-[10vw]"
                  {...register("cnpj")}
                />
                {isLoading ? (
                  <Button type="button" onClick={validateCnpj}>
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
                  <Button type="button" onClick={validateCnpj}>
                    <Search />
                  </Button>
                )}
              </div>
            </div>
            <div>
              <Label>Telefone</Label>
              <Input
                type="text"
                placeholder="Telefone"
                className="w-[13vw]"
                {...register("phone")}
              />
            </div>
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
          <div className="flex items-center gap-5">
            <div>
              <Label>Nome fantasia</Label>
              <Input
                type="text"
                placeholder="Nome Fantasia"
                className="w-[13vw]"
                {...register("fantasyName")}
              />
            </div>
            <div>
              <Label>Razão social</Label>
              <Input
                type="text"
                placeholder="*Razão social"
                className="w-[13vw]"
                {...register("socialReason")}
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
