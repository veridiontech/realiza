import { propsUser } from "@/types/interfaces";
import { ip } from "@/utils/ip";
import axios from "axios";
import React, { createContext, useContext, useEffect, useState } from "react";

interface userContextProps {
  user: propsUser | null;
  authUser: boolean;
  setUser: React.Dispatch<React.SetStateAction<propsUser | null>>;
  setAuthUser: (auth: boolean) => void;
  logout: () => void;
}

const UserContext = createContext<userContextProps | undefined>(undefined);

export function useUser() {
  const context = useContext(UserContext);
  if (!context) {
    throw new Error("erro no provider");
  }
  return context;
}

export function UserProvider({ children }: { children: React.ReactNode }) {
  const [authUser, setAuthUser] = useState(false);
  const [user, setUser] = useState<propsUser | null>(null);

  useEffect(() => {
    const userId = localStorage.getItem("userId");
    const getUser = async () => {
      try {
        const res = await axios.get(`http://localhost:3001/User${userId}`, {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("tokenClient")}`,
          },
        });
        const loggedInUser = res.data;
        if (loggedInUser) {
          setUser(loggedInUser);
          setAuthUser(true);
        } else {
          console.error("Usuário não encontrado.");
          setUser(null);
          setAuthUser(false);
        }
      } catch (error) {
        console.log(error);
      }
    };
    getUser();
  }, []);

  const logout = async () => {
    try {
      localStorage.removeItem("userId");
      localStorage.removeItem("tokenClient");
      setUser(null);
      setAuthUser(false);
    } catch (error) {
      console.log(`erro ao deslogar ${error}`);
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