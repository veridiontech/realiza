import { useNavigate } from "react-router-dom";

export function NewPassword2() {
  const navigate = useNavigate();

  const toSignIn = () => {
    navigate("/sign-in");
  };

  return (
    <div className="flex h-screen items-center justify-center">
      <div className="flex w-full max-w-md flex-col justify-center px-6">
        <h1 className="mb-4 text-center text-3xl font-bold">
          Senha Alterada Com Sucesso!
        </h1>
        <span className="mb-10 text-center text-gray-600">
          Sua senha foi alterada com sucesso, para acessar a plataforma clique
          no botão abaixo
        </span>

        <button
          onClick={toSignIn}
          className="bg-realizaBlue hover:bg-realizaBlue rounded px-4 py-2 font-bold text-white"
          type="button"
        >
          Acessar
        </button>
      </div>
    </div>
  );
}
