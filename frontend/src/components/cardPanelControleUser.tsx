import axios from "axios";
import { CalendarDays, ThumbsUp, User } from "lucide-react";
import { ip } from "@/utils/ip";
import { toast } from "sonner";
import { Oval } from "react-loader-spinner";
import { useState } from "react";
import { MoreDetailsUser } from "@/pages/auth/realizaProfile/panel-control/more-details-user";
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
  data: {
    idSolicitation: string;
    title: string;
    details: string;
    creationDate: string;
    requester: {
      idUser: string;
      firstName: string;
      surname: string;
      nameEnterprise?: string | undefined;
      cpf?: string | undefined;
    };
    newUser: {
      idUser: string;
      firstName?: string | undefined;
      surname?: string | undefined;
      nameEnterprise?: string | undefined;
      cpf?: string | undefined;
      email?: string | undefined;
      enterprise?: string | undefined;
    };
  };
  // Callback opcional para atualizar a lista após a ação
  onActionCompleted?: (idSolicitation: string) => void;
}

export function CardPanelControlUser({
  data,
  onActionCompleted,
}: CardPanelControlProps) {
  const [isLoading, setIsLoading] = useState(false);

  const handleApprove = async () => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      // Define o loading como true antes de começar a requisição
      setIsLoading(true);

      console.log("teste id da solicitacao", data.idSolicitation);

      await axios.patch(
        `${ip}/item-management/${data.idSolicitation}/approve`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        }
      );

      toast.success("Solicitação aprovada");

      // Atualize o estado do loading para false quando a requisição for concluída
      setIsLoading(false);

      // Recarregue ou atualize conforme necessário
      if (onActionCompleted) {
        onActionCompleted(data.idSolicitation);
      }
      window.location.reload();
    } catch (error) {
      console.error("Erro ao aprovar solicitação:", error);
      toast.error("Erro ao aceitar solicitação");
      setIsLoading(false); // Também precisa garantir que o loading seja alterado em caso de erro
    }
  };

  const handleDeny = async () => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const response = await axios.patch(
        `${ip}/item-management/${data.idSolicitation}/deny`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        }
      );

      alert(response.data);
      // Remove o item negado da lista se houver callback
      if (onActionCompleted) {
        onActionCompleted(data.idSolicitation);
      }
    } catch (error) {
      console.error("Erro ao negar solicitação:", error);
      alert("Erro ao negar solicitação.");
    }
  };

  return (
    <div className="flex flex-col justify-center gap-2 rounded-md border border-neutral-300 bg-white p-4 shadow-md">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-1">
          <User color="#2563EB" />
          <span className="font-semibold text-[#2563EB]">
            Solicitação de: {data.requester.firstName} {data.requester.surname}
          </span>
        </div>
        {}
        <MoreDetailsUser
          idSolicitation={data.idSolicitation}
          requesterFirstName={data.requester.firstName}
          requesterSurname={data.requester.surname}
          requesterCpf={data.requester.cpf}
          nameEnterprise={data.requester.nameEnterprise}
          newUserFirstName={data.newUser.firstName}
          newUserSurname={data.newUser.surname}
          newUserEmail={data.newUser.email}
          newUserEnterprise={data.newUser.enterprise}
        />
      </div>
      <div className="flex w-full flex-col gap-2 border-y border-[#7CA1F333] py-4">
        <h3 className="mb-3 text-sm font-semibold text-[#2563EB]">
          {data.title}
        </h3>
        <p className="text-sm font-semibold text-[#3F3F46]">
          Detalhes: {data.details}
        </p>
      </div>
      <div className="row flex w-full items-center justify-between">
        <div className="flex flex-row items-center justify-center gap-2">
          <CalendarDays color="#3F3F46" />
          <span className="text-xs text-[#3F3F46]">
            {new Date(data.creationDate).toLocaleString()}
          </span>
        </div>
        <div className="flex flex-row items-center justify-center gap-2">
          {status === "APPROVED" || status === "DENIED"? (
            <div></div>
          ) : (
            <div className="flex flex-row items-center justify-center gap-2">
              <AlertDialog>
                <AlertDialogTrigger>
                  {" "}
                  <button className="flex flex-row items-center justify-center gap-2 rounded-sm bg-red-300 p-1 text-xs text-red-500 hover:bg-stone-300">
                    Dispensar <ThumbsUp size={15} />
                  </button>
                </AlertDialogTrigger>
                <AlertDialogContent>
                  <AlertDialogHeader>
                    <AlertDialogTitle>
                      Deseja mesmo dispensar a solicitação de acesso de{" "}
                      {data.newUser.firstName} {data.newUser.surname} ao
                      sistema?
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
                    {" "}
                    <button className="flex flex-row items-center justify-center gap-2 rounded-sm bg-[#16A34A33] p-1 text-xs text-[#16A34A] hover:bg-stone-300">
                      Aceitar <ThumbsUp size={15} />
                    </button>
                  </AlertDialogTrigger>
                  <AlertDialogContent>
                    <AlertDialogHeader>
                      <AlertDialogTitle>
                        Deseja mesmo confirmar o acesso de{" "}
                        {data.newUser.firstName} {data.newUser.surname}ao
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
    </div>
  );
}
