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
import { useState } from "react";
import { Pencil } from "lucide-react";

const profilePicFormSchema = z.object({
  file: z
    .instanceof(File, { message: "O arquivo deve ser uma imagem válida." })
    .refine(
      (file) => file.size <= 2 * 1024 * 1024,
      "O arquivo deve ter no máximo 2MB."
    )
    .refine(
      (file) => ["image/png", "image/jpeg", "image/jpg"].includes(file.type),
      "Apenas imagens PNG ou JPG são permitidas."
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
      console.log("Enviando arquivo:", selectedFile.name);

      const response = await axios.patch(
        `${ip}/user/manager/change-profile-picture/${user?.idUser}`,
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        }
      );

      console.log("Imagem enviada com sucesso!", response.data);
      window.location.reload()
    } catch (error) {
      console.error("Erro ao enviar imagem:", error);
    }
  };

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      setSelectedFile(file);
      setValue("file", file);
    }
  };

  const getProfilePic = user?.profilePictureData;
  const base64Image = getProfilePic
    ? `data:image/${getProfilePic.charAt(0) === '/' ? 'png' : 'jpeg'};base64,${getProfilePic}`
    : null;
  
  const getNameUser = user?.firstName?.[0] || "";
  const getSurnameUser = user?.surname?.[0] || "";

  return (
    <Dialog>
      <DialogTrigger>
        <div >
          {base64Image ? (
            <div>
            <img src={base64Image} className="w-[10vw] rounded-full h-[20vh] object-cover hover:bg-gray-600" alt="Foto de perfil" />
            <Pencil className="relative bottom-[5.5vw] left-[4.5vw] text-gray-300 hover:text-gray-500"/>
            </div>
          ) : user ? (
            <div className="flex items-center justify-center bg-gray-300 text-black font-bold text-xl rounded-full">
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
            <img src={base64Image} className="w-[10vw] h-[20vh] rounded-full object-cover" alt="Foto atual" />
          ) : (
            <p>Nenhuma foto encontrada.</p>
          )}
        </div>
        <form onSubmit={handleSubmit(handleFileUpload)} className="flex flex-col gap-1">
          <div>
            <Input type="file" accept="image/*" onChange={handleFileChange} className="cursor-pointer"/>
            {errors.file && <p className="text-red-500">{errors.file.message}</p>}
          </div>
          <Button type="submit" className="bg-realizaBlue">Enviar</Button>
        </form>
      </DialogContent>
    </Dialog>
  );
}
