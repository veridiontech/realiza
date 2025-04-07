import { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { NotebookPen, Users } from "lucide-react";

import { Table } from "@/components/ui/tableVanila";
import { Pagination } from "@/components/ui/pagination";
import { SupplierAddContract } from "@/components/supplierAddContract";
import { useUser } from "@/context/user-provider";
import { Contract } from "@/types/contracts";
import { ip } from "@/utils/ip";
import { ModalTesteSendSupplier } from "@/components/client-add-supplier";
import { useSupplier } from "@/context/Supplier-context";

export default function SupplierContracts() {
  const navigate = useNavigate();

  const itemsPerPage = 10;

  const [activeTab, setActiveTab] = useState<"client" | "supplier">("client");

  const [clientContracts, setClientContracts] = useState<Contract[]>([]);
  const [clientTotalPages, setClientTotalPages] = useState<number>(0);
  const [clientLoading, setClientLoading] = useState<boolean>(false);
  const [clientError, setClientError] = useState<string | null>(null);
  const [clientCurrentPage, setClientCurrentPage] = useState<number>(0);

  /** Estados para contratos do fornecedor/quarteirizado */
  const [supplierContracts, setSupplierContracts] = useState<Contract[]>([]);
  const [supplierTotalPages, setSupplierTotalPages] = useState<number>(0);
  const [supplierLoading, setSupplierLoading] = useState<boolean>(false);
  const [supplierError, setSupplierError] = useState<string | null>(null);
  const [supplierCurrentPage, setSupplierCurrentPage] = useState<number>(0);
  const {supplier} = useSupplier()

  // Colunas comuns para ambas as tabelas
  const columns = [
    { key: "serviceName" as keyof Contract, label: "Serviço" },
    { key: "startDate" as keyof Contract, label: "Data de Início" },
    { key: "endDate" as keyof Contract, label: "Data de Fim" },
    {
      key: "id" as keyof Contract,
      label: "Ações",
      render: (_: any, row: Contract) => (
        <div className="flex items-center space-x-2">
          <button
            onClick={() => console.log("Contrato Selecionado:", row)}
            className="text-realizaBlue hover:underline"
          >
            <NotebookPen />
          </button>
          <button
            onClick={() => {
              console.log("ID do contrato antes da navegação:", row.id);
              if (row.id) {
                navigate(`/sistema/employee-to-contract/${row.id}`);
              } else {
                console.error("ID do contrato não encontrado!", row);
              }
            }}
            className="text-green-500 hover:underline"
          >
            <Users />
          </button>
        </div>
      ),
    },
  ];

  // Chamada para buscar os contratos filtrados por cliente (rota: filtered-client)
  useEffect(() => {
    if (activeTab === "supplier" && supplier?.idProvider) {
      setClientLoading(true);
      axios
        .get(`${ip}/contract/supplier/filtered-client`, {
          params: {
            page: clientCurrentPage,
            size: itemsPerPage,
            sort: "idContract",
            direction: "ASC",
            idSearch: supplier.idProvider,
          },
          headers: {
            Authorization: `Bearer ${localStorage.getItem("tokenClient")}`,
          },
        })
        .then((response) => {
          // Supõe-se que o retorno seja um objeto Page com "content" e "totalPages"
          setClientContracts(response.data.content);
          setClientTotalPages(response.data.totalPages);
          setClientLoading(false);
        })
        .catch((error) => {
          setClientError(error.message);
          setClientLoading(false);
        });
    }
  }, [activeTab, supplier?.idProvider, clientCurrentPage]);

  // Chamada para buscar os contratos filtrados por fornecedor (rota: filtered-supplier)
  useEffect(() => {
    if (activeTab === "client" && supplier?.idProvider) {
      setSupplierLoading(true);
      axios
        .get(`${ip}/contract/supplier/filtered-supplier`, {
          params: {
            page: supplierCurrentPage,
            size: itemsPerPage,
            sort: "idContract",
            direction: "ASC",
            idSearch: supplier?.idProvider,
          },
          headers: {
            Authorization: `Bearer ${localStorage.getItem("tokenClient")}`,
          },
        })
        .then((response) => {
          console.log("log dos contratos:", response.data.content);
          
          setSupplierContracts(response.data.content);
          setSupplierTotalPages(response.data.totalPages);
          setSupplierLoading(false);
        })
        .catch((error) => {
          setSupplierError(error.message);
          setSupplierLoading(false);
        });
    }
  }, [activeTab, supplier?.idProvider, supplierCurrentPage]);

  return (
    <div className="m-10 flex min-h-full justify-center">
      <div className="dark:bg-primary flex h-full w-[90rem] flex-col rounded-lg bg-white">
        {/* Cabeçalho com o título e botão para adicionar contrato */}
        <div className="m-8 flex items-center justify-between">
          <h1 className="text-xl font-semibold">Tabela de Contratos</h1>
          {/* <SupplierAddContract /> */}
          <ModalTesteSendSupplier />
        </div>

        {/* Navegação por abas */}
        <div className="mx-8 mb-4">
          <div className="flex border-b">
            <button
              className={`mr-4 pb-2 ${
                activeTab === "client"
                  ? "border-realizaBlue border-b-2 font-semibold"
                  : "text-gray-500"
              }`}
              onClick={() => setActiveTab("client")}
            >
              Contratos com Clientes
            </button>
            <button
              className={`pb-2 ${
                activeTab === "supplier"
                  ? "border-realizaBlue border-b-2 font-semibold"
                  : "text-gray-500"
              }`}
              onClick={() => setActiveTab("supplier")}
            >
              Contratos com SubContratados
            </button>
          </div>
        </div>

        {/* Conteúdo da aba selecionada */}
        {activeTab === "client" && (
          <div className="mx-8">
            {clientError ? (
              <p className="text-center text-red-600">
                Erro ao carregar os dados: {clientError}
              </p>
            ) : clientLoading ? (
              <p className="text-center">Carregando contratos...</p>
            ) : clientContracts.length > 0 ? (
              <Table<Contract> data={clientContracts} columns={columns} />
            ) : (
              <p className="text-center text-gray-500">
                Nenhum contrato disponível.
              </p>
            )}

            <Pagination
              currentPage={clientCurrentPage}
              totalPages={clientTotalPages}
              onPageChange={(newPage) => setClientCurrentPage(newPage)}
            />
          </div>
        )}

        {activeTab === "supplier" && (
          <div className="mx-8">
            {supplierError ? (
              <p className="text-center text-red-600">
                Erro ao carregar os dados: {supplierError}
              </p>
            ) : supplierLoading ? (
              <p className="text-center">Carregando contratos...</p>
            ) : supplierContracts.length > 0 ? (
              <Table<Contract> data={supplierContracts} columns={columns} />
            ) : (
              <p className="text-center text-gray-500">
                Nenhum contrato disponível.
              </p>
            )}

            <Pagination
              currentPage={supplierCurrentPage}
              totalPages={supplierTotalPages}
              onPageChange={(newPage) => setSupplierCurrentPage(newPage)}
            />
          </div>
        )}
      </div>
    </div>
  );
}
