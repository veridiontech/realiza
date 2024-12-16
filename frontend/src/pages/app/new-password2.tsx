import { useNavigate } from "react-router-dom";

export function NewPassword2() {

    const navigate = useNavigate()

    const toSignIn = () => {
        navigate("/sign-in")
    }

  return (
    <div className="flex justify-center items-center h-screen">
      <div className="flex flex-col justify-center max-w-md w-full px-6">
        <h1 className="text-center font-bold text-3xl mb-4">
          Senha Alterada Com Sucesso!
        </h1>
        <span className="text-center text-gray-600 mb-10">
            Sua senha foi alterada com sucesso, para acessar a plataforma clique no botão abaixo
        </span>
        
          <button
            onClick={toSignIn}
            className="bg-realizaBlue hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
            type="button"
          >
            Acessar
          </button>

      </div>
    </div>
  );
}
