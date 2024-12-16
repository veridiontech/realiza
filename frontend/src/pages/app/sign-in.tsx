import { useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { Form, Link } from "react-router-dom";

const loginFormSchema = z.object({
  email: z
    .string()
    .nonempty("O email é obrigatório")
    .email("Formato de email inválido"),
  password: z.string().min(6, "A senha precisa conter no mínimo 6 caracteres"),
});

type loginFormData = z.infer<typeof loginFormSchema>;

export function SignIn() {
  const [showPassword, setShowPassword] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<loginFormData>({
    resolver: zodResolver(loginFormSchema),
  });

  const onSubmit = (data: loginFormData) => {
    console.log("Dados do formulário:", data);
  };

  return (
    <>
      <div className="flex h-screen items-center justify-center">
        <div className="flex h-3/6 flex-col justify-center">
          <h1 className="text-center text-3xl font-bold">Bem Vindo</h1>
          <span className="text-center">
            Insira seu email e senha para continuar
          </span>
          <Form
            className="mt-16 flex flex-col"
            onSubmit={handleSubmit(onSubmit)}
          >
            <label className="" htmlFor="email">
              E-mail
            </label>
            <input
              className="mb-10 block w-full rounded-md border border-gray-300 px-3 py-2 text-gray-900 focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="email@gmail.com"
              type="email"
              {...register("email")}
            />
            {errors.email && <span>{errors.email.message}</span>}

            <label htmlFor="password">Senha</label>
            <div className="relative">
              <input
                className="mb-2 block w-full rounded-md border border-gray-300 px-3 py-2 text-gray-900 focus:outline-none focus:ring-2 focus:ring-blue-500"
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

            <span className="mb-16 text-xs font-light text-gray-600">
              Esqueceu a senha?{" "}
              <Link
                to="/forgot-password"
                className="text-blue-600 hover:underline"
              >
                Recupere-a aqui!
              </Link>
            </span>
            <button
              className="rounded bg-realizaBlue px-4 py-2 font-bold text-white hover:bg-blue-700"
              type="submit"
            >
              Entrar
            </button>
          </Form>
        </div>
      </div>
    </>
  );
}
