import { Button } from "@/components/ui/button";
import { ip } from "@/utils/ip";
import axios from "axios";
import { useEffect, useState } from "react";

interface TableResumeProps {
  idBranch: string | undefined;
}

export function TableResume({ idBranch }: TableResumeProps) {
  const [selectTab, setSelectTab] = useState("suppliers");
  const [suppliers, setSuppliers] = useState([]);
  const [userByBranch, setUsersByBranch] = useState([]);

  const token = localStorage.getItem("tokenClient");

  const getSuppliers = async () => {
    try {
      const res = await axios.get(`${ip}/supplier/filtered-client`, {
        params: {
          idSearch: idBranch,
        },
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      //   console.log(res.data.content);

      setSuppliers(res.data.content);
    } catch (err: any) {
      console.log(err);
    }
  };

  const getUserByBranch = async () => {
    try {
      const res = await axios.get(`${ip}/user/client/filtered-client`, {
        params: {
          idSearch: idBranch,
        },
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      console.log(res.data.content);
      setUsersByBranch(res.data.content);
    } catch (err: any) {
      console.log(err);
    }
  };

  useEffect(() => {
    if (idBranch) {
      getSuppliers();
      getUserByBranch();
    }
  }, [idBranch]);

  const isActive = (tab: string) =>
    selectTab === tab
      ? "bg-realizaBlue text-white"
      : "bg-neutral-100 text-neutral-700 hover:bg-neutral-200 border border-black";

  return (
    <div className="shadow-lg p-10 border border-neutral-200  h-[55vh] relative bottom-[7vw] rounded-md">
      <div className="flex items-center gap-5 mb-6">
        <Button
          className={isActive("suppliers")}
          onClick={() => setSelectTab("suppliers")}
        >
          Fornecedores
        </Button>
        <Button
          className={isActive("users")}
          onClick={() => setSelectTab("users")}
        >
          Usuários
        </Button>
      </div>

      {selectTab === "suppliers" && (
        <div>
          <h1 className="text-xl font-semibold mb-4">Lista de Fornecedores</h1>
          <div className="overflow-x-auto">
            <table className="w-[30vw] border border-neutral-300">
              <thead className="bg-[#345D5C33]">
                <tr>
                  <th className="px-4 py-2 text-left border-b">Nome</th>
                  <th className="px-4 py-2 text-left border-b">CNPJ</th>
                </tr>
              </thead>
              <tbody>
                {suppliers.length > 0 ? (
                  suppliers.map((supplier: any) => (
                    <tr
                      key={supplier.idProvider}
                      className="hover:bg-neutral-50"
                    >
                      <td className="px-4 py-2 border-b">
                        {supplier.corporateName}
                      </td>
                      <td className="px-4 py-2 border-b">{supplier.cnpj}</td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td className="px-4 py-2 border-b text-center" colSpan={4}>
                      Nenhum fornecedor encontrado.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}
      {selectTab === "users" && (
        <div>
          <h1 className="text-xl font-semibold mb-4">
            Lista de usuários da filial
          </h1>
          <div className="overflow-x-auto">
            <table className="w-[30vw] border border-neutral-300">
              <thead className="bg-[#345D5C33]">
                <tr>
                  <th className="px-4 py-2 text-left border-b">Nome</th>
                  <th className="px-4 py-2 text-left border-b">CPF</th>
                  <th className="px-4 py-2 text-left border-b">Cargo</th>
                </tr>
              </thead>
              <tbody>
                {userByBranch.length > 0 ? (
                  userByBranch.map((user: any) => (
                    <tr key={user.idUser} className="hover:bg-neutral-50">
                      <td className="px-4 py-2 border-b">
                        {user.firstName} {user.surname}
                      </td>
                      <td className="px-4 py-2 border-b">{user.cpf}</td>
                      <td className="px-4 py-2 border-b">{user.position}</td>
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td className="px-4 py-2 border-b text-center" colSpan={4}>
                      Nenhum fornecedor encontrado.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}
