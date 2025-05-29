import { Mail, Phone } from "lucide-react";
import { Helmet } from "react-helmet-async";
import { EditModalEnterprise } from "./edit-modal-enterprise";
// import { useClient } from "@/context/Client-Provider";
// import { Skeleton } from "@/components/ui/skeleton";
import { UploadDocumentButton } from "@/components/ui/upload-document-button";
// import { useUser } from "@/context/user-provider"
import { useState } from "react";
import { useClient } from "@/context/Client-Provider";

export function ProfileEnterpriseReprise() {
  // const { user } = useUser();
  const [loading,] = useState(false);
  const { client } = useClient();

  // 🧠 Prioridade: prop > state da rota > contexto
  const firstLetter = client?.corporateName?.charAt(0) || "";
  const lastLetter = client?.corporateName?.slice(-1) || "";

  // const isclientResponsible = user?.role === "ROLE_client_RESPONSIBLE";


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
                  { loading ? "Carregando..." : <span>{client?.corporateName}</span> }
                  {client ? <div>{client.tradeName}</div> : <div></div>}
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
                <span>E-mail: {client?.email || "Não disponível"}</span>
              </div>
              <div className="flex flex-row items-center gap-2">
                <Phone />
                <span className="flex items-center gap-2">
                  <span>CNPJ: </span>
                  <span>{client?.cnpj || "Não disponível"}</span>
                </span>
              </div>
              {/* <div className="flex flex-row items-center gap-2">
                <Locate />
                <span className="flex items-center gap-2">
                  <span>Cep: </span>
                  <span>{client?.cep || "Não disponível"}</span>
                </span>
              </div> */}
            </div>
            <span>Outras formas de contato</span>
          </div>
        </div>
      </section>
    </>
  );
}
