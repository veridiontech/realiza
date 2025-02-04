import axios from "axios";
import React, { createContext, useContext, useEffect, useState } from "react";
import { jwtDecode } from "jwt-decode";
import { propsUser } from "@/types/interfaces";
import { ip } from "@/utils/ip";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { useLocation, useNavigate } from "react-router-dom";

interface UserContextProps {
  user: propsUser | null;
  authUser: boolean;
  setUser: React.Dispatch<React.SetStateAction<propsUser | null>>;
  setAuthUser: (auth: boolean) => void;
  logout: () => void;
}

const UserContext = createContext<UserContextProps | undefined>(undefined);

export function useUser() {
  const context = useContext(UserContext);
  if (!context) {
    throw new Error("O UserProvider não está configurado corretamente.");
  }
  return context;
}

export function UserProvider({ children }: { children: React.ReactNode }) {
  const [authUser, setAuthUser] = useState(false);
  const [user, setUser] = useState<propsUser | null>(null);
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    validateTokenAndFetchUser();
  }, []);

  const isTokenValid = (token: string): boolean => {
    try {
      const decoded: any = jwtDecode(token);
      const currentTime = Math.floor(Date.now() / 1000);
      return decoded.exp > currentTime;
    } catch (error) {
      console.error("Erro ao decodificar o token:", error);
      return false;
    }
  };

  const validateTokenAndFetchUser = async () => {
    const token = localStorage.getItem("tokenClient");
    const userId = localStorage.getItem("userId");

    
    

    if (token && isTokenValid(token) && userId) {
         try {
          const res = await axios.get(`${ip}/user/manager/${userId}`, {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          });
          console.log("Dados do usuário ADMIN:", );

          if (res.data) {
            setUser(res.data);
            setAuthUser(true);
          } else {
            console.error("Usuário não encontrado.");
            logout();
          }
        
        // switch (user?.role) {
        //   case "ROLE_ADMIN":
        //     console.log("provider teste:",user.idUser);
            
            
        //     break;
        //   case "ROLE_REALIZA_PLUS":
        //     break;
        //   case "ROLE_REALIZA_BASIC":
        //     break;
        //   case "ROLE_CLIENT_RESPONSIBLE":
        //     const resClient = await axios.get(`${ip}/user/client/${userId}`, {
        //       headers: {
        //         Authorization: `Bearer ${token}`,
        //       },
        //     });
        //     console.log("Dados do usuário CLIENT_RESPONSIBLE:", resClient.data);
        //     if (resClient.data) {
        //       setUser(resClient.data);
        //       setAuthUser(true);
        //     } else {
        //       console.error("Usuário não encontrado.");
        //       logout();
        //     }
        //     break;
        //   case "ROLE_CLIENT_MANAGER":
        //     break;
        //   case "ROLE_SUPPLIER_RESPONSIBLE":
        //     const resSupplier = await axios.get(
        //       `${ip}/user/supplier/${userId}`,
        //       {
        //         headers: {
        //           Authorization: `Bearer ${token}`,
        //         },
        //       },
        //     );
        //     console.log(resSupplier.data);

        //     if (resSupplier.data) {
        //       setUser(resSupplier.data);
        //       setAuthUser(true);
        //     } else {
        //       console.error("Usuário não encontrado.");
        //       logout();
        //     }
        //     break;
        //   case "ROLE_SUPPLIER_MANAGER":
        //     break;
        //   case "ROLE_SUBCONTRACTOR_RESPONSIBLE":
        //     break;
        //   case "ROLE_SUBCONTRACTOR_MANAGER":
        //     break;
        //   case "ROLE_VIEWER":
        //     break;
        // }
      } catch (error) {
        console.error("Erro ao buscar usuário:", error);
        logout();
      }
    } else {
      toast("Sua sessão expirou. Faça seu Login novamente", {
        action: (
          <Button className="bg-realizaBlue" onClick={() => navigate("/")}>
            Entendido
          </Button>
        ),
      });
      }
    };

  const logout = () => {
    try {
      localStorage.removeItem("userId");
      localStorage.removeItem("tokenClient");
      localStorage.removeItem("hasShownToast");
      setUser(null);
      setAuthUser(false);
      navigate("/");
    } catch (error) {
      console.error(`Erro ao deslogar: ${error}`);
    }
  };

  return (
    <UserContext.Provider
      value={{
        user,
        authUser,
        setUser,
        setAuthUser,
        logout,
      }}
    >
      {children}
    </UserContext.Provider>
  );
}
