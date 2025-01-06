import ultraIcon from "@/assets/ultraIcon.png";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { zodResolver } from "@hookform/resolvers/zod";
import { useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { LabelWithInput } from "@/components/ui/labelWithInput";

const EnterpriseSchema = z.object({
  enterpriseName: z.string().min(1, "Nome da empresa é obrigatório"),
  unitName: z.string(),
  cep: z.string(),
  state: z.string(),
  city: z.string(),
  addres: z.string(),
  responsibleForUnity: z.string(),
  category: z.string(),
  telephone: z.number().min(1, "Número de telefone inválido"),
});

type EnterpriseFormData = z.infer<typeof EnterpriseSchema>;

export function Enterprise() {
  const [isOpen, setIsOpen] = useState(false);

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<EnterpriseFormData>({
    resolver: zodResolver(EnterpriseSchema),
  });

  const onSubmit = (data: EnterpriseFormData) => {
    console.log("Dados do formulário:", data);
  };

  return (
    <>
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
                  alt=""
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
                onClick={() => setIsOpen(true)}
                className="rounded-md border-2 border-blue-300 px-10 py-2 text-black hover:border-blue-600 hover:bg-blue-300 hover:text-white"
              >
                Editar ✏️
              </button>
            </div>
          </div>
        </div>
      </div>
      <Dialog open={isOpen} onOpenChange={setIsOpen}>
        <DialogContent className="max-h-[80vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle className="flex justify-center">
              <h1>Empresa</h1>
            </DialogTitle>
          </DialogHeader>
          <div className="mt-4">
            <form className="flex flex-col" onSubmit={handleSubmit(onSubmit)}>
              <LabelWithInput
                label="Nome da Empresa"
                type="text"
                register={register}
                name="enterpriseName"
                error={errors.enterpriseName?.message}
              />
              <LabelWithInput
                label="Nome da Unidade"
                type="text"
                register={register}
                name="unitName"
                error={errors.unitName?.message}
              />
              <LabelWithInput
                label="CEP"
                type="text"
                register={register}
                name="cep"
                error={errors.cep?.message}
              />
              <LabelWithInput
                label="Estado"
                type="text"
                register={register}
                name="state"
                error={errors.state?.message}
              />
              <LabelWithInput
                label="Cidade"
                type="text"
                register={register}
                name="city"
                error={errors.city?.message}
              />
              <LabelWithInput
                label="Endereço"
                type="text"
                register={register}
                name="addres"
                error={errors.addres?.message}
              />
              <LabelWithInput
                label="Responsável da Unidade"
                type="text"
                register={register}
                name="responsibleForUnity"
                error={errors.responsibleForUnity?.message}
              />
              <LabelWithInput
                label="Categoria"
                type="text"
                register={register}
                name="category"
                error={errors.category?.message}
              />
              <LabelWithInput
                label="Telefone"
                type="number"
                register={register}
                name="telephone"
                error={errors.telephone?.message}
              />
              <button
                type="submit"
                className="mt-4 rounded-md bg-blue-500 px-4 py-2 text-white hover:bg-blue-700"
              >
                Salvar
              </button>
            </form>
          </div>
        </DialogContent>
      </Dialog>
    </>
  );
}
