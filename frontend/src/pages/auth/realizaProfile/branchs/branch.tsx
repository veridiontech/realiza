import { useEffect, useState } from "react";
import { SearchCnpjModal } from "./modals/newBranchStepOne";
import { NewBranchModal } from "./modals/newBranchStepTwo";
import { ButtonBlue } from "@/components/ui/buttonBlue";
import { Pagination } from "@/components/ui/pagination";
import { Table } from "@/components/ui/tableVanila";
import axios from "axios";
import { Puff } from "react-loader-spinner";
import { ip } from "@/utils/ip";
import { useClient } from "@/context/Client-Provider";

interface BranchType {
  idBranch: string;
  name: string; // Atualizado para refletir o campo "name"
  cnpj: string;
  address: string;
}

const columns: {
  key: keyof BranchType;
  label: string;
  render?: (value: any, row: BranchType) => JSX.Element;
}[] = [
  { key: "name", label: "Nome da Filial" }, // Alterado para "name"
  { key: "cnpj", label: "CNPJ" },
  { key: "address", label: "Endereço" },
];

export function Branch() {
  const [branches, setBranches] = useState<BranchType[]>([]);
  const [totalPages, setTotalPages] = useState(1);
  const [currentPage, setCurrentPage] = useState(1);
  const [isSearchModalOpen, setIsSearchModalOpen] = useState(false);
  const [isNewBranchModalOpen, setIsNewBranchModalOpen] = useState(false);
  const [selectedCnpj, setSelectedCnpj] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const {client} = useClient()

  //   const itemsPerPage = 10;


  const fetchBranches = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await axios.get(`${ip}/branch/filtered-client?idSearch=${client?.idClient}`);
      const { content, totalPages: total } = response.data;
      setBranches(content);
      setTotalPages(total);
      console.log(content);
    } catch (err) {
      console.error("Erro ao buscar filiais:", err);
      setError("Erro ao buscar filiais. Tente novamente.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (client?.idClient) {
      setBranches([]); 
      setCurrentPage(1); 
      fetchBranches(); 
    }
  }, [client?.idClient]); 
  const handlePageChange = (page: number) => {
    if (page >= 1 && page <= totalPages) {
      setCurrentPage(page);
    }
  };

  return (
    <div>
      <div className="m-4 flex justify-center">
        <div className="flex w-[90rem] flex-col rounded-lg bg-white p-4 shadow-md">
          <div className="mb-6 flex items-center justify-between">
            <h1 className="m-8 text-2xl">Filiais</h1>
            <ButtonBlue onClick={() => setIsSearchModalOpen(true)}>
              Adicionar Filial
            </ButtonBlue>
          </div>
          {loading ? (
            <div className="flex w-[20vw] items-center justify-start rounded-md border p-2 dark:bg-white">
              <Puff
                visible={true}
                height="30"
                width="30"
                color="#34495D"
                ariaLabel="puff-loading"
              />
              <span className="ml-2 text-black">Carregando...</span>
            </div>
          ) : error ? (
            <div className="text-center text-red-500">{error}</div>
          ) : (
            <Table<BranchType> data={branches} columns={columns} />
          )}
          <Pagination
            currentPage={currentPage}
            totalPages={totalPages}
            onPageChange={handlePageChange}
          />
        </div>
      </div>

      {isSearchModalOpen && (
        <SearchCnpjModal
          onClose={() => setIsSearchModalOpen(false)}
          onProceed={(cnpj) => {
            setSelectedCnpj(cnpj);
            setIsSearchModalOpen(false);
            setIsNewBranchModalOpen(true);
          }}
        />
      )}

      {isNewBranchModalOpen && selectedCnpj && (
        <NewBranchModal
          onClose={() => setIsNewBranchModalOpen(false)}
          onSubmit={(data) => {
            console.log("Dados enviados:", data);
            setIsNewBranchModalOpen(false);
          }}
        />
      )}
    </div>
  );
}
