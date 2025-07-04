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
  Cog,
} from "lucide-react";
import { Button } from "../ui/button";
import { SheetContent, SheetTitle } from "../ui/sheet";
import { Link } from "react-router-dom";
import { useUser } from "@/context/user-provider";
import bgModalRealiza from "@/assets/modalBG.jpeg";
import { RealizaMenuItem } from "./realizaMenuItem";

import { useState } from "react";


export function LateralMenu({ onClose }: { onClose: () => void }) {
  const { user, logout } = useUser();
  const getIdUser = user?.idUser;

  const [, setActiveMenuKey] = useState<string | null>(null);

  return (
    <SheetContent
      className="h-full overflow-auto dark:bg-white bg-right bg-cover"
      side="left"
      onMouseLeave={onClose}
      // onInteractOutside={onClose} // Fecha se clicar fora
      onEscapeKeyDown={onClose} // Fecha se pressionar "Esc"
      style={{ backgroundImage: `url(${bgModalRealiza})` }}
    >
      <SheetTitle className="sr-only">Menu Lateral</SheetTitle>

      <RealizaMenuItem
        to={`/sistema/dashboard/${getIdUser}`}
        icon={<Home />}
        label="Início"
        menuKey="home"

        setActiveMenuKey={setActiveMenuKey}
        onClick={onClose}
      />

      <h3 className="pl-4 pt-6 text-xs text-white">Prestadores de serviço</h3>

      <RealizaMenuItem
        to={`/sistema/serviceProviders/${getIdUser}`}
        icon={<Users2 />}
        label="Ver contratos"
        menuKey="ver-contratos"

        setActiveMenuKey={setActiveMenuKey}
        onClick={onClose}
      />
      <RealizaMenuItem
        to={`/sistema/dashboard-details/${getIdUser}`}
        icon={<ChartPie />}
        label="Ver BI’s"
        menuKey="ver-bis"

        setActiveMenuKey={setActiveMenuKey}
        onClick={onClose}
      />

      

      <h3 className="pl-4 pt-6 text-xs text-white">Gestão de fornecedores</h3>

      <RealizaMenuItem
        to={`/sistema/table-providers/${getIdUser}`}
        icon={<Users2 />}
        label="Fornecedores"
        menuKey="fornecedores"

        setActiveMenuKey={setActiveMenuKey}
        onClick={onClose}
      />

      {/* <h3 className="pl-4 pt-6 text-xs text-zinc-800">Colaboradores e contratos</h3>
      <Link to={`/sistema/contracts/${getIdUser}`} onClick={onClose}>
        <Button variant="ghost" className="mt-1 w-full justify-start px-4 py-2">
          <File className="size-4 text-zinc-800" />
          <span className="ml-2 text-sm font-medium text-zinc-900">Meus contratos</span>
        </Button>
      </Link> */}

      <h3 className="pl-4 pt-6 text-xs text-white">Sobre a empresa</h3>
      <RealizaMenuItem
        to={`/sistema/profile/${getIdUser}`}
        icon={<Building2 />}
        label="Empresa"
        menuKey="empresa"
        setActiveMenuKey={setActiveMenuKey}
        onClick={onClose}
      />
      <RealizaMenuItem
        to={`/sistema/branch/${getIdUser}`}
        icon={<Factory />}
        label="Filiais"
        menuKey="filiais"
        setActiveMenuKey={setActiveMenuKey}
        onClick={onClose}
      />
      <RealizaMenuItem
        to={`/sistema/employees/${getIdUser}`}
        icon={<PersonStanding />}
        label="Colaboradores"
        menuKey="colaboradores"

        setActiveMenuKey={setActiveMenuKey}
        onClick={onClose}
      />

      <h3 className="pl-4 pt-6 text-xs text-white">Funcionalidades</h3>

      <RealizaMenuItem
        to={`/sistema/configPanel/${getIdUser}`}
        icon={<Cog />}
        label="Configurações gerais"
        menuKey="configuracoes"

        setActiveMenuKey={setActiveMenuKey}
        onClick={onClose}
      />
      <RealizaMenuItem
        to={`/sistema/controlPanel/${getIdUser}`}
        icon={<LayoutPanelTop />}
        label="Painel de solicitação"
        menuKey="painel-solicitacao"

        setActiveMenuKey={setActiveMenuKey}
        onClick={onClose}
      />
      <RealizaMenuItem
        to={`/sistema/documents/${getIdUser}`}
        icon={<Paperclip />}
        label="Painel do cliente"
        menuKey="painel-cliente"

        setActiveMenuKey={setActiveMenuKey}
        onClick={onClose}
      />

      <div className="md:hidden">
        <h3 className="pl-4 pt-6 text-xs text-white">Gestão de perfil</h3>
        <Link to={`/profile-user/${user?.idUser}`}>
          <Button
            variant="ghost"
            className="mt-2 w-full justify-start px-4 py-2"
          >
            <UserRound className="text-white" />
            <span className="ml-2 text-sm font-medium text-white">Perfil</span>
          </Button>
        </Link>

        <Link to={`/sistema/create-new-user/${user?.idUser}`}>
          <Button
            variant="ghost"
            className="mt-2 w-full justify-start px-4 py-2"
          >
            <UserPlus className="text-white" />
            <span className="ml-2 text-sm font-medium text-white">
              Criar novo usuário
            </span>
          </Button>
        </Link>

        <Button
          variant="ghost"
          className="mt-2 w-full justify-start px-4 py-2"
          onClick={logout}
        >
          <LogOut className="text-white" />
          <span className="ml-2 text-sm font-medium text-white">Sair</span>
        </Button>
      </div>
    </SheetContent>
  );
}
