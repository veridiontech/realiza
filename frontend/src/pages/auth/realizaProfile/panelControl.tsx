import { useState } from "react";
import { CardPanelControl } from "@/components/cardPanelControl";
import { ChevronLeft, ChevronRight, ClipboardList, Mail, PencilLine, TriangleAlert } from "lucide-react";
import { Link } from "react-router-dom";

interface Solicitacao {
    nome: string;
    id: number;
    tipo: string;
    detalhes: string;
    data: string;
}

export function ControlPanel() {
    const tipos = [
        "Consulta",
        "Suporte",
        "Atualização",
        "Manutenção",
        "Instalação",
        "Orçamento",
        "Reparação",
        "Feedback",
    ];
    const solicitacoes: Solicitacao[] = Array.from({ length: 40 }, (_, index) => {
        const id = index + 1;
        return {
            id,
            nome: `Usuário ${id}`,
            tipo: tipos[index % tipos.length],
            detalhes: `Detalhes da solicitação ${id}`,
            data: `2025-03-${((id % 28) + 1).toString().padStart(2, "0")}`,
        };
    });

    const itemsPerPage = 12;
    const [currentPage, setCurrentPage] = useState(1);

    const totalPages = Math.ceil(solicitacoes.length / itemsPerPage);

    const startIndex = (currentPage - 1) * itemsPerPage;
    const currentSolicitacoes = solicitacoes.slice(startIndex, startIndex + itemsPerPage);

    const goToNextPage = () => {
        if (currentPage < totalPages) setCurrentPage(currentPage + 1);
    };

    const goToPreviousPage = () => {
        if (currentPage > 1) setCurrentPage(currentPage - 1);
    };

    return (
        <div className="w-full h-full flex flex-col items-center justify-center p-4 gap-9">
            <div className="w-full bg-white flex flex-col items-center justify-center p-4 rounded-md shadow-sm gap-9">
                <div className="w-full flex flex-row items-center justify-start gap-4">
                    <h2 className="text-lg font-semibold text-center">Painel de Controle</h2>
                    <p className="text-[#2563EB]">X Solicitações </p>
                </div>

                <div className="w-full flex flex-row items-center justify-center flex-wrap gap-4 border-t border-[#7CA1F333] pt-7 pb-4">
                    <Link to={"#"} className="rounded-md bg-[#7CA1F333] px-6 py-2 text-[#7CA1F3] flex flex-row gap-4">
                        Denúncia e desligamento <TriangleAlert />
                    </Link>
                    <Link to={"#"} className="rounded-md bg-[#7CA1F333] px-6 py-2 text-[#7CA1F3] flex flex-row gap-4">
                        Alteração Cadastral <PencilLine />
                    </Link>
                    <Link to={"#"} className="rounded-md bg-[#7CA1F333] px-6 py-2 text-[#7CA1F3] flex flex-row gap-4">
                        Solicitações de cadastro <Mail />
                    </Link>
                    <Link to={"#"} className="rounded-md bg-[#7CA1F333] px-6 py-2 text-[#7CA1F3] flex flex-row gap-4">
                        Todas as solicitações <ClipboardList />
                    </Link>
                </div>
            </div>
            <div className="w-full h-full bg-white flex flex-col p-4 rounded-md shadow-sm gap-6">

                <div className=" grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5 p-4 rounded-md shadow-sm">
                    {currentSolicitacoes.map((solicitacao) => (
                        <CardPanelControl key={solicitacao.id} data={solicitacao} />
                    ))}

                </div>
                    <div className="w-full flex flex-row items-center justify-between gap-4 px-4">
                        <span className="text-[#7CA1F3]">
                            Página {currentPage} de {totalPages}
                        </span>
                        <div className="flex flex-row items-center justify-center gap-2">
                            <button
                                onClick={goToPreviousPage}
                                disabled={currentPage === 1}
                                className="px-1 py-1 bg-white  border-[2px] border-[#2563EB] rounded disabled:opacity-50"
                            >
                                <ChevronLeft color="#2563EB" />
                            </button>
                            <button
                                onClick={goToNextPage}
                                disabled={currentPage === totalPages}
                                className="px-1 py-1 bg-[#2563EB] border-[2px] border-[#2563EB] rounded disabled:opacity-50"
                            >
                                <ChevronRight color="#fff" />

                            </button>
                        </div>

                    </div>
            </div>

        </div>
    );
}
