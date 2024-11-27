import {
  ChartPie,
  Users2,
  UserSquare,
  File,
  Home,
} from "lucide-react";

export function LateralMenu() {
  return (
    <aside className=" w-full p-4 shadow-md">
      <button className="flex items-center gap-2 p-2 rounded-md hover:bg-gray-200">
        <Home className="w-5 h-5 text-blue-600" />
        <span className="text-sm font-medium">Configurações</span>
      </button>
      <button className="mt-2 flex items-center gap-2 p-2 rounded-md hover:bg-gray-200">
        <Users2 className="w-5 h-5 text-gray-800" />
        <span className="text-sm font-medium">Perfil</span>
      </button>
      <button className="mt-2 flex items-center gap-2 p-2 rounded-md hover:bg-gray-200">
        <ChartPie className="w-5 h-5 text-gray-800" />
        <span className="text-sm font-medium">Empresa</span>
      </button>
      <button className="mt-2 flex items-center gap-2 p-2 rounded-md hover:bg-gray-200">
        <UserSquare className="w-5 h-5 text-gray-800" />
        <span className="text-sm font-medium">Mensagens</span>
      </button>
      <button className="mt-2 flex items-center gap-2 p-2 rounded-md hover:bg-gray-200">
        <File className="w-5 h-5 text-gray-800" />
        <span className="text-sm font-medium">Notificações</span>
      </button>
      <button className="mt-2 flex items-center gap-2 p-2 rounded-md hover:bg-gray-200">
        <File className="w-5 h-5 text-gray-800" />
        <span className="text-sm font-medium">Aparência</span>
      </button>
      <button className="mt-2 flex items-center gap-2 p-2 rounded-md hover:bg-gray-200">
        <File className="w-5 h-5 text-gray-800" />
        <span className="text-sm font-medium">Suporte</span>
      </button>
    </aside>
  );
}
