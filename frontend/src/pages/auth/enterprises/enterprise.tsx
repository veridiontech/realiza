import { useState } from "react";
import ultraIcon from "@/assets/ultraIcon.png";
import { Modal } from "@/components/modal";
// import { z } from "zod";

// const enterpriseSchema = z.object({
//   nameEnterprise: z.string(),
//   unityName: z.string(),
//   cep: z.string(),
//   state: z.string(),
//   city: z.string(),
//   addres: z.string(),
//   responsibleForUnity: z.string(),
//   category: z.string(),
//   phone: z.string()
// })

// type EnterpriseSchema = z.infer<typeof enterpriseSchema>
export function Enterprise() {
  const [isModalOpen, setIsModalOpen] = useState(false);

  // const { 
  //   register,
  //   handleSubmit,
  //   formState
  // }

  const handleModalClose = () => {
    setIsModalOpen(false);
  };

  const handleFormSubmit = (formData: Record<string, any>) => {
    console.log("Dados do formulário:", formData);
    setIsModalOpen(false);
  };

  const fields = [
    {
      name: "enterpriseName",
      label: "Nome da Empresa",
      type: "text" as const, // Garantindo o tipo correto
      required: true,
    },
    { name: "unitName", label: "Nome da Unidade", type: "text" as const },
    { name: "cep", label: "CEP", type: "text" as const },
    { name: "state", label: "Estado", type: "text" as const },
    { name: "city", label: "Cidade", type: "text" as const },
    { name: "addres", label: "Endereço", type: "text" as const },
    {
      name: "responsibleForUnity",
      label: "Responsável da Unidade",
      type: "text" as const,
    },
    { name: "category", label: "Categoria", type: "text" as const },
    {
      name: "telephone",
      label: "Telefone",
      type: "number" as const,
      required: true,
    },
  ];

  return (
    <div className="ml-4 flex h-auto w-full max-w-screen-2xl flex-col bg-white p-6">
      <h1 className="border-b-2 pb-6 text-2xl font-semibold text-blue-600">
        Empresa
      </h1>
      <div className="flex">
        <div className="mt-4 flex h-80 w-full flex-row items-center justify-center text-lg text-white">
          <div className="flex h-auto w-1/3 items-center justify-center">
            <div className="h-40 w-40 rounded-full">
              <img
                className="h-40 w-40 rounded-full"
                src={ultraIcon}
                alt="Ícone da Empresa"
              />
            </div>
          </div>
          <div className="flex h-auto w-1/3 flex-col text-black">
            <span>Ultragaz</span>
            <span>Ultragaz | Distribuidora de gás nacional</span>
            <span>Matriz</span>
            <span>12.345.678/0001-91</span>
            <span>12345678</span>
            <span>email@email.com.br</span>
          </div>
          <div className="flex h-auto w-1/3 items-center justify-center">
            <button
              onClick={() => setIsModalOpen(true)}
              className="rounded-md border-2 border-blue-300 px-10 py-2 text-black hover:border-blue-600 hover:bg-blue-300 hover:text-white"
            >
              Editar ✏️
            </button>
          </div>
        </div>
      </div>

      {/* Modal */}
      {isModalOpen && (
        <Modal
          title="Editar Empresa"
          fields={fields}
          onSubmit={handleFormSubmit}
          onClose={handleModalClose}
        />
      )}
    </div>
  );
}
