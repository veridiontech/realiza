import { useState, useEffect } from "react";
import { Pagination } from "@/components/ui/pagination";
import { useFetchServiceProviders } from "@/hooks/gets/realiza/useServiceProviders";
// import { ModalTesteSendSupplier } from "@/components/realiza-add-supplier";
import { useClient } from "@/context/Client-Provider";
import { TableServiceProvider } from "./tableServiceProvider";
// import { ModalAddContract } from "@/components/realizaAddContract";
import { ModalTesteSendSupplier } from "@/components/client-add-supplier";

export function ServiceProvider() {
  const { client } = useClient();
  // console.log(client);

  const itemsPerPage = 5;

  const {
    totalPages = 0,

    fetchServiceProviders,
  } = useFetchServiceProviders();

  const [currentPage, setCurrentPage] = useState(0);
  // const [isStepTwoModalOpen, setIsStepTwoModalOpen] = useState(false);
  // const [providerData] = useState<Record<string, any> | null>(null);

  useEffect(() => {
    fetchServiceProviders(itemsPerPage, currentPage, client?.idClient);
  }, [currentPage, client?.idClient]);

  const handlePageChange = (newPage: number) => {
    setCurrentPage(newPage);
  };

  return (
    <div className="m-10 flex min-h-full justify-center ">
      <div className="dark:bg-primary flex h-full w-[90rem] flex-col rounded-lg ">
        <div className="flex items-center md:justify-between justify-around">
          <h1 className="hidden md:block m-8 text-2xl">Prestadores de Serviço</h1>
          <h1 className="md:hidden m-8 text-2xl">Prestadores</h1>
          <ModalTesteSendSupplier />
        </div>

        {/* <div className="flex w-[90rem] flex-row justify-between px-10">
          <ModalTesteSendSupplier />
        </div> */}

        <TableServiceProvider />

        <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          onPageChange={handlePageChange}
        />
      </div>
    </div>
  );
}
