import axios from "axios";
import {
  CalendarDays,
  User,
  SquareCheckBig,
  SquareX,
} from "lucide-react";
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
  branchName: string;
  clientCnpj: string;
  onActionCompleted?: (idSolicitation: string, newStatus: "APPROVED" | "DENIED") => void;
  status: string;
}

const formatCnpj = (cnpj: string) => {
  if (!cnpj) return "";
  const numericCnpj = cnpj.replace(/[^\d]/g, "");
  return numericCnpj.replace(
    /^(\d{2})(\d{3})(\d{3})(\d{4})(\d{2})$/,
    "$1.$2.$3/$4-$5"
  );
};

const DetailItem: React.FC<{ label: string; value: string | boolean | undefined }> = ({ label, value }) => {
  if (!value) return null;
  const displayValue = typeof value === 'boolean' ? (value ? 'Sim' : 'Não') : value;
  return (
    <div className="flex items-center gap-1">
      <strong className="text-gray-700">{label}:</strong>
      <span className="text-gray-600">{displayValue}</span>
    </div>
  );
};


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

  const formattedCnpj = formatCnpj(clientCnpj);

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
    setIsLoading(true);
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      console.log(tokenFromStorage);

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

      toast.success("Solicitação aprovada com sucesso!");

      if (onActionCompleted) {
        onActionCompleted(idSolicitation, "APPROVED");
      }
    } catch (error) {
      console.error("Erro ao aprovar solicitação:", error);
      toast.error("Erro ao aceitar solicitação");
    } finally {
      setIsLoading(false);
    }
  };

  const handleDeny = async () => {
    setIsLoading(true);
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      await axios.patch(
        `${ip}/item-management/${idSolicitation}/deny`,
        {},
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        }
      );

      toast.success("Solicitação dispensada com sucesso!");

      if (onActionCompleted) {
        onActionCompleted(idSolicitation, "DENIED");
      }
    } catch (error) {
      console.error("Erro ao negar solicitação:", error);
      toast.error("Erro ao negar solicitação");
    } finally {
      setIsLoading(false);
    }
  };

  const formattedDate = new Date(creationDate).toLocaleString("pt-BR", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });

  const StatusDisplay = () => {
    switch (status) {
      case "PENDING":
        return (
          <div className="flex items-center gap-1">
            <User color="#2563EB" />
            <span className="font-semibold text-[#2563EB]">
              Solicitação PENDENTE de: {requesterName}
            </span>
          </div>
        );
      case "APPROVED":
        return (
          <div className="flex items-center gap-1">
            <SquareCheckBig color="#16A34A" />
            <span className="font-semibold text-[#16A34A]">
              Solicitação APROVADA
            </span>
          </div>
        );
      case "DENIED":
        return (
          <div className="flex items-center gap-1">
            <SquareX color="#DC2626" />
            <span className="font-semibold text-[#DC2626]">
              Solicitação DISPENSADA
            </span>
          </div>
        );
      default:
        return (
          <div className="flex items-center gap-1">
            <User color="#2563EB" />
            <span className="font-semibold text-[#2563EB]">
              Solicitação de: {requesterName}
            </span>
          </div>
        );
    }
  };

  return (
    <div className="flex flex-col justify-center gap-5 rounded-md border border-neutral-300 bg-white p-4 shadow-md">
      <div className="flex items-center justify-between">
        <div>
          <StatusDisplay />
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
        <hr className="border-gray-200" />
        <div className="flex items-center gap-1">
          <span className="text-[16px] font-bold text-gray-800">Motivo:</span>
          {solicitationType === "CREATION" && (
            <p className="text-[15px] text-gray-600">CADASTRO da empresa</p>
          )}
          {solicitationType === "EXCLUSION" && (
            <p className="text-[15px] text-gray-600">Inativação da empresa</p>
          )}
          {solicitationType === "CHANGE_ACTIVITY" && (
            <p className="text-[15px] text-gray-600">Formação/Alteração teste de atividades</p>
          )}
          {solicitationType === "TEST_CLIENT_CREATION" && (
            <p className="text-[15px] text-gray-600">TESTE DE CRIAÇÃO DE CLIENTE</p>
          )}
        </div>
        <div className="flex flex-col gap-1 text-[14px]">
          <DetailItem label="Nome da empresa" value={enterpriseName} />
          <DetailItem label="Cliente" value={clientName} />
          {formattedCnpj && <DetailItem label="CNPJ" value={formattedCnpj} />}
          <DetailItem label="Filial" value={branchName} />
        </div>
        <hr className="border-gray-200" />
      </div>
      <div className="row flex-col w-full items-center">
        <div className="flex flex-row items-center justify-start gap-2 mb-5">
          <CalendarDays color="#3F3F46" size={20} />
          <span className="text-normal text-[#3F3F46]">{formattedDate}</span>
        </div>
        {status === "PENDING" && (
          <div className="flex flex-row items-center justify-between gap-2">
            <AlertDialog>
              <AlertDialogTrigger asChild className="w-full justify-center">
                <button
                  className="flex flex-row items-center justify-center gap-2 rounded-sm bg-red-100 p-1 text-lg px-6 py-2 w-full text-red-600 hover:bg-red-200 transition-colors disabled:opacity-50"
                  disabled={isLoading}
                >
                  Dispensar <SquareX size={20} />
                </button>
              </AlertDialogTrigger>
              <AlertDialogContent>
                <AlertDialogHeader className="flex flex-row items-center">
                  <SquareX
                    className="w-[40%] text-red-600"
                    width={100}
                    height={100}
                  />
                  <AlertDialogTitle className="w-[60%]">
                    Deseja mesmo dispensar a solicitação de acesso de{" "}
                    <span className="text-red-500">{enterpriseName}</span> ao
                    sistema?
                    <div className="flex justify-between mt-5 ">
                      <AlertDialogCancel>Cancelar</AlertDialogCancel>
                      <AlertDialogAction
                        onClick={handleDeny}
                        className="bg-red-600 hover:bg-red-700"
                        disabled={isLoading}
                      >
                        Dispensar
                      </AlertDialogAction>
                    </div>
                  </AlertDialogTitle>
                </AlertDialogHeader>
              </AlertDialogContent>
            </AlertDialog>

            <AlertDialog>
              <AlertDialogTrigger asChild className="w-full flex justify-center">
                <button
                  className="flex flex-row w-full items-center justify-center gap-2 rounded-sm bg-green-100 p-1 text-lg px-6 py-2 text-green-600 hover:bg-green-200 transition-colors disabled:opacity-50"
                  disabled={isLoading}
                >
                  Aceitar <SquareCheckBig size={20} />
                </button>
              </AlertDialogTrigger>
              <AlertDialogContent>
                <AlertDialogHeader className="flex flex-row items-center">
                  <SquareCheckBig
                    className="w-[40%] text-green-600"
                    width={100}
                    height={100}
                  />
                  <AlertDialogTitle className="w-[60%]">
                    Deseja mesmo confirmar o acesso de{" "}
                    <span className="text-green-600">{enterpriseName}</span> ao
                    sistema?
                    <div className="flex justify-between mt-5 ">
                      <AlertDialogCancel>Cancelar</AlertDialogCancel>
                      <AlertDialogAction
                        onClick={handleApprove}
                        className="bg-green-600 text-white hover:bg-green-700"
                        disabled={isLoading}
                      >
                        Aceitar
                      </AlertDialogAction>
                    </div>
                  </AlertDialogTitle>
                </AlertDialogHeader>
              </AlertDialogContent>
            </AlertDialog>
          </div>
        )}

        {isLoading && status === "PENDING" && (
          <div className="flex flex-row items-center justify-center gap-2 w-full py-2 bg-gray-100 rounded-sm text-gray-700">
            <Oval
              visible={true}
              height="20"
              width="20"
              color="#4fa94d"
              ariaLabel="oval-loading"
              wrapperStyle={{}}
              wrapperClass=""
            />
            <span>Aguarde...</span>
          </div>
        )}
      </div>
    </div>
  );
}