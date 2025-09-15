import { useEffect, useState, useMemo } from "react";
import axios from "axios";
import { ip } from "@/utils/ip";
import { Button } from "@/components/ui/button";
import { toast } from "sonner";
import { Eye } from "lucide-react";

interface Document {
  id: string;
  description: string;
  documentId: string;
  documentTitle: string;
}

interface CBO {
  id: string;
  code: string;
  title: string;
}

interface Position {
  id: string;
  title: string;
}

interface Service {
  id: string;
  title: string;
  risk: string;
}

interface Activity {
  id: string;
  title: string;
  risk: string;
}

export interface DocumentMatrixEntry {
  documentId: string;
  idDocumentMatrix: string;
  name: string;
  type: string;
  doesBlock: boolean;
  isDocumentUnique: boolean;
  expirationDateUnit: string;
  expirationDateAmount: number;
  idDocumentSubgroup: string;
  subgroupName: string;
  idDocumentGroup: string;
  groupName: string;
}

interface SingleProfileItem {
  id: string;
  name: string;
  status: boolean;
  description: string;
  admin: boolean;
  viewer: boolean;
  manager: boolean;
  inspector: boolean;
  documentViewer: boolean;
  registrationUser: boolean;
  registrationContract: boolean;
  laboral: boolean;
  workplaceSafety: boolean;
  registrationAndCertificates: boolean;
  general: boolean;
  health: boolean;
  environment: boolean;
  concierge: boolean;
  clientId: string;
  branchIds?: string[];
  contractIds?: string[];
}

interface ProfileDetails {
  id: string;
  name: string;
  description: string;
  admin: boolean;
  viewer: boolean;
  manager: boolean;
  inspector: boolean;
  documentViewer: boolean;
  registrationUser: boolean;
  registrationContract: boolean;
  laboral: boolean;
  workplaceSafety: boolean;
  registrationAndCertificates: boolean;
  general: boolean;
  health: boolean;
  environment: boolean;
  concierge: boolean;
  clientId: string;
  branchIds?: string[];
  contractIds?: string[];
}

interface NewProfilePayload {
  name: string;
  description: string;
  admin: boolean;
  viewer: boolean;
  manager: boolean;
  inspector: boolean;
  documentViewer: boolean;
  registrationUser: boolean;
  registrationContract: boolean;
  laboral: boolean;
  workplaceSafety: boolean;
  registrationAndCertificates: boolean;
  general: boolean;
  health: boolean;
  environment: boolean;
  concierge: boolean;
}

export function ConfigPanel() {
  const [selectTab, setSelectedTab] = useState<
    | "documents_ai"
    | "cbos"
    | "positions"
    | "services"
    | "activities"
    | "validate"
    | "profiles"
    | "documents"
  >("documents");
  const [documents, setDocuments] = useState<Document[]>([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [selectedDoc, setSelectedDoc] = useState<Document | null>(null);
  const [description, setDescription] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [cbos, setCbos] = useState<CBO[]>([]);
  const [selectedCBO, setSelectedCBO] = useState<CBO | null>(null);
  const [cboCode, setCboCode] = useState("");
  const [cboTitle, setCboTitle] = useState("");
  const [positions, setPositions] = useState<Position[]>([]);
  const [selectedPosition, setSelectedPosition] = useState<Position | null>(
    null
  );
  const [positionName, setPositionName] = useState("");
  const [services, setServices] = useState<Service[]>([]);
  const [isLoadingServices, setIsLoadingServices] = useState(false);
  const [newServiceTitle, setNewServiceTitle] = useState("");
  const [newServiceRisk, setNewServiceRisk] = useState("LOW");
  const [isCreatingService, setIsCreatingService] = useState(false);
  const [serviceSearchTerm, setServiceSearchTerm] = useState("");
  const [activities, setActivities] = useState<Activity[]>([]);
  const [isLoadingActivities, setIsLoadingActivities] = useState(false);
  const [activitySearchTerm, setActivitySearchTerm] = useState("");
  const [newActivityTitle, setNewActivityTitle] = useState("");
  const [newActivityRisk, setNewActivityRisk] = useState("LOW");
  const [isCreatingActivity, setIsCreatingActivity] = useState(false);
  const [matrixEntries, setMatrixEntries] = useState<DocumentMatrixEntry[]>([]);
  const [isLoadingMatrix, setIsLoadingMatrix] = useState(false);
  const [searchMatrixTerm, setSearchMatrixTerm] = useState("");
  const [profilesRepoItems, setProfilesRepoItems] = useState<
    SingleProfileItem[]
  >([]);
  const [isLoadingProfilesRepo, setIsLoadingProfilesRepo] = useState(false);
  const [profileSearchTerm, setProfileSearchTerm] = useState("");
  const [isProfileDetailsModalOpen, setIsProfileDetailsModalOpen] =
    useState(false);
  const [selectedProfileDetails, setSelectedProfileDetails] =
    useState<ProfileDetails | null>(null);
  const [name, setName] = useState("");
  const [newProfileDescription, setNewProfileDescription] = useState("");
  const [admin, setAdmin] = useState(false);
  const [viewer, setViewer] = useState(false);
  const [manager, setManager] = useState(false);
  const [isInspector, setIsInspector] = useState(false);
  const [documentViewer, setDocumentViewer] = useState(false);
  const [registrationUser, setRegistrationUser] = useState(false);
  const [registrationContract, setRegistrationContract] = useState(false);
  const [laboral, setLaboral] = useState(false);
  const [workplaceSafety, setWorkplaceSafety] = useState(false);
  const [registrationAndCertificates, setRegistrationAndCertificates] =
    useState(false);
  const [general, setGeneral] = useState(false);
  const [health, setHealth] = useState(false);
  const [environment, setEnvironment] = useState(false);
  const [concierge, setConcierge] = useState(false);
  const [isCreatingProfile, setIsCreatingProfile] = useState(false);
  const token = localStorage.getItem("tokenClient");
  const authHeader = { headers: { Authorization: `Bearer ${token}` } };
  const riskTranslations: Record<string, string> = {
    LOW: "Baixo",
    MEDIUM: "Médio",
    HIGH: "Alto",
    VERY_HIGH: "Muito Alto",
  };
  const expirationUnits = [
    { value: "DAYS", label: "Dias" },
    { value: "MONTHS", label: "Meses" },
    { value: "YEARS", label: "Anos" },
  ];

  const [selectedMatrixEntry, setSelectedMatrixEntry] =
    useState<DocumentMatrixEntry | null>(null);

  const [editName, setEditName] = useState("");
  const [editExpirationUnit, setEditExpirationUnit] = useState("");
  const [editExpirationAmount, setEditExpirationAmount] = useState(0);
  const [editType, setEditType] = useState("");
  const [editIsDocumentUnique, setEditIsDocumentUnique] = useState(false);
  const [editDoesBlock, setEditDoesBlock] = useState(false);
  const [newDocName, setNewDocName] = useState("");
  const [newDocType, setNewDocType] = useState("");
  const [newDocExpirationAmount, setNewDocExpirationAmount] = useState(0);
  const [newDocExpirationUnit, setNewDocExpirationUnit] =
    useState("MONTHS");
  const [newDocDoesBlock, setNewDocDoesBlock] = useState(false);
  const [newDocIsUnique, setNewDocIsUnique] = useState(false);
  const [isCreatingDocument, setIsCreatingDocument] = useState(false);

  function handleSelectMatrixEntry(entry: DocumentMatrixEntry) {
    setSelectedMatrixEntry(entry);
    setEditName(entry.name);
    setEditExpirationUnit(entry.expirationDateUnit);
    setEditExpirationAmount(entry.expirationDateAmount);
    setEditType(entry.type);
    setEditIsDocumentUnique(entry.isDocumentUnique);
    setEditDoesBlock(entry.doesBlock);
  }

  useEffect(() => {
    getDocuments();
    getCbos();
    getPositions();
    getServices();
    getActivities();
    getMatrixEntries();
    getProfilesRepo();
    getDocumentGroups();
  }, []);

  async function getDocuments() {
    setIsLoading(true);
    try {
      const { data } = await axios.get<Document[]>(`${ip}/prompt`, authHeader);
      setDocuments(data);
    } finally {
      setIsLoading(false);
    }
  }

  function handleSelect(doc: Document) {
    setSelectedDoc(doc);
    setDescription(doc.description || "");
  }

  async function handleSave() {
    if (!selectedDoc) return;
    await axios.put(
      `${ip}/prompt/${selectedDoc.id}`,
      { documentId: selectedDoc.documentId, description },
      authHeader
    );
    setDocuments((prev) =>
      prev.map((d) => (d.id === selectedDoc.id ? { ...d, description } : d))
    );
    setSelectedDoc(null);
  }

  async function getCbos() {
    const { data } = await axios.get<CBO[]>(`${ip}/cbo`, authHeader);
    setCbos(data || []);
  }

  async function handleSaveCBO() {
    if (selectedCBO) {
      await axios.put(
        `${ip}/cbo/${selectedCBO.id}`,
        { code: cboCode, title: cboTitle },
        authHeader
      );
    } else {
      await axios.post(
        `${ip}/cbo`,
        { code: cboCode, title: cboTitle },
        authHeader
      );
    }
    setCboCode("");
    setCboTitle("");
    setSelectedCBO(null);
    getCbos();
  }

  async function handleDeleteCBO(id: string) {
    await axios.delete(`${ip}/cbo/${id}`, authHeader);
    getCbos();
  }

  async function getPositions() {
    const { data } = await axios.get<Position[]>(`${ip}/position`, authHeader);
    setPositions(data || []);
  }

  async function handleSavePosition() {
    if (selectedPosition) {
      await axios.put(
        `${ip}/position/${selectedPosition.id}`,
        { title: positionName },
        authHeader
      );
    } else {
      await axios.post(`${ip}/position`, { title: positionName }, authHeader);
    }
    setPositionName("");
    setSelectedPosition(null);
    getPositions();
  }

  async function handleDeletePosition(id: string) {
    await axios.delete(`${ip}/position/${id}`, authHeader);
    getPositions();
  }

  async function getServices() {
    setIsLoadingServices(true);
    try {
      const { data } = await axios.get<Service[]>(
        `${ip}/contract/service-type`,
        { params: { owner: "REPO", idOwner: "" }, ...authHeader }
      );
      setServices(data || []);
    } catch (err) {
      console.error("Erro ao buscar serviços:", err);
    } finally {
      setIsLoadingServices(false);
    }
  }

  async function handleCreateService() {
    if (!newServiceTitle || !newServiceRisk) {
      toast.error(
        "Por favor, preencha o título e selecione o risco para o novo serviço."
      );
      return;
    }
    setIsCreatingService(true);
    try {
      await axios.post(
        `${ip}/contract/service-type/repository`,
        { title: newServiceTitle, risk: newServiceRisk },
        authHeader
      );
      toast.success("Serviço criado com sucesso!");
      setNewServiceTitle("");
      setNewServiceRisk("LOW");
      getServices();
    } catch (err) {
      console.error("Erro ao criar serviço:", err);
      toast.error("Erro ao criar serviço. Tente novamente.");
    } finally {
      setIsCreatingService(false);
    }
  }

  async function getActivities() {
    setIsLoadingActivities(true);
    try {
      const res = await axios.get<{ content?: Activity[] | Activity[] }>(
        `${ip}/contract/activity-repo`,
        {
          params: { page: 0, size: 100, sort: "title", direction: "ASC" },
          ...authHeader,
        }
      );
      if (Array.isArray(res.data)) {
        setActivities(res.data);
      } else if (res.data && Array.isArray(res.data.content)) {
        setActivities(res.data.content);
      } else {
        console.warn(
          "Formato inesperado da resposta da API de atividades:",
          res.data
        );
        setActivities([]);
      }
    } catch (err) {
      console.error("Erro ao buscar atividades:", err);
      toast.error("Erro ao carregar atividades.");
    } finally {
      setIsLoadingActivities(false);
    }
  }

  async function handleCreateActivity() {
    if (!newActivityTitle || !newActivityRisk) {
      toast.error(
        "Por favor, preencha o título e selecione o risco para a nova atividade."
      );
      return;
    }
    setIsCreatingActivity(true);
    try {
      await axios.post(
        `${ip}/contract/activity-repo`,
        { title: newActivityTitle, risk: newActivityRisk },
        authHeader
      );
      toast.success("Atividade criada com sucesso!");
      setNewActivityTitle("");
      setNewActivityRisk("LOW");
      getActivities();
    } catch (err) {
      console.error("Erro ao criar atividade:", err);
      toast.error("Erro ao criar atividade. Tente novamente.");
    } finally {
      setIsCreatingActivity(false);
    }
  }

  async function getMatrixEntries() {
    setIsLoadingMatrix(true);
    try {
      const { data } = await axios.get<DocumentMatrixEntry[]>(
        `${ip}/document/matrix`,
        {
          ...authHeader,
          params: { page: 0, size: 1000 },
        }
      );
      const list = Array.isArray(data)
        ? data
        : Array.isArray((data as any).content)
        ? (data as any).content
        : [];
      setMatrixEntries(list);
    } catch (error) {
      console.error("Erro ao carregar documentos de matriz:", error);
      toast.error("Não foi possível carregar documentos de matriz.");
    } finally {
      setIsLoadingMatrix(false);
    }
  }

  async function handleUpdateEntry(id: string) {
    const entry = matrixEntries.find((e) => e.idDocumentMatrix === id);
    if (!entry) {
      console.warn(
        "Entrada da matriz de documento não encontrada para o ID:",
        id
      );
      toast.error("Documento não encontrado para atualização.");
      return;
    }
    const payload = {
      ...entry,
      expirationDateUnit: entry.expirationDateUnit,
      expirationDateAmount: entry.expirationDateAmount,
    };
    console.log("Payload:", payload);
    try {
      const response = await axios.put(
        `${ip}/document/matrix/${id}`,
        payload,
        authHeader
      );
      console.log("ID ", id);
      console.log("Resposta da API (sucesso):", response.data);
      toast.success("Validade atualizada com sucesso!");
      getMatrixEntries();
    } catch (error) {
      console.error("Erro ao atualizar validade do documento:", error);
      if (axios.isAxiosError(error) && error.response) {
        console.error("Detalhes do erro da API:", error.response.data);
        console.error("Status do erro:", error.response.status);
        toast.error(
          `Erro ao atualizar validade: ${error.response.data.message || "Verifique o console para mais detalhes."}`
        );
      } else {
        toast.error(
          "Erro ao atualizar validade. Verifique sua conexão ou tente novamente."
        );
      }
    }
  }

  const fetchProfileDetails = (profileId: string) => {
    const profile = profilesRepoItems.find((p) => p.id === profileId);

    if (profile) {
      setSelectedProfileDetails(profile);
      setIsProfileDetailsModalOpen(true);
    } else {
      toast.error(
        "Detalhes do perfil não encontrados localmente. Recarregue a página se persistir."
      );
      console.error(
        "Perfil com ID",
        profileId,
        "não encontrado em profilesRepoItems."
      );
    }
  };

  const getProfilesRepo = async () => {
    setIsLoadingProfilesRepo(true);
    try {
      const response = await axios.get<SingleProfileItem[]>(
        `${ip}/profile/repo`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      console.log(
        "Dados da requisição de perfis do repositório:",
        response.data
      );

      const sortedProfiles = response.data.sort((a, b) =>
        a.name.localeCompare(b.name, "pt-BR", { sensitivity: "base" })
      );

      setProfilesRepoItems(sortedProfiles);
    } catch (err) {
      console.error("Erro ao buscar perfis do repositório:", err);
      toast.error("Erro ao carregar perfis do repositório.");
    } finally {
      setIsLoadingProfilesRepo(false);
    }
  };

  const handleCreateProfile = async () => {
    if (!name.trim()) {
      toast.error("Por favor, preencha o nome do perfil.");
      return;
    }

    if (!admin && !viewer && !manager && !isInspector) {
      toast.error(
        "Por favor, selecione um tipo de perfil (Admin, Visitante, Gestor ou Fiscal de contrato)."
      );
      return;
    }

    setIsCreatingProfile(true);
    try {
      const payload: NewProfilePayload = {
        name: name,
        description: newProfileDescription,
        admin: admin,
        viewer: viewer,
        manager: manager,
        inspector: isInspector,
        documentViewer: documentViewer,
        registrationUser: registrationUser,
        registrationContract: registrationContract,
        laboral: laboral,
        workplaceSafety: workplaceSafety,
        registrationAndCertificates: registrationAndCertificates,
        general: general,
        health: health,
        environment: environment,
        concierge: concierge,
      };

      const response = await axios.post<SingleProfileItem>(
        `${ip}/profile/repo`,
        payload,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );

      toast.success("Perfil criado com sucesso! 🎉");
      console.log("Perfil criado:", response.data);

      setName("");
      setNewProfileDescription("");
      setAdmin(false);
      setViewer(false);
      setManager(false);
      setIsInspector(false);
      setDocumentViewer(false);
      setRegistrationUser(false);
      setRegistrationContract(false);
      setLaboral(false);
      setWorkplaceSafety(false);
      setRegistrationAndCertificates(false);
      setGeneral(false);
      setHealth(false);
      setEnvironment(false);
      setConcierge(false);

      getProfilesRepo();
    } catch (err) {
      console.error("Erro ao criar perfil:", err);
      if (axios.isAxiosError(err) && err.response) {
        toast.error(
          `Erro ao criar perfil: ${
            err.response.data.message || "Verifique os dados."
          }`
        );
      } else {
        toast.error("Erro ao criar perfil. Tente novamente.");
      }
    } finally {
      setIsCreatingProfile(false);
    }
  };

  const filteredDocuments = useMemo(
    () =>
      documents
        .filter((d) =>
          d.documentTitle.toLowerCase().includes(searchTerm.toLowerCase())
        )
        .sort((a, b) =>
          a.documentTitle.localeCompare(b.documentTitle, "pt-BR", {
            sensitivity: "base",
          })
        ),
    [documents, searchTerm]
  );

  const filteredServices = useMemo(
    () =>
      services
        .filter(
          (s) =>
            s.title.toLowerCase().includes(serviceSearchTerm.toLowerCase()) ||
            riskTranslations[s.risk.toUpperCase()]
              ?.toLowerCase()
              .includes(serviceSearchTerm.toLowerCase())
        )
        .sort((a, b) =>
          a.title.localeCompare(b.title, "pt-BR", { sensitivity: "base" })
        ),
    [services, serviceSearchTerm, riskTranslations]
  );

  const filteredActivities = useMemo(
    () =>
      activities
        .filter(
          (a) =>
            a.title.toLowerCase().includes(activitySearchTerm.toLowerCase()) ||
            (a.risk &&
              riskTranslations[a.risk.toUpperCase()]
                ?.toLowerCase()
                .includes(activitySearchTerm.toLowerCase()))
        )
        .sort((a, b) =>
          a.title.localeCompare(b.title, "pt-BR", { sensitivity: "base" })
        ),
    [activities, activitySearchTerm, riskTranslations]
  );

  const filteredMatrixEntries = useMemo(
    () =>
      matrixEntries.filter((e) =>
        e.name.toLowerCase().includes(searchMatrixTerm.toLowerCase())
      ),
    [matrixEntries, searchMatrixTerm]
  );

  const filteredProfilesRepo = useMemo(() => {
    return profilesRepoItems
      .filter((profile) =>
        profile.name.toLowerCase().includes(profileSearchTerm.toLowerCase())
      )
      .sort((a, b) =>
        a.name.localeCompare(b.name, "pt-BR", {
          sensitivity: "base",
        })
      );
  }, [profilesRepoItems, profileSearchTerm]);

  const [documentGroups, setDocumentGroups] = useState<any[]>([]);
  // Adiciona um estado para o termo de pesquisa dos documentos do grupo
  const [docsSearchTerm, setDocsSearchTerm] = useState("");

  async function getDocumentGroups() {
    try {
      const response = await axios.get(`${ip}/document/matrix/group`, {
        headers: { Authorization: `Bearer ${token}` },
        params: {
          page: 0,
          size: 10000,
        },
      });

      setDocumentGroups(response.data.content || []);
    } catch (error) {
      console.error("Erro ao buscar grupos de documentos", error);
      toast.error("Erro ao carregar grupos de documentos.");
    }
  }

  useEffect(() => {
    getDocumentGroups();
  }, []);

  const [selectedGroup, setSelectedGroup] = useState<string | null>(null);

  const handleGroupChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const selectedGroupId = e.target.value;
    setSelectedGroup(selectedGroupId);
  };

  const [docsList, setDocsList] = useState<DocumentMatrixEntry[]>([]);
  const [isLoadingDocsList, setIsLoadingDocsList] = useState(false);

  // AQUI: A função agora busca TODOS os documentos do grupo
  async function getDocsByGroup(idDocumentGroup: string) {
    setIsLoadingDocsList(true);
    try {
      const { data } = await axios.get(
        `${ip}/document/matrix/filtered-group`,
        {
          headers: { Authorization: `Bearer ${token}` },
          params: { idSearch: idDocumentGroup, page: 0, size: 1000 },
        }
      );

      const list: DocumentMatrixEntry[] = Array.isArray(data)
        ? data
        : Array.isArray((data as any).content)
        ? (data as any).content
        : [];

      setDocsList(list);
    } catch (error) {
      console.error("Erro axios em filtered-group:", error);
      toast.error("Erro ao carregar documentos do grupo.");
    } finally {
      setIsLoadingDocsList(false);
    }
  }

  useEffect(() => {
    if (selectedGroup) {
      getDocsByGroup(selectedGroup);
    } else {
      setDocsList([]);
    }
  }, [selectedGroup, token]);

  // AQUI: Cria um `useMemo` para filtrar a lista de documentos do grupo
  const filteredDocsList = useMemo(() => {
    if (!docsSearchTerm) {
      return docsList;
    }
    return docsList.filter((doc) =>
      doc.name.toLowerCase().includes(docsSearchTerm.toLowerCase())
    );
  }, [docsList, docsSearchTerm]);

  async function saveMatrixEntry() {
    if (!selectedMatrixEntry) return;

    const payload: DocumentMatrixEntry = {
      ...selectedMatrixEntry,
      name: editName,
      expirationDateUnit: editExpirationUnit,
      expirationDateAmount: editExpirationAmount,
      type: editType,
      isDocumentUnique: editIsDocumentUnique,
      doesBlock: editDoesBlock,
    };

    try {
      await axios.put(
        `${ip}/document/matrix/${selectedMatrixEntry.idDocumentMatrix}`,
        payload,
        authHeader
      );
      toast.success("Documento atualizado com sucesso!");

      setMatrixEntries((prev) =>
        prev.map((e) =>
          e.idDocumentMatrix === payload.idDocumentMatrix ? payload : e
        )
      );
      setDocsList((prev) =>
        prev.map((e) =>
          e.idDocumentMatrix === payload.idDocumentMatrix ? payload : e
        )
      );

      setSelectedMatrixEntry(null);
    } catch (err) {
      console.error(err);
      toast.error("Erro ao atualizar documento.");
    }
  }

  async function createMatrixEntry() {
    if (!selectedGroup) {
      toast.error("Por favor, selecione um grupo antes de criar.");
      return;
    }
    if (!newDocName.trim() || !newDocType.trim()) {
      toast.error("Nome e Tipo são campos obrigatórios.");
      return;
    }

    setIsCreatingDocument(true);

    const payload = {
      name: newDocName,
      type: newDocType,
      doesBlock: newDocDoesBlock,
      isDocumentUnique: newDocIsUnique,
      expirationDateUnit: newDocExpirationUnit,
      expirationDateAmount: newDocExpirationAmount,
      idDocumentGroup: selectedGroup,
    };

    try {
      const response = await axios.post(
        `${ip}/document/matrix`,
        payload,
        authHeader
      );

      toast.success("Documento criado com sucesso! 🎉");
      console.log("Novo documento criado:", response.data);

      setNewDocName("");
      setNewDocType("");
      setNewDocExpirationAmount(0);
      setNewDocExpirationUnit("MONTHS");
      setNewDocDoesBlock(false);
      setNewDocIsUnique(false);

      if (selectedGroup) {
        getDocsByGroup(selectedGroup);
      }
    } catch (error) {
      console.error("Erro ao criar documento de matriz:", error);
      if (axios.isAxiosError(error)) {
        toast.error(
          `Erro ao criar: ${error.response?.data?.message || "Erro desconhecido."}`
        );
      } else {
        toast.error("Erro ao criar documento.");
      }
    } finally {
      setIsCreatingDocument(false);
    }
  }

  return (
    <div className="p-6 md:p-10 flex flex-col gap-0 md:gap-0">
      <div className="shadow-lg rounded-lg bg-white p-6 md:p-8 flex flex-col gap-6 md:gap-10 relative bottom-[8vw]">
        <h1 className="text-2xl md:text-[25px]">Configurações gerais</h1>
        <div className="bg-[#7CA1F3] w-full h-[1px]" />
        <div className="flex items-center gap-5">
          {[
            "documents_ai",
            "cbos",
            "positions",
            "services",
            "activities",
            "profiles",
            "validate",
            "documents",
          ].map((tab) => (
            <Button
              key={tab}
              className={`${
                selectTab === tab
                  ? "bg-realizaBlue text-white"
                  : "bg-transparent border text-black border-black hover:bg-neutral-300"
              }`}
              onClick={() => setSelectedTab(tab as any)}
            >
              {
                {
                  documents_ai: "Avaliação IA",
                  cbos: "CBOs",
                  positions: "Cargos",
                  services: "Serviços",
                  activities: "Atividades",
                  profiles: "Perfis e Permissões",
                  validate: "Validade Padrão",
                  documents: "Documentos",
                }[tab]
              }
            </Button>
          ))}
        </div>
      </div>
      <div className="shadow-lg rounded-lg bg-white p-6 flex flex-col gap-6">
        {selectTab === "documents_ai" && (
          <div className="flex gap-10">
            <div className="w-1/2 space-y-4">
              <h2 className="text-xl font-bold">
                Descrição de documentos para IA
              </h2>
              <input
                type="text"
                placeholder="Buscar por título..."
                className="w-full p-2 border rounded"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
              {isLoading ? (
                <p>Carregando...</p>
              ) : filteredDocuments.length > 0 ? (
                <ul className="max-h-[60vh] overflow-auto space-y-2">
                  {filteredDocuments.map((doc) => (
                    <li
                      key={doc.id}
                      onClick={() => handleSelect(doc)}
                      className="p-3 border rounded hover:bg-gray-100 cursor-pointer"
                    >
                      <strong>{doc.documentTitle}</strong>
                      <p className="text-sm text-gray-600 line-clamp-1">
                        {doc.description || "Sem descrição"}
                      </p>
                    </li>
                  ))}
                </ul>
              ) : (
                <p>Nenhum documento.</p>
              )}
            </div>
            {selectedDoc && (
              <div className="w-1/2 border-l pl-6 space-y-4">
                <h2 className="text-xl font-bold">
                  Editar: {selectedDoc.documentTitle}
                </h2>
                <textarea
                  className="w-full h-40 p-2 border rounded"
                  maxLength={1000}
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                />
                <div className="text-right text-sm text-gray-500">
                  {description.length} / 1000
                </div>
                <div className="flex gap-2">
                  <Button onClick={handleSave}>Salvar</Button>
                  <Button
                    onClick={() => setSelectedDoc(null)}
                    className="bg-gray-300 text-black"
                  >
                    Cancelar
                  </Button>
                </div>
              </div>
            )}
          </div>
        )}
        {selectTab === "cbos" && (
          <div className="flex gap-10">
            <div className="w-1/2 space-y-4">
              <h2 className="text-xl font-bold">CBOs</h2>
              <ul className="max-h-[60vh] overflow-auto space-y-2">
                {cbos.map((cbo) => (
                  <li
                    key={cbo.id}
                    className="p-3 border rounded flex justify-between"
                  >
                    <span>
                      <strong>{cbo.code}</strong> — {cbo.title}
                    </span>
                    <div className="flex gap-2">
                      <button
                        onClick={() => {
                          setSelectedCBO(cbo);
                          setCboCode(cbo.code);
                          setCboTitle(cbo.title);
                        }}
                        className="text-blue-600"
                      >
                        Editar
                      </button>
                      <button
                        onClick={() => handleDeleteCBO(cbo.id)}
                        className="text-red-600"
                      >
                        Deletar
                      </button>
                    </div>
                  </li>
                ))}
              </ul>
            </div>
            <div className="w-1/2 border-l pl-6 space-y-4">
              <h2 className="text-xl font-bold">
                {selectedCBO ? "Editar CBO" : "Novo CBO"}
              </h2>
              <input
                className="w-full p-2 border rounded"
                placeholder="Código"
                value={cboCode}
                onChange={(e) => setCboCode(e.target.value)}
              />
              <input
                className="w-full p-2 border rounded"
                placeholder="Título"
                value={cboTitle}
                onChange={(e) => setCboTitle(e.target.value)}
              />
              <div className="flex gap-2">
                <Button onClick={handleSaveCBO}>
                  {selectedCBO ? "Salvar" : "Criar CBO"}
                </Button>
                {selectedCBO && (
                  <Button
                    onClick={() => {
                      setSelectedCBO(null);
                      setCboCode("");
                      setCboTitle("");
                    }}
                    className="bg-gray-300 text-black"
                  >
                    Cancelar
                  </Button>
                )}
              </div>
            </div>
          </div>
        )}
        {selectTab === "positions" && (
          <div className="flex gap-10">
            <div className="w-1/2 space-y-4">
              <h2 className="text-xl font-bold">Cargos</h2>
              <ul className="max-h-[60vh] overflow-auto space-y-2">
                {positions.map((pos) => (
                  <li
                    key={pos.id}
                    className="p-3 border rounded flex justify-between"
                  >
                    <span>{pos.title}</span>
                    <div className="flex gap-2">
                      <button
                        onClick={() => {
                          setSelectedPosition(pos);
                          setPositionName(pos.title);
                        }}
                        className="text-blue-600"
                      >
                        Editar
                      </button>
                      <button
                        onClick={() => handleDeletePosition(pos.id)}
                        className="text-red-600"
                      >
                        Deletar
                      </button>
                    </div>
                  </li>
                ))}
              </ul>
            </div>
            <div className="w-1/2 border-l pl-6 space-y-4">
              <h2 className="text-xl font-bold">
                {selectedPosition ? "Editar Cargo" : "Novo Cargo"}
              </h2>
              <input
                className="w-full p-2 border rounded"
                placeholder="Nome do cargo"
                value={positionName}
                onChange={(e) => setPositionName(e.target.value)}
              />
              <div className="flex gap-2">
                <Button onClick={handleSavePosition}>
                  {selectedPosition ? "Salvar" : "Criar Cargo"}
                </Button>
                {selectedPosition && (
                  <Button
                    onClick={() => {
                      setSelectedPosition(null);
                      setPositionName("");
                    }}
                    className="bg-gray-300 text-black"
                  >
                    Cancelar
                  </Button>
                )}
              </div>
            </div>
          </div>
        )}
        {selectTab === "services" && (
          <div className="flex gap-10">
            <div className="w-1/2 space-y-4">
              <h2 className="text-xl font-bold">Serviços</h2>
              <input
                type="text"
                placeholder="Buscar por título ou risco..."
                className="w-full p-2 border rounded"
                value={serviceSearchTerm}
                onChange={(e) => setServiceSearchTerm(e.target.value)}
              />
              {isLoadingServices ? (
                <p className="text-gray-500">Carregando serviços...</p>
              ) : (
                <ul className="space-y-2 max-h-[65vh] overflow-y-auto pr-1">
                  {filteredServices.length > 0 ? (
                    filteredServices.map((service) => (
                      <li
                        key={service.id}
                        className="p-3 border rounded-md flex justify-between items-center"
                      >
                        <div>
                          <strong>{service.title}</strong> (Risco:{" "}
                          {riskTranslations[service.risk.toUpperCase()] ||
                            service.risk}
                          )
                        </div>
                      </li>
                    ))
                  ) : (
                    <p className="text-gray-400">Nenhum serviço encontrado.</p>
                  )}
                </ul>
              )}
            </div>
            <div className="w-1/2 border-l pl-6 space-y-4">
              <h2 className="text-xl font-bold">Novo Serviço</h2>
              <input
                className="w-full p-2 border rounded"
                placeholder="Título do Serviço"
                value={newServiceTitle}
                onChange={(e) => setNewServiceTitle(e.target.value)}
                disabled={isCreatingService}
              />
              <select
                className="w-full p-2 border rounded"
                value={newServiceRisk}
                onChange={(e) => setNewServiceRisk(e.target.value)}
                disabled={isCreatingService}
              >
                {Object.entries(riskTranslations).map(([key, label]) => (
                  <option key={key} value={key}>
                    {label}
                  </option>
                ))}
              </select>
              <div className="flex gap-2">
                <Button
                  onClick={handleCreateService}
                  disabled={isCreatingService}
                >
                  {isCreatingService ? "Criando..." : "Criar Serviço"}
                </Button>
                <Button
                  onClick={() => setNewServiceTitle("")}
                  className="bg-gray-300 text-black"
                  disabled={isCreatingService}
                >
                  Limpar
                </Button>
              </div>
            </div>
          </div>
        )}
        {selectTab === "activities" && (
          <div className="flex gap-10">
            <div className="w-1/2 space-y-4">
              <h2 className="text-xl font-bold">Atividades</h2>
              <input
                type="text"
                placeholder="Buscar por título ou risco..."
                className="w-full p-2 border rounded"
                value={activitySearchTerm}
                onChange={(e) => setActivitySearchTerm(e.target.value)}
              />
              {isLoadingActivities ? (
                <p className="text-gray-500">Carregando atividades...</p>
              ) : (
                <ul className="space-y-2 max-h-[65vh] overflow-y-auto pr-1">
                  {filteredActivities.length > 0 ? (
                    filteredActivities.map((activity) => (
                      <li
                        key={activity.id}
                        className="p-3 border rounded-md flex justify-between items-center"
                      >
                        <div>
                          <strong>{activity.title}</strong> (Risco:{" "}
                          {riskTranslations[activity.risk.toUpperCase()] ||
                            activity.risk}
                          )
                        </div>
                      </li>
                    ))
                  ) : (
                    <p className="text-gray-400">
                      Nenhuma atividade encontrada.
                    </p>
                  )}
                </ul>
              )}
            </div>
            <div className="w-1/2 border-l pl-6 space-y-4">
              <h2 className="text-xl font-bold">Nova Atividade</h2>
              <input
                className="w-full p-2 border rounded"
                placeholder="Título da Atividade"
                value={newActivityTitle}
                onChange={(e) => setNewActivityTitle(e.target.value)}
                disabled={isCreatingActivity}
              />
              <select
                className="w-full p-2 border rounded"
                value={newActivityRisk}
                onChange={(e) => setNewActivityRisk(e.target.value)}
                disabled={isCreatingActivity}
              >
                {Object.entries(riskTranslations).map(([key, label]) => (
                  <option key={key} value={key}>
                    {label}
                  </option>
                ))}
              </select>
              <div className="flex gap-2">
                <Button
                  onClick={handleCreateActivity}
                  disabled={isCreatingActivity}
                >
                  {isCreatingActivity ? "Criando..." : "Criar Atividade"}
                </Button>
                <Button
                  onClick={() => setNewActivityTitle("")}
                  className="bg-gray-300 text-black"
                  disabled={isCreatingActivity}
                >
                  Limpar
                </Button>
              </div>
            </div>
          </div>
        )}
        {selectTab === "validate" && (
          <div className="flex flex-col gap-4">
            <h2 className="text-xl font-bold">Validade de Documentos</h2>
            <input
              type="text"
              placeholder="Buscar documento..."
              className="w-full p-2 border rounded"
              value={searchMatrixTerm}
              onChange={(e) => setSearchMatrixTerm(e.target.value)}
            />
            {isLoadingMatrix ? (
              <p>Carregando documentos...</p>
            ) : filteredMatrixEntries.length === 0 ? (
              <p className="text-gray-500">
                Nenhum documento corresponde à busca.
              </p>
            ) : (
              <ul className="max-h-[70vh] overflow-auto space-y-3">
                {filteredMatrixEntries.map((entry) => (
                  <li
                    key={entry.idDocumentMatrix}
                    className="p-4 border rounded flex flex-col gap-2"
                  >
                    <strong>{entry.name}</strong>
                    <div className="flex items-center gap-2">
                      <input
                        type="number"
                        min={0}
                        className="w-20 p-1 border rounded"
                        value={entry.expirationDateAmount}
                        onChange={(e) => {
                          const amt = Number(e.target.value);
                          setMatrixEntries((list) =>
                            list.map((i) =>
                              i.idDocumentMatrix === entry.idDocumentMatrix
                                ? { ...i, expirationDateAmount: amt }
                                : i
                            )
                          );
                        }}
                      />
                      <select
                        className="p-1 border rounded"
                        value={entry.expirationDateUnit}
                        onChange={(e) => {
                          const unit = e.target.value;
                          setMatrixEntries((list) =>
                            list.map((i) =>
                              i.idDocumentMatrix === entry.idDocumentMatrix
                                ? { ...i, expirationDateUnit: unit }
                                : i
                            )
                          );
                        }}
                      >
                        {expirationUnits.map((u) => (
                          <option key={u.value} value={u.value}>
                            {u.label}
                          </option>
                        ))}
                      </select>
                      <Button
                        onClick={() =>
                          handleUpdateEntry(entry.idDocumentMatrix)
                        }
                      >
                        Salvar
                      </Button>
                    </div>
                  </li>
                ))}
              </ul>
            )}
          </div>
        )}
        {selectTab === "profiles" && (
          <div className="flex items-start justify-center gap-10 w-full">
            <div className="w-[45%] space-y-4">
              <h1 className="text-2xl font-bold mb-2">Perfis e Permissões</h1>
              <input
                type="text"
                placeholder="Buscar por nome do perfil..."
                className="w-full p-2 border rounded-md mb-4"
                value={profileSearchTerm}
                onChange={(e) => setProfileSearchTerm(e.target.value)}
              />
              {isLoadingProfilesRepo ? (
                <p className="text-gray-500">Carregando perfis...</p>
              ) : (
                <ul className="space-y-2 max-h-[65vh] overflow-y-auto pr-1">
                  {filteredProfilesRepo.length > 0 ? (
                    filteredProfilesRepo.map((profile) => (
                      <li
                        key={profile.id}
                        className="p-3 border rounded-md flex justify-between items-center"
                      >
                        <div>
                          <strong>{profile.name}</strong>
                        </div>
                        <button
                          onClick={() => fetchProfileDetails(profile.id)}
                          className="p-1 rounded-full hover:bg-gray-200"
                          title="Ver detalhes do perfil"
                        >
                          <Eye className="w-5 h-5 text-gray-600" />
                        </button>
                      </li>
                    ))
                  ) : (
                    <p className="text-gray-400">Nenhum perfil encontrado.</p>
                  )}
                </ul>
              )}
            </div>
            <div className="w-[45%] border-l pl-6 space-y-4">
              <h2 className="text-xl font-bold">Gerenciar Perfis</h2>
              <div className="border-t pt-4">
                <h3 className="text-lg font-medium mb-2">Criar novo perfil</h3>
                <div className="flex flex-col gap-4">
                  <input
                    className="border border-gray-300 rounded px-3 py-2"
                    placeholder="Nome do perfil"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    disabled={isCreatingProfile}
                  />
                  <input
                    className="border border-gray-300 rounded px-3 py-2"
                    placeholder="Descrição"
                    value={newProfileDescription}
                    onChange={(e) => setNewProfileDescription(e.target.value)}
                    disabled={isCreatingProfile}
                  />
                  <div className="flex flex-col gap-2">
                    <p className="font-medium">Tipo do perfil</p>
                    <div className="flex gap-6 flex-wrap">
                      <label className="flex items-center gap-2">
                        <input
                          type="radio"
                          name="profileType"
                          checked={admin}
                          onChange={() => {
                            setAdmin(true);
                            setViewer(false);
                            setManager(false);
                            setIsInspector(false);
                            setDocumentViewer(false);
                            setRegistrationUser(false);
                            setRegistrationContract(false);
                            setLaboral(false);
                            setWorkplaceSafety(false);
                            setRegistrationAndCertificates(false);
                            setGeneral(false);
                            setHealth(false);
                            setEnvironment(false);
                            setConcierge(false);
                          }}
                          disabled={isCreatingProfile}
                        />
                        Admin
                      </label>
                      <label className="flex items-center gap-2">
                        <input
                          type="radio"
                          name="profileType"
                          checked={viewer}
                          onChange={() => {
                            setAdmin(false);
                            setViewer(true);
                            setManager(false);
                            setIsInspector(false);
                            setDocumentViewer(false);
                            setRegistrationUser(false);
                            setRegistrationContract(false);
                            setLaboral(false);
                            setWorkplaceSafety(false);
                            setRegistrationAndCertificates(false);
                            setGeneral(false);
                            setHealth(false);
                            setEnvironment(false);
                            setConcierge(false);
                          }}
                          disabled={isCreatingProfile}
                        />
                        Visitante
                      </label>
                      <label className="flex items-center gap-2">
                        <input
                          type="radio"
                          name="profileType"
                          checked={manager}
                          onChange={() => {
                            setAdmin(false);
                            setViewer(false);
                            setManager(true);
                            setIsInspector(false);
                            setDocumentViewer(true);
                            setRegistrationUser(false);
                            setRegistrationContract(false);
                            setLaboral(false);
                            setWorkplaceSafety(false);
                            setRegistrationAndCertificates(false);
                            setGeneral(false);
                            setHealth(false);
                            setEnvironment(false);
                            setConcierge(false);
                          }}
                          disabled={isCreatingProfile}
                        />
                        Gestor
                      </label>
                      <label className="flex items-center gap-2">
                        <input
                          type="radio"
                          name="profileType"
                          checked={isInspector}
                          onChange={() => {
                            setAdmin(false);
                            setViewer(false);
                            setManager(false);
                            setIsInspector(true);
                            setDocumentViewer(false);
                            setRegistrationUser(false);
                            setRegistrationContract(false);
                            setLaboral(false);
                            setWorkplaceSafety(false);
                            setRegistrationAndCertificates(false);
                            setGeneral(false);
                            setHealth(false);
                            setEnvironment(false);
                            setConcierge(false);
                          }}
                          disabled={isCreatingProfile}
                        />
                        Fiscal de contrato
                      </label>
                    </div>
                  </div>

                  {(manager || isInspector) && (
                    <div className="flex flex-col gap-2 mt-4">
                      <p className="font-medium">Permissões</p>
                      <div className="grid grid-cols-2 gap-2">
                        <label className="flex items-center gap-2">
                          <input
                            type="checkbox"
                            checked={documentViewer}
                            onChange={(e) =>
                              setDocumentViewer(e.target.checked)
                            }
                            disabled={isCreatingProfile}
                          />{" "}
                          Visualizador de Documentos
                        </label>
                        <label className="flex items-center gap-2">
                          <input
                            type="checkbox"
                            checked={registrationUser}
                            onChange={(e) =>
                              setRegistrationUser(e.target.checked)
                            }
                            disabled={isCreatingProfile}
                          />{" "}
                          Cadastro de Usuários
                        </label>
                        <label className="flex items-center gap-2">
                          <input
                            type="checkbox"
                            checked={registrationContract}
                            onChange={(e) =>
                              setRegistrationContract(e.target.checked)
                            }
                            disabled={isCreatingProfile}
                          />{" "}
                          Cadastro de Contratos
                        </label>
                        <label className="flex items-center gap-2">
                          <input
                            type="checkbox"
                            checked={laboral}
                            onChange={(e) => setLaboral(e.target.checked)}
                            disabled={isCreatingProfile}
                          />{" "}
                          Trabalhista
                        </label>
                        <label className="flex items-center gap-2">
                          <input
                            type="checkbox"
                            checked={workplaceSafety}
                            onChange={(e) =>
                              setWorkplaceSafety(e.target.checked)
                            }
                            disabled={isCreatingProfile}
                          />{" "}
                          Segurança do Trabalho
                        </label>
                        <label className="flex items-center gap-2">
                          <input
                            type="checkbox"
                            checked={registrationAndCertificates}
                            onChange={(e) =>
                              setRegistrationAndCertificates(e.target.checked)
                            }
                            disabled={isCreatingProfile}
                          />{" "}
                          Cadastro e certidões
                        </label>
                        <label className="flex items-center gap-2">
                          <input
                            type="checkbox"
                            checked={general}
                            onChange={(e) => setGeneral(e.target.checked)}
                            disabled={isCreatingProfile}
                          />{" "}
                          Geral
                        </label>
                        <label className="flex items-center gap-2">
                          <input
                            type="checkbox"
                            checked={health}
                            onChange={(e) => setHealth(e.target.checked)}
                            disabled={isCreatingProfile}
                          />{" "}
                          Saúde
                        </label>
                        <label className="flex items-center gap-2">
                          <input
                            type="checkbox"
                            checked={environment}
                            onChange={(e) => setEnvironment(e.target.checked)}
                            disabled={isCreatingProfile}
                          />{" "}
                          Meio Ambiente
                        </label>
                        <label className="flex items-center gap-2">
                          <input
                            type="checkbox"
                            checked={concierge}
                            onChange={(e) => setConcierge(e.target.checked)}
                            disabled={isCreatingProfile}
                          />{" "}
                          Portaria
                        </label>
                      </div>
                    </div>
                  )}

                  <button
                    onClick={handleCreateProfile}
                    className="bg-realizaBlue text-white px-4 py-2 rounded w-fit"
                    disabled={isCreatingProfile}
                  >
                    {isCreatingProfile ? "Criando perfil..." : "Criar perfil"}
                  </button>
                  <button
                    onClick={() => {
                      setName("");
                      setNewProfileDescription("");
                      setAdmin(false);
                      setViewer(false);
                      setManager(false);
                      setIsInspector(false);
                      setDocumentViewer(false);
                      setRegistrationUser(false);
                      setRegistrationContract(false);
                      setLaboral(false);
                      setWorkplaceSafety(false);
                      setRegistrationAndCertificates(false);
                      setGeneral(false);
                      setHealth(false);
                      setEnvironment(false);
                      setConcierge(false);
                    }}
                    className="bg-gray-400 text-white px-4 py-2 rounded w-fit"
                    disabled={isCreatingProfile}
                  >
                    Limpar Campos
                  </button>
                </div>
              </div>
            </div>
          </div>
        )}
        {selectTab === "documents" && (
          <div className="flex items-start justify-center gap-10 w-full">
            <div className="w-[45%] space-y-4">
              <h2 className="text-xl font-bold">Grupos e Documentos</h2>
              <p className="text-sm text-gray-500">
                Selecione um grupo para visualizar e gerenciar os documentos.
              </p>
              <div className="space-y-2">
                <label className="block text-sm font-medium">
                  Selecione um Grupo
                </label>
                <select
                  id="documentGroup"
                  className="w-full p-2 border rounded-md"
                  value={selectedGroup || ""}
                  onChange={handleGroupChange}
                >
                  <option value="">Selecione um Grupo</option>
                  {documentGroups.map((group) => (
                    <option
                      key={group.idDocumentGroup}
                      value={group.idDocumentGroup}
                    >
                      {group.groupName}
                    </option>
                  ))}
                </select>
              </div>
              <h3 className="text-lg font-semibold mt-6 mb-2">
                Documentos do Grupo
              </h3>
              {!selectedGroup ? (
                <p className="text-gray-500">
                  Selecione um grupo para visualizar os documentos.
                </p>
              ) : isLoadingDocsList ? (
                <p className="text-gray-500">Carregando documentos...</p>
              ) : (
                <>
                  {/* AQUI: Adiciona a barra de pesquisa */}
                  <input
                    type="text"
                    placeholder="Filtrar documentos por nome..."
                    className="w-full p-2 border rounded-md mb-4"
                    value={docsSearchTerm}
                    onChange={(e) => setDocsSearchTerm(e.target.value)}
                  />
                  {filteredDocsList.length > 0 ? (
                    <ul className="space-y-2 max-h-[40vh] overflow-y-auto pr-1">
                      {filteredDocsList.map((doc) => (
                        <li
                          key={doc.idDocumentMatrix}
                          className="p-3 border rounded-md hover:bg-gray-100 cursor-pointer transition-colors"
                          onClick={() => handleSelectMatrixEntry(doc)}
                        >
                          <strong className="block">{doc.name}</strong>
                          <p className="text-xs text-gray-500 mt-1">
                            {doc.expirationDateAmount > 0
                              ? `Validade: ${
                                  doc.expirationDateAmount
                                } ${
                                  expirationUnits.find(
                                    (u) => u.value === doc.expirationDateUnit
                                  )?.label
                                }`
                              : "Sem validade padrão"}
                          </p>
                        </li>
                      ))}
                    </ul>
                  ) : (
                    <p className="text-gray-400">
                      Nenhum documento encontrado para este grupo ou filtro.
                    </p>
                  )}
                  {selectedMatrixEntry && (
                    <div className="mt-6 p-4 bg-gray-50 rounded-lg shadow-inner space-y-4">
                      <h3 className="text-lg font-semibold">
                        Editar Documento
                      </h3>
                      <div>
                        <label
                          htmlFor="editName"
                          className="block text-sm font-medium mb-1"
                        >
                          Nome do Documento
                        </label>
                        <input
                          id="editName"
                          className="w-full p-2 border rounded-md"
                          value={editName}
                          onChange={(e) => setEditName(e.target.value)}
                        />
                      </div>
                      <div>
                        <label
                          htmlFor="editType"
                          className="block text-sm font-medium mb-1"
                        >
                          Tipo de Arquivo
                        </label>
                        <input
                          id="editType"
                          className="w-full p-2 border rounded-md"
                          value={editType}
                          onChange={(e) => setEditType(e.target.value)}
                        />
                      </div>
                      <div>
                        <p className="block text-sm font-medium mb-1">
                          Validade
                        </p>
                        <div className="flex gap-2 items-end">
                          <div className="w-1/3">
                            <label htmlFor="editExpAmount" className="sr-only">
                              Quantidade
                            </label>
                            <input
                              id="editExpAmount"
                              type="number"
                              min={0}
                              className="w-full p-2 border rounded-md"
                              value={editExpirationAmount}
                              onChange={(e) =>
                                setEditExpirationAmount(Number(e.target.value))
                              }
                            />
                          </div>
                          <div className="w-2/3">
                            <label htmlFor="editExpUnit" className="sr-only">
                              Unidade
                            </label>
                            <select
                              id="editExpUnit"
                              className="w-full p-2 border rounded-md"
                              value={editExpirationUnit}
                              onChange={(e) =>
                                setEditExpirationUnit(e.target.value)
                              }
                            >
                              {expirationUnits.map((u) => (
                                <option key={u.value} value={u.value}>
                                  {u.label}
                                </option>
                              ))}
                            </select>
                          </div>
                        </div>
                      </div>
                      <div className="space-y-2">
                        <label className="flex items-center gap-2 text-sm font-medium">
                          <input
                            type="checkbox"
                            checked={editDoesBlock}
                            onChange={(e) => setEditDoesBlock(e.target.checked)}
                          />
                          Bloqueia pendência
                        </label>
                        <label className="flex items-center gap-2 text-sm font-medium">
                          <input
                            type="checkbox"
                            checked={editIsDocumentUnique}
                            onChange={(e) =>
                              setEditIsDocumentUnique(e.target.checked)
                            }
                          />
                          Documento único
                        </label>
                      </div>
                      <div className="flex gap-2 pt-2">
                        <Button onClick={saveMatrixEntry} className="w-full">
                          Salvar Alterações
                        </Button>
                        <Button
                          onClick={() => setSelectedMatrixEntry(null)}
                          className="w-full bg-gray-300 text-black hover:bg-gray-400"
                        >
                          Cancelar
                        </Button>
                      </div>
                    </div>
                  )}
                </>
              )}
            </div>
            <div className="w-[45%] border-l pl-6 space-y-6">
              <h2 className="text-xl font-bold">Gerenciar Documentos</h2>
              <div className="p-4 bg-gray-50 rounded-lg shadow-inner">
                <h3 className="text-lg font-semibold mb-3">
                  Criar Novo Documento
                </h3>
                <p className="text-xs text-red-500 mb-4">
                  * Campos obrigatórios.
                </p>
                <div className="space-y-4">
                  <div>
                    <label
                      htmlFor="newDocName"
                      className="block text-sm font-medium mb-1"
                    >
                      Nome do Documento <span className="text-red-500">*</span>
                    </label>
                    <input
                      id="newDocName"
                      className="w-full p-2 border rounded-md"
                      placeholder="Ex: ASO - Atestado de Saúde Ocupacional"
                      value={newDocName}
                      onChange={(e) => setNewDocName(e.target.value)}
                      disabled={!selectedGroup || isCreatingDocument}
                    />
                  </div>
                  <div>
                    <label
                      htmlFor="newDocType"
                      className="block text-sm font-medium mb-1"
                    >
                      Tipo de Arquivo <span className="text-red-500">*</span>
                    </label>
                    <input
                      id="newDocType"
                      className="w-full p-2 border rounded-md"
                      placeholder="Ex: PDF, PNG, JPEG"
                      value={newDocType}
                      onChange={(e) => setNewDocType(e.target.value)}
                      disabled={!selectedGroup || isCreatingDocument}
                    />
                  </div>
                  <div>
                    <p className="block text-sm font-medium mb-1">
                      Validade (Opcional)
                    </p>
                    <div className="flex gap-2 items-end">
                      <div className="w-1/3">
                        <label htmlFor="expAmount" className="sr-only">
                          Quantidade
                        </label>
                        <input
                          id="expAmount"
                          type="number"
                          min={0}
                          className="w-full p-2 border rounded-md"
                          placeholder="0"
                          value={newDocExpirationAmount}
                          onChange={(e) =>
                            setNewDocExpirationAmount(Number(e.target.value))
                          }
                          disabled={!selectedGroup || isCreatingDocument}
                        />
                      </div>
                      <div className="w-2/3">
                        <label htmlFor="expUnit" className="sr-only">
                          Unidade
                        </label>
                        <select
                          id="expUnit"
                          className="w-full p-2 border rounded-md"
                          value={newDocExpirationUnit}
                          onChange={(e) => setNewDocExpirationUnit(e.target.value)}
                          disabled={!selectedGroup || isCreatingDocument}
                        >
                          {expirationUnits.map((u) => (
                            <option key={u.value} value={u.value}>
                              {u.label}
                            </option>
                          ))}
                        </select>
                      </div>
                    </div>
                  </div>
                  <div className="space-y-2">
                    <label className="flex items-center gap-2 text-sm font-medium">
                      <input
                        type="checkbox"
                        checked={newDocDoesBlock}
                        onChange={(e) => setNewDocDoesBlock(e.target.checked)}
                        disabled={!selectedGroup || isCreatingDocument}
                      />
                      Documento bloqueia?
                    </label>
                    <p className="text-xs text-gray-500 ml-6 -mt-1">
                      Se marcado, o documento precisa estar valido para permitir a
                      entrada de um colaborador.
                    </p>
                    <label className="flex items-center gap-2 text-sm font-medium">
                      <input
                        type="checkbox"
                        checked={newDocIsUnique}
                        onChange={(e) => setNewDocIsUnique(e.target.checked)}
                        disabled={!selectedGroup || isCreatingDocument}
                      />
                      Este é um documento único?
                    </label>
                    <p className="text-xs text-gray-500 ml-6 -mt-1">
                      Marque se o documento se espelha em outros contratos.
                    </p>
                  </div>
                  <Button
                    onClick={createMatrixEntry}
                    disabled={!selectedGroup || isCreatingDocument}
                    className="w-full mt-4"
                  >
                    {isCreatingDocument ? "Criando..." : "Criar Documento"}
                  </Button>
                </div>
              </div>
            </div>
          </div>
        )}
        {isProfileDetailsModalOpen && selectedProfileDetails && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white p-6 rounded shadow-lg w-full max-w-md max-h-[90vh] overflow-y-auto">
              <h3 className="text-xl font-semibold mb-4">
                Detalhes do Perfil: {selectedProfileDetails.name}
              </h3>
              <div className="grid grid-cols-1 gap-2 text-sm">
                <p>
                  <strong>Descrição:</strong>{" "}
                  {selectedProfileDetails.description ||
                    "Nenhuma descrição informada"}
                </p>
                <p>
                  <strong>Admin:</strong>{" "}
                  {selectedProfileDetails.admin ? "Sim" : "Não"}
                </p>
                <p>
                  <strong>Visitante:</strong>{" "}
                  {selectedProfileDetails.viewer ? "Sim" : "Não"}
                </p>
                <p>
                  <strong>Gestor:</strong>{" "}
                  {selectedProfileDetails.manager ? "Sim" : "Não"}
                </p>
                <p>
                  <strong>Fiscal:</strong>{" "}
                  {selectedProfileDetails.inspector ? "Sim" : "Não"}
                </p>
                <p>
                  <strong>Visualizador de Documentos:</strong>{" "}
                  {selectedProfileDetails.documentViewer ? "Sim" : "Não"}
                </p>
                <p>
                  <strong>Cadastro de Usuários:</strong>{" "}
                  {selectedProfileDetails.registrationUser ? "Sim" : "Não"}
                </p>
                <p>
                  <strong>Cadastro de Contratos:</strong>{" "}
                  {selectedProfileDetails.registrationContract ? "Sim" : "Não"}
                </p>
                <p>
                  <strong>Trabalhista:</strong>{" "}
                  {selectedProfileDetails.laboral ? "Sim" : "Não"}
                </p>
                <p>
                  <strong>Segurança do Trabalho:</strong>{" "}
                  {selectedProfileDetails.workplaceSafety ? "Sim" : "Não"}
                </p>
                <p>
                  <strong>Cadastro e Certidões:</strong>{" "}
                  {selectedProfileDetails.registrationAndCertificates
                    ? "Sim"
                    : "Não"}
                </p>
                <p>
                  <strong>Geral:</strong>{" "}
                  {selectedProfileDetails.general ? "Sim" : "Não"}
                </p>
                <p>
                  <strong>Saúde:</strong>{" "}
                  {selectedProfileDetails.health ? "Sim" : "Não"}
                </p>
                <p>
                  <strong>Meio Ambiente:</strong>{" "}
                  {selectedProfileDetails.environment ? "Sim" : "Não"}
                </p>
                <p>
                  <strong>Portaria:</strong>{" "}
                  {selectedProfileDetails.concierge ? "Sim" : "Não"}
                </p>
              </div>
              <button
                onClick={() => setIsProfileDetailsModalOpen(false)}
                className="mt-6 bg-gray-300 text-gray-800 px-4 py-2 rounded hover:bg-gray-400"
              >
                Fechar
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}