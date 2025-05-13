import axios from "axios";
import { CalendarDays, ThumbsDown, ThumbsUp, User } from "lucide-react";
import { ip } from "@/utils/ip";
import { toast } from "sonner";
import { Oval } from "react-loader-spinner";
import { useState } from "react";
import { MoreDetails } from "@/pages/auth/realizaProfile/panel-control/more-details";

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
      nameEnterprise?: string | undefined ;
      cpf?: string | undefined;
    };
    newProvider: {
      cnpj?: string | undefined;
      corporateName?: string | undefined;
      telephone?: string | undefined;
    };
  };
  // Callback opcional para atualizar a lista após a ação
  onActionCompleted?: (idSolicitation: string) => void;
}

export function CardPanelControl({
  data,
  onActionCompleted,
}: CardPanelControlProps) {
  const [isLoading, setIsLoading] = useState(false);

  const handleApprove = async () => {
    try {
      // Define o loading como true antes de começar a requisição
      setIsLoading(true);

      console.log("teste id da solicitacao", data.idSolicitation);

      await axios.patch(`${ip}/item-management/${data.idSolicitation}/approve`);

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
      const response = await axios.patch(
        `${ip}/item-management/${data.idSolicitation}/deny`,
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
        <MoreDetails
          idSolicitation={data.idSolicitation}
          firstName={data.requester.firstName}
          surname={data.requester.surname}
          nameEnterprise={data.requester.nameEnterprise}
          cpf={data.requester.cpf}
          corporateName={data.newProvider.corporateName}
          cnpj={data.newProvider.cnpj}
        />{" "}
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
          <button
            onClick={handleDeny}
            className="flex flex-row items-center justify-center gap-2 rounded-sm bg-[#FF464633] p-1 text-xs text-[#FF4646]"
          >
            Dispensar <ThumbsDown size={15} />
          </button>
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
            <button
              onClick={handleApprove}
              className="flex flex-row items-center justify-center gap-2 rounded-sm bg-[#16A34A33] p-1 text-xs text-[#16A34A] hover:bg-stone-300"
            >
              Aceitar <ThumbsUp size={15} />
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
