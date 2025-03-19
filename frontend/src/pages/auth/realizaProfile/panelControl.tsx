import { useState, useEffect } from "react";
import axios from "axios";
import { Link } from "react-router-dom";
import {
  Ban,
  CheckCircle,
  ChevronLeft,
  ChevronRight,
  ClipboardList,
  Mail,
  PencilLine,
  Rotate3D,
  TriangleAlert,
} from "lucide-react";
import { ip } from "@/utils/ip";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";

import { CardPanelControl } from "@/components/cardPanelControl";
import { Button } from "@/components/ui/button";
import { ColumnPanelControl } from "@/components/column-panel-control";

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

  // if (loading) return <div>Carregando...</div>;
  // if (error) return <div>Erro ao carregar as solicitações.</div>;

  return (
    <div className="flex h-full w-full flex-col items-center justify-center gap-9 p-4">
      {/* Cabeçalho */}
      <div className="flex w-full flex-col items-center justify-center gap-9 rounded-md bg-white p-4 shadow-sm">
        <div className="flex w-full flex-row items-center justify-between gap-4">
          <div>
            <h2 className="text-center text-lg font-semibold">
              Painel de Controle
            </h2>
            <p className="text-[#2563EB]">
              {solicitations.length} Solicitações
            </p>
          </div>
          <Dialog>
            <DialogTrigger asChild>
              <Button className="bg-realizaBlue">Todas solicitações</Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Are you absolutely sure?</DialogTitle>
              </DialogHeader>
            </DialogContent>
          </Dialog>
        </div>
      </div>

      {/* Listagem de Solicitações */}
      <div className="flex h-full w-full flex-col gap-6 rounded-md bg-white p-4 pt-16 shadow-sm">
        <div className="flex items-center justify-around">
          <div>
            <ColumnPanelControl
              lenghtControl="10"
              title="Solicitações pendentes"
              bgColor="bg-[#F9731640]"
              textColor="text-[#F97316]"
              icon={<Rotate3D className="text-[#F97316]" />}
            />
                      <div>
            <div className="bg-gray-100 p-8">
              <div className="w-[20vw]">
                {solicitations.map((solicitation) => (
                  <CardPanelControl
                    key={solicitation.idSolicitation}
                    data={solicitation}
                    onActionCompleted={removeSolicitation}
                  />
                ))}
              </div>
            </div>
          </div>  
          </div>
          <ColumnPanelControl
            lenghtControl="9"
            title="Solicitações Confirmadas"
            bgColor="bg-[#2563EB40]"
            textColor="text-[#2563EB]"
            icon={<CheckCircle className="text-[#2563EB]" />}
          />
          <ColumnPanelControl
            lenghtControl="4"
            title="Solicitações Negadas"
            bgColor="bg-[#FF464640]  "
            textColor=" text-[#FF4646]"
            icon={<Ban className="text-[#FF4646]" />}
          />
        </div>
        {/* <div className="grid grid-cols-1 gap-5 rounded-md p-4 shadow-sm sm:grid-cols-2 lg:grid-cols-4">
          {solicitations.map((solicitation) => (
            <CardPanelControl
              key={solicitation.idSolicitation}
              data={solicitation}
              onActionCompleted={removeSolicitation}
            />
          ))}
        </div> */}

        {/* Controles de Paginação */}
      </div>
    </div>
  );
}
