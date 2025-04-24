import { useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { Form, Link, useNavigate } from "react-router-dom";
import axios from "axios";
import { useUser } from "@/context/user-provider";
import { ip } from "@/utils/ip";
import SplashPage from "@/pages/app/splashPage";

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
  const [showSplash, setShowSplash] = useState(false);
  const { user, setUser } = useUser();
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    formState: { errors },
    setError,
  } = useForm<loginFormData>({
    resolver: zodResolver(loginFormSchema),
  });

  const getUser = async (data: loginFormData) => {
    setLoading(true);
    try {
      const res = await axios.post(`${ip}/login`, {
        email: data.email,
        password: data.password,
      });
      console.log("token: ", res.data);

      const obj = {
        token: res.data.token,
      };
      localStorage.setItem("tokenClient", res.data.token);

      const userResponse = await axios.post(`${ip}/login/extract-token`, obj, {
        headers: {
          Authorization: `Bearer ${res.data.token}`,
        },
      });

      // Extração do idClient do token após a chamada ao extract-token
      const token = res.data.token;
      const payload = JSON.parse(window.atob(token.split(".")[1]));
      console.log("idClient extraído do token:", payload.idClient);

      const userData = userResponse.data;
      localStorage.setItem("userBranches", JSON.stringify(userData.branches));
      localStorage.setItem(
        "userSubcontractor",
        JSON.stringify(userData.subcontractor),
      );
      console.log("Coletando dados:", userResponse.data);
      localStorage.setItem("userId", userData.idUser);
      localStorage.setItem("role", userData.role);
      console.log("Dados recebidos:", userData);

      setUser(userData);
      setShowSplash(true);

      setTimeout(() => {
        console.log("Redirecionando para:", userData.role);
        switch (userData.role) {
          case "ROLE_ADMIN":
          case "ROLE_REALIZA_PLUS":
          case "ROLE_REALIZA_BASIC":
            navigate(`/sistema/dashboard/${userData.idUser}`);
            break;
          case "ROLE_CLIENT_RESPONSIBLE":
            navigate(`/cliente/home/${userData.idUser}`);
            break;
          case "ROLE_CLIENT_MANAGER":
            navigate(`/cliente/home/${userData.idUser}`);
            break;
          case "ROLE_SUPPLIER_RESPONSIBLE":
            navigate(`/fornecedor/home/${userData.idUser}`);
            break;
          case "ROLE_SUPPLIER_MANAGER":
            navigate(`/fornecedor/home/${userData.idUser}`);
            break;
          case "ROLE_SUBCONTRACTOR_RESPONSIBLE":
            navigate(`/sub/employees/${userData.idUser}`);
            break;
          case "ROLE_SUBCONTRACTOR_MANAGER":
            navigate(`/sub/contracts/${userData.idUser}`);
            break;
          case "ROLE_VIEWER":
            navigate(`/client-test`);
            break;
          default:
            navigate(`/`);
            alert("Usuário sem ROLE");
            break;
        }
        window.location.reload();
      }, 3000);
    } catch (err: any) {
      if (err.response && err.response.status === 500) {
        setError("email", {
          type: "manual",
          message: "Usuário não encontrado. Verifique suas credenciais",
        });
      }
      console.error("Erro ao buscar usuário:", err);
    } finally {
      setLoading(false);
    }
  };

  if (showSplash) {
    return (
      <div className="fixed inset-0 z-50 flex items-center justify-center bg-[#34495e]">
        <SplashPage
          nome={`${user?.firstName}${user?.surname ? ' ' + user.surname : ''}`}
          onComplete={() => setShowSplash(false)}
        />
      </div>
    );
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-white px-4 sm:px-6 lg:px-8">
      <div className="w-full 2xl:w-[20-vw] xl:w-[30vw] lg:w-[40vw] md:w-[40vw] bg-white rounded-xl p-6 sm:p-8">
        <h1 className="text-center text-3xl font-bold text-gray-900">Bem Vindo</h1>
        <span className="text-center block text-sm text-gray-600">
          Insira seu e-mail e senha para continuar
        </span>
  
        <Form
          className="mt-10 flex flex-col gap-4"
          onSubmit={handleSubmit(getUser)}
        >
          <div>
            <label htmlFor="email" className="block text-sm font-medium text-gray-700">
              E-mail
            </label>
            <input
              className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 text-gray-900 shadow-sm focus:border-realizaBlue focus:ring-2 focus:ring-realizaBlue"
              placeholder="email@gmail.com"
              type="email"
              {...register("email")}
            />
            {errors.email && (
              <span className="text-red-500 text-sm">{errors.email.message}</span>
            )}
          </div>
  
          <div>
            <label htmlFor="password" className="block text-sm font-medium text-gray-700">
              Senha
            </label>
            <div className="relative">
              <input
                className="mt-1 block w-full rounded-md border border-gray-300 px-3 py-2 text-gray-900 shadow-sm focus:border-realizaBlue focus:ring-2 focus:ring-realizaBlue"
                type={showPassword ? "text" : "password"}
                {...register("password")}
              />
              <button
                type="button"
                className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-500"
                onClick={() => setShowPassword(!showPassword)}
              >
                {showPassword ? "🔒" : "👁️"}
              </button>
            </div>
            {errors.password && (
              <span className="text-red-500 text-sm">{errors.password.message}</span>
            )}
          </div>
  
          <div className="text-xs text-gray-600">
            Esqueceu a senha?{" "}
            <Link to="/forgot-password" className="text-realizaBlue hover:underline">
              Recupere-se aqui!
            </Link>
          </div>
  
          <button
            className="mt-4 w-full rounded bg-realizaBlue px-4 py-2 font-bold text-white hover:bg-blue-700"
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
