import { Locate, Mail, Phone } from "lucide-react";
import { Helmet } from "react-helmet-async";
import { EditModalEnterprise } from "./edit-modal-enterprise";
import { Skeleton } from "@/components/ui/skeleton";
import { useSupplier } from "@/context/Supplier-context";
import { useClient } from "@/context/Client-Provider";


export function ProfileBranch() {

    const { client } = useClient();
    const { supplier } = useSupplier();
  
    const firstLetter = client?.tradeName?.charAt(0) || "";
    const lastLetter = client?.tradeName?.slice(-1) || "";
  

    return(
        <>
        <Helmet title="profile" />
        <section className="mx-4 flex flex-col md:mx-8 lg:mx-20">
          <div className="dark:bg-primary mb-10 rounded-t-xl bg-white">
            <div className="bg-realizaBlue min-h-[220px] w-full rounded-t-xl" />
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
                    <div className="text-lg font-medium text-sky-800">
                      {supplier?.tradeName ? (
                        <h2>{supplier.tradeName}</h2>
                      ) : (
                        <span className="font-normal">
                          Nenhum fornecedor selecionado
                        </span>
                      )}
                    </div>
                    <div className="flex items-center gap-2">
                      <div>
                        {supplier?.tradeName ? (
                          <span className="text-xs font-medium text-sky-700">
                            {supplier.tradeName} / {supplier?.cnpj}
                          </span>
                        ) : (
                          <div className="flex items-center gap-2">
                            <Skeleton className="h-[1vh] w-[4vw]" />
                            <span className="text-sky-700"> / </span>
                            <Skeleton className="h-[1vh] w-[4vw]" />
                          </div>
                        )}
                      </div>
                    </div>
                  </div>
                </div>
                <div>
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
                  <span>E-mail: {supplier?.email || "Não disponível"}</span>
                </div>
                <div className="flex flex-row items-center gap-2">
                    <Locate/>
                  <span className="flex items-center gap-2">
                    <span>Cep: </span>
                    <span>{supplier?.cep || "Não disponível"}</span>
                  </span>
                </div>
              </div>
              <span>Outras formas de contato</span>
            </div>
          </div>
        </section>
      </>
    );
};