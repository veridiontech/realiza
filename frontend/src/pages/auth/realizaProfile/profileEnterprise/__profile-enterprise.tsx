import { Locate, Mail, Phone } from "lucide-react";
import { Helmet } from "react-helmet-async";
import { EditModalEnterprise } from "./edit-modal-enterprise";
// import { useClient } from "@/context/Client-Provider";
// import { Skeleton } from "@/components/ui/skeleton";
import { useSupplier } from "@/context/Supplier-context";
import { UploadDocumentButton } from "@/components/ui/upload-document-button";
// import { useUser } from "@/context/user-provider"
import { useLocation, useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import axios from "axios";
import { ip } from "@/utils/ip";
import { propsSupplier } from "@/types/interfaces";

interface Supplier {
  tradeName?: string;
  cnpj?: string;
  email?: string;
  telephone?: string;
  cep?: string;
}

interface ProfileEnterpriseRepriseProps {
  supplier?: Supplier; // opcional via prop
}

export function ProfileEnterpriseReprise({ supplier: propSupplier }: ProfileEnterpriseRepriseProps) {
  const { supplier: contextSupplier } = useSupplier();
  const { id } = useParams();
  // const { user } = useUser();
  const location = useLocation();
  const [loading, setLoading] = useState(false);
  const [suppliers, setSuppliers] = useState<propsSupplier | null>(null);

  // 🧠 Prioridade: prop > state da rota > contexto
  const supplier = propSupplier || location.state?.supplier || contextSupplier;


  const firstLetter = supplier?.tradeName?.charAt(0) || "";
  const lastLetter = supplier?.tradeName?.slice(-1) || "";

  // const isSupplierResponsible = user?.role === "ROLE_SUPPLIER_RESPONSIBLE";

  console.log("teste id", id);


  const fetchSuppliers = async () => {
    setLoading(true);
    try {
      const token = localStorage.getItem("tokenClient");
      if (!token) {
        console.error("Token não encontrado.");
        setLoading(false);
        return;
      }

      const response = await axios.get(`${ip}/supplier/${id}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      console.log(response.data);

      setSuppliers(response.data);

    } catch (error) {
      console.error("Erro ao buscar fornecedores:", error);
      setSuppliers(null);
    } finally {
      setLoading(false);
    }
  };

  console.log(suppliers);


  useEffect(() => {
    fetchSuppliers()
  }, []);

  return (
    <>
      <Helmet title="profile" />
      <section className="mx-4 flex flex-col md:mx-8 lg:mx-20">
        <div className="dark:bg-primary mb-10 rounded-t-xl bg-white">
          <div className="bg-realizaBlue min-h-[100px] w-full rounded-t-xl" />
          <div className="shadow-custom-blue relative flex w-full flex-col px-4 pb-10 sm:px-6 md:px-8 lg:px-12">
            <div className="flex flex-col items-center justify-between md:flex-row">
              <div className="relative bottom-10 left-1 flex items-center gap-4">
                <div className="bg-realizaBlue flex h-[16vh] w-[8vw] items-center justify-center rounded-full p-7">
                  <div className="text-[40px] text-white">
                    {firstLetter}
                    {lastLetter}
                  </div>
                </div>
                <div className="relative top-5 flex flex-col gap-2 md:gap-5 dark:text-white">
                  { loading ? "Carregando..." : <span>{suppliers?.corporateName}</span> }
                  {suppliers ? <div>{suppliers.tradeName}</div> : <div></div>}
                </div>
              </div>
              <div className="flex space-x-4">
                <UploadDocumentButton />
                <EditModalEnterprise />
              </div>
            </div>
          </div>
        </div>

        <div className="flex flex-col gap-8 lg:flex-row">
          <div className="shadow-custom-blue mb-10 flex flex-1 flex-col gap-6 bg-white px-4 py-5 md:px-6 md:py-6">
            <h2 className="text-lg font-medium">Formas de contato</h2>
            <div className="flex flex-col gap-4">
              <div className="flex flex-row items-center gap-2">
                <Mail />
                <span>E-mail: {suppliers?.email || "Não disponível"}</span>
              </div>
              <div className="flex flex-row items-center gap-2">
                <Phone />
                <span className="flex items-center gap-2">
                  <span>CNPJ: </span>
                  <span>{suppliers?.cnpj || "Não disponível"}</span>
                </span>
              </div>
              <div className="flex flex-row items-center gap-2">
                <Locate />
                <span className="flex items-center gap-2">
                  <span>Cep: </span>
                  <span>{suppliers?.cep || "Não disponível"}</span>
                </span>
              </div>
            </div>
            <span>Outras formas de contato</span>
          </div>
        </div>
      </section>
    </>
  );
}
