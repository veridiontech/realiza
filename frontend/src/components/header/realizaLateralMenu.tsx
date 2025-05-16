import {
  ChartPie,
  // File,
  Home,
  Users2,
  Building2,
  Factory,
  PersonStanding,
  LayoutPanelTop,
  Paperclip,
  UserPlus,
  UserRound,
  LogOut,
} from "lucide-react";
import { Button } from "../ui/button";
import { SheetContent, SheetTitle } from "../ui/sheet";
import { Link } from "react-router-dom";
import { useUser } from "@/context/user-provider";
import bgModalRealiza from "@/assets/modalBG.jpeg";

export function LateralMenu({ onClose }: { onClose: () => void }) {
  const { user, logout } = useUser();
  const getIdUser = user?.idUser;

  return (
    <SheetContent
      className="h-full overflow-auto dark:bg-white bg-right bg-cover"
      side="left"
      onMouseLeave={onClose}
      onInteractOutside={onClose} // Fecha se clicar fora
      onEscapeKeyDown={onClose} // Fecha se pressionar "Esc"
      style={{backgroundImage: `url(${bgModalRealiza})`}}
    >
      <SheetTitle  className="sr-only">Menu Lateral</SheetTitle >
      <Link to={`/sistema/dashboard/${getIdUser}`} onClick={onClose}>
        <Button variant="ghost"  className="mt-2 w-full justify-start px-4 py-2 hover:bg-neutral-500">
          <Home  className="text-white" />
          <span className="ml-2 text-sm font-medium text-white">Início</span>
        </Button>
      </Link>

      <h3 className="pl-4 pt-6 text-xs text-white">Prestadores de serviço</h3>
      <Link to={`/sistema/serviceProviders/${getIdUser}`} onClick={onClose}>
        <Button variant="ghost"  className="mt-2 w-full justify-start px-4 py-2 hover:bg-neutral-500">
          <Users2  className="text-white" />
          <span className="ml-2 text-sm font-medium text-white">Ver prestadores</span>
        </Button>
      </Link>

      <Link to={`/sistema/dashboard/${getIdUser}`} onClick={onClose}>
        <Button variant="ghost"  className="mt-2 w-full justify-start px-4 py-2 hover:bg-neutral-500">
          <ChartPie  className="text-white" />
          <span className="ml-2 text-sm font-medium text-white">Ver BI’s</span>
        </Button>
      </Link>

      {/* <h3 className="pl-4 pt-6 text-xs text-zinc-800">Colaboradores e contratos</h3>
      <Link to={`/sistema/contracts/${getIdUser}`} onClick={onClose}>
        <Button variant="ghost" className="mt-1 w-full justify-start px-4 py-2">
          <File className="size-4 text-zinc-800" />
          <span className="ml-2 text-sm font-medium text-zinc-900">Meus contratos</span>
        </Button>
      </Link> */}

      <h3 className="pl-4 pt-6 text-xs text-white">Sobre a empresa</h3>
      <Link to={`/sistema/profile/${getIdUser}`} onClick={onClose}>
        <Button variant="ghost" className="mt-2 w-full justify-start px-4 py-2 hover:bg-neutral-500">
          <Building2  className="text-white" />
          <span className="ml-2 text-sm font-medium text-white">Empresa</span>
        </Button>
      </Link>

      <Link to={`/sistema/branch/${getIdUser}`} onClick={onClose}>
        <Button variant="ghost"  className="mt-2 w-full justify-start px-4 py-2 hover:bg-neutral-500">
          <Factory  className="text-white" />
          <span className="ml-2 text-sm font-medium text-white">Filiais</span>
        </Button>
      </Link>

      <Link to={`/sistema/employees/${getIdUser}`} onClick={onClose}>
        <Button variant="ghost"  className="mt-2 w-full justify-start px-4 py-2 hover:bg-neutral-500">
          <PersonStanding  className="text-white" />
          <span className="ml-2 text-sm font-medium text-white">Colaboradores</span>
        </Button>
      </Link>

      <h3 className="pl-4 pt-6 text-xs text-white">Funcionalidades</h3>
      <Link to={`/sistema/controlPanel/${user?.idUser}`}>
          <Button variant="ghost"  className="mt-2 w-full justify-start px-4 py-2 hover:bg-neutral-500">
            <LayoutPanelTop  className="text-white"/>
            <span className="ml-2 text-sm font-medium text-white">Painel de solicitação</span>
          </Button>
      </Link>

      <Link to={`/sistema/documents/${user?.idUser}`}>
          <Button variant="ghost" className="mt-2 w-full justify-start px-4 py-2 hover:bg-neutral-500">
          <Paperclip  className="text-white"/>
            <span className="ml-2 text-sm font-medium text-white">Painel do cliente</span>
          </Button>
      </Link>

      <div className="md:hidden">
      <h3 className="pl-4 pt-6 text-xs text-white">Gestão de perfil</h3>
      <Link to={`/profile-user/${user?.idUser}`}>
      <Button variant="ghost" className="mt-2 w-full justify-start px-4 py-2">
          <UserRound   className="text-white"/>
            <span className="ml-2 text-sm font-medium text-white">Perfil</span>
          </Button>
      </Link>

      <Link to={`/sistema/create-new-user/${user?.idUser}`}>
      <Button variant="ghost" className="mt-2 w-full justify-start px-4 py-2">
          <UserPlus   className="text-white"/>
            <span className="ml-2 text-sm font-medium text-white">Criar novo usuário</span>
          </Button>
      </Link>

      <Button variant="ghost" className="mt-2 w-full justify-start px-4 py-2" onClick={logout}>
          <LogOut className="text-white"/>
          <span className="ml-2 text-sm font-medium text-white">Sair</span>
      </Button>
      </div>
    </SheetContent>
  );
}
