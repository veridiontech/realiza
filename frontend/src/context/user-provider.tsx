import axios from "axios";
import React, { createContext, useContext, useEffect, useState } from "react";
import { jwtDecode } from "jwt-decode";
import { propsUser } from "@/types/interfaces";
import { ip } from "@/utils/ip";
import { toast } from "sonner";
import { Button } from "@/components/ui/button";
import { useNavigate } from "react-router-dom";

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
    const roleUser = localStorage.getItem("role");

    if (token && isTokenValid(token) && userId && roleUser) {
      try {
        switch (roleUser) {
          case "ROLE_ADMIN":
          case "ROLE_REALIZA_PLUS":
          case "ROLE_REALIZA_BASIC":
            try {
              const res = await axios.get(`${ip}/user/manager/${userId}`, {
                headers: {
                  Authorization: `Bearer ${token}`,
                },
              });

              if (res.data) {
                setUser(res.data);
                setAuthUser(true);
              } else {
                console.error("Usuário não encontrado.");
                logout();
              }
            } catch (error) {
              console.error("Erro ao buscar usuário:", error);
              logout();
            }
            break;
          case "ROLE_CLIENT_MANAGER":
          case "ROLE_CLIENT_RESPONSIBLE":
            try {
              const res = await axios.get(`${ip}/user/client/${userId}`, {
                headers: {
                  Authorization: `Bearer ${token}`,
                },
              });

              if (res.data) {
                setUser(res.data);
                setAuthUser(true);
              } else {
                console.error("Usuário não encontrado.");
                logout();
              }
            } catch (error) {
              console.error("Erro ao buscar usuário:", error);
              logout();
            }
            break;
          case "ROLE_SUPPLIER_MANAGER":
          case "ROLE_SUPPLIER_RESPONSIBLE":
            try {
              const res = await axios.get(`${ip}/user/supplier/${userId}`, {
                headers: {
                  Authorization: `Bearer ${token}`,
                },
              });

              if (res.data) {
                setUser(res.data);
                setAuthUser(true);
              } else {
                console.error("Usuário não encontrado.");
                logout();
              }
            } catch (error) {
              console.error("Erro ao buscar usuário:", error);
              logout();
            }
            break;
          case "ROLE_SUBCONTRACTOR_RESPONSIBLE":
          case "ROLE_SUBCONTRACTOR_MANAGER":
            try {
              const res = await axios.get(
                `${ip}/user/subcontractor/${userId}`,
                {
                  headers: {
                    Authorization: `Bearer ${token}`,
                  },
                },
              );

              if (res.data) {
                setUser(res.data);
                setAuthUser(true);
              } else {
                console.error("Usuário não encontrado.");
                logout();
              }
            } catch (error) {
              console.error("Erro ao buscar usuário:", error);
              logout();
            }
            break;
          case "ROLE_VIEWER":
            try {
              const resClient = await axios.get(`${ip}user/client`);
              if (resClient.data?.content) {
                const dataClient = resClient.data.content;
                if (dataClient.branch) {
                  const resUser = await axios.get(
                    `${ip}/user/client/${userId}`,
                    {
                      headers: {
                        Authorization: `Bearer ${token}`,
                      },
                    },
                  );

                  if (resUser.data) {
                    setUser(resUser.data);
                    setAuthUser(true);
                  } else {
                    console.error("Usuário não encontrado.");
                    logout();
                  }
                }
              } else {
                logout();
              }
              const resSupplier = await axios.get(`${ip}/user/supplier`);
              if (resSupplier.data.content) {
                const dataSupplier = resSupplier.data.content;
                if (dataSupplier.supplier) {
                  try {
                    const res = await axios.get(
                      `${ip}/user/supplier/${userId}`,
                      {
                        headers: {
                          Authorization: `Bearer ${token}`,
                        },
                      },
                    );

                    if (res.data) {
                      setUser(res.data);
                      setAuthUser(true);
                    } else {
                      console.error("Usuário não encontrado.");
                      logout();
                    }
                  } catch (error) {
                    console.error("Erro ao buscar usuário:", error);
                    logout();
                  }
                }
              } else {
                logout();
              }
              const resSubcontractor = await axios.get(
                `${ip}/user/subcontractor`,
              );
              if (resSubcontractor.data.content) {
                const dataSubcontractor = resSubcontractor.data.content;
                if (dataSubcontractor.subcontrator) {
                  try {
                    const res = await axios.get(
                      `${ip}/user/subcontractor/${userId}`,
                      {
                        headers: {
                          Authorization: `Bearer ${token}`,
                        },
                      },
                    );

                    if (res.data) {
                      setUser(res.data);
                      setAuthUser(true);
                    } else {
                      console.error("Usuário não encontrado.");
                      logout();
                    }
                  } catch (error) {
                    console.error("Erro ao buscar usuário:", error);
                    logout();
                  }
                }
              } else {
                logout();
              }
            } catch (error) {
              console.error("Erro ao buscar usuário:", error);
              logout();
            }
            break;
          default:
            console.log("Função não definida para este tipo de usuário.");
        }
      } catch (error) {
        console.log("erro ao logar usuario", error);

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
