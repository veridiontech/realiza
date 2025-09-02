import { useEffect, useState } from "react";
import axios from "axios";
import { ip } from "@/utils/ip";
import { useBranch } from "@/context/Branch-provider";
import { Link } from "react-router-dom";
import { Settings2 } from "lucide-react";

// 1. Definição completa de tipos para os usuários
interface User {
  idUser: string;
  cpf: string;
  description: string;
  password?: string; // Opcional, talvez não seja exibida
  position: string;
  role: string;
  firstName: string;
  surname: string;
  profilePictureSignedUrl?: string; // Opcional
  email: string;
  telephone?: string; // Opcional
  cellphone: string;
}

export function TableUsers() {
  const [users, setUsers] = useState<User[]>([]);
  const { selectedBranch } = useBranch();

  // Função para buscar os usuários da filial selecionada
  const getUsersByBranch = async () => {
    if (!selectedBranch?.idBranch) {
      console.log(
        "Nenhuma filial selecionada. A busca por usuários foi ignorada."
      );
      setUsers([]);
      return;
    }

    try {
      const token = localStorage.getItem("tokenClient");
      const res = await axios.get(`${ip}/user/client/filtered-client`, {
        params: {
          idSearch: selectedBranch.idBranch,
        },
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      console.log("Dados de usuários recebidos:", res.data.content);
      setUsers(res.data.content);
    } catch (err) {
      console.error("Erro ao buscar usuários:", err);
    }
  };

  useEffect(() => {
    getUsersByBranch();
  }, [selectedBranch?.idBranch]);

  return (
    <div className="flex flex-col items-center justify-center gap-5 p-10">
      <div className="mr-10 rounded-lg border bg-white p-8 shadow-lg">
        <div>
          <h1 className="text-xl font-semibold mb-4">Lista de Usuários</h1>
          <div className="overflow-x-auto">
            {/* 2. Aumentando o tamanho da tabela para acomodar mais colunas */}
            <table className="w-full border border-neutral-300">
              <thead className="bg-[#345D5C33]">
                <tr>
                  <th className="px-4 py-2 text-left border-b">Nome</th>
                  <th className="px-4 py-2 text-left border-b">E-mail</th>
                  <th className="px-4 py-2 text-left border-b">CPF</th>
                  <th className="px-4 py-2 text-left border-b">Cargo</th>
                  <th className="px-4 py-2 text-left border-b">Ações</th>
                </tr>
              </thead>
              <tbody>
                {users.length > 0 ? (
                  users.map((user) => (
                    <tr key={user.idUser} className="hover:bg-neutral-50">
                      <td className="px-4 py-2 border-b">
                        {user.firstName} {user.surname}
                      </td>
                      <td className="px-4 py-2 border-b">{user.email}</td>
                      <td className="px-4 py-2 border-b">{user.cpf}</td>
                      <td className="px-4 py-2 border-b">{user.position}</td>
                      <td className="border border-gray-300 px-4 py-2">
                        <Link to={`/sistema/detailsUsers/${user.idUser}`}>
                          <button className="text-realizaBlue ml-4 hover:underline">
                            <Settings2 />
                          </button>
                        </Link>
                      </td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td
                      className="px-4 py-2 border-b text-center"
                      colSpan={8}
                    >
                      Nenhum usuário encontrado.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}