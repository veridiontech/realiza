import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useState } from "react";
import { LockKeyhole } from "lucide-react";
import axios from "axios";
import { ip } from "@/utils/ip";
import { useUser } from "@/context/user-provider";
import { toast } from "sonner";
// import { useUser } from "@/context/user-provider";

const profileSchema = z.object({
  password: z.string(),
  newPassword: z.string().min(6, "A senha deve ter pelo menos 6 caracteres"),
});

type ProfileFormData = z.infer<typeof profileSchema>;

export function BottomSection() {
  const { user } = useUser();
  const [showPassword, setShowPassword] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
    setError,
  } = useForm<ProfileFormData>({
    resolver: zodResolver(profileSchema),
  });

  const newPassword = async (data: ProfileFormData) => {
    console.log(data);
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      await axios.patch(
        `${ip}/user/manager/change-password/${user?.idUser}`,
        data,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` }
        }
      );
      toast.success(`Senha alterada com sucesso ${user?.firstName}`);
    } catch (err: any) {
      console.log(err);
      toast.error("Erro ao alterar senha, tente novamente");
      if (err.response.status === 500) {
        setError("password", {
          type: "manual",
          message: "Senha incorreta ou inválida, tente novemente",
        });
      }
    }
  };

  return (
  <div className="mt-6 flex flex-col rounded-lg bg-white p-6 shadow">
    <div className="flex items-center gap-2 mb-6">
      <LockKeyhole className="text-realizaBlue h-6 w-6" />
      <h2 className="text-realizaBlue text-base font-medium">Segurança</h2>
    </div>

    <form onSubmit={handleSubmit(newPassword)} className="flex w-full flex-col gap-6">
      
      <div className="relative">
        <label className="block text-sm text-gray-700 mb-1">Sua senha</label>
        <input
          type={showPassword ? "text" : "password"}
          {...register("password")}
          placeholder="Digite sua senha"
          title={errors.password?.message}
          className={`w-full rounded border px-3 py-2 pr-10 text-sm text-gray-900 focus:outline-none focus:ring-2 ${errors.password
              ? "border-red-500 focus:ring-red-500"
              : "border-gray-300 focus:ring-realizaBlue"
            }`}
        />
        <button
          type="button"
          className="absolute top-9 right-3 text-gray-500 hover:text-gray-700"
          onClick={() => setShowPassword(!showPassword)}
        >
          {showPassword ? "🔒" : "👁️"}
        </button>
      </div>

      
      <div className="relative">
        <label className="block text-sm text-gray-700 mb-1">Nova Senha</label>
        <input
          type={showPassword ? "text" : "password"}
          {...register("newPassword")}
          placeholder="Digite sua nova senha"
          className={`w-full rounded border px-3 py-2 pr-10 text-sm text-gray-900 focus:outline-none focus:ring-2 ${errors.newPassword
              ? "border-red-500 focus:ring-red-500"
              : "border-gray-300 focus:ring-realizaBlue"
            }`}
        />
        <button
          type="button"
          className="absolute top-9 right-3 text-gray-500 hover:text-gray-700"
          onClick={() => setShowPassword(!showPassword)}
        >
          {showPassword ? "🔒" : "👁️"}
        </button>
      </div>

     
      {errors.newPassword && (
        <p className="text-sm text-red-500 -mt-4">{errors.newPassword.message}</p>
      )}

      
      <div className="flex justify-end mt-2">
        <button
          type="submit"
          className="bg-[#1f2f54] hover:bg-[#152446] text-white px-6 py-2 rounded text-sm font-medium shadow-sm transition"
        >
          Salvar Alterações
        </button>
      </div>
    </form>
  </div>
);
}