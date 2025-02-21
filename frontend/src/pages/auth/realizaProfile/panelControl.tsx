import { useState, useEffect } from "react";
import axios from "axios";
import { Link } from "react-router-dom";
import {
  ChevronLeft,
  ChevronRight,
  ClipboardList,
  Mail,
  PencilLine,
  TriangleAlert,
} from "lucide-react";
import { ip } from "@/utils/ip";
import { CardPanelControl } from "@/components/cardPanelControl";

interface Requester {
  idUser: string;
  firstName: string;
  surname: string;
}

export interface Solicitation {
  idSolicitation: string;
  title: string;
  details: string;
  creationDate: string;
  requester: Requester;
  newUser: {
    idUser: string;
  };
}

interface ApiResponse {
  content: Solicitation[];
  totalPages: number;
  // outros campos da paginação se necessário
}

export function ControlPanel() {
  const [solicitations, setSolicitations] = useState<Solicitation[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);

  // Configurações de paginação
  const itemsPerPage = 12;
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);

  const fetchSolicitations = async (pageNumber: number) => {
    setLoading(true);
    try {
      const response = await axios.get<ApiResponse>(
        `${ip}/item-management/new`,
        {
          params: {
            page: pageNumber,
            size: itemsPerPage,
            sort: "idSolicitation",
            direction: "ASC",
          },
        },
      );
      setSolicitations(response.data.content);
      setTotalPages(response.data.totalPages);
    } catch (err: any) {
      setError(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSolicitations(page);
  }, [page]);

  const goToNextPage = () => {
    if (page < totalPages - 1) {
      setPage(page + 1);
    }
  };

  const goToPreviousPage = () => {
    if (page > 0) {
      setPage(page - 1);
    }
  };

  // Callback para remover o item aprovado ou negado da lista
  const removeSolicitation = (idSolicitation: string) => {
    setSolicitations((prev) =>
      prev.filter((s) => s.idSolicitation !== idSolicitation),
    );
  };

  if (loading) return <div>Carregando...</div>;
  if (error) return <div>Erro ao carregar as solicitações.</div>;

  return (
    <div className="flex h-full w-full flex-col items-center justify-center gap-9 p-4">
      {/* Cabeçalho */}
      <div className="flex w-full flex-col items-center justify-center gap-9 rounded-md bg-white p-4 shadow-sm">
        <div className="flex w-full flex-row items-center justify-start gap-4">
          <h2 className="text-center text-lg font-semibold">
            Painel de Controle
          </h2>
          <p className="text-[#2563EB]">{solicitations.length} Solicitações</p>
        </div>
        <div className="flex w-full flex-row flex-wrap items-center justify-center gap-4 border-t border-[#7CA1F333] pb-4 pt-7">
          <Link
            to={"#"}
            className="flex flex-row gap-4 rounded-md bg-[#7CA1F333] px-6 py-2 text-[#7CA1F3]"
          >
            Denúncia e desligamento <TriangleAlert />
          </Link>
          <Link
            to={"#"}
            className="flex flex-row gap-4 rounded-md bg-[#7CA1F333] px-6 py-2 text-[#7CA1F3]"
          >
            Alteração Cadastral <PencilLine />
          </Link>
          <Link
            to={"#"}
            className="flex flex-row gap-4 rounded-md bg-[#7CA1F333] px-6 py-2 text-[#7CA1F3]"
          >
            Solicitações de cadastro <Mail />
          </Link>
          <Link
            to={"#"}
            className="flex flex-row gap-4 rounded-md bg-[#7CA1F333] px-6 py-2 text-[#7CA1F3]"
          >
            Todas as solicitações <ClipboardList />
          </Link>
        </div>
      </div>

      {/* Listagem de Solicitações */}
      <div className="flex h-full w-full flex-col gap-6 rounded-md bg-white p-4 shadow-sm">
        <div className="grid grid-cols-1 gap-5 rounded-md p-4 shadow-sm sm:grid-cols-2 lg:grid-cols-4">
          {solicitations.map((solicitation) => (
            <CardPanelControl
              key={solicitation.idSolicitation}
              data={solicitation}
              onActionCompleted={removeSolicitation}
            />
          ))}
        </div>

        {/* Controles de Paginação */}
        <div className="flex w-full flex-row items-center justify-between gap-4 px-4">
          <span className="text-[#7CA1F3]">
            Página {page + 1} de {totalPages}
          </span>
          <div className="flex flex-row items-center justify-center gap-2">
            <button
              onClick={goToPreviousPage}
              disabled={page === 0}
              className="rounded border-[2px] border-[#2563EB] bg-white px-1 py-1 disabled:opacity-50"
            >
              <ChevronLeft color="#2563EB" />
            </button>
            <button
              onClick={goToNextPage}
              disabled={page === totalPages - 1}
              className="rounded border-[2px] border-[#2563EB] bg-[#2563EB] px-1 py-1 disabled:opacity-50"
            >
              <ChevronRight color="#fff" />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
