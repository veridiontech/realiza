import { useState, useEffect } from "react";
import { useParams } from "react-router-dom"; // Importa o hook para capturar o ID da URL
import { Table } from "@/components/ui/table";
import { Pagination } from "@/components/ui/pagination";
import { Pendencia } from "@/types/pendencia";
import { ButtonBlue } from "@/components/ui/buttonBlue";
import { AddDocument } from "./modals/addDocument";
import { ip } from "@/utils/ip";

interface Employee {
  id: number;
  name: string;
  status: string;
}

export function DetailsEmployee() {
  const { id } = useParams<{ id: string }>(); // Obtém o ID da URL
  const [employee, setEmployee] = useState<Employee | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const allPendencias: Pendencia[] = [
    {
      pendencia: "Atualização de cadastros",
      tipoServico: "Atualização de cadastros",
      dataProposta: "20/07/24",
      dataVencimento: "23/10/24",
      status: "Concluído",
    },
    {
      pendencia: "Revisão de documentos",
      tipoServico: "Revisão",
      dataProposta: "10/08/24",
      dataVencimento: "15/10/24",
      status: "Pendente",
    },
  ];

  const rowsPerPage = 2;
  const totalPages = Math.ceil(allPendencias.length / rowsPerPage);
  const currentData = allPendencias.slice(
    (currentPage - 1) * rowsPerPage,
    currentPage * rowsPerPage,
  );

  const pendenciasColumns: {
    key: keyof Pendencia;
    label: string;
    className?: string;
    render?: (value: string, row: Pendencia) => React.ReactNode;
  }[] = [
    { key: "pendencia", label: "Pendência" },
    { key: "tipoServico", label: "Tipo de serviço" },
    { key: "dataProposta", label: "Data proposta" },
    { key: "dataVencimento", label: "Data de vencimento" },
    { key: "status", label: "Status" },
  ];

  useEffect(() => {
    const fetchEmployee = async () => {
      setIsLoading(true);
      setError(null);

      try {
        // Faz a requisição usando o ID do funcionário
        const response = await fetch(`${ip}/employee/brazilian/${id}`);
        if (!response.ok) {
          throw new Error("Erro ao carregar os detalhes do funcionário");
        }

        const data = await response.json();
        setEmployee({
          id: data.id,
          name: data.name,
          status: data.status || "Ativo",
        });
      } catch (err) {
        setError((err as Error).message);
      } finally {
        setIsLoading(false);
      }
    };

    if (id) fetchEmployee();
  }, [id]);

  if (isLoading) {
    return <p>Carregando...</p>;
  }

  if (error) {
    return <p className="text-red-500">Erro: {error}</p>;
  }

  if (!employee) {
    return <p>Funcionário não encontrado.</p>;
  }

  return (
    <div className="flex h-full w-full flex-col bg-gray-100 p-10">
      <div className="mb-6 flex items-center">
        <h1 className="text-xl font-bold">Detalhes do Funcionário</h1>
      </div>
      <div className="flex gap-6">
        <div className="flex w-2/3 flex-col gap-6">
          <div className="rounded-lg bg-white p-6 shadow">
            <div className="mb-4 flex flex-row items-center justify-between">
              <h2 className="text-lg font-semibold">Pendências</h2>
              <ButtonBlue onClick={() => setIsModalOpen(true)}>
                Adicionar Documento
              </ButtonBlue>
            </div>
            <Table data={currentData} columns={pendenciasColumns} />
            <Pagination
              currentPage={currentPage}
              totalPages={totalPages}
              onPageChange={(page) => setCurrentPage(page)}
            />
          </div>
        </div>
        <div className="w-1/3">
          <div className="rounded-lg bg-white p-6 shadow">
            <div className="mb-4 flex flex-col items-center">
              <div className="mb-4 h-20 w-20 rounded-full bg-blue-100"></div>
              <h3 className="text-lg font-medium">{employee.name}</h3>
              <p className="text-sm text-gray-500">Status: {employee.status}</p>
            </div>
            <button className="w-full rounded bg-red-600 py-2 text-white hover:bg-red-700">
              Desligar colaborador
            </button>
          </div>
        </div>
      </div>

      {isModalOpen && <AddDocument onClose={() => setIsModalOpen(false)} />}
    </div>
  );
}
