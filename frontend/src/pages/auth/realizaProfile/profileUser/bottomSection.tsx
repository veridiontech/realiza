import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useState } from "react";
import { LockKeyhole } from "lucide-react";
// import { useUser } from "@/context/user-provider";

const profileSchema = z.object({
  password: z.string().min(6, "A senha deve ter pelo menos 6 caracteres"),
});

type ProfileFormData = z.infer<typeof profileSchema>;

export function BottomSection() {
  const [showPassword, setShowPassword] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ProfileFormData>({
    resolver: zodResolver(profileSchema),
  });

  const onSubmit = () => {
    alert("Dados salvos com sucesso!");
  };


  return (
    <div className="flex flex-col bg-white shadow rounded-lg p-6 mt-6">
      <div className="flex flex-row gap-2">
        <LockKeyhole className="w-7 h-7 text-blue-600" />
        <h2 className="text-lg text-blue-600">Segurança</h2>
      </div>
      <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-4 w-full">
        <div className="relative">
          <label htmlFor="password" className="block text-sm font-medium text-gray-700 mt-8">
            Nova Senha
          </label>
          <input
            type={showPassword ? "text" : "password"}
            id="password"
            {...register("password")}
            className={`block w-full rounded-md border px-3 py-2 text-gray-900 focus:outline-none focus:ring-2 ${
              errors.password ? "border-red-500 focus:ring-red-500" : "border-gray-300 focus:ring-blue-500"
            }`}
            placeholder="Digite sua nova senha"
          />
          <button
            type="button"
            className="absolute mt-12 inset-y-0 right-3 flex items-center text-gray-500 hover:text-gray-700"
            onClick={() => setShowPassword(!showPassword)}
          >
            {showPassword ? "🙈" : "👁️"}
          </button>
        </div>
        {errors.password && <p className="text-sm text-red-500">{errors.password.message}</p>}
        <div className="flex justify-end">
          <button type="submit" className="w-[20vw] p-3 text-white bg-blue-500 rounded hover:bg-blue-600">
            Alterar Senha
          </button>
        </div>
      </form>
    </div>
  );
}
