import { useState, useEffect } from "react";
import axios from "axios";
import { Ban, CheckCircle, Rotate3D } from "lucide-react";
import { ip } from "@/utils/ip";

import { CardPanelControlProvider } from "@/components/cardPanelControlProvider";
import { ColumnPanelControl } from "@/components/column-panel-control";
import { ScrollArea } from "@/components/ui/scroll-area";

// Interface simplificada para refletir a resposta real da API
export interface Solicitation {
  idSolicitation: string;
  creationDate: string;
  status: string;
  
  // Campos que a API está retornando no nível superior:
  requesterName: string; // Vindo como "realiza Assessoria"
  requesterEmail: string; 
  solicitationType: string; 
  
  // O nome da empresa deve estar vindo como "enterpriseName" no JSON
  enterpriseName?: string; 
  
  clientCnpj: string;
  clientName: string;
  branchName: boolean;
  
  // Removido: 'requester' e 'newProvider' complexos, pois não estão no JSON
  // Os campos abaixo (do newUser) parecem não estar sendo usados ou não vêm no JSON real
  // newUser: { ... }; 
}

interface ApiResponse {
  content: Solicitation[];
  totalPages: number;
}


export function ProviderSolicitations() {
  const [solicitations, setSolicitations] = useState<Solicitation[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const fetchSolicitations = async () => {
    setIsLoading(true);
    const tokenFromStorage = localStorage.getItem("tokenClient");
    try {
      const response = await axios.get<ApiResponse>(
        `${ip}/item-management/new-provider`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        }
      );
      
      console.log("Dados recebidos da API (content):", response.data.content);

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

  // Funções getRequesterName e getEnterpriseName foram removidas, pois os dados estão no nível superior.

  return (
    <div className="flex h-full w-full flex-col items-center justify-center gap-9 p-4">
      <div className="relative bottom-[3vw] flex h-full w-full flex-col gap-6 rounded-md bg-white p-4 shadow-sm">
        <h1 className="font-semibold text-[30px]">Empresas Solicitantes </h1>
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
                  <div className="flex flex-col gap-5">
                    {solicitations
                      .filter(
                        (solicitation) => solicitation.status === "PENDING"
                      )
                      .map((solicitation: Solicitation) => (
                        <CardPanelControlProvider
                          branchName={solicitation.branchName}
                          key={solicitation.idSolicitation}
                          // Mapeamento Direto: Usa o campo que veio no JSON
                          requesterName={solicitation.requesterName} 
                          creationDate={solicitation.creationDate}
                          // Mapeamento Direto: Usa o campo que veio no JSON
                          enterpriseName={solicitation.enterpriseName} 
                          idSolicitation={solicitation.idSolicitation}
                          requesterEmail={solicitation.requesterEmail}
                          solicitationType={solicitation.solicitationType}
                          clientCnpj={solicitation.clientCnpj}
                          clientName={solicitation.clientName}
                          onActionCompleted={removeSolicitation}
                          status="PENDING"
                        />
                      ))}
                  </div>
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
              <div className="bg-gray-100 p-8">
                <ScrollArea className="h-[40vh]">
                  <div className="flex flex-col gap-2">
                    {solicitations
                      .filter(
                        (solicitation) => solicitation.status === "APPROVED"
                      )
                      .map((solicitation: Solicitation) => (
                        <CardPanelControlProvider
                          branchName={solicitation.branchName}
                          key={solicitation.idSolicitation}
                          requesterName={solicitation.requesterName}
                          creationDate={solicitation.creationDate}
                          enterpriseName={solicitation.enterpriseName}
                          idSolicitation={solicitation.idSolicitation}
                          requesterEmail={solicitation.requesterEmail}
                          solicitationType={solicitation.solicitationType}
                          clientCnpj={solicitation.clientCnpj}
                          clientName={solicitation.clientName}
                          status="APPROVED"
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
                    .filter((solicitation) => solicitation.status === "DENIED")
                    .map((solicitation: Solicitation) => (
                      <CardPanelControlProvider
                        branchName={solicitation.branchName}
                        key={solicitation.idSolicitation}
                        requesterName={solicitation.requesterName}
                        creationDate={solicitation.creationDate}
                        enterpriseName={solicitation.enterpriseName}
                        idSolicitation={solicitation.idSolicitation}
                        requesterEmail={solicitation.requesterEmail}
                        solicitationType={solicitation.solicitationType}
                        clientCnpj={solicitation.clientCnpj}
                        clientName={solicitation.clientName}
                        status="DENIED"
                      />
                    ))}
                </ScrollArea>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}