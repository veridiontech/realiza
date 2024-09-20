import { Bell, BriefcaseBusiness, Headset, MessageSquareText, Palette, SquareChevronLeft, UserPen } from "lucide-react";
import { NavLink } from "react-router-dom";

export function AsideProfileEdit() {
    return (
        <aside className="bg-white w-[308px] h-[1303px] flex flex-col items-start  rounded-md">
            <div className="flex flex-row gap-4 p-4">
                <NavLink to='/profile'>
                    <SquareChevronLeft className="text-[#2563EB]" cursor={'pointer'} />
                </NavLink>
                <span className="text-[#2563EB] text-[18px] font-medium">Configurações</span>
            </div>

            {/* Divider */}
            <div className="bg-[#E3E3E3] w-full h-[1px]"></div>

            {/* Conteúdo com padding aplicado sem afetar o divider */}
            <div className="p-4 w-full">
                <div className="flex flex-col items-start justify-center gap-[30px]">
                    <div className="flex items-center justify-center gap-2">
                        <UserPen />
                        <span>Perfil</span>
                    </div>
                    <div className="flex items-center justify-center gap-2">
                        <BriefcaseBusiness />
                        <span>Empresa</span>
                    </div>
                    <div className="flex items-center justify-center gap-2">
                        <MessageSquareText />
                        <span>Mensagem</span>
                    </div>
                    <div className="flex items-center justify-center gap-2">
                        <Bell />
                        <span>Notificação</span>
                    </div>
                    <div className="flex items-center justify-center gap-2">
                        <Palette />
                        <span>Aparência</span>
                    </div>
                    <div className="flex items-center justify-center gap-2">
                        <Headset />
                        <span>Suporte</span>
                    </div>
                </div>
            </div>
        </aside>
    );
}
