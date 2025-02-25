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
import { propsBranch } from "@/types/interfaces";
import { useBranch } from "@/context/Branch-provider";
import { Link } from "react-router-dom";
import { useUser } from "@/context/user-provider";

export function DocumentPage() {
  const { client } = useClient();
  const { user } = useUser();
  const [isLoading, setIsLoading] = useState(false);
  const [branchs, setBranchs] = useState<propsBranch[]>([]);
  const { branch, setBranch } = useBranch();
  const [documents, setDocuments] = useState<
    { idDocument: string; name: string }[]
  >([]);
  const [selectedDocuments, setSelectedDocuments] = useState<any[]>([]);
  const [servicesDocuments, setServiceDocuments] = useState<any[]>([]);
  const [nonSelectedDocumentsService, setNonSeletedDocumentsService] = useState<
    any[]
  >([]);
  const [nonSelectedDocumentsEnterprise, setnonSelectedDocumentsEnterprise] =
    useState<any[]>([]);
  const [nonSeletedDocumentsPersonal, setNonSeletedDocumentsPersonal] =
    useState<any[]>([]);

  const getBranchClient = async () => {
    if (!client?.idClient) return;
    setIsLoading(true);
    try {
      const res = await axios.get(
        `${ip}/branch/filtered-client?idSearch=${client.idClient}`,
      );
      setBranchs(res.data.content);
      console.log("Filiais:", res.data.content);
    } catch (err) {
      console.log("Erro ao buscar filial do cliente", err);
    } finally {
      setIsLoading(false);
    }
  };

  const getUniqueBranch = async (idBranch: string) => {
    try {
      const res = await axios.get(`${ip}/branch/${idBranch}`);
      console.log("dados das filiais", res.data);
      setBranch(res.data);
    } catch (err) {
      console.log("erro ao buscar filial", err);
    }
  };

  const getDocuments = async (idBranch: string) => {
    if (!idBranch) return;
    setIsLoading(true);

    setNonSeletedDocumentsService([]);
    setNonSeletedDocumentsPersonal([]);
    setnonSelectedDocumentsEnterprise([]);
    try {
      const res = await axios.get(
        `${ip}/document/branch/${idBranch}/document-matrix`,
      );
      console.log("selecionados:", res.data.selectedDocumentsEnterprise);

      console.log("nao selecionados:", res.data.nonSelectedDocumentsEnterprise);

      if (Array.isArray(res.data.nonSelectedDocumentsService)) {
        setNonSeletedDocumentsService(res.data.nonSelectedDocumentsService);
      }
      if (Array.isArray(res.data.nonSelectedDocumentsEnterprise)) {
        setnonSelectedDocumentsEnterprise(
          res.data.nonSelectedDocumentsEnterprise,
        );
        console.log(
          "documentos nao selecionados da matriz:",
          res.data.nonSelectedDocumentsEnterprise,
        );
      }
      if (Array.isArray(res.data.nonSelectedDocumentsPersonal)) {
        setNonSeletedDocumentsPersonal(res.data.nonSelectedDocumentsPersonal);
      }
      if (Array.isArray(res.data.selectedDocumentsService)) {
        setServiceDocuments(res.data.selectedDocumentsService);
      }
      if (Array.isArray(res.data.selectedDocumentsPersonal)) {
        setSelectedDocuments(res.data.selectedDocumentsPersonal);
      }
      if (Array.isArray(res.data.selectedDocumentsEnterprise)) {
        setDocuments(res.data.selectedDocumentsEnterprise);
      } else {
        setDocuments([]);
      }
    } catch (err) {
      console.error("Erro ao buscar documentos:", err);
      setDocuments([]);
    } finally {
      setIsLoading(false);
    }
  };

  // Função para remover um documento do array após a seleção
  const removeDocument = (documentId: string) => {
    setDocuments((prevDocs) =>
      prevDocs.filter((doc) => doc.idDocument !== documentId),
    );
  };

  useEffect(() => {
    if (client) {
      getBranchClient();
    }
  }, [client]);

  useEffect(() => {
    const fetchDocuments = async () => {
      if (!branch?.idBranch) return;
      setIsLoading(true);
      try {
        await getDocuments(branch.idBranch);
      } catch (err) {
        console.error("Erro ao carregar documentos:", err);
      } finally {
        setIsLoading(false);
      }
    };

    fetchDocuments();
  }, [branch?.idBranch]);

  return (
    <div className="px-56 py-16">
      <div className="flex flex-col gap-5 rounded-md bg-white p-10 shadow-md">
        <div className="flex items-center justify-between">
          <h1 className="text-[20px]">Visão geral</h1>
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
                <select
                  className="h-[3vh] w-full rounded-md border"
                  defaultValue=""
                  onChange={(e) => getUniqueBranch(e.target.value)}
                >
                  <option value="" disabled>
                    Selecione a filial do cliente
                  </option>
                  {branchs.map((branch) => (
                    <option value={branch.idBranch} key={branch.idBranch}>
                      {branch.name}
                    </option>
                  ))}
                </select>
              </div>
            </div>
          </div>
          <div className="flex flex-col gap-8">
            <div className="flex flex-col gap-5 rounded-md border border-sky-700 p-5">
              <h2 className="text-[20px] underline">
                Documentos Empresa Terceiro
              </h2>
              <div className="flex items-center justify-around">
                <DocumentBox
                  isLoading={isLoading}
                  documents={nonSelectedDocumentsEnterprise}
                  onSelectDocument={removeDocument}
                />
                <DocumentSelectedBox
                  isLoading={isLoading}
                  selectedDocuments={documents}
                />
              </div>
            </div>
            <div className="flex flex-col gap-5 rounded-md border border-sky-700 p-5">
              <h2 className="text-[20px] underline">
                Documentos Colaboradores Terceiro
              </h2>
              <div className="flex items-center justify-around">
                <DocumentBox
                  isLoading={isLoading}
                  documents={selectedDocuments}
                  onSelectDocument={(docId) => {
                    setSelectedDocuments((prev) =>
                      prev.filter((doc) => doc.idDocument !== docId),
                    );
                  }}
                />
                <DocumentSelectedBox
                  isLoading={isLoading}
                  selectedDocuments={nonSeletedDocumentsPersonal}
                />
              </div>
            </div>
            <div className="flex flex-col gap-5 rounded-md border border-sky-700 p-5">
              <h2 className="text-[20px] underline">Treinamentos</h2>
              <div className="flex items-center justify-around">
                <DocumentBox
                  isLoading={isLoading}
                  documents={servicesDocuments}
                  onSelectDocument={(docId) => {
                    setServiceDocuments((prev) =>
                      prev.filter((doc) => doc.idDocument !== docId),
                    );
                  }}
                />
                <DocumentSelectedBox
                  isLoading={isLoading}
                  selectedDocuments={nonSelectedDocumentsService}
                />
              </div>
            </div>
            <div className="flex flex-col gap-5 rounded-md border border-sky-700 p-5">
              <h2 className="text-[20px] underline">
                Outras exigências por serviço
              </h2>
              <div className="flex items-center justify-around">
                <DocumentBox
                  isLoading={isLoading}
                  documents={servicesDocuments}
                  onSelectDocument={(docId) => {
                    setServiceDocuments((prev) =>
                      prev.filter((doc) => doc.idDocument !== docId),
                    );
                  }}
                />
                <DocumentSelectedBox
                  isLoading={isLoading}
                  selectedDocuments={nonSelectedDocumentsService}
                />
              </div>
            </div>
          </div>
        </div>
        <div className="flex justify-end">
          <Link to={`/sistema/risk-matriz/${user?.idUser}`}>
            <Button className="bg-realizaBlue w-[20rem]">Próximo passo</Button>
          </Link>
        </div>
      </div>
    </div>
  );
}
