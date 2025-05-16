import { ChartNoAxesGantt, LogOut, User } from "lucide-react";
import { Link } from "react-router-dom";
import realizaLogo from "@/assets/logoRealiza/Logo Realiza Completo 1.png";
import { Button } from "../ui/button";
import { Sheet, SheetTrigger } from "../ui/sheet";
import { ProviderLateralMenu } from "./providerLateralMenu";
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
import { useSupplier } from "@/context/Supplier-context";
import axios from "axios";
import { ip } from "@/utils/ip";
import { useEffect } from "react";
import bannerHeader from "@/assets/banner/Rectangle 42203.png";
import { useState } from "react";

export function HeaderProvider() {
  const { user, logout } = useUser();
  const { supplier, setSupplier } = useSupplier();
  const [menuOpen, setMenuOpen] = useState(false);

  const getSupplier = async () => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res = await axios.get(`${ip}/supplier/${user?.supplier}`, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` }
      }
      );
      setSupplier(res.data);
    } catch (err) {
      console.log("erro ao buscar supplier:", err);
    }
  };

  useEffect(() => {
    if (user?.supplier) {
      getSupplier();
    }
  }, []);

  if (user?.role === "ROLE_SUBCONTRACTOR_RESPONSIBLE") {
    return (
      <header className="dark:bg-primary relative p-5">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-5">
            <div className="flex items-center">
              <Sheet open={menuOpen} onOpenChange={setMenuOpen}>
                <SheetTrigger asChild>
                  <Button
                    variant={"ghost"}
                    className="hover:bg-realizaBlue/80 bg-realizaBlue mr-5 w-fit rounded p-2"
                  >
                    <ChartNoAxesGantt className="text-white" />
                  </Button>
                </SheetTrigger>
                <ProviderLateralMenu onClose={() => setMenuOpen(false)}/>
              </Sheet>
              <Link to={`/fornecedor/home/${user?.idUser}`}>
                <img src={realizaLogo} alt="" className="w-[6vw]" />
              </Link>
            </div>
            <div>
              <div className="flex items-center gap-2">
                <span>Fornecedor:</span>
                <h1>{supplier?.corporateName}</h1>
              </div>
            </div>
          </div>

          <div className="hidden items-center md:flex">
            <div className="ml-12 flex items-center gap-8">
              <div className="flex items-center gap-1">
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

  return (
    <header
      className="relative h-[27vh] rounded-b-xl bg-transparent p-5"
      style={{ backgroundImage: `url(${bannerHeader})` }}
    >
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-5">
          <div className="flex items-center">
            <Sheet open={menuOpen} onOpenChange={setMenuOpen}>
              <SheetTrigger asChild>
                <Button
                  variant={"ghost"}
                  className="hover:bg-realizaBlue/80 bg-realizaBlue mr-5 w-fit rounded p-2"
                >
                  <ChartNoAxesGantt className="text-white" />
                </Button>
              </SheetTrigger>
              <ProviderLateralMenu onClose={() => setMenuOpen(false)}/>
            </Sheet>
            <Link to={`/fornecedor/home/${user?.idUser}`}>
              <img src={realizaLogo} alt="" className="w-[6vw]" />
            </Link>
          </div>
        </div>

        <div className="hidden items-center md:flex">
          <div className="ml-12 flex items-center gap-28">
            <div>
              <div className="flex items-center gap-2 rounded-md border border-white p-2 text-white">
                <div className="flex items-center gap-2">
                  <User />
                  <span>Fornecedor:</span>
                </div>
                <h1>{supplier?.corporateName}</h1>
              </div>
            </div>
            <div className="flex items-center gap-1">
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
