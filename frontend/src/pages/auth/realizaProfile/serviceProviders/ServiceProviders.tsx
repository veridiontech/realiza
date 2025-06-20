import { useState, useEffect } from "react";
import { Pagination } from "@/components/ui/pagination";
import { useFetchServiceProviders } from "@/hooks/gets/realiza/useServiceProviders";
// import { ModalTesteSendSupplier } from "@/components/realiza-add-supplier";
import { useClient } from "@/context/Client-Provider";
import { TableServiceProvider } from "./tableServiceProvider";
// import { ModalAddContract } from "@/components/realizaAddContract";

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
    <div className="flex min-h-full justify-center relative bottom-[7vw]">
      <div className="dark:bg-primary w-full flex flex-col rounded-lg px-4">
        <div className="flex items-center md:justify-between justify-around">
          <h1 className="hidden md:block m-8 text-2xl">Contratos</h1>
          <h1 className="md:hidden m-8 text-2xl">Prestadores</h1>
        </div>
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
