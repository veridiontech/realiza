import axios from "axios";
import { CalendarDays, ThumbsUp, User } from "lucide-react";
import { ip } from "@/utils/ip";
import { toast } from "sonner";
import { Oval } from "react-loader-spinner";
import { useState } from "react";
import { MoreDetails } from "@/pages/auth/realizaProfile/panel-control/more-details";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/components/ui/alert-dialog";

interface CardPanelControlProps {
  idSolicitation: string;
  creationDate: string;
  requesterEmail: string;
  requesterName: string;
  solicitationType: string;
  enterpriseName?: string | undefined;
  clientName: string;
  branchName: boolean;
  clientCnpj: string;
  onActionCompleted?: (idSolicitation: string) => void;
  status: string;
}

export function CardPanelControlProvider({
  idSolicitation,
  creationDate,
  enterpriseName,
  requesterEmail,
  requesterName,
  clientName,
  clientCnpj,
  branchName,
  solicitationType,
  onActionCompleted,
  status,
}: CardPanelControlProps) {
  const [isLoading, setIsLoading] = useState(false);

  const client = {
    cnpj: clientCnpj,
    tradeName: clientName,
    corporateName: enterpriseName || "",
  };

  const requester = {
    fullName: requesterName,
    email: requesterEmail,
  };

  const newProvider = {
    corporateName: enterpriseName || "",
    cnpj: "",
  };

  const handleApprove = async () => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      console.log(tokenFromStorage);

      setIsLoading(true);

      await axios.patch(
        `${ip}/item-management/${idSolicitation}/approve`,
        {},
        {
          headers: {
            Authorization: `Bearer ${tokenFromStorage}`,
            "Content-Type": "application/json",
          },
        }
      );

      toast.success("Solicitação aprovada");
      setIsLoading(false);

      if (onActionCompleted) {
        onActionCompleted(idSolicitation);
      }
      // window.location.reload();
    } catch (error) {
      console.error("Erro ao aprovar solicitação:", error);
      toast.error("Erro ao aceitar solicitação");
      setIsLoading(false);
    }
  };

  const handleDeny = async () => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      await axios.patch(
        `${ip}/item-management/${idSolicitation}/deny`,
        {},
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        }
      );
      if (onActionCompleted) {
        onActionCompleted(idSolicitation);
      }
    } catch (error) {
      console.error("Erro ao negar solicitação:", error);
      alert("Erro ao negar solicitação.");
    }
  };

  return (
    <div className="flex flex-col justify-center gap-5 rounded-md border border-neutral-300 bg-white p-4 shadow-md">
      <div className="flex items-center justify-between">
        <div>
          {status === "PENDING" ? (
            <div className="flex items-center gap-1">
              <User color="#2563EB" />
              <span className="font-semibold text-[#2563EB]">
                Solicitação de: {requesterName}
              </span>
            </div>
          ) : (
            <div className="flex items-center gap-1">
              <User color="#2563EB" />
              <span className="font-semibold text-[#2563EB]">
                Solicitação aceita
              </span>
            </div>
          )}
          <span className="text-[14px] text-neutral-600">{requesterEmail}</span>
        </div>
        <MoreDetails
          idSolicitation={idSolicitation}
          client={client}
          requester={requester}
          newProvider={newProvider}
        />
      </div>
      <div className="flex flex-col gap-2">
        <div className="flex items-center gap-1">
          <span className="text-[18px] font-semibold">Motivo: </span>
          {solicitationType === "CREATION" && (
            <p className="text-[15px]">CADASTRO da empresa</p>
          )}
          {solicitationType === "EXCLUSION" && (
            <div>
              <span>Inativação da empresa</span>
            </div>
          )}
        </div>
        <div className="flex flex-col gap-1 text-[14px]">
          <div className="flex items-center gap-1">
            <strong>Nome da empresa: </strong>
            <span>{enterpriseName}</span>
          </div>
        </div>
        <div className="flex flex-col gap-1 text-[14px]">
          <div className="flex items-center gap-1">
            <strong>Cliente: </strong>
            <span>{clientName}</span>
          </div>
          <div className="flex items-center ">
            <strong>Filial: </strong>
            <span>{branchName}</span>
          </div>
        </div>
      </div>
      <div className="row flex w-full items-center justify-between">
        <div className="flex flex-row items-center justify-center gap-2">
          <CalendarDays color="#3F3F46" />
          <span className="text-xs text-[#3F3F46]">
            {new Date(creationDate).toLocaleString()}
          </span>
        </div>
        {status === "APPROVED" || status === "DENIED" ? (
          <div></div>
        ) : (
          <div className="flex flex-row items-center justify-center gap-2">
            <AlertDialog>
              <AlertDialogTrigger>
                <button className="flex flex-row items-center justify-center gap-2 rounded-sm bg-red-300 p-1 text-xs text-red-500 hover:bg-stone-300">
                  Dispensar <ThumbsUp size={15} />
                </button>
              </AlertDialogTrigger>
              <AlertDialogContent>
                <AlertDialogHeader>
                  <AlertDialogTitle>
                    Deseja mesmo dispensar a solicitação de acesso de{" "}
                    {enterpriseName} ao sistema?
                  </AlertDialogTitle>
                </AlertDialogHeader>
                <AlertDialogFooter>
                  <AlertDialogCancel>Cancelar</AlertDialogCancel>
                  <AlertDialogAction onClick={handleDeny}>
                    Dispensar
                  </AlertDialogAction>
                </AlertDialogFooter>
              </AlertDialogContent>
            </AlertDialog>
            {isLoading ? (
              <button
                onClick={handleApprove}
                className="flex flex-row items-center justify-center gap-2 rounded-sm bg-[#16A34A33] p-1 text-xs text-[#16A34A] hover:bg-stone-300"
              >
                <Oval
                  visible={true}
                  height="20"
                  width="20"
                  color="#4fa94d"
                  ariaLabel="oval-loading"
                  wrapperStyle={{}}
                  wrapperClass=""
                />
              </button>
            ) : (
              <AlertDialog>
                <AlertDialogTrigger>
                  <button className="flex flex-row items-center justify-center gap-2 rounded-sm bg-[#16A34A33] p-1 text-xs text-[#16A34A] hover:bg-stone-300">
                    Aceitar <ThumbsUp size={15} />
                  </button>
                </AlertDialogTrigger>
                <AlertDialogContent>
                  <AlertDialogHeader>
                    <AlertDialogTitle>
                      Deseja mesmo confirmar o acesso de {enterpriseName} ao
                      sistema?
                    </AlertDialogTitle>
                  </AlertDialogHeader>
                  <AlertDialogFooter>
                    <AlertDialogCancel className="bg-red-300 hover:bg-red-400">
                      Cancelar
                    </AlertDialogCancel>
                    <AlertDialogAction
                      onClick={handleApprove}
                      className="bg-green-800 text-white"
                    >
                      Aceitar
                    </AlertDialogAction>
                  </AlertDialogFooter>
                </AlertDialogContent>
              </AlertDialog>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
