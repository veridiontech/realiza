import { Mail, Phone } from "lucide-react";
import { Helmet } from "react-helmet-async";
// import { useState } from "react";
import { EditModalEnterprise } from "./edit-modal-enterprise";
import { useClient } from "@/context/Client-Provider";
import { Skeleton } from "@/components/ui/skeleton";
export function ProfileEnterpriseReprise() {
  // const [isOpen, setIsOpen] = useState(false);
  const { client } = useClient();

  console.log(client);

  return (
    <>
      <Helmet title="profile" />
      <section className="mx-4 flex flex-col md:mx-8 lg:mx-20">
        <div className="dark:bg-primary mb-10 rounded-t-xl bg-white">
          <div className="min-h-[220px] w-full rounded-t-xl bg-blue-800" />
          <div className="shadow-custom-blue relative flex w-full flex-col px-4 pb-10 sm:px-6 md:px-8 lg:px-12">
            <div className="flex flex-col items-center justify-between md:flex-row">
              <div className="relative bottom-10 left-1 flex items-center gap-4">
                <div className="bg-realizaBlue flex h-[16vh] w-[8vw] items-center justify-center rounded-full">
                  <h1 className="text-white">logo</h1>
                </div>
                <div className="relative top-5 flex flex-col gap-2 md:gap-5 dark:text-white">
                  <div className="text-lg font-medium text-sky-800">
                    {client ? (
                      <h2>{client.companyName}</h2>
                    ) : (
                      <span className="font-normal">
                        Nenhum cliente selecionado
                      </span>
                    )}
                  </div>
                  <div className="flex items-center gap-2">
                    <div>
                      {client ? (
                        <span className="text-xs font-medium text-sky-700">
                          {client?.tradeName} / {client?.cnpj}
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
                <span>E-mail: {client?.email}</span>
              </div>
              <div className="flex flex-row items-center gap-2">
                <Phone />
                <span className="flex items-center gap-2">
                  <span>Telefone: </span>
                  <span>{client?.telephone}</span>
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
