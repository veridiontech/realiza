import {
  ArrowLeftRight,
  Bell,
  ChartNoAxesGantt,
  LogOut,
  Paperclip,
  Plus,
  Search,
  User,
} from "lucide-react";
import { Link } from "react-router-dom";

import realizaLogo from "../../assets/logoRealiza/Background - Realiza.png";
// import { Avatar, AvatarFallback, AvatarImage } from "../ui/avatar";
import { Button } from "../ui/button";
import { Sheet, SheetTrigger } from "../ui/sheet";
import { LateralMenu } from "./realizaLateralMenu";
import { ToggleTheme } from "../toggle-theme";
import { useUser } from "@/context/user-provider";
import axios from "axios";
import { ip } from "@/utils/ip";
import { useEffect, useState } from "react";
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
  const { setClient } = useClient();
  const { user, logout } = useUser();

  const getIdUser = user?.idUser;

  const getClients = async () => {
    try {
      const res = await axios.get(`${ip}/client`);
      setClients(res.data.content);
      console.log(res.data.content);
    } catch (err) {
      console.log("erro ao puxar clientes", err);
    }
  };
  const handleSelectClient = async (id: string) => {
    try {
      const res = await axios.get(`${ip}/client/${id}`);
      setClient(res.data);
    } catch (err) {
      console.log("erro ao selecionar cliente", err);
    }
  };

  useEffect(() => {
    getClients();
  }, []);

  return (
    <header className="dark:bg-primary relative p-5">
      <div className="flex items-center justify-between">
        <div className="flex items-center">
          <Sheet>
            <SheetTrigger asChild>
              <Button
                variant={"ghost"}
                className="mr-5 w-fit rounded bg-blue-600 p-2 hover:bg-blue-500/80"
              >
                <ChartNoAxesGantt className="text-white" />
              </Button>
            </SheetTrigger>
            <LateralMenu />
          </Sheet>
          <Link to={`/sistema/select-client/${getIdUser}`}>
            <img src={realizaLogo} alt="" className="w-[6vw]" />
          </Link>
        </div>
        <div className="flex items-center gap-4">
          <span>
            <span className="mr-4 text-xl text-blue-600">
              Cliente Selecionado:
            </span>
            <select
              onChange={(e) => handleSelectClient(e.target.value)}
              defaultValue=""
              className="rounded-md border p-1 text-black"
            >
              <option value="" disabled>
                Selecione um cliente
              </option>{" "}
              {/* Placeholder */}
              {clients.map((client) => (
                <option key={client.idClient} value={client.idClient}>
                  {client.tradeName}
                </option>
              ))}
            </select>
          </span>
          <button
            className="flex h-8 w-8 items-center justify-center rounded-full bg-blue-200 hover:bg-blue-600"
            title="Trocar"
          >
            <Link to={`/sistema/select-client/${getIdUser}`}>
              <ArrowLeftRight className="h-6 w-6 hover:text-white" />
            </Link>
          </button>
        </div>

        <div className="hidden items-center md:flex">
          <div className="flex w-[320px] items-center gap-3 rounded-full border border-none bg-zinc-100 px-4 py-2">
            <Search className="size-5 text-zinc-900" />
            <input
              className="h-auto flex-1 border-0 bg-transparent p-0 text-sm outline-none"
              placeholder="Pesquise aqui..."
            />
          </div>
          <div className="ml-12 flex items-center gap-8">
            <ToggleTheme />
            <div className="flex items-center gap-1">
              <Button
                variant={"ghost"}
                className="dark:bg-primary-foreground w-[2.2vw] rounded-full bg-zinc-100 p-2"
              >
                <Bell size={24} />
              </Button>
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
      </div>
    </header>
  );
}
