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
import { useFormDataContext } from "@/context/formDataProvider";

const enterprisePageEmailFormSchema = z.object({
  tradeName: z.string().optional(),
  corporateName: z.string().nonempty("A razão social é obrigatória"),
  email: z.string().nonempty("O email é obrigatório"),
  phone: z.string().nonempty("O telefone é obrigatório"),
  company: z.string().nullable().optional(),
  cnpj: z.string().nonempty("O cnpj é obrigatório"),
});

type EnterprisePageEmailFormSchema = z.infer<typeof enterprisePageEmailFormSchema>;

export function EnterprisePageEmail() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const tokenFromUrl = searchParams.get("token");
  const { token } = useUser();
  const { setEnterpriseData } = useFormDataContext();

  const [isValidToken, setIsValidToken] = useState(false);
  const findId = searchParams.get("id");
  const findCompany = searchParams.get("company");
  const findBranchId = searchParams.get("idBranch");
  const [isLoading, setIsLoading] = useState(false);

  const [showConfirmModal, setShowConfirmModal] = useState(false);
  const [formData, setFormData] = useState<EnterprisePageEmailFormSchema | null>(null);

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
    formState: { isValid },
  } = useForm<EnterprisePageEmailFormSchema>({
    resolver: zodResolver(enterprisePageEmailFormSchema),
    mode: "onChange",
  });

  const onSubmit = async (data: EnterprisePageEmailFormSchema) => {
    setFormData(data);
    setShowConfirmModal(true);
  };

  const handleConfirm = () => {
    if (!formData) return;

    setIsLoading(true);
    let payload;

    switch (findCompany) {
      case "SUBCONTRACTOR":
        payload = {
          ...formData,
          idCompany: findId || "",
          company: findCompany || "",
          fantasyName: formData.tradeName || "",
          socialReason: formData.corporateName,
          role: "ROLE_SUPPLIER_RESPONSIBLE",
        };
        break;
      case "CLIENT":
        payload = {
          ...formData,
          idCompany: findId || "",
          company: findCompany || "",
          fantasyName: formData.tradeName || "",
          socialReason: formData.corporateName,
          role: "ROLE_CLIENT_RESPONSIBLE",
        };
        break;
      case "SUPPLIER":
        payload = {
          ...formData,
          idBranch: findBranchId || "",
          idCompany: findId || "",
          company: findCompany || "SUPPLIER",
          fantasyName: formData.tradeName || "",
          socialReason: formData.corporateName,
        };
        break;
      default:
        payload = {
          ...formData,
          idBranch: findBranchId || "",
          idCompany: findId || "",
          company: "SUPPLIER",
          fantasyName: formData.tradeName || "",
          socialReason: formData.corporateName,
        };
        break;
    }

    console.log("enviando cadastro", payload);
    setEnterpriseData(payload);
    navigate(`/email/Sign-Up?token=${token}`);
    setIsLoading(false);
    setShowConfirmModal(false);
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
          <div>
            <Label>CNPJ</Label>
            <Input
              placeholder="Digite o CNPJ"
              className="w-[27vw]"
              {...register("cnpj")}
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
              Próximo
            </Button>
          )}
        </form>
      </div>
      {showConfirmModal && formData && (
       <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50">
       <div className="bg-[#1d314a] text-white p-6 rounded-xl shadow-xl w-[90vw] max-w-md">
         <h2 className="text-xl font-semibold mb-4">Confirmar CNPJ</h2>
         <p className="mb-4">
           Você confirma que o <strong className="font-semibold">CNPJ</strong> informado é o mesmo em que o funcionário está registrado? Caso não, entre em contato com o suporte!</p>
         <strong className="block text-center font-medium mb-4">{formData.cnpj} - {formData.corporateName}</strong>
         <div className="flex justify-end gap-4">
           <Button
             type="button"
             className="bg-white text-[#1d314a] font-semibold px-4 py-2 rounded-lg hover:bg-gray-200"
             onClick={() => setShowConfirmModal(false)}
           >
             Cancelar
           </Button>
           <Button
             type="button"
             className="bg-white text-[#1d314a] font-semibold px-4 py-2 rounded-lg hover:bg-gray-200"
             onClick={handleConfirm}
           >
             Confirmar
           </Button>
         </div>
       </div>
     </div>
      )}
    </div>
  );
}
