import {
  ChevronLeft ,
  UserRoundCog ,
  Building2 ,
  MessageCircleMore,
  BellDot ,
  Paintbrush,
  HandHelping 
} from "lucide-react";

export function LateralMenu() {
  return (
    <aside className=" w-full p-4 shadow-md">
      <button className="flex items-center gap-2 p-2 rounded-md hover:bg-gray-200">
        <ChevronLeft  className="w-5 h-5 text-blue-600" />
        <span className="text-lg font-medium text-blue-600">Configurações</span>
      </button>
      <button className="mt-2 flex items-center gap-2 p-2 rounded-md hover:bg-gray-200">
        <UserRoundCog  className="w-5 h-5 text-gray-800" />
        <span className="text-sm font-medium">Perfil</span>
      </button>
      <button className="mt-2 flex items-center gap-2 p-2 rounded-md hover:bg-gray-200">
        <Building2  className="w-5 h-5 text-gray-800" />
        <span className="text-sm font-medium">Empresa</span>
      </button>
      <button className="mt-2 flex items-center gap-2 p-2 rounded-md hover:bg-gray-200">
        <MessageCircleMore className="w-5 h-5 text-gray-800" />
        <span className="text-sm font-medium">Mensagens</span>
      </button>
      <button className="mt-2 flex items-center gap-2 p-2 rounded-md hover:bg-gray-200">
        <BellDot  className="w-5 h-5 text-gray-800" />
        <span className="text-sm font-medium">Notificações</span>
      </button>
      <button className="mt-2 flex items-center gap-2 p-2 rounded-md hover:bg-gray-200">
        <Paintbrush className="w-5 h-5 text-gray-800" />
        <span className="text-sm font-medium">Aparência</span>
      </button>
      <button className="mt-2 flex items-center gap-2 p-2 rounded-md hover:bg-gray-200">
        <HandHelping  className="w-5 h-5 text-gray-800" />
        <span className="text-sm font-medium">Suporte</span>
      </button>
    </aside>
  );
}
