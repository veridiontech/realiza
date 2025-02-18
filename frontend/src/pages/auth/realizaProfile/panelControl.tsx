import { useState, useEffect } from "react";
import axios from "axios";
import { CardPanelControl } from "@/components/cardPanelControl";
import {
  ChevronLeft,
  ChevronRight,
  ClipboardList,
  Mail,
  PencilLine,
  TriangleAlert,
} from "lucide-react";
import { Link } from "react-router-dom";
import { ip } from "@/utils/ip";

export function ControlPanel() {
  // Estado para armazenar os itens inativos vindos da API
  const [inactiveItems, setInactiveItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Lógica de paginação
  const itemsPerPage = 12;
  const [currentPage, setCurrentPage] = useState(1);

  useEffect(() => {
    // Faz a requisição para a rota de itens inativos usando axios
    axios
      .get(`${ip}/item-management/innactive-items`)
      .then((res) => {
        setInactiveItems(res.data);
        setLoading(false);
      })
      .catch((err) => {
        setError(err);
        setLoading(false);
      });
  }, []);

  if (loading) return <div>Carregando...</div>;
  if (error) return <div>Erro ao carregar os itens inativos.</div>;

  const totalPages = Math.ceil(inactiveItems.length / itemsPerPage);
  const startIndex = (currentPage - 1) * itemsPerPage;
  const currentItems = inactiveItems.slice(
    startIndex,
    startIndex + itemsPerPage,
  );

  const goToNextPage = () => {
    if (currentPage < totalPages) setCurrentPage(currentPage + 1);
  };

  const goToPreviousPage = () => {
    if (currentPage > 1) setCurrentPage(currentPage - 1);
  };

  return (
    <div className="flex h-full w-full flex-col items-center justify-center gap-9 p-4">
      <div className="flex w-full flex-col items-center justify-center gap-9 rounded-md bg-white p-4 shadow-sm">
        <div className="flex w-full flex-row items-center justify-start gap-4">
          <h2 className="text-center text-lg font-semibold">
            Painel de Controle
          </h2>
          <p className="text-[#2563EB]">{inactiveItems.length} Solicitações</p>
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

      <div className="flex h-full w-full flex-col gap-6 rounded-md bg-white p-4 shadow-sm">
        <div className="grid grid-cols-1 gap-5 rounded-md p-4 shadow-sm sm:grid-cols-2 lg:grid-cols-4">
          {currentItems.map((item, index) => (
            // Usamos um id único conforme as propriedades disponíveis (idBranch, idClient, idProvider ou idUser)
            <CardPanelControl
              key={
                item.idBranch ||
                item.idClient ||
                item.idProvider ||
                item.idUser ||
                index
              }
              data={item}
            />
          ))}
        </div>

        <div className="flex w-full flex-row items-center justify-between gap-4 px-4">
          <span className="text-[#7CA1F3]">
            Página {currentPage} de {totalPages}
          </span>
          <div className="flex flex-row items-center justify-center gap-2">
            <button
              onClick={goToPreviousPage}
              disabled={currentPage === 1}
              className="rounded border-[2px] border-[#2563EB] bg-white px-1 py-1 disabled:opacity-50"
            >
              <ChevronLeft color="#2563EB" />
            </button>
            <button
              onClick={goToNextPage}
              disabled={currentPage === totalPages}
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
