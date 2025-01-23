import React from "react";

export default function EmployeeToContract() {
  return (
    <div className="relative flex h-screen w-full items-center justify-center bg-gray-100">
      {/* Título no canto superior esquerdo */}
      <h1 className="absolute left-16 top-20 text-xl font-bold">TESTE</h1>

      {/* Contêiner Principal */}
      <div className="flex w-4/5 overflow-hidden rounded-lg bg-white shadow-lg">
        {/* Sidebar - Assessoria administrativa */}
        <div className="flex w-1/2 flex-col bg-cyan-300 p-6">
          <h2 className="mb-4 text-lg font-semibold">
            Assessoria administrativa
          </h2>

          {/* Funcionários Alocados */}
          <div className="mb-6">
            <h3 className="mb-2 font-semibold text-gray-700">
              Funcionários alocados no serviço
            </h3>
            <div className="flex items-center rounded-lg bg-white p-4 shadow-md">
              <div className="flex h-12 w-12 items-center justify-center rounded-full bg-gray-200 text-xl font-bold text-gray-400">
                {/* Placeholder para avatar */}
                <span>👤</span>
              </div>
              <div className="ml-4">
                <p className="font-medium text-gray-800">
                  Jean De Castro Aleixo
                </p>
              </div>
              <button className="ml-auto text-cyan-600 hover:text-cyan-800">
                ➡️
              </button>
            </div>
          </div>

          {/* Funcionários que já participaram */}
          <div>
            <h3 className="mb-2 font-semibold text-gray-700">
              Funcionários que já participaram do serviço
            </h3>
            <div className="rounded-md bg-gray-50 p-2 text-sm text-gray-500">
              Nenhum funcionário registrado.
            </div>
          </div>

          {/* Funcionários desligados */}
          <div className="mt-6">
            <h3 className="mb-2 font-semibold text-gray-700">
              Funcionários desligados
            </h3>
            <div className="rounded-md bg-gray-50 p-2 text-sm text-gray-500">
              Nenhum funcionário registrado.
            </div>
          </div>
        </div>

        {/* Main Content - BL Comunicações */}
        <div className="flex-1 bg-gray-50 p-6">
          <h2 className="mb-4 text-lg font-semibold">
            BL Comunicações - Unidade SP
          </h2>

          {/* Meus Funcionários */}
          <div>
            <h3 className="font-semibold text-gray-700">Meus Funcionários</h3>
            <div className="mt-2 rounded-md bg-gray-50 p-2 text-sm text-gray-500">
              Nenhum funcionário listado.
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
