import { useState, useEffect } from "react";
import axios from "axios";
import { Ban, CheckCircle, Rotate3D } from "lucide-react";
import { ip } from "@/utils/ip";
import { ColumnPanelControl } from "@/components/column-panel-control";
import { ScrollArea } from "@/components/ui/scroll-area";
import { CardPanelControlUser } from "@/components/cardPanelControleUser";

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
    firstName: string;
    surname: string;
    nameEnterprise?: string | undefined;
    cpf?: string | undefined;
    email: string | undefined;
  };
  status: string;
}

interface ApiResponse {
  content: Solicitation[];
  totalPages: number;
}

export function UserSolicitations() {
  const [solicitations, setSolicitations] = useState<Solicitation[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const fetchSolicitations = async () => {
    const tokenFromStorage = localStorage.getItem("tokenClient");
    setIsLoading(true);
    try {
      const response = await axios.get<ApiResponse>(
        `${ip}/item-management/new-user`,
        {
          headers: {
            Authorization: `Bearer ${tokenFromStorage}`,
          },
        }
      );

      console.log("teste de solicitacoes de usuários:", response.data.content);

      setSolicitations(response.data.content);
    } catch (err) {
      console.error("Erro ao buscar solicitações:", err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchSolicitations();
  }, []);

  const removeSolicitation = (idSolicitation: string) => {
    setSolicitations((prev) =>
      prev.filter((s) => s.idSolicitation !== idSolicitation)
    );
  };

  const countStatus = (status: "APPROVED" | "DENIED" | "PENDING") => {
    return solicitations.filter(
      (solicitation) => solicitation.status === status
    ).length;
  };

  return (
    <div className="flex h-full w-full flex-col items-center justify-center gap-9 p-4">
      <div className="relative bottom-[3vw] flex h-full w-full flex-col gap-6 rounded-md bg-white p-4 shadow-sm">
        <h1 className="font-semibold text-[30px]">Usuários solicitantes</h1>
        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 md:grid-cols-3">
          <div>
            <ColumnPanelControl
              lenghtControl={countStatus("PENDING")}
              title="Solicitações pendentes"
              bgColor="bg-[#F9731640]"
              textColor="text-[#F97316]"
              icon={<Rotate3D className="text-[#F97316]" />}
              isLoading={isLoading}
            />
            <div>
              <div className="bg-gray-100 p-8">
                <ScrollArea className="h-[40vh]">
                  {solicitations
                    .filter((solicitation) => solicitation.status === "PENDING")
                    .map((solicitation: any) => (
                      <CardPanelControlUser
                        key={solicitation.idSolicitation}
                        clientCnpj={solicitation.clientCnpj}
                        clientTradeName={solicitation.clientTradeName}
                        creationDate={solicitation.creationDate}
                        idSolicitation={solicitation.idSolicitation}
                        requesterEmail={solicitation.requesterEmail}
                        requesterFullName={solicitation.requesterFullName}
                        userFullName={solicitation.userFullName}
                        onActionCompleted={removeSolicitation}
                      />
                    ))}
                </ScrollArea>
              </div>
            </div>
          </div>
          <div>
            <ColumnPanelControl
              lenghtControl={countStatus("APPROVED")}
              title="Solicitações Confirmadas"
              bgColor="bg-[#2563EB40]"
              textColor="text-[#2563EB]"
              icon={<CheckCircle className="text-[#2563EB]" />}
              isLoading={isLoading}
            />
            <div>
              <div className="bg-gray-100 p-8 ">
                <ScrollArea className="h-[40vh] ">
                  <div className=" flex flex-col gap-2">
                    {solicitations
                      .filter(
                        (solicitation) => solicitation.status === "APPROVED"
                      )
                      .map((solicitation: any) => (
                        <CardPanelControlUser
                          key={solicitation.idSolicitation}
                          clientCnpj={solicitation.clientCnpj}
                          clientTradeName={solicitation.clientTradeName}
                          creationDate={solicitation.creationDate}
                          idSolicitation={solicitation.idSolicitation}
                          requesterEmail={solicitation.requesterEmail}
                          requesterFullName={solicitation.requesterFullName}
                          userFullName={solicitation.userFullName}
                          onActionCompleted={removeSolicitation}
                        />
                      ))}
                  </div>
                </ScrollArea>
              </div>
            </div>
          </div>

          <div>
            <ColumnPanelControl
              lenghtControl={countStatus("DENIED")}
              title="Solicitações Negadas"
              bgColor="bg-[#FF464640]"
              textColor="text-[#FF4646]"
              icon={<Ban className="text-[#FF4646]" />}
              isLoading={isLoading}
            />
            <div>
              <div className="bg-gray-100 p-8">
                <ScrollArea className="h-[40vh]">
                  {solicitations
                    .filter(
                      (solicitation: any) => solicitation.status === "DENIED"
                    )
                    .map((solicitation: any) => (
                      <CardPanelControlUser
                        key={solicitation.idSolicitation}
                        clientCnpj={solicitation.clientCnpj}
                        clientTradeName={solicitation.clientTradeName}
                        creationDate={solicitation.creationDate}
                        idSolicitation={solicitation.idSolicitation}
                        requesterEmail={solicitation.requesterEmail}
                        requesterFullName={solicitation.requesterFullName}
                        userFullName={solicitation.userFullName}
                        onActionCompleted={removeSolicitation}
                      />
                    ))}
                </ScrollArea>
              </div>
            </div>
          </div>
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
