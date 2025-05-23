import { useState, useEffect } from "react";
import { Pagination } from "@/components/ui/pagination";
import { useFetchServiceProviders } from "@/hooks/gets/realiza/useServiceProviders";
// import { ModalTesteSendSupplier } from "@/components/realiza-add-supplier";
import { useClient } from "@/context/Client-Provider";
// import { ModalAddContract } from "@/components/realizaAddContract";
import { TableProviders } from "./tableProviders";

export function FornecedoresPage() {
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
          <h1 className="hidden md:block m-8 text-2xl">Fornecedores</h1>
        </div>


        <TableProviders />

        <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          onPageChange={handlePageChange}
        />
      </div>
    </div>
  );
}
