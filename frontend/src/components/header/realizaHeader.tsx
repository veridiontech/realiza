import { useState, useEffect } from "react";
import { Bell, ChartNoAxesGantt, LogOut, Plus, User } from "lucide-react";
import { Link } from "react-router-dom";
import realizaLogo from "@/assets/logoRealiza/Logo Realiza Completo 1.png";
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
import { Solicitation } from "@/pages/auth/realizaProfile/panel-control/panelControl";
import { ScrollArea } from "@radix-ui/react-scroll-area";
import { useBoard } from "@/context/context-ultra/Board-provider";
import { useMarket } from "@/context/context-ultra/Market-provider";
import { useCenter } from "@/context/context-ultra/Center-provider";
import { useBranchUltra } from "@/context/context-ultra/BranchUltra-provider";
import bannerHeader from "@/assets/banner/Rectangle 42203.png"

interface ApiResponse {
  content: Solicitation[];
  totalPages: number;
}

export function Header() {
  const [clients, setClients] = useState<propsClient[]>([]);
  const { client, setClient } = useClient();
  const { branch, selectedBranch, setSelectedBranch } = useBranch();
  const { user, logout } = useUser();
  const [menuOpen, setMenuOpen] = useState(false);
  const [solicitations, setSolicitations] = useState<Solicitation[]>([]);
  const { boards, setSelectedBoard, selectedBoard } = useBoard()
  const { markets, setSelectedMarket, selectedMarket } = useMarket()
  const { center, selectedCenter, setSelectedCenter } = useCenter()
  const { branchUltra, setSelectedBranchUltra, selectedBranchUltra } = useBranchUltra()
  const getIdUser = user?.idUser;

  // Busca clientes
  useEffect(() => {
    const getAllClients = async () => {
      try {
        const tokenFromStorage = localStorage.getItem("tokenClient");
        const firstRes = await axios.get(`${ip}/client`, {
          params: { page: 0, size: 1000 },
          headers: { Authorization: `Bearer ${tokenFromStorage}` }
        });
        const totalPages = firstRes.data.totalPages;
        const requests = Array.from({ length: totalPages - 1 }, (_, i) =>
          axios.get(`${ip}/client`, { params: { page: i + 1, size: 1000 }, headers: { Authorization: `Bearer ${tokenFromStorage}` } }),
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
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res = await axios.get(`${ip}/client/${id}`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` }
        }
      );
      setClient(res.data);
    } catch (err) {
      console.error("Erro ao selecionar cliente", err);
    }
  };

  const fetchSolicitations = async () => {
    // setLoading(true);
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const response = await axios.get<ApiResponse>(
        `${ip}/item-management/new-provider`, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` }
      }
      );
      console.log("solicitacao:", response.data.content);
      setSolicitations(response.data.content);
    } catch (err: any) {
      // setError(err);
    } finally {
      // setLoading(false);
    }
  };

  console.log("diretorias: ", boards);
  console.log("diretoria selecionada:", selectedBoard);



  const handleMouseEnter = () => setMenuOpen(true);
  const handleMouseLeave = () => setMenuOpen(false);

  const pendingSolicitationsCount = solicitations.filter(
    (solicitation) => solicitation.status === "PENDING",
  ).length;

  if (client?.isUltragaz === true) {
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

            <Link to={`/sistema/dashboard/${getIdUser}`}>
              <img src={realizaLogo} alt="Logo" className="w-[6vw]" />
            </Link>
          </div>

          <div className="flex flex-col gap-3 items-center">
            <div className="flex items-center gap-4">
              <div className="hidden md:block text-realizaBlue mr-4 text-xl">
                Cliente Selecionado:
              </div>
              <div className="block md:hidden text-realizaBlue mr-4 text-xl">
                Cliente:
              </div>
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

            <div className="flex items-center gap-5">
              <div>
                <span className="text-realizaBlue text-[14px]">Diretoria: </span>
                <select
                  value={selectedBoard?.idBoard || ""}
                  onChange={(e) => {
                    const selected = boards.find(
                      (b) => b.idBoard === e.target.value,
                    );
                    setSelectedBoard(selected || null);
                  }}
                  className="text-[12px]"
                >
                  <option value="" disabled>Selecione um cliente</option>
                  {Array.isArray(branch) &&
                    boards.map((b) => (
                      <option value={b.idBoard} key={b.idBoard}>
                        {b.name}
                      </option>
                    ))}
                </select>
              </div>
              <div>
                <span className="text-realizaBlue text-[14px]">Mercado: </span>
                <select
                  value={selectedMarket?.idMarket || ""}
                  onChange={(e) => {
                    const selected = markets.find(
                      (b) => b.idMarket === e.target.value,
                    );
                    setSelectedMarket(selected || null);
                  }}
                  className="text-[12px]"
                >
                  <option value="">Selecione uma diretoria</option>
                  {Array.isArray(branch) &&
                    markets.map((b) => (
                      <option value={b.idMarket} key={b.idMarket}>
                        {b.name}
                      </option>
                    ))}
                </select>
              </div>
              <div>
                <span className="text-realizaBlue text-[14px]">Núcleo: </span>
                <select
                  value={selectedCenter?.idCenter || ""}
                  onChange={(e) => {
                    const selected = center.find(
                      (b) => b.idCenter === e.target.value,
                    );
                    setSelectedCenter(selected || null);
                  }}
                  className="text-[12px]"
                >
                  <option value="">Selecione um mercado</option>
                  {Array.isArray(center) &&
                    center.map((b) => (
                      <option value={b.idCenter} key={b.idCenter}>
                        {b.name}
                      </option>
                    ))}
                </select>
              </div>
              <div>
                <span className="text-realizaBlue text-[14px]">Unidade: </span>
                <select
                  value={selectedBranchUltra?.idBranch || ""}
                  onChange={(e) => {
                    const selected = branchUltra.find(
                      (b) => b.idBranch === e.target.value,
                    );
                    setSelectedBranchUltra(selected || null);
                  }}
                  className="text-[12px]"
                >
                  <option value="">Selecione uma filial</option>
                  {Array.isArray(center) &&
                    branchUltra.map((b) => (
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
          <div className="hidden items-center lg:flex md:flex">
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
                  <ScrollArea className="h-[40vh] w-[20vw] overflow-auto">
                    {solicitations.filter(
                      (solicitation) => solicitation.status === "PENDING",
                    ).length > 0 ? (
                      solicitations
                        .filter(
                          (solicitation) => solicitation.status === "PENDING",
                        )
                        .map((solicitation) => (
                          <div
                            className="border p-4 shadow-md border-l-[#F97316] border-l-[10px] "
                            key={solicitation.idSolicitation}
                          >
                            <div className="flex flex-col gap-2 ">
                              <div className="flex items-center gap-2 ">
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
                  <div className="w-10 h-10 md:w-12 md:h-12 rounded-full overflow-hidden flex items-center justify-center"> <ProfilePhoto /></div>
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

  return (
    <header className="bg-transparent relative p-5 h-[27vh] rounded-b-xl" style={{ backgroundImage: `url(${bannerHeader})` }}>
      <div className="flex items-center md:justify-between justify-center">
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
          <div className="hidden md:block">
            <Link to={`/sistema/dashboard/${getIdUser}`}>
              <img src={realizaLogo} alt="Logo" className="w-[6vw]" />
            </Link>
          </div>
        </div>
        {/* Perfil do usuário e demais itens */}
        <div className="hidden items-center gap-14 md:flex">
          <div className="flex gap-10 items-start">
            <div className="flex items-center gap-4">
              <div className="block md:hidden text-realizaBlue mr-4 text-xl">
                Cliente:
              </div>
              <select
                onChange={(e) => handleSelectClient(e.target.value)}
                defaultValue=""
                className="rounded-md border p-1 bg-transparent text-white w-[15vw]"
              >
                <option value="" disabled>
                  Selecione um cliente
                </option>
                {clients.map((client) => (
                  <option key={client.idClient} value={client.idClient} className="text-black">
                    {client.tradeName}
                  </option>
                ))}
              </select>
            </div>

            <div className="flex items-center gap-5">
              <div>
                <select
                  value={selectedBranch?.idBranch || ""}
                  onChange={(e) => {
                    const selected = branch.find(
                      (b) => b.idBranch === e.target.value,
                    );
                    setSelectedBranch(selected || null);
                  }}
                  className="rounded-md border p-1 bg-transparent text-white w-[15vw]"
                >
                  <option value="" className="text-black">Selecione uma filial</option>
                  {Array.isArray(branch) &&
                    branch.map((b) => (
                      <option value={b.idBranch} key={b.idBranch} className="text-black">
                        {b.name}
                      </option>
                    ))}
                </select>
              </div>
            </div>
            {/* Seleção de cliente */}
          </div>
          <div className="flex items-center">
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <div className="cursor-pointer rounded-full bg-gray-300 p-2 relative">
                  <Bell />
                  {/* Exibir contagem de notificações pendentes */}
                  {pendingSolicitationsCount > 0 && (
                    <span className="absolute top-0 right-0 rounded-full bg-red-500 text-white text-xs px-2 py-1">
                      {pendingSolicitationsCount}
                    </span>
                  )}
                </div>
              </DropdownMenuTrigger>
              <DropdownMenuContent className="mr-32 flex flex-col gap-2 p-5">
                {/* Verificando se solicitations existe e se há itens pendentes */}
                {solicitations && solicitations.length > 0 ? (
                  <ScrollArea className="h-[40vh] w-[20vw] overflow-auto">
                    {solicitations.filter(
                      (solicitation) => solicitation.status === "PENDING",
                    ).length > 0 ? (
                      solicitations
                        .filter(
                          (solicitation) => solicitation.status === "PENDING",
                        )
                        .map((solicitation) => (
                          <div
                            className="border p-4 shadow-md border-l-[#F97316] border-l-[10px] "
                            key={solicitation.idSolicitation}
                          >
                            <div className="flex flex-col gap-2 ">
                              <div className="flex items-center gap-2 ">
                                <strong>Título:</strong>
                                <span>{solicitation.title}</span>
                              </div>
                              <div className="flex flex-col gap-1">
                                <strong>Detalhes da solicitação:</strong>{" "}
                                <span className="text-[14px]">
                                  {solicitation.details}
                                </span>
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
            <div className="ml-12 hidden md:flex items-center gap-8">
              {/* <ToggleTheme /> */}

              <DropdownMenu>
                <DropdownMenuTrigger>
                  <div className="w-10 h-10 md:w-12 md:h-12 rounded-full border border-white overflow-hidden flex items-center justify-center"> <ProfilePhoto /></div>
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
      </div>
    </header>
  );
}