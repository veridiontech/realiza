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
import { ip } from "@/utils/ip";

// Definição do schema com todos os campos exigidos pelo Swagger para supplier
const supplierFormSchema = z.object({
  cnpj: z.string().nonempty("O CNPJ é obrigatório"),
  tradeName: z.string().optional(),
  corporateName: z.string().nonempty("A razão social é obrigatória"),
  email: z.string().nonempty("O email é obrigatório"),
  cep: z.string().nonempty("O CEP é obrigatório"),
  state: z.string().nonempty("O estado é obrigatório"),
  city: z.string().nonempty("A cidade é obrigatória"),
  address: z.string().nonempty("O endereço é obrigatório"),
  number: z.string().nonempty("O número é obrigatório"),
  // Campo branches: array de strings com pelo menos um item
  branches: z
    .array(z.string())
    .nonempty("Pelo menos uma branch deve ser selecionada"),
});

type SupplierFormSchema = z.infer<typeof supplierFormSchema>;

export function SupplierRegistration() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const tokenFromUrl = searchParams.get("token");
  const { token, setToken } = useUser();
  const [isValidToken, setIsValidToken] = useState(false);
  const findIdCompany = searchParams.get("id");
  const findCompany = searchParams.get("company");
  const idClient = searchParams.get("idClient"); // idClient passado na URL
  const [isLoading, setIsLoading] = useState(false);

  // Estado para armazenar as branches do client
  const [branches, setBranches] = useState<any[]>([]);

  // Se o token vier pela URL, armazena-o no contexto
  useEffect(() => {
    if (tokenFromUrl) {
      setToken(tokenFromUrl);
    }
  }, [tokenFromUrl, setToken]);

  // Validação do token usando a rota de validação
  useEffect(() => {
    if (token) {
      axios
        .get(`${ip}/email/Enterprise-sign-up/validate?token=${token}`)
        .then((res) => {
          if (res.status === 200) {
            setIsValidToken(true);
          } else {
            setIsValidToken(false);
          }
        })
        .catch((err) => {
          console.error("Erro ao validar token", err);
          setIsValidToken(false);
        });
    }
  }, [token]);

  // Nova lógica: busca das branches do client usando o idClient
  useEffect(() => {
    if (idClient) {
      // Adiciona parâmetros de paginação para garantir o formato Page com o campo "content"
      axios
        .get(
          `${ip}/branch/filtered-client?idSearch=${idClient}&page=0&size=100`,
        )
        .then((res) => {
          console.log("Resposta da API de branches:", res.data);
          // Se a resposta tiver o campo "content", usamos ele; senão, usamos res.data
          const data = res.data.content || res.data;
          // Garante que data seja um array
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
  } = useForm<SupplierFormSchema>({
    resolver: zodResolver(supplierFormSchema),
    mode: "onChange",
  });

  // Função para validar CNPJ usando uma API externa
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
        // Opcional: se a API fornecer CEP, estado, cidade, endereço, número, você pode preenchê-los aqui
      }
    } catch (err) {
      console.error("Erro ao buscar CNPJ:", err);
    } finally {
      setIsLoading(false);
    }
  };

  // Função para enviar os dados do formulário para o cadastro do supplier
  const onSubmit = async (data: SupplierFormSchema) => {
    setIsLoading(true);
    try {
      // Constrói o payload conforme necessário. Campos adicionais idCompany e company podem ser incluídos se necessário.
      let payload = {
        ...data,
        idCompany: findIdCompany || "",
        company: findCompany || "",
        // Note: não enviamos o campo "branch", somente "branches"
      };

      await axios.post(`${ip}/supplier`, payload);
      // Após o cadastro, redireciona para a página de dashboard ou outro fluxo
      navigate(`/supplier/dashboard?token=${token}`);
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
        <h1 className="text-[40px]">Cadastro de Supplier</h1>
      </div>
      <form className="flex flex-col gap-6" onSubmit={handleSubmit(onSubmit)}>
        {/* Campo CNPJ com botão para validação */}
        <div className="flex flex-col gap-2">
          <Label>CNPJ</Label>
          <div className="flex items-center gap-2">
            <Input
              type="text"
              placeholder="CNPJ: __.___.___/____-__"
              {...register("cnpj")}
              className="w-[10vw]"
            />
            <Button type="button" onClick={validateCnpj}>
              <Search />
            </Button>
          </div>
        </div>
        {/* Campo Email corporativo */}
        <div className="flex flex-col gap-2">
          <Label>Email corporativo</Label>
          <Input
            type="email"
            placeholder="Digite o email corporativo"
            {...register("email")}
            className="w-[27vw]"
          />
        </div>
        {/* Campo Nome fantasia */}
        <div className="flex flex-col gap-2">
          <Label>Nome fantasia</Label>
          <Input
            type="text"
            placeholder="Nome Fantasia"
            {...register("tradeName")}
            className="w-[27vw]"
          />
        </div>
        {/* Campo Razão social */}
        <div className="flex flex-col gap-2">
          <Label>Razão social</Label>
          <Input
            type="text"
            placeholder="Razão social"
            {...register("corporateName")}
            className="w-[27vw]"
          />
        </div>
        {/* Campo CEP */}
        <div className="flex flex-col gap-2">
          <Label>CEP</Label>
          <Input
            type="text"
            placeholder="CEP"
            {...register("cep")}
            className="w-[10vw]"
          />
        </div>
        {/* Campo Estado */}
        <div className="flex flex-col gap-2">
          <Label>Estado</Label>
          <Input
            type="text"
            placeholder="Estado"
            {...register("state")}
            className="w-[10vw]"
          />
        </div>
        {/* Campo Cidade */}
        <div className="flex flex-col gap-2">
          <Label>Cidade</Label>
          <Input
            type="text"
            placeholder="Cidade"
            {...register("city")}
            className="w-[10vw]"
          />
        </div>
        {/* Campo Endereço */}
        <div className="flex flex-col gap-2">
          <Label>Endereço</Label>
          <Input
            type="text"
            placeholder="Endereço"
            {...register("address")}
            className="w-[27vw]"
          />
        </div>
        {/* Campo Número */}
        <div className="flex flex-col gap-2">
          <Label>Número</Label>
          <Input
            type="text"
            placeholder="Número"
            {...register("number")}
            className="w-[10vw]"
          />
        </div>
        {/* Seleção das Branches do client */}
        {branches && branches.length > 0 && (
          <div className="flex flex-col gap-2">
            <Label>Selecione a Branch</Label>
            {/* Aqui usamos um <select> simples. Se necessário, pode ser um multi-select */}
            <select
              {...register("branches")}
              className="w-[27vw] rounded border p-2"
            >
              {branches.map((branch: any) => (
                <option
                  key={branch.idBranch || branch.id}
                  value={branch.idBranch || branch.id}
                >
                  {branch.name}
                </option>
              ))}
            </select>
          </div>
        )}
        {/* Botão de envio */}
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
            Cadastrar Supplier
          </Button>
        )}
      </form>
    </div>
  );
}
