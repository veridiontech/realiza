import { useState, useEffect } from "react";
import {
  Bell,
  ChartNoAxesGantt,
  LogOut,
  Paperclip,
  Plus,
  Search,
  User,
  LayoutPanelTop,
} from "lucide-react";
import { Link } from "react-router-dom";
import realizaLogo from "../../assets/logoRealiza/Background - Realiza.png";
import { Button } from "../ui/button";
import { Sheet, SheetTrigger, SheetContent } from "../ui/sheet";
import { LateralMenu } from "./realizaLateralMenu";
import { ToggleTheme } from "../toggle-theme";
import { useUser } from "@/context/user-provider";
import axios from "axios";
import { ip } from "@/utils/ip";
import { useClient } from "@/context/Client-Provider";
import { propsClient } from "@/types/interfaces";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { ProfilePhoto } from "./profile-photo";

export function Header() {
  const [clients, setClients] = useState<propsClient[]>([]);
  const { setClient, branches} = useClient();
  const { user, logout } = useUser();
  const [menuOpen, setMenuOpen] = useState(false);

  const getIdUser = user?.idUser;

  // Busca clientes
  useEffect(() => {
    const getAllClients = async () => {
      try {
        const firstRes = await axios.get(`${ip}/client`, {
          params: { page: 0, size: 100 },
        });
        const totalPages = firstRes.data.totalPages;
        const requests = Array.from({ length: totalPages - 1 }, (_, i) =>
          axios.get(`${ip}/client`, { params: { page: i + 1, size: 100 } }),
        );

        const responses = await Promise.all(requests);
        const allClients = [
          firstRes.data.content,
          ...responses.map((res) => res.data.content),
        ].flat();

        setClients(allClients);
      } catch (err) {
        console.error("Erro ao puxar clientes", err);
      }
    };

    getAllClients();
  }, []);

  const handleSelectClient = async (id: string) => {
    try {
      const res = await axios.get(`${ip}/client/${id}`);
      setClient(res.data);
    } catch (err) {
      console.error("Erro ao selecionar cliente", err);
    }
  };
  

  // Handlers de hover:
  const handleMouseEnter = () => setMenuOpen(true);
  const handleMouseLeave = () => setMenuOpen(false);

  return (
    <header className="dark:bg-primary relative p-5">
      <div className="flex items-center justify-between">
        {/* Botão que abre o menu lateral via hover */}
        <div className="flex items-center">
          <div
            className="relative"
            onMouseEnter={handleMouseEnter}
            onMouseLeave={handleMouseLeave}
          >
            <Sheet open={menuOpen} onOpenChange={setMenuOpen}>
              <SheetTrigger asChild>
                <Button
                  variant={"ghost"}
                  className="hover:bg-realizaBlue/80 bg-realizaBlue mr-5 w-fit rounded p-2"
                >
                  <ChartNoAxesGantt className="text-white" />
                </Button>
              </SheetTrigger>
              <SheetContent
                className="h-full overflow-auto dark:bg-white"
                side="left"
                onMouseEnter={handleMouseEnter}
                onMouseLeave={handleMouseLeave}
              >
                <LateralMenu onClose={() => setMenuOpen(false)} />
              </SheetContent>
            </Sheet>
          </div>

          <Link to={`/sistema/select-client/${getIdUser}`}>
            <img src={realizaLogo} alt="Logo" className="w-[6vw]" />
          </Link>
        </div>

        <div>
          <div>
            <span>Filial: </span>
            <select defaultValue="">
              <option value="" disabled>Selecione uma filial</option>
              {Array.isArray(branches) && branches.map((branch) => (
                <option value="" key={branch.idBranch}>{branch.name}</option>
              ))}
            </select>
          </div>
        </div>

        {/* Seleção de cliente */}
        <div className="flex items-center gap-4">
          <span className="text-realizaBlue mr-4 text-xl">
            Cliente Selecionado:
          </span>
          <select
            onChange={(e) => handleSelectClient(e.target.value)}
            defaultValue=""
            className="rounded-md border p-1 text-black"
          >
            <option value="" disabled>
              Selecione um cliente
            </option>
            {clients.map((client) => (
              <option key={client.idClient} value={client.idClient}>
                {client.tradeName}
              </option>
            ))}
          </select>
        </div>

        {/* Perfil do usuário e demais itens */}
        <div className="hidden items-center md:flex">
          <div className="flex w-[320px] items-center gap-3 rounded-full border bg-zinc-100 px-4 py-2">
            <Search className="size-5 text-zinc-900" />
            <input
              className="h-auto flex-1 border-0 bg-transparent p-0 text-sm outline-none"
              placeholder="Pesquise aqui..."
            />
          </div>
          <div className="ml-12 flex items-center gap-8">
            <ToggleTheme />
            <Button
              variant={"ghost"}
              className="dark:bg-primary-foreground w-[2.2vw] rounded-full bg-zinc-100 p-2"
            >
              <Bell size={24} />
            </Button>
            <DropdownMenu>
              <DropdownMenuTrigger>
                <ProfilePhoto />
              </DropdownMenuTrigger>
              <DropdownMenuContent className="dark:bg-primary mr-5">
                <DropdownMenuLabel>
                  {user?.firstName} {user?.surname}
                </DropdownMenuLabel>
                <DropdownMenuSeparator />
                <Link to={`/profile-user/${user?.idUser}`}>
                  <DropdownMenuItem className="cursor-pointer hover:bg-gray-200">
                    <div className="flex items-center gap-1">
                      <User />
                      <p>Perfil</p>
                    </div>
                  </DropdownMenuItem>
                </Link>
                <Link to={`/sistema/controlPanel/${user?.idUser}`}>
                  <DropdownMenuItem className="cursor-pointer hover:bg-gray-200">
                    <div className="flex items-center gap-1">
                      <LayoutPanelTop />
                      <p>Painel de Solicitações</p>
                    </div>
                  </DropdownMenuItem>
                </Link>
                <Link to={`/sistema/documents/${user?.idUser}`}>
                  <DropdownMenuItem className="cursor-pointer hover:bg-gray-200">
                    <div className="flex items-center gap-1">
                      <Paperclip />
                      <p>Gestão de documentos</p>
                    </div>
                  </DropdownMenuItem>
                </Link>
                <Link to={`/sistema/create-new-user/${user?.idUser}`}>
                  <DropdownMenuItem className="cursor-pointer hover:bg-gray-200">
                    <div className="flex items-center gap-1">
                      <Plus />
                      <p>Criar usuário</p>
                    </div>
                  </DropdownMenuItem>
                </Link>
                <DropdownMenuItem
                  onClick={logout}
                  className="cursor-pointer hover:bg-gray-200"
                >
                  <div className="flex items-center gap-1">
                    <LogOut />
                    <p>Sair</p>
                  </div>
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          </div>
        </div>
      </div>
    </header>
  );
}
