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

  // Estados para Documentos Empresa Terceiro
  const [enterpriseSelectedDocs, setEnterpriseSelectedDocs] = useState<
    { idDocument: string; name: string }[]
  >([]);
  const [enterpriseNonSelectedDocs, setEnterpriseNonSelectedDocs] = useState<
    any[]
  >([]);

  // Estados para Documentos Colaboradores Terceiro
  const [personalSelectedDocs, setPersonalSelectedDocs] = useState<any[]>([]);
  const [personalNonSelectedDocs, setPersonalNonSelectedDocs] = useState<any[]>(
    []
  );

  // Estados para Treinamentos
  const [serviceSelectedDocs, setServiceSelectedDocs] = useState<any[]>([]);
  const [serviceNonSelectedDocs, setServiceNonSelectedDocs] = useState<any[]>([]);

  // Estados para Outras exigências por serviço
  const [otherServiceSelectedDocs, setOtherServiceSelectedDocs] = useState<any[]>(
    []
  );
  const [otherServiceNonSelectedDocs, setOtherServiceNonSelectedDocs] = useState<
    any[]
  >([]);

  const getBranchClient = async () => {
    if (!client?.idClient) return;
    setIsLoading(true);
    try {
      const res = await axios.get(
        `${ip}/branch/filtered-client?idSearch=${client.idClient}`
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
      console.log("Dados da filial:", res.data);
      setBranch(res.data);
    } catch (err) {
      console.log("Erro ao buscar filial", err);
    }
  };

  const getDocuments = async (idBranch: string) => {
    if (!idBranch) return;
    setIsLoading(true);

    // Reseta os arrays antes de buscar novos dados
    setEnterpriseNonSelectedDocs([]);
    setEnterpriseSelectedDocs([]);
    setPersonalNonSelectedDocs([]);
    setPersonalSelectedDocs([]);
    setServiceNonSelectedDocs([]);
    setServiceSelectedDocs([]);
    setOtherServiceNonSelectedDocs([]);
    setOtherServiceSelectedDocs([]);

    try {
      const res = await axios.get(
        `${ip}/document/branch/${idBranch}/document-matrix`
      );

      // Documentos Empresa Terceiro
      if (Array.isArray(res.data.nonSelectedDocumentsEnterprise)) {
        setEnterpriseNonSelectedDocs(res.data.nonSelectedDocumentsEnterprise);
        console.log(
          "Enterprise não selecionados:",
          res.data.nonSelectedDocumentsEnterprise
        );
      }
      if (Array.isArray(res.data.selectedDocumentsEnterprise)) {
        setEnterpriseSelectedDocs(res.data.selectedDocumentsEnterprise);
      }

      // Documentos Colaboradores Terceiro
      if (Array.isArray(res.data.nonSelectedDocumentsPersonal)) {
        setPersonalNonSelectedDocs(res.data.nonSelectedDocumentsPersonal);
      }
      if (Array.isArray(res.data.selectedDocumentsPersonal)) {
        setPersonalSelectedDocs(res.data.selectedDocumentsPersonal);
      }

      // Treinamentos
      if (Array.isArray(res.data.nonSelectedDocumentsService)) {
        setServiceNonSelectedDocs(res.data.nonSelectedDocumentsService);
      }
      if (Array.isArray(res.data.selectedDocumentsService)) {
        setServiceSelectedDocs(res.data.selectedDocumentsService);
      }

      // Outras exigências por serviço
      // Caso a API retorne dados diferentes para esta categoria, ajuste as propriedades abaixo
      if (Array.isArray(res.data.nonSelectedDocumentsOtherService)) {
        setOtherServiceNonSelectedDocs(
          res.data.nonSelectedDocumentsOtherService
        );
      }
      if (Array.isArray(res.data.selectedDocumentsOtherService)) {
        setOtherServiceSelectedDocs(res.data.selectedDocumentsOtherService);
      }
    } catch (err) {
      console.error("Erro ao buscar documentos:", err);
      // Em caso de erro, zera os arrays
      setEnterpriseSelectedDocs([]);
      setPersonalSelectedDocs([]);
      setServiceSelectedDocs([]);
      setOtherServiceSelectedDocs([]);
    } finally {
      setIsLoading(false);
    }
  };

  // Funções para mover documentos da lista não selecionada para a lista selecionada em cada categoria

  const handleSelectEnterpriseDocument = (docId: string) => {
    const doc = enterpriseNonSelectedDocs.find(
      (d: any) => d.idDocument === docId
    );
    if (doc) {
      setEnterpriseNonSelectedDocs((prev) =>
        prev.filter((d: any) => d.idDocument !== docId)
      );
      setEnterpriseSelectedDocs((prev) => [...prev, doc]);
    }
  };

  const handleSelectPersonalDocument = (docId: string) => {
    const doc = personalNonSelectedDocs.find(
      (d: any) => d.idDocument === docId
    );
    if (doc) {
      setPersonalNonSelectedDocs((prev) =>
        prev.filter((d: any) => d.idDocument !== docId)
      );
      setPersonalSelectedDocs((prev) => [...prev, doc]);
    }
  };

  const handleSelectServiceDocument = (docId: string) => {
    const doc = serviceNonSelectedDocs.find(
      (d: any) => d.idDocument === docId
    );
    if (doc) {
      setServiceNonSelectedDocs((prev) =>
        prev.filter((d: any) => d.idDocument !== docId)
      );
      setServiceSelectedDocs((prev) => [...prev, doc]);
    }
  };

  const handleSelectOtherServiceDocument = (docId: string) => {
    const doc = otherServiceNonSelectedDocs.find(
      (d: any) => d.idDocument === docId
    );
    if (doc) {
      setOtherServiceNonSelectedDocs((prev) =>
        prev.filter((d: any) => d.idDocument !== docId)
      );
      setOtherServiceSelectedDocs((prev) => [...prev, doc]);
    }
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
              <Button className="bg-realizaBlue">
                Adicionar documento
              </Button>
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
            {/* Documentos Empresa Terceiro */}
            <div className="flex flex-col gap-5 rounded-md border border-sky-700 p-5">
              <h2 className="text-[20px] underline">
                Documentos Empresa Terceiro
              </h2>
              <div className="flex items-center justify-around">
                <DocumentBox
                  isLoading={isLoading}
                  documents={enterpriseNonSelectedDocs}
                  onSelectDocument={handleSelectEnterpriseDocument}
                />
                <DocumentSelectedBox
                  isLoading={isLoading}
                  selectedDocuments={enterpriseSelectedDocs}
                />
              </div>
            </div>
            {/* Documentos Colaboradores Terceiro */}
            <div className="flex flex-col gap-5 rounded-md border border-sky-700 p-5">
              <h2 className="text-[20px] underline">
                Documentos Colaboradores Terceiro
              </h2>
              <div className="flex items-center justify-around">
                <DocumentBox
                  isLoading={isLoading}
                  documents={personalNonSelectedDocs}
                  onSelectDocument={handleSelectPersonalDocument}
                />
                <DocumentSelectedBox
                  isLoading={isLoading}
                  selectedDocuments={personalSelectedDocs}
                />
              </div>
            </div>
            {/* Treinamentos */}
            <div className="flex flex-col gap-5 rounded-md border border-sky-700 p-5">
              <h2 className="text-[20px] underline">Treinamentos</h2>
              <div className="flex items-center justify-around">
                <DocumentBox
                  isLoading={isLoading}
                  documents={serviceNonSelectedDocs}
                  onSelectDocument={handleSelectServiceDocument}
                />
                <DocumentSelectedBox
                  isLoading={isLoading}
                  selectedDocuments={serviceSelectedDocs}
                />
              </div>
            </div>
            {/* Outras exigências por serviço */}
            <div className="flex flex-col gap-5 rounded-md border border-sky-700 p-5">
              <h2 className="text-[20px] underline">
                Outras exigências por serviço
              </h2>
              <div className="flex items-center justify-around">
                <DocumentBox
                  isLoading={isLoading}
                  documents={otherServiceNonSelectedDocs}
                  onSelectDocument={handleSelectOtherServiceDocument}
                />
                <DocumentSelectedBox
                  isLoading={isLoading}
                  selectedDocuments={otherServiceSelectedDocs}
                />
              </div>
            </div>
          </div>
        </div>
        <div className="flex justify-end">
          <Link to={`/sistema/risk-matriz/${user?.idUser}`}>
            <Button className="bg-realizaBlue w-[20rem]">
              Próximo passo
            </Button>
          </Link>
        </div>
      </div>
    </div>
  );
}
