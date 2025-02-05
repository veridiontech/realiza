import {
  Bell,
  ChartNoAxesGantt,
  LogOut,
  Search,
  User,
} from "lucide-react";
import { Link } from "react-router-dom";
import realizaLogo from "../../assets/logoRealiza/Background - Realiza.png";
import { Button } from "../ui/button";
import { Sheet, SheetTrigger } from "../ui/sheet";
import { ClientLateralMenu } from "./clientLateralMenu";
import { ToggleTheme } from "../toggle-theme";
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

export function HeaderClient() {
  const { user, logout } = useUser();

  const getIdUser = user?.idUser;

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
            <ClientLateralMenu />
          </Sheet>
          <Link to={`/cliente/contracts/${getIdUser}`}>
            <img src={realizaLogo} alt="" className="w-[6vw]" />
          </Link>
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
