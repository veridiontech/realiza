import { Bell, BriefcaseBusiness, Headset, MessageSquareText, Palette, SquareChevronLeft, UserPen } from "lucide-react";

export function AsideProfileEdit() {
    return(
        <aside className="bg-white w-[308px] h-[1303px] flex flex-col items-start">
            <div className="flex flex-row gap-4">
            <SquareChevronLeft  className="text-[#2563EB]" cursor={'pointer'}/>
             <span className="text-[#2563EB] text-[18px] font-medium">Confugurações</span>
            </div>
            <div className="bg-[#E3E3E3] w-[312px] border border"></div>
            <div className="flex flex-col items-start justify-center gap-[30px]">
                <div className="flex items-center justify-center">
                    <UserPen />
                    <span>Perfil</span>
                </div>
                <div  className="flex items-center justify-center">
                    <BriefcaseBusiness />
                    <span>Empresa</span>
                </div>
                <div  className="flex items-center justify-center">
                    <MessageSquareText />
                    <span>Mensagem</span>
                </div>
                <div  className="flex items-center justify-center">
                    <Bell />
                    <span>Notificação</span>
                </div>
                <div  className="flex items-center justify-center">
                    <Palette />
                    <span>Aparência</span>
                </div>
                <div  className="flex items-center justify-center">
                    <Headset />
                    <span>Suporte</span>
                </div>
            </div>
        </aside>
    )
}