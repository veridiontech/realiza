import { useState, useEffect } from "react";
import { useFetchServiceProviders } from "@/hooks/gets/realiza/useServiceProviders";
// import { ModalTesteSendSupplier } from "@/components/realiza-add-supplier";
import { useClient } from "@/context/Client-Provider";
import { TableServiceProvider } from "./tableServiceProvider";
import { ScrollText } from "lucide-react";
// import { ModalAddContract } from "@/components/realizaAddContract";

export function ServiceProvider() {
  const { client } = useClient();
  // console.log(client);

  const itemsPerPage = 5;

  const {

    fetchServiceProviders,
  } = useFetchServiceProviders();

  const [currentPage, ] = useState(0);
  // const [isStepTwoModalOpen, setIsStepTwoModalOpen] = useState(false);
  // const [providerData] = useState<Record<string, any> | null>(null);

  useEffect(() => {
    fetchServiceProviders(itemsPerPage, currentPage, client?.idClient);
  }, [currentPage, client?.idClient]);

  return (
    <div className="flex min-h-full justify-center relative bottom-[10vw] p-10">
      <div className="dark:bg-primary w-full flex flex-col rounded-lg px-4">
        <div className="flex items-center md:justify-between justify-around">
          <h1 className=" text-2xl text-white font-semibold flex items-center gap-2">
            <ScrollText className="text-[#C0B15B]" />
            Todos os Contratos
          </h1>
          <h1 className="md:hidden m-8 text-2xl">Prestadores</h1>
        </div>
        <TableServiceProvider />
      </div>
    </div>
  );
}
