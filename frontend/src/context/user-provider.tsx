import axios from "axios";
import React, { createContext, useContext, useEffect, useState } from "react";
import { propsUser } from "@/types/interfaces";
import { ip } from "@/utils/ip";

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

  useEffect(() => {
    const userId = localStorage.getItem("userId");
    if (userId) {
      getUser(userId);
    }
  }, []);

  const getUser = async (userId: string) => {
    try {
      const res = await axios.get(`${ip}/user/client/${userId}`, {
        // params: {
        //   idUser: userId
        // },
        headers: {
          Authorization: `Bearer ${localStorage.getItem("tokenClient")}`,
        },
      });

      if (res.data) {
        setUser(res.data);
        setAuthUser(true);
      } else {
        console.error("Usuário não encontrado.");
        setUser(null);
        setAuthUser(false);
      }
    } catch (error) {
      console.error("Erro ao buscar usuário:", error);
      setUser(null);
      setAuthUser(false);
    }
  };

  const logout = async () => {
    try {
      localStorage.removeItem("userId");
      localStorage.removeItem("tokenClient");
      setUser(null);
      setAuthUser(false);
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
