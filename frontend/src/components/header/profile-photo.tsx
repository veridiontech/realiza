import { useUser } from "@/context/user-provider";
import { Skeleton } from "../ui/skeleton";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Input } from "../ui/input";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";
import { ip } from "@/utils/ip";
import { Button } from "../ui/button";
import { Trash2 } from "lucide-react";
import { useEffect, useState } from "react";
import { toast } from "sonner";

const profilePicFormSchema = z.object({
  file: z
    .instanceof(File, { message: "O arquivo deve ser uma imagem válida." })
    .refine(
      (file) => file.size <= 2 * 1024 * 1024,
      "O arquivo deve ter no máximo 2MB."
    )
    .refine(
      (file) => ["image/png", "image/jpeg"].includes(file.type),
      "Apenas imagens PNG ou JPEG são permitidas."
    ),
});

type ProfilePictureFormSchema = z.infer<typeof profilePicFormSchema>;

export function ProfilePhoto() {
  const { user } = useUser();
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [profileImage, setProfileImage] = useState<string | null>(null);

  const {
    handleSubmit,
    setValue,
    formState: { errors },
  } = useForm<ProfilePictureFormSchema>({
    resolver: zodResolver(profilePicFormSchema),
  });

  useEffect(() => {
    if (user?.profilePictureData) {
      const isPng = user.profilePictureData.charAt(0) === "/";
      const mime = isPng ? "image/png" : "image/jpeg";
      setProfileImage(`data:${mime};base64,${user.profilePictureData}`);
    }
  }, [user]);

  const handleFileUpload = async () => {
    if (!selectedFile) {
      toast.error("Nenhum arquivo selecionado.");
      return;
    }

    const formData = new FormData();
    formData.append("file", selectedFile);

    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");

      await axios.patch(
        `${ip}/user/manager/change-profile-picture/${user?.idUser}`,
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
            Authorization: `Bearer ${tokenFromStorage}`,
          },
        }
      );

      const reader = new FileReader();
      reader.onloadend = () => {
        setProfileImage(reader.result as string);
        toast.success("Imagem enviada com sucesso!");
      };
      reader.readAsDataURL(selectedFile);
    } catch (error) {
      if (axios.isAxiosError(error)) {
        console.error("Erro do Axios:", error.response?.data || error.message);
      } else {
        console.error("Erro inesperado:", error);
      }
      toast.error("Erro ao enviar imagem.");
    }
  };

  const handleRemoveImage = async () => {
    try {
      const token = localStorage.getItem("tokenClient");
      if (!user?.idUser || !token) return;

      await axios.patch(
        `${ip}/user/manager/remove-profile-picture/${user.idUser}`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      setProfileImage(null);
      toast.success("Imagem removida com sucesso!");
    } catch (error) {
      if (axios.isAxiosError(error)) {
        console.error("Erro do Axios:", error.response?.data || error.message);
      } else {
        console.error("Erro inesperado:", error);
      }
      toast.error("Erro ao remover imagem.");
    }
  };

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (!file) return;

    const allowedTypes = ["image/png", "image/jpeg"];
    const maxSizeInMB = 2;
    const maxSizeInBytes = maxSizeInMB * 1024 * 1024;

    if (!allowedTypes.includes(file.type)) {
      toast.error("Apenas imagens PNG ou JPEG são permitidas.");
      return;
    }

    if (file.size > maxSizeInBytes) {
      toast.error(`A imagem deve ter no máximo ${maxSizeInMB}MB.`);
      return;
    }

    setSelectedFile(file);
    setValue("file", file, { shouldValidate: true });
  };

  const getNameUser = user?.firstName?.[0] || "";
  const getSurnameUser = user?.surname?.[0] || "";

  return (
    <Dialog>
      <DialogTrigger>
        <div>
          {profileImage ? (
            <img
              src={profileImage}
              className="w-[3vw] h-[6vh] rounded-full object-cover"
              alt="Foto de perfil"
            />
          ) : user ? (
            <div className="bg-realizaBlue rounded-full w-[3vw] h-[6vh] flex items-center justify-center text-white">
              {getNameUser}
              {getSurnameUser}
            </div>
          ) : (
            <Skeleton className="h-[10vh] w-[5vw] rounded-full" />
          )}
        </div>
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Alterar foto de perfil</DialogTitle>
        </DialogHeader>
        <div>
          {profileImage ? (
            <div className="relative w-[120px] h-[120px] mx-auto">
              <div className="w-full h-full rounded-full overflow-hidden border relative">
                <img
                  src={profileImage}
                  className="w-full h-full object-cover peer"
                  alt="Foto atual"
                />
                <button
                  type="button"
                  onClick={handleRemoveImage}
                  className="absolute top-2 right-2 bg-red-600 p-1 rounded-full text-white opacity-0 hover:opacity-100 peer-hover:opacity-100 transition-opacity duration-300 shadow-md z-10"
                  title="Remover imagem"
                >
                  <Trash2 size={18} />
                </button>
              </div>
            </div>
          ) : (
            <p>Nenhuma foto encontrada.</p>
          )}


        </div>
        <form onSubmit={handleSubmit(handleFileUpload)} className="mt-4 flex flex-col gap-3">
          <Input type="file" accept="image/*" onChange={handleFileChange} />
          {errors.file && <p className="text-red-500">{errors.file.message}</p>}
          <Button type="submit">Enviar</Button>
        </form>
      </DialogContent>
    </Dialog>
  );
}
