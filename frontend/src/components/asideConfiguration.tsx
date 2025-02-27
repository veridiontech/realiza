import {
  ChevronLeft,
  UserRoundCog,
  Building2,
  MessageCircleMore,
  BellDot,
  Paintbrush,
  HandHelping,
} from "lucide-react";

export function LateralMenu() {
  return (
    <aside className="w-full p-4 shadow-md">
      <button className="flex items-center gap-2 rounded-md p-2 hover:bg-gray-200">
        <ChevronLeft className="text-realizaBlue h-5 w-5" />
        <span className="text-realizaBlue text-lg font-medium">
          Configurações
        </span>
      </button>
      <button className="mt-2 flex items-center gap-2 rounded-md p-2 hover:bg-gray-200">
        <UserRoundCog className="h-5 w-5 text-gray-800" />
        <span className="text-sm font-medium">Perfil</span>
      </button>
      <button className="mt-2 flex items-center gap-2 rounded-md p-2 hover:bg-gray-200">
        <Building2 className="h-5 w-5 text-gray-800" />
        <span className="text-sm font-medium">Empresa</span>
      </button>
      <button className="mt-2 flex items-center gap-2 rounded-md p-2 hover:bg-gray-200">
        <MessageCircleMore className="h-5 w-5 text-gray-800" />
        <span className="text-sm font-medium">Mensagens</span>
      </button>
      <button className="mt-2 flex items-center gap-2 rounded-md p-2 hover:bg-gray-200">
        <BellDot className="h-5 w-5 text-gray-800" />
        <span className="text-sm font-medium">Notificações</span>
      </button>
      <button className="mt-2 flex items-center gap-2 rounded-md p-2 hover:bg-gray-200">
        <Paintbrush className="h-5 w-5 text-gray-800" />
        <span className="text-sm font-medium">Aparência</span>
      </button>
      <button className="mt-2 flex items-center gap-2 rounded-md p-2 hover:bg-gray-200">
        <HandHelping className="h-5 w-5 text-gray-800" />
        <span className="text-sm font-medium">Suporte</span>
      </button>
    </aside>
  );
}
