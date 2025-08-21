import { ChartNoAxesGantt, LogOut, Plus, User, Menu } from "lucide-react";
import { Link } from "react-router-dom";
import realizaLogo from "@/assets/logoRealiza/Logo Realiza Completo 1.png";
import { Button } from "../ui/button";
import { Sheet, SheetContent, SheetTrigger } from "../ui/sheet";
import { ClientLateralMenu } from "./clientLateralMenu";
// import { ToggleTheme } from "../toggle-theme";
import { Skeleton } from "@/components/ui/skeleton";
import { useUser } from "@/context/user-provider";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { ProfilePhoto } from "./profile-photo";
import { useEffect, useState } from "react";
import { ip } from "@/utils/ip";
import axios from "axios";
import { useBranch } from "@/context/Branch-provider";
import { useClient } from "@/context/Client-Provider";
import { propsBranch } from "@/types/interfaces";
import bannerHeader from "@/assets/banner/Rectangle 42203.png";

export function HeaderClient() {
  const [menuOpen, setMenuOpen] = useState(false);
  const { user, logout } = useUser();
  const [clients, setClients] = useState<any>(null);
  const { branch, selectedBranch, setSelectedBranch } = useBranch();
  const [uniqueBranch, setUniqueBranch] = useState<propsBranch | null>(null);
  const { client, setClient } = useClient();

  const getBranch = async () => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res = await axios.get(`${ip}/branch/${user?.branch}`, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });
      setUniqueBranch(res.data);
      setSelectedBranch(res.data);
    } catch (err) {
      console.log("erro ao buscar filial:", err);
    }
  };

  const getClientWithUser = async () => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res = await axios.get(
        `${ip}/client/find-by-branch/${user?.branch}`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        }
      );
      setClients(res.data);
      setClient(res.data);
      fetchBranchesByClient(res.data.idClient);
    } catch (err) {
      console.log(err);
    }
  };

  const fetchBranchesByClient = async (idClient: string) => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res = await axios.get(
        `${ip}/branch/filtered-client?idSearch=${idClient}`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        }
      );
      setSelectedBranch(res.data);
    } catch (err) {
      console.error("Erro ao buscar filiais:", err);
    }
  };

  useEffect(() => {
    if (user?.role === "ROLE_CLIENT_MANAGER") {
      getBranch();
    }
  }, [user?.role === "ROLE_CLIENT_MANAGER"]);

  useEffect(() => {
    if (user?.branch && user?.role === "ROLE_CLIENT_RESPONSIBLE") {
      getClientWithUser();
    }
  }, [user?.branch]);

  useEffect(() => {
    if (client && client.idClient && user?.role === "ROLE_CLIENT_RESPONSIBLE") {
      fetchBranchesByClient(client.idClient);
    }
  }, [client?.idClient]);

  const handleMouseEnter = () => setMenuOpen(true);
  const handleMouseLeave = () => setMenuOpen(false);

  if (user?.role === "ROLE_CLIENT_MANAGER") {
    return (
      <header
        className="dark:bg-primary relative p-5 h-[27vh]"
        style={{ backgroundImage: `url(${bannerHeader})` }}
      >
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-8">
            <div
              onMouseEnter={handleMouseEnter}
              onMouseLeave={handleMouseLeave}
              className="flex items-center"
            >
              <Sheet open={menuOpen} onOpenChange={setMenuOpen}>
                <SheetTrigger asChild>
                  <Menu
                    size={40}
                    className="text-white border p-2 rounded-full"
                  />
                </SheetTrigger>
                <SheetContent
                  className="h-full overflow-auto dark:bg-white"
                  side="left"
                  onMouseEnter={handleMouseEnter}
                  onMouseLeave={handleMouseLeave}
                >
                  <ClientLateralMenu onClose={() => setMenuOpen(false)} />
                </SheetContent>
              </Sheet>
              <Link to={`/cliente/home/${user.idUser}`}>
                <img src={realizaLogo} alt="Logo" className="w-[6vw]" />
              </Link>
            </div>

            <div>
              <div>
                {uniqueBranch ? (
                  <div className="flex items-center gap-1 text-[20px]">
                    <span className="text-white">Filial:</span>
                    <h1 className="font-semibold text-white">
                      {uniqueBranch.name}
                    </h1>
                  </div>
                ) : (
                  <div className="flex items-center">
                    <span>Filial:</span>
                    <Skeleton />
                  </div>
                )}
              </div>
            </div>

            {/* <div>
            {clients ? (
              <h1>Empresa: {clients?.corporateName}</h1>
            ) : (
              <div className="flex items-center gap-1">
                <h1>Empresa:</h1>
                <Skeleton className="h-[10px] w-[100px] rounded-full" />
              </div>
            )}

            <div className="flex items-center gap-1">
              <h2>Filiais:</h2>{" "}
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
          </div> */}
          </div>

          <div className="hidden items-center md:flex">
            {/* <div className="flex w-[320px] items-center gap-3 rounded-full border border-none bg-zinc-100 px-4 py-2">
            <Search className="size-5 text-zinc-900" />
            <input
              className="h-auto flex-1 border-0 bg-transparent p-0 text-sm outline-none"
              placeholder="Pesquise aqui..."
            />
          </div> */}
            <div className="ml-12 flex items-center gap-8">
              {/* <ToggleTheme /> */}
              <div className="flex items-center gap-1">
                {/* <Button
                variant={"ghost"}
                className="dark:bg-primary-foreground w-[2.2vw] rounded-full bg-zinc-100 p-2"
              >
                <Bell size={24} />
              </Button> */}
                {/* <Link to={`/profile-user`}>
                  <Avatar>
                    <AvatarImage src="https://github.com/shadcn.png" />
                    <AvatarFallback>CN</AvatarFallback>
                  </Avatar>
                </Link> */}

                <DropdownMenu>
                  <DropdownMenuTrigger className="border border-white rounded-full">
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

  return (
    <header className="dark:bg-primary relative p-5">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-8">
          <div
            onMouseEnter={handleMouseEnter}
            onMouseLeave={handleMouseLeave}
            className="flex items-center"
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
                <ClientLateralMenu onClose={() => setMenuOpen(false)} />
              </SheetContent>
            </Sheet>
            <Link to={`/cliente/home/${user?.idUser}`}>
              <img src={realizaLogo} alt="" className="w-[6vw]" />
            </Link>
          </div>

          <div>
            {clients ? (
              <h1>Empresa: {clients?.corporateName}</h1>
            ) : (
              <div className="flex items-center gap-1">
                <h1>Empresa:</h1>
                <Skeleton className="h-[10px] w-[100px] rounded-full" />
              </div>
            )}

            <div className="flex items-center gap-1">
              <h2>Filiais:</h2>{" "}
              <select
                value={selectedBranch?.idBranch || ""}
                onChange={(e) => {
                  const selected = branch.find(
                    (b) => b.idBranch === e.target.value
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
        </div>

        <div className="hidden items-center md:flex">
          {/* <div className="flex w-[320px] items-center gap-3 rounded-full border border-none bg-zinc-100 px-4 py-2">
            <Search className="size-5 text-zinc-900" />
            <input
              className="h-auto flex-1 border-0 bg-transparent p-0 text-sm outline-none"
              placeholder="Pesquise aqui..."
            />
          </div> */}
          <div className="ml-12 flex items-center gap-8">
            {/* <ToggleTheme /> */}
            <div className="flex items-center gap-1">
              {/* <Button
                variant={"ghost"}
                className="dark:bg-primary-foreground w-[2.2vw] rounded-full bg-zinc-100 p-2"
              >
                <Bell size={24} />
              </Button> */}
              {/* <Link to={`/profile-user`}>
                  <Avatar>
                    <AvatarImage src="https://github.com/shadcn.png" />
                    <AvatarFallback>CN</AvatarFallback>
                  </Avatar>
                </Link> */}

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
                  <Link to={`/cliente/create-manager/${user?.idUser}`}>
                    <DropdownMenuItem className="cursor-pointer hover:bg-gray-200">
                      <div className="flex items-center gap-1">
                        <Plus />
                        <p>Criar gerente</p>
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