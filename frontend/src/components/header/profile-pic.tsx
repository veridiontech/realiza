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

interface profilePicProps {
  className: string;
}

const profilePicFormSchema = z.object({
  file: z
    .instanceof(File, { message: "O arquivo deve ser uma imagem válida." })
    .refine((file) => file.size <= 2 * 1024 * 1024, "O arquivo deve ter no máximo 2MB.")
    .refine(
      (file) => ["image/png", "image/jpeg", "image/jpg"].includes(file.type),
      "Apenas imagens PNG ou JPG são permitidas."
    ),
});

type ProfilePictureFormSchema = z.infer<typeof profilePicFormSchema>;

export function ProfilePic({ className }: profilePicProps) {
  const { user } = useUser();
  const [selectedFile, setSelectedFile] = useState<File | null>(null);

  const {
    register,
    handleSubmit,
    setValue, 
    formState: { errors },
  } = useForm<ProfilePictureFormSchema>({
    resolver: zodResolver(profilePicFormSchema),
  });

  const postProfilePicture = async (data: ProfilePictureFormSchema) => {
    if (!selectedFile) {
      console.log("Nenhum arquivo selecionado.");
      return;
    }

    try {
      const formData = new FormData();
      formData.append("file", selectedFile);

      console.log("Enviando dados:", formData);

      await axios.patch(`${ip}/user/manager/change-profile-picture/${user?.idUser}`, formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });

      console.log("Upload feito com sucesso!");
    } catch (err) {
      console.log("Erro ao enviar arquivo", err);
    }
  };

  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      setSelectedFile(file);
      setValue("file", file); 
    }
  };

  const getProfilePic = user?.profilePicture;
  const getNameUser = user?.firstName?.[0];
  const getSurnameUser = user?.surname?.[0];

  if (getProfilePic) {
    return (
      <Dialog>
        <DialogTrigger>Open</DialogTrigger>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Alterar foto de perfil</DialogTitle>
          </DialogHeader>
          <div>
            <img src={getProfilePic} alt="Profile Picture" className={className} />
          </div>
        </DialogContent>
      </Dialog>
    );
  }

  return (
    <Dialog>
      <DialogTrigger asChild>
        <div>
          {user ? (
            <div className={className}>
              <span>{getNameUser}</span>
              <span>{getSurnameUser}</span>
            </div>
          ) : (
            <Skeleton className="h-[10vh] w-[5vw] rounded-full" />
          )}
        </div>
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Nova foto de perfil</DialogTitle>
        </DialogHeader>
        <form onSubmit={handleSubmit(postProfilePicture)}>
          <div>
            <Input type="file" accept="image/*" onChange={handleFileChange} />
            {errors.file && <p className="text-red-500">{errors.file.message}</p>}
          </div>
          <Button type="submit">Enviar</Button>
        </form>
      </DialogContent>
    </Dialog>
  );
}
