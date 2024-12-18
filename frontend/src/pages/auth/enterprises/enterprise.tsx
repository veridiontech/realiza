import ultraIcon from "@/assets/ultraIcon.png";
import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
  } from "@/components/ui/dialog"
import { zodResolver } from "@hookform/resolvers/zod";
import { useState } from "react";
import { useForm } from "react-hook-form";

import { z } from 'zod'

const EnterpriseSchema = z.object ({
    enterpriseName: z.string(),
    unitName: z.string(),
    cep: z.string(),
    state: z.string(),
    city: z.string(),
    addres: z.string(),
    responsibleForUnity: z.string(),
    category: z.string(),
    telephone: z.number()
})

type EnterpriseFormData = z.infer<typeof EnterpriseSchema>

export function Enterprise() {

    const [isOpen, setIsOpen] = useState(false);

    const {
        register,
        handleSubmit,
        formState: {errors}
    } = useForm <EnterpriseFormData> ({
        resolver: zodResolver(EnterpriseSchema),
    })

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
              <button onClick={ () => setIsOpen(true)} className="rounded-md border-2 border-blue-300 px-10 py-2 text-black hover:border-blue-600 hover:bg-blue-300 hover:text-white">
                Editar ✏️
              </button>
            </div>
          </div>
        </div>
      </div>
      <Dialog open={isOpen} onOpenChange={setIsOpen}>
        <DialogTrigger className="bg-greenThird flex w-[5vw] items-center justify-center rounded-md border px-1 shadow-md">
        </DialogTrigger>
        <DialogContent>
          <DialogHeader>
            <DialogTitle className="flex justify-center"><h1>Empresa</h1></DialogTitle>
          </DialogHeader>
          <div className="mt-4">
           <form className="flex flex-col" onSubmit={handleSubmit(onSubmit)} action="">
                <label htmlFor="">
                    Nome da Empresa
                </label>
                <input
                className=""
                 type="text" 
                {...register("enterpriseName")}
                />
                <label htmlFor="">
                    Nome da Unidade
                </label>
                <input
                className=""
                 type="text" 
                {...register("unitName")}
                />
                <label htmlFor="">
                    CEP
                </label>
                <input
                className=""
                 type="text" 
                {...register("cep")}
                />
                <label htmlFor="">
                    Estado
                </label>
                <input
                className=""
                 type="text" 
                {...register("state")}
                />
                <label htmlFor="">
                    Cidade
                </label>
                <input
                className=""
                 type="text" 
                {...register("city")}
                />
                <label htmlFor="">
                    Endereço
                </label>
                <input
                className=""
                 type="text" 
                {...register("addres")}
                />
                <label htmlFor="">
                    Responsável da Unidade
                </label>
                <input
                className=""
                 type="text" 
                {...register("responsibleForUnity")}
                />
                <label htmlFor="">
                    Categoria
                </label>
                <input
                className=""
                 type="text" 
                {...register("category")}
                />
                <label htmlFor="">
                    Telefone
                </label>
                <input
                className=""
                 type="number" 
                {...register("telephone")}
                />
           </form>
          </div>
        </DialogContent>
      </Dialog>
    </>
  );
}
