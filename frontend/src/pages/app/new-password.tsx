import { useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { Form } from "react-router-dom";

const newPasswordFormSchema = z.object({
  email: z
    .string()
    .nonempty("O email é obrigatório")
    .email("Formato de email inválido"),
  password: z.string().min(6, "A senha precisa conter no mínimo 6 caracteres"),
});

type newPasswordFormData = z.infer<typeof newPasswordFormSchema>;

export function NewPassword() {
  const [showPassword, setShowPassword] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<newPasswordFormData>({
    resolver: zodResolver(newPasswordFormSchema),
  });

  const onSubmit = (data: newPasswordFormData) => {
    console.log("dados do formulário", data);
  };

  return (
    <>
      <div className="flex h-screen items-center justify-center">
        <div className="flex h-3/6 w-full max-w-md flex-col justify-center">
          <h1 className="text-center text-3xl font-bold">
            Crie uma Nova Senha
          </h1>
          <span className="text-center">
            Por favor, escolha uma senha que não tenha sido usada antes. Deve
            ter pelo menos 8 caracteres
          </span>
          <Form
            className="mt-16 flex flex-col"
            onSubmit={handleSubmit(onSubmit)}
          >
            <label htmlFor="password">Nova Senha</label>
            <div className="relative">
              <input
                className="focus:ring-realizaBlue mb-2 block w-full rounded-md border border-gray-300 px-3 py-2 text-gray-900 focus:outline-none focus:ring-2"
                type={showPassword ? "text" : "password"}
                {...register("password")}
              />
              <button
                type="button"
                className="absolute right-2 top-2 text-gray-500"
                onClick={() => setShowPassword(!showPassword)}
              >
                {showPassword ? "🙈" : "👁️"}
              </button>
            </div>
            {errors.password && <span>{errors.password.message}</span>}
            <label htmlFor="password">Confirme a Senha</label>
            <div className="relative">
              <input
                className="focus:ring-realizaBlue mb-2 block w-full rounded-md border border-gray-300 px-3 py-2 text-gray-900 focus:outline-none focus:ring-2"
                type={showPassword ? "text" : "password"}
                {...register("password")}
              />
              <button
                type="button"
                className="absolute right-2 top-2 text-gray-500"
                onClick={() => setShowPassword(!showPassword)}
              >
                {showPassword ? "🙈" : "👁️"}
              </button>
            </div>
            {errors.password && <span>{errors.password.message}</span>}

            <button
              className="bg-realizaBlue hover:bg-realizaBlue rounded px-4 py-2 font-bold text-white"
              type="submit"
            >
              Confirmar
            </button>
          </Form>
        </div>
      </div>
    </>
  );
}
