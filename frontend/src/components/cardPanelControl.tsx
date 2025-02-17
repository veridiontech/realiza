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
        <div className="flex flex-col gap-2 items-start justify-center p-4 bg-white rounded-md shadow-md border-[#C3C3C3CC]">
            <div className=" w-full flex  flex-row gap-2">
                <User color="#2563EB" />
                <span className="text-[#2563EB] font-semibold">Solicitação de: {data.nome}</span>
            </div>
            <div className="w-full lex flex-col gap-2 border-y border-[#7CA1F333] py-4">
                <h3 className="text-[#2563EB] font-semibold text-sm mb-3">Tipo: {data.tipo}</h3>
                <p className="text-[#3F3F46] text-sm font-semibold">Detalhes: </p>
                <p className="text-xs text-[#3F3F46] ">{data.detalhes}</p>
            </div>
            <div className="flex flex row items-center justify-between w-full">
                <div className="flex flex-row gap-2 items-center justify-center">
                    <CalendarDays color="#3F3F46" />
                    <span className="text-[#3F3F46] text-xs">
                        {data.data}
                    </span>
                </div>
                <div className="flex flex-row gap-2 items-center justify-center">
                    <button className="text-[#FF4646] bg-[#FF464633] text-xs rounded-sm flex flex-row gap-2 p-1 items-center justify-center"> Dispensar <ThumbsDown  size={15} /></button>
                    <button className="text-[#16A34A] bg-[#16A34A33] text-xs rounded-sm flex flex-row gap-2 p-1 items-center justify-center"> Aceitar <ThumbsUp size={15} /></button>
                </div>
            </div>
        </div>
    );
}