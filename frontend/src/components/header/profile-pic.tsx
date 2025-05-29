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
import { Pencil, Trash2 } from "lucide-react";
import { useState } from "react";

const MAX_FILE_SIZE_MB = 2;

const profilePicFormSchema = z.object({
  file: z
    .instanceof(File, { message: "O arquivo deve ser uma imagem válida." })
    .refine(
      (file) => file.size <= MAX_FILE_SIZE_MB * 1024 * 1024,
      `O arquivo deve ter no máximo ${MAX_FILE_SIZE_MB}MB.`
    )
    .refine(
      (file) => ["image/png", "image/jpeg"].includes(file.type),
      "Apenas imagens PNG ou JPEG são permitidas."
    ),
});

type ProfilePictureFormSchema = z.infer<typeof profilePicFormSchema>;

export function ProfilePic() {
  const { user } = useUser();
  const [selectedFile, setSelectedFile] = useState<File | null>(null);

  const {
    handleSubmit,
    setValue,
    formState: { errors },
  } = useForm<ProfilePictureFormSchema>({
    resolver: zodResolver(profilePicFormSchema),
  });

  const handleFileUpload = async () => {
    if (!selectedFile) {
      console.error("Nenhum arquivo selecionado.");
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

      setTimeout(() => window.location.reload(), 300);
    } catch (error) {
      console.error("Erro ao enviar imagem:", error);
    }
  };

  const handleRemoveImage = async () => {
    console.log("🧹 Remover imagem acionado!"); // Aqui mostra no console do navegador
    try {
      const token = localStorage.getItem("tokenClient");
      if (!user?.idUser || !token) {
        console.warn("⚠️ Usuário ou token ausente.");
        return;
      }

      await axios.patch(
        `${ip}/user/manager/remove-profile-picture/${user.idUser}`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      console.log("✅ Imagem removida com sucesso!");
      setTimeout(() => window.location.reload(), 300);
    } catch (error) {
      console.error("❌ Erro ao remover imagem:", error);
    }
  };


  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      setSelectedFile(file);
      setValue("file", file, { shouldValidate: true });
    }
  };

  const getProfilePic = user?.profilePictureData;
  const base64Image = getProfilePic
    ? `data:image/${getProfilePic.charAt(0) === "/" ? "png" : "jpeg"};base64,${getProfilePic}`
    : null;

  const getNameUser = user?.firstName?.[0] || "";
  const getSurnameUser = user?.surname?.[0] || "";

  return (
    <Dialog>
      <DialogTrigger>
        <div>
          {base64Image ? (
            <div className="group relative w-24 h-24 rounded-full overflow-hidden cursor-pointer border">
              <img
                src={base64Image}
                alt="Foto de perfil"
                className="w-full h-full object-cover"
              />
              <div className="absolute inset-0 bg-black/30 opacity-0 group-hover:opacity-100 transition-opacity duration-300 flex items-center justify-center gap-3">
                <Pencil className="text-white w-5 h-5" />
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    handleRemoveImage();
                  }}
                  className="text-white hover:text-red-500"
                  title="Remover imagem"
                >
                  <Trash2 className="w-5 h-5" />
                </button>
              </div>
            </div>
          ) : user ? (
            <div className="flex items-center justify-center bg-gray-300 text-black font-bold text-xl rounded-full w-[10vw] h-[20vh]">
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
        <div className="flex items-center justify-center">
          {base64Image ? (
            <img
              src={base64Image}
              className="w-[10vw] h-[20vh] rounded-full object-cover"
              alt="Foto atual"
            />
          ) : (
            <p>Nenhuma foto encontrada.</p>
          )}
        </div>
        <form
          onSubmit={handleSubmit(handleFileUpload)}
          className="flex flex-col gap-1"
        >
          <div>
            <Input
              type="file"
              accept="image/*"
              onChange={handleFileChange}
              className="cursor-pointer"
            />
            {errors.file && (
              <p className="text-red-500">{errors.file.message}</p>
            )}
          </div>
          <Button type="submit" className="bg-realizaBlue">
            Enviar
          </Button>
        </form>
      </DialogContent>
    </Dialog>
  );
}
