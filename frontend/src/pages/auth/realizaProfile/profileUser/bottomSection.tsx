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
      await axios.patch(
        `${ip}/user/manager/change-password/${user?.idUser}`,
        data,
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
      <div className="flex flex-row gap-2">
        <LockKeyhole className="text-realizaBlue h-7 w-7" />
        <h2 className="text-realizaBlue text-lg">Segurança</h2>
      </div>
      <form
        onSubmit={handleSubmit(newPassword)}
        className="flex w-full flex-col gap-4"
      >
        <div className="relative">
          <label className="mt-8 block text-sm font-medium text-gray-700">
            Sua senha
          </label>
          <input
            type={showPassword ? "text" : "password"}
            {...register("password")}
            className={`block w-full rounded-md border px-3 py-2 text-gray-900 focus:outline-none focus:ring-2 ${
              errors.password
                ? "border-red-500 focus:ring-red-500"
                : "focus:ring-realizaBlue border-gray-300"
            }`}
            placeholder="Digite sua senha"
            title={errors.password?.message}
          />
          <button
            type="button"
            className="absolute inset-y-0 right-3 mt-12 flex items-center text-gray-500 hover:text-gray-700"
            onClick={() => setShowPassword(!showPassword)}
          >
            {showPassword ? "🙈" : "👁️"}
          </button>
        </div>
        <div className="relative">
          <label className="mt-8 block text-sm font-medium text-gray-700">
            Nova Senha
          </label>
          <input
            type={showPassword ? "text" : "password"}
            {...register("newPassword")}
            className={`block w-full rounded-md border px-3 py-2 text-gray-900 focus:outline-none focus:ring-2 ${
              errors.newPassword
                ? "border-red-500 focus:ring-red-500"
                : "focus:ring-realizaBlue border-gray-300"
            }`}
            placeholder="Digite sua nova senha"
          />
          <button
            type="button"
            className="absolute inset-y-0 right-3 mt-12 flex items-center text-gray-500 hover:text-gray-700"
            onClick={() => setShowPassword(!showPassword)}
          >
            {showPassword ? "🙈" : "👁️"}
          </button>
        </div>
        {errors.newPassword && (
          <p className="text-sm text-red-500">{errors.newPassword.message}</p>
        )}
        <div className="flex justify-end">
          <button
            type="submit"
            className="bg-realizaBlue hover:bg-realizaBlue w-[20vw] rounded p-3 text-white"
          >
            Alterar Senha
          </button>
        </div>
      </form>
    </div>
  );
}
