import { ScrollArea } from "@/components/ui/scroll-area";
import { Eye, Notebook, NotebookText, Plus, User } from "lucide-react";

export function ContarctsByProvider() {
  const contracts = [
    {
      id: 1,
      title: " Industria ultra gás - altura",
    },
    {
      id: 2,
      title: " Industria teste - altura",
    },
    {
      id: 3,
      title: " Teste Dois - altura",
    },
    {
      id: 5,
      title: " Teste Dois - altura",
    },
    {
      id: 6,
      title: " Teste Dois - altura",
    },
    {
      id: 7,
      title: " Teste Dois - altura",
    },
    {
      id: 7,
      title: " Teste Dois - altura",
    },
    {
      id: 7,
      title: " Teste Dois - altura",
    },
    {
      id: 7,
      title: " Teste Dois - altura",
    },
    {
      id: 7,
      title: " Teste Dois - altura",
    },
  ];

  const employees = [
    {
      id: 1,
      title: "João Carlos",
      position: "Desenvolvedor",
    },
    {
      id: 2,
      title: "Levi Utima",
      position: "Desenvolvedor",
    },
    {
      id: 3,
      title: "Jhonatan Sampaio",
      position: "Desenvolvedor",
    },
  ];

  return (
    <div className="flex items-start gap-10 px-10 relative bottom-[4vw]">
      <div className="bg-realizaBlue border rounded-md flex flex-col w-[25vw]">
        <div className=" p-5 flex items-center gap-1">
          <Notebook className="text-[#C0B15B]" />
          <h1 className="text-white font-medium">
            Fornecedor: Levi Yuki Utima
          </h1>
        </div>
        <div className="bg-neutral-600 h-[1px]" />
        <div className="w-[] flex flex-col gap-5">
          <span className="text-neutral-400 text-[14px] pt-5 px-5">
            Selecione um contrato:{" "}
          </span>
          <ScrollArea className="flex items-start flex-col gap-1 h-[60vh]">
            {contracts.map((contract, index) => (
              <div
                className={`w-full p-2 ${
                  index % 2 === 1 ? "bg-realizaBlue" : "bg-[#4D657A]"
                }`}
              >
                <p className="text-white text-[18px] px-5">{contract.title}</p>
              </div>
            ))}
          </ScrollArea>
        </div>
      </div>
      <div className="w-full flex flex-col gap-5">
        <div className="bg-white p-5 rounded-md shadow-md w-full">
          <span className="text-neutral-500 text-[14px]">
            Contrato selecionado:
          </span>
          <div>
            <h2 className="text-[#34495E] text-[20px] font-semibold underline">
              Industria ultra gás - altura
            </h2>
          </div>
        </div>
        <div className="bg-white rounded-md p-5 border border-neutral-400  shadow-md flex gap-10 h-[50vh]">
          <div className="border border-neutral-400 rounded-md shadow-md p-5 w-[40vw] flex flex-col gap-10">
            <div className="flex items-start justify-between">
              <div className="flex items-center gap-2 text-[#34495E]">
                <NotebookText />{" "}
                <h2 className="text-[20px]">
                  Documentos vinculádos ao contrato:
                </h2>
              </div>
              <div className="bg-realizaBlue p-2 rounded-md text-white">
                <Plus />
              </div>
            </div>
            <div>
              <div className="flex items-center justify-between">
                <span className="text-[20px] text-neutral-600">
                  Documento Exemplo
                </span>{" "}
                <Eye />
              </div>
            </div>
          </div>
          <div className="flex flex-col items-start gap-10">
            <div className="flex items-center gap-2 text-[#34495E]">
              <User />
              <h2 className="text-[20px]">Colaboradores</h2>
            </div>
            <div className="flex flex-col gap-8">
              {employees.map((employee) => (
                <div className=" flex flex-col gap-5">
                  <div className="flex items-center gap-5">
                    <div className="bg-neutral-400 p-2 rounded-full">
                      <User />
                    </div>
                    <div className="">
                      <p className="text-[20px]">{employee.title}</p>
                      <span className="text-[12px] text-realizaBlue font-semibold underline">
                        {employee.position}
                      </span>
                    </div>
                  </div>
                  <div className="bg-neutral-400 h-[1px] " />
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
