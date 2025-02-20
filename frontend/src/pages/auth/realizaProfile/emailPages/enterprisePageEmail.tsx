import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";
import { Search } from "lucide-react";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { Oval } from "react-loader-spinner";
import { useNavigate, useSearchParams } from "react-router-dom";
import { z } from "zod";
import { useUser } from "@/context/user-provider";

const enterprisePageEmailFormSchema = z.object({
  cnpj: z.string().nonempty("O CNPJ é obrigatório"),
  tradeName: z.string().optional(),
  corporateName: z.string().nonempty("A razão social é obrigatória"),
  email: z.string().nonempty("O email é obrigatório"),
  phone: z.string().nonempty("O telefone é obrigatório"),
  idCompany: z.string().optional(),
  company: z.string().nullable().optional(),
});

type EnterprisePageEmailFormSchema = z.infer<
  typeof enterprisePageEmailFormSchema
>;

export function EnterprisePageEmail() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const tokenFromUrl = searchParams.get("token");
  const { token, setToken } = useUser(); // Usa o contexto para obter e setar o token
  const [isValidToken, setIsValidToken] = useState(false);
  const findIdCompany = searchParams.get("id");
  const findCompany = searchParams.get("company");
  const idClient = searchParams.get("idClient"); // Novo parâmetro idClient vindo da URL
  const [isLoading, setIsLoading] = useState(false);

  // Estado para armazenar as branches do client (inicialmente como array vazio)
  const [branches, setBranches] = useState<any[]>([]);

  // Se o token vier pela URL, armazena-o no contexto
  useEffect(() => {
    if (tokenFromUrl) {
      setToken(tokenFromUrl);
    }
  }, [tokenFromUrl, setToken]);

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

  // Nova lógica: busca das branches do client usando o idClient
  useEffect(() => {
    if (idClient) {
      axios
        .get(
          `https://realiza-1.onrender.com/branch/filtered-client?idSearch=${idClient}`,
        )
        .then((res) => {
          // Supondo que a resposta possa vir no campo "content"
          const data = res.data.content || res.data;
          // Se "data" não for um array, converte-o em array ou define como vazio
          const branchesArray = Array.isArray(data) ? data : data ? [data] : [];
          setBranches(branchesArray);
        })
        .catch((err) => {
          console.error("Erro ao buscar branches:", err);
        });
    }
  }, [idClient]);

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
    if (cnpj.length !== 14) {
      setIsLoading(false);
      return;
    }
    try {
      const res = await axios.get(
        `https://www.receitaws.com.br/v1/cnpj/${cnpj}`,
      );
      if (res.data) {
        setValue("corporateName", res.data.nome);
        // Se não houver nome fantasia, define como string vazia
        setValue("tradeName", res.data.fantasia || "");
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
      let payload;
      switch (findCompany) {
        case "SUBCONTRACTOR":
          payload = {
            ...data,
            idCompany: findIdCompany || "",
            company: findCompany || "",
            fantasyName: data.tradeName,
            socialReason: data.corporateName,
            role: "ROLE_SUPPLIER_RESPONSIBLE",
          };
          break;
        case "CLIENT":
          payload = {
            ...data,
            idCompany: findIdCompany || "",
            company: findCompany || "",
            fantasyName: data.tradeName,
            socialReason: data.corporateName,
            role: "ROLE_CLIENT_RESPONSIBLE",
          };
          break;
        case "SUPPLIER":
          payload = {
            ...data,
            idCompany: findIdCompany || "",
            company: findCompany || "",
            fantasyName: data.tradeName,
            socialReason: data.corporateName,
            role: "ROLE_SUPPLIER_RESPONSIBLE",
          };
          break;
        default:
          payload = {
            ...data,
            idCompany: findIdCompany || "",
            company: findCompany || "",
            fantasyName: data.tradeName,
            socialReason: data.corporateName,
          };
          break;
      }

      await axios.post(
        "https://realiza-1.onrender.com/email/Enterprise-sign-up",
        payload,
      );
      // Após cadastrar a empresa, redireciona para a página de cadastro individual
      navigate(`/email/Sign-Up?token=${token}`);
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
          {/* Nova lógica: exibição das branches do client */}
          {branches && branches.length > 0 && (
            <div>
              <Label>Selecione a Branch</Label>
              <select className="w-[27vw] rounded border p-2">
                {branches.map((branch: any) => (
                  <option key={branch.id} value={branch.id}>
                    {branch.name}
                  </option>
                ))}
              </select>
            </div>
          )}
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
