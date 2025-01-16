import { ArrowLeftRight, Bell, ChartNoAxesGantt, Search } from "lucide-react";
import { Link } from "react-router-dom";

import realizaLogo from "../../assets/realiza-logo.png";
import { Avatar, AvatarFallback, AvatarImage } from "../ui/avatar";
import { Button } from "../ui/button";
import { Sheet, SheetTrigger } from "../ui/sheet";
import { LateralMenu } from "./lateralMenu";
import { ToggleTheme } from "../toggle-theme";

export function Header() {
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
          <Link to="/">
            <img src={realizaLogo} alt="" />
          </Link>
        </div>
        <div className="flex items-center gap-4">
          <span>
            <span className="mr-4 text-xl text-blue-600">
              Cliente Selecionado:
            </span>
            UltraGaz BR
          </span>
          <button
            className="flex h-8 w-8 items-center justify-center rounded-full bg-blue-200 hover:bg-blue-600"
            title="Trocar"
          >
            <Link to={"/selectCLient"}>
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
          <div className="flex items-center gap-8 ml-12">
            <ToggleTheme />
            <div className="flex items-center gap-1">
              <Button
                variant={"ghost"}
                className=" rounded-full bg-zinc-100 w-[2.2vw] p-2 dark:bg-primary-foreground"
              >
                <Bell size={24} />
              </Button>
              <Link to={"/profile-user"}>
                <Avatar>
                  <AvatarImage src="https://github.com/shadcn.png" />
                  <AvatarFallback>CN</AvatarFallback>
                </Avatar>
              </Link>
            </div>
          </div>
        </div>
      </div>
    </header>
  );
}
