import { useState, useEffect } from "react";
import { Bell, ChartNoAxesGantt, LogOut, Plus, User } from "lucide-react";
import { Link } from "react-router-dom";
import realizaLogo from "../../assets/logoRealiza/Background - Realiza.png";
import { Button } from "../ui/button";
import { Sheet, SheetTrigger, SheetContent } from "../ui/sheet";
import { LateralMenu } from "./realizaLateralMenu";
// import { ToggleTheme } from "../toggle-theme";
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
import { useBranch } from "@/context/Branch-provider";
import { Solicitation } from "@/pages/auth/realizaProfile/panelControl";
import { ScrollArea } from "@radix-ui/react-scroll-area";

interface ApiResponse {
  content: Solicitation[];
  totalPages: number;
}

export function Header() {
  const [clients, setClients] = useState<propsClient[]>([]);
  const { setClient } = useClient();
  const { branch, selectedBranch, setSelectedBranch } = useBranch();
  const { user, logout } = useUser();
  const [menuOpen, setMenuOpen] = useState(false);
  const [solicitations, setSolicitations] = useState<Solicitation[]>([]);

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
    fetchSolicitations();
  }, []);

  const handleSelectClient = async (id: string) => {
    try {
      const res = await axios.get(`${ip}/client/${id}`);
      setClient(res.data);
    } catch (err) {
      console.error("Erro ao selecionar cliente", err);
    }
  };

  const fetchSolicitations = async () => {
    // setLoading(true);
    try {
      const response = await axios.get<ApiResponse>(
        `${ip}/item-management/new-provider`,
      );
      console.log("solicitacao:", response.data.content);
      setSolicitations(response.data.content);
    } catch (err: any) {
      // setError(err);
    } finally {
      // setLoading(false);
    }
  };

  // Handlers de hover:
  const handleMouseEnter = () => setMenuOpen(true);
  const handleMouseLeave = () => setMenuOpen(false);

  return (
    <header className="dark:bg-primary relative p-5">
      <div>{/* seach */}</div>
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

        <div className="flex flex-col items-start">
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

          <div className="">
            <div>
              <span className="text-realizaBlue text-[14px]">Filial: </span>
              <select
                value={selectedBranch?.idBranch || ""}
                onChange={(e) => {
                  const selected = branch.find(
                    (b) => b.idBranch === e.target.value,
                  );
                  setSelectedBranch(selected || null);
                }}
                className="text-[12px]"
              >
                <option value="">Selecione uma filial</option>
                {Array.isArray(branch) &&
                  branch.map((b) => (
                    <option value={b.idBranch} key={b.idBranch}>
                      {b.name}
                    </option>
                  ))}
              </select>
            </div>
          </div>
          {/* Seleção de cliente */}
        </div>
        {/* Perfil do usuário e demais itens */}
        <div className="hidden items-center md:flex">
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <div className="cursor-pointer rounded-full bg-gray-300 p-2">
                <Bell />
              </div>
            </DropdownMenuTrigger>
            <DropdownMenuContent className="mr-32 flex flex-col gap-2 p-5">
              {/* Verificando se solicitations existe e se há itens pendentes */}
              {solicitations && solicitations.length > 0 ? (
                // Filtra solicitações com status "PENDING"
                <ScrollArea className="h-[40vh] w-[20vw]">
                  {solicitations.filter(
                    (solicitation) => solicitation.status === "PENDING",
                  ).length > 0 ? (
                    solicitations
                      .filter(
                        (solicitation) => solicitation.status === "PENDING",
                      )
                      .map((solicitation) => (
                        <div
                          className="border p-4 shadow-md"
                          key={solicitation.idSolicitation}
                        >
                          <div className="flex flex-col gap-2">
                            <div className="flex items-center gap-2">
                              <strong>Título:</strong>
                              <span>{solicitation.title}</span>
                            </div>
                            <div className="flex flex-col gap-1">
                              <strong>Detalhes da solicitação:</strong>{" "}
                              <span className="text-[14px]">{solicitation.details}</span>
                            </div>
                          </div>
                        </div>
                      ))
                  ) : (
                    <div className="flex items-center justify-center">
                      <span className="text-black">Nenhuma notificação</span>
                    </div>
                  )}
                </ScrollArea>
              ) : (
                <div>
                  <span className="text-black">Nenhuma notificação</span>
                </div>
              )}
            </DropdownMenuContent>
          </DropdownMenu>
          <div className="ml-12 flex items-center gap-8">
            {/* <ToggleTheme /> */}

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
