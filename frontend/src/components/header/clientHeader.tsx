import { LogOut, User, Menu } from "lucide-react";
import { Link } from "react-router-dom";
import realizaLogo from "@/assets/logoRealiza/Logo Realiza Completo 1.png";
import { Sheet, SheetContent, SheetTrigger } from "../ui/sheet";
import { ClientLateralMenu } from "./clientLateralMenu";
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
import bannerHeader from "@/assets/banner/Rectangle 42203.png";
import { propsBranch } from "@/types/interfaces";

interface BranchFromAPI {
  id: string;
  name: string;
}

export function HeaderClient() {
  const [menuOpen, setMenuOpen] = useState(false);
  const { user, logout } = useUser();
  const { selectedBranch, setSelectedBranch } = useBranch();
  const [branches, setBranches] = useState<propsBranch[]>([]);

  const fetchUserBranches = async () => {
    console.log("Iniciando processo de busca de filiais.");
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const userBranches = JSON.parse(localStorage.getItem("userBranches") || "[]");

      console.log("Token do localStorage:", tokenFromStorage);
      console.log("userBranches do localStorage:", userBranches);

      if (userBranches.length > 0 && tokenFromStorage) {
        console.log("userBranches e token encontrados. Preparando a requisição.");
        const branchIdsQuery = userBranches.map((id: string) => `branchIds=${id}`).join('&');
        console.log("Query de IDs de filiais:", branchIdsQuery);

        const res = await axios.get<BranchFromAPI[]>(`${ip}/branch/find-by-access?${branchIdsQuery}`, {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        });

        console.log("Resposta da API de filiais:", res.data);

        if (res.data.length > 0) {
          const mappedBranches: propsBranch[] = res.data.map(branch => ({
            idBranch: branch.id,
            name: branch.name,
            email: '',
            cnpj: '',
            address: '',
            telephone: '',
            cep: '',
            state: '',
            row: '',
            actions: '',
            client: '',
          }));

          console.log("Filiais mapeadas para o estado:", mappedBranches);
          setBranches(mappedBranches);
          setSelectedBranch(mappedBranches[0]);
          console.log("Estado de filiais atualizado com sucesso.");
        } else {
          console.log("A API retornou um array de filiais vazio.");
        }
      } else {
        console.log("Condição para buscar filiais não satisfeita (sem token ou sem userBranches).");
      }
    } catch (err) {
      console.error("Erro no processo de busca de filiais:", err);
    }
  };

  useEffect(() => {
    console.log("useEffect acionado. user.role:", user?.role);
    if (user?.role === "ROLE_CLIENT_RESPONSIBLE" || user?.role === "ROLE_CLIENT_MANAGER") {
      fetchUserBranches();
    }
  }, [user?.role]);

  const handleMouseEnter = () => setMenuOpen(true);
  const handleMouseLeave = () => setMenuOpen(false);

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
            <Link to={`/cliente/home/${user?.idUser}`}>
              <img src={realizaLogo} alt="Logo" className="w-[6vw]" />
            </Link>
          </div>

          <div>
            {branches.length > 0 ? (
              <div className="flex items-center gap-1 text-[20px] text-white">
                <span>Filial:</span>
                <select
                  value={selectedBranch?.idBranch || ''}
                  onChange={(e) => {
                    const selected = branches.find(
                      (b) => b.idBranch === e.target.value,
                    );
                    setSelectedBranch(selected || null);
                  }}
                  className="bg-transparent border border-white rounded-md text-white p-1"
                >
                  {branches.map((b) => (
                    <option value={b.idBranch} key={b.idBranch}>
                      {b.name}
                    </option>
                  ))}
                </select>
              </div>
            ) : (
              <div className="flex items-center gap-1 text-[20px]">
                <span className="text-white">Filial:</span>
                <Skeleton className="w-[150px] h-6 bg-gray-400 rounded-full" />
              </div>
            )}
          </div>
        </div>

        <div className="hidden items-center md:flex">
          <div className="ml-12 flex items-center gap-8">
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