export function ServiceProvider() {
  return (
    <div className="m-10 flex min-h-full justify-center">
      <div className="flex h-[30rem] w-[90rem] flex-col rounded-lg bg-white">
        <h1 className="m-8">Prestadores de Serviço</h1>
        <div className="flex w-[90rem] flex-row justify-between px-10">
          <div className="relative mb-4">
            <input
              type="text"
              placeholder="🔍 Pesquisar unidades, ações etc..."
              className="w-[34rem] rounded-lg border border-gray-300 p-2 focus:outline-blue-400"
              value={""}
            />
          </div>
          <button>Adicionar Prestador</button>
        </div>
      </div>
    </div>
  );
}
