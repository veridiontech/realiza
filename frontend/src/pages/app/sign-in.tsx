import { useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { Form, Link, useNavigate } from "react-router-dom";
import axios from "axios";
import { useUser } from "@/context/user-provider";
import { ip } from "@/utils/ip";

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
  const [loading, setLoading] = useState(false);
  const { setUser } = useUser();
  const navigate = useNavigate()

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<loginFormData>({
    resolver: zodResolver(loginFormSchema),
  });

  const getUser = async (data: loginFormData) => {
    setLoading(true)
    try {
      console.log('aa');
      console.log('dados:',data);
      
      const res = await axios.post(`${ip}/login`, {
        email: data.email,
        password: data.password,
      });
      console.log(res.data);
      const token = res.data.token;
      localStorage.setItem("tokenClient", token);
      const userResponse = await axios.post(`${ip}/login/extract-token`, token, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      
      
      const userData = userResponse.data
      localStorage.setItem("userId", userData.idUser)
      console.log(userData);
      
      setUser(userData)
      navigate(`/sistema/select-client/${userData.idUser}`)
      window.location.reload()
    } catch (err) {
      console.error("Erro ao buscar usuário:", err);
    }finally{
      setLoading(false)
    }
  };

  return (
    <div className="flex h-screen items-center justify-center">
      <div className="flex h-3/6 flex-col justify-center">
        <h1 className="text-center text-3xl font-bold">Bem Vindo</h1>
        <span className="text-center">
          Insira seu email e senha para continuar
        </span>
        <Form className="mt-16 flex flex-col" onSubmit={handleSubmit(getUser)}>
          <label htmlFor="email">E-mail</label>
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
            className="bg-realizaBlue rounded px-4 py-2 font-bold text-white hover:bg-blue-700"
            type="submit"
            disabled={loading}
          >
            {loading ? "Carregando..." : "Entrar"}
          </button>
        </Form>
      </div>
    </div>
  );
}
