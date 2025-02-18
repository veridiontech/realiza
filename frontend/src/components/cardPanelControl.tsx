import { CalendarDays, ThumbsDown, ThumbsUp, User } from "lucide-react";

interface Solicitacao {
  nome: string;
  id: number;
  tipo: string;
  detalhes: string;
  data: string;
}

interface CardPanelControlProps {
  data: Solicitacao;
}

export function CardPanelControl({ data }: CardPanelControlProps) {
  return (
    <div className="flex flex-col items-start justify-center gap-2 rounded-md border-[#C3C3C3CC] bg-white p-4 shadow-md">
      <div className="flex w-full flex-row gap-2">
        <User color="#2563EB" />
        <span className="font-semibold text-[#2563EB]">
          Solicitação de: {data.nome}
        </span>
      </div>
      <div className="lex w-full flex-col gap-2 border-y border-[#7CA1F333] py-4">
        <h3 className="mb-3 text-sm font-semibold text-[#2563EB]">
          Tipo: {data.tipo}
        </h3>
        <p className="text-sm font-semibold text-[#3F3F46]">Detalhes: </p>
        <p className="text-xs text-[#3F3F46]">{data.detalhes}</p>
      </div>
      <div className="row flex w-full items-center justify-between">
        <div className="flex flex-row items-center justify-center gap-2">
          <CalendarDays color="#3F3F46" />
          <span className="text-xs text-[#3F3F46]">{data.data}</span>
        </div>
        <div className="flex flex-row items-center justify-center gap-2">
          <button className="flex flex-row items-center justify-center gap-2 rounded-sm bg-[#FF464633] p-1 text-xs text-[#FF4646]">
            {" "}
            Dispensar <ThumbsDown size={15} />
          </button>
          <button className="flex flex-row items-center justify-center gap-2 rounded-sm bg-[#16A34A33] p-1 text-xs text-[#16A34A]">
            {" "}
            Aceitar <ThumbsUp size={15} />
          </button>
        </div>
      </div>
    </div>
  );
}
