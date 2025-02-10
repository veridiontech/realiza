import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { ip } from "@/utils/ip";
import axios from "axios";
import { useEffect, useState } from "react";
import { DocumentBox } from "./document-box";
import { DocumentSelectedBox } from "./document-selected-box";
import { useClient } from "@/context/Client-Provider";
import { propsDocument } from "@/types/interfaces";

export function DocumentPage() {
  const {client} = useClient()
  const [isLoading, setIsLoading] = useState(false);
  const [branchs, setBranchs] = useState([]);
  const [documents, setDocuments] = useState<propsDocument[]>([])
  // const [subcontractors, setSubContractors] = useState([])

  const getBranchClient = async() => {
    setIsLoading(true)
    try{
      const res = await axios.get(`${ip}/branch/filtered-client/${client?.idClient}`)
      setBranchs(res.data.content)
      console.log(res.data.content);
      
    }catch(err) {
      console.log("erro ao buscar filial do cliente",err);
      
    }finally{
      setIsLoading(false)
    }
  }


  useEffect(() => {
    getBranchClient()
  }, []);

  return (
    <div className="px-56 py-16">
      <div className="flex flex-col gap-5 rounded-md bg-white p-10 shadow-md">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-[20px]">Visão geral</h1>
          </div>
          <Dialog>
            <DialogTrigger asChild>
              <Button className="bg-realizaBlue">Adicionar documento</Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Adicione mais documentos</DialogTitle>
              </DialogHeader>
            </DialogContent>
          </Dialog>
        </div>
        <div className="flex flex-col gap-8 rounded-md border border-gray-300 px-10 py-5 shadow-md">
          <div className="flex items-center gap-2">
            <p className="font-medium">Filtros:</p>
            <div className="flex items-center gap-2">
              <div className="w-[15vw]">
                {/* /branch/idDoCliente */}
                <select
                  className="h-[3vh] w-full border p-1 rounded-md"
                  defaultValue=""
                >
                  <option value="" disabled>Selecione a filial do cliente</option>
                  {branchs.map((branch) => (
                    <option value="" key={branch.idBranch}>{branch.name}</option>
                  ))}
                </select>
              </div>
            </div>
          </div>
          <div className="flex flex-col gap-8">
            <div className="flex flex-col gap-5 rounded-md border border-sky-700 p-5">
              <div className="flex flex-col">
                <h2 className="text-[20px] underline">
                  Documentos Empresa Terceiro
                </h2>
              </div>
              <div className="flex items-center justify-around">
                <DocumentBox isLoading={isLoading} documents={documents} />
                <DocumentSelectedBox />
              </div>
            </div>
            <div className="flex flex-col gap-5 rounded-md border border-sky-700 p-5">
              <div className="flex flex-col">
                <h2 className="text-[20px] underline">
                  Documentos Colaboradores Terceiro
                </h2>
              </div>
              <div className="flex items-center justify-around">
                <DocumentBox isLoading={isLoading} documents={documents} />
                <DocumentSelectedBox />
              </div>
            </div>
            <div className="flex flex-col gap-5 rounded-md border border-sky-700 p-5">
              <div className="flex flex-col">
                <h2 className="text-[20px] underline">Treinamentos</h2>
              </div>
              <div className="flex items-center justify-around">
                <DocumentBox isLoading={isLoading} documents={documents} />
                <DocumentSelectedBox />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
