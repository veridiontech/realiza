import { Bell, ChartNoAxesGantt, Search, ArrowLeftRight } from "lucide-react";

import realizaLogo from "../../assets/realiza-logo.png";
import { Avatar, AvatarFallback, AvatarImage } from "../ui/avatar";
import { Button } from "../ui/button";
import { Sheet, SheetTrigger } from "../ui/sheet";
import { LateralMenu } from "./lateralMenu";
import { Link } from "react-router-dom";

export function Header() {
  return (
    <header className="relative p-5">
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
            <span className="text-xl mr-4 text-blue-600">Cliente Selecionado:</span>
            UrbanWear Co.
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
          <Button
            variant={"ghost"}
            className="ml-14 mr-5 rounded-full bg-zinc-100 p-2"
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
    </header>
  );
}
