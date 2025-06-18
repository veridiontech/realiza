import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import axios from "axios";
import { ip } from "@/utils/ip";
import { Helmet } from "react-helmet-async";
import { Button } from "@/components/ui/button";
import { toast } from "sonner";
import {
  Dialog,
  DialogTrigger,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";

interface UserData {
  firstName: string;
  surname: string;
  cpf: string;
  email: string;
  position: string;
  role: string;
  status: string;
  idUser: string;
}

export function DetailsUsers() {
  const { id } = useParams();
  const [userData, setUserData] = useState<UserData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [processing, setProcessing] = useState(false);
  const [openDialog, setOpenDialog] = useState(false);

  useEffect(() => {
    const fetchUser = async () => {
      if (!id) return;

      try {
        const token = localStorage.getItem("tokenClient");
        const response = await axios.get(`${ip}/user/client/${id}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        // Aqui definimos o status como "ACTIVE" se não existir
        const user = response.data;
        if (!user.status) {
          user.status = "ACTIVE"; // Define o status como "ACTIVE" se não estiver presente
        }

        setUserData(user);
      } catch (err) {
        console.error("Erro ao buscar usuário:", err);
        setError("Erro ao buscar dados do usuário.");
      } finally {
        setLoading(false);
      }
    };

    fetchUser();
  }, [id]);

  const handleStatusChange = async () => {
    if (!id || !userData) return;

    setProcessing(true);

    try {
      const token = localStorage.getItem("tokenClient");

      const payload = {
        cpf: userData.cpf,
        description: userData.status === "ACTIVE" ? "Usuário inativado" : "Usuário reativado",
        password: "",
        newPassword: "",
        position: userData.position,
        role: userData.role,
        firstName: userData.firstName,
        surname: userData.surname,
        email: userData.email,
        profilePicture: "",
        telephone: "",
        cellphone: "",
        branch: "",
        idUser: userData.idUser,
        status: userData.status === "ACTIVE" ? "INACTIVE" : "ACTIVE",
      };

      await axios.put(`${ip}/user/client/${id}`, payload, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      toast.success(
        `Usuário ${userData.status === "ACTIVE" ? "inativado" : "reativado"} com sucesso!`
      );

      setUserData({ ...userData, status: payload.status });
      setOpenDialog(false);
    } catch (err: any) {
      console.error("Erro ao alterar status:", err);
      const errorMessage = err.response?.data?.message || "Erro ao atualizar status do usuário.";
      toast.error(errorMessage);
    } finally {
      setProcessing(false);
    }
  };

  if (loading) return <p className="p-4">Carregando usuário...</p>;
  if (error) return <p className="text-red-600 p-4">{error}</p>;
  if (!userData) return <p className="text-red-600 p-4">Usuário não encontrado.</p>;

  const isActive = userData.status === "ACTIVE";

  return (
    <>
      <Helmet title="Detalhes do Usuário" />
      <div className="max-w-3xl mx-auto bg-white p-6 rounded-lg shadow-md mt-6">
        <h1 className="text-2xl font-bold mb-4 text-realizaBlue">Detalhes do Usuário</h1>
        <ul className="space-y-2 text-gray-700 mb-6">
          <li><strong>Nome:</strong> {userData.firstName} {userData.surname}</li>
          <li><strong>CPF:</strong> {userData.cpf}</li>
          <li><strong>Email:</strong> {userData.email}</li>
          <li><strong>Cargo:</strong> {userData.position}</li>
          <li>
            <strong>Status:</strong>{" "}
            <span className={`font-semibold ${isActive ? "text-green-600" : "text-red-600"}`}>
              {isActive ? "Ativo" : "Inativo"}
            </span>
          </li>
        </ul>

        <Dialog open={openDialog} onOpenChange={setOpenDialog}>
          <DialogTrigger asChild>
            <Button variant={isActive ? "destructive" : "default"}>
              {isActive ? "Inativar" : "Reativar"} Usuário
            </Button>
          </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>
                Tem certeza que deseja {isActive ? "inativar" : "reativar"} este usuário?
              </DialogTitle>
            </DialogHeader>
            <DialogFooter className="mt-4">
              <Button onClick={() => setOpenDialog(false)} variant="ghost">
                Cancelar
              </Button>
              <Button onClick={handleStatusChange} disabled={processing}>
                {processing
                  ? isActive
                    ? "Inativando..."
                    : "Reativando..."
                  : isActive
                    ? "Inativar"
                    : "Reativar"}
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </div>
    </>
  );
}
