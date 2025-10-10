import { useEffect, useState, useMemo } from "react";
import axios from "axios";
import * as XLSX from "xlsx";
import { ip } from "@/utils/ip";
import { Button } from "@/components/ui/button";
import { toast } from "sonner";
import { Trash2, Pencil, FileDown } from "lucide-react";
import { useClient } from "@/context/Client-Provider";

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

type ActivityApi = {
    id?: string;
    idActivity?: string;
    idActivityRepo?: string;
    title: string;
    risk: string;
};

export interface DocumentMatrixEntry {
    documentId: string;
    idDocumentMatrix: string;
    name: string;
    type: string;
    doesBlock: boolean;
    isDocumentUnique: boolean;
    expirationDateUnit: string;
    expirationDateAmount: number;
    idDocumentGroup: string;
    groupName: string;
    required: boolean;
}

type Permissions = {
    dashboard: {
        general: boolean;
        provider: boolean;
        document: boolean;
        documentDetail: boolean;
    };
    document: {
        view: {
            laboral: boolean;
            workplaceSafety: boolean;
            registrationAndCertificates: boolean;
            general: boolean;
            health: boolean;
            environment: boolean;
        };
        upload: {
            laboral: boolean;
            workplaceSafety: boolean;
            registrationAndCertificates: boolean;
            general: boolean;
            health: boolean;
            environment: boolean;
        };
        exempt: {
            laboral: boolean;
            workplaceSafety: boolean;
            registrationAndCertificates: boolean;
            general: boolean;
            health: boolean;
            environment: boolean;
        };
    };
    contract: {
        finish: boolean;
        suspend: boolean;
        create: boolean;
    };
    reception: boolean;
};

type ProfileDetails = {
    id: string;
    name: string;
    description: string;
    admin: boolean;
    permissions: Permissions;
    clientId: string;
};

interface SingleProfileItem {
    id: string;
    name: string;
    profileName?: string;
    status: boolean;
    description: string;
    admin: boolean;
}

type User = {
    id: string;
    fullName: string;
    email: string;
};

interface ServiceResponse {
    idServiceType: string;
    title: string;
    risk: string;
}

export function ConfigPanel() {
    const { client } = useClient();
    const clientId = client?.idClient;
    const [selectTab, setSelectedTab] = useState<
        | "documents_ai"
        | "cbos"
        | "positions"
        | "services"
        | "activities"
        | "profiles"
        | "documents"
    >("activities");
    const [documents, setDocuments] = useState<Document[]>([]);
    const [searchTerm, setSearchTerm] = useState("");
    const [selectedDoc, setSelectedDoc] = useState<Document | null>(null);
    const [description, setDescription] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const [cbos, setCbos] = useState<CBO[]>([]);
    const [cboSearchTerm, setCboSearchTerm] = useState("");
    const [selectedCBO, setSelectedCBO] = useState<CBO | null>(null);
    const [cboCode, setCboCode] = useState("");
    const [cboTitle, setCboTitle] = useState("");
    const [positions, setPositions] = useState<Position[]>([]);
    // NOVO ESTADO DE BUSCA PARA CARGOS
    const [positionSearchTerm, setPositionSearchTerm] = useState(""); 
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
    const [, setMatrixEntries] = useState<DocumentMatrixEntry[]>([]);
    const [, setProfilesRepoItems] = useState<
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
    const [isCreatingProfile, setIsCreatingProfile] = useState(false);
    const token = localStorage.getItem("tokenClient");
    const authHeader = { headers: { Authorization: `Bearer ${token}` } };
    const riskTranslations: Record<string, string> = {
        LOW: "Baixo",
        MEDIUM: "Médio",
        HIGH: "Alto",
        VERY_HIGH: "Muito Alto",
    };
    const expirationUnits = [{ value: "MONTHS", label: "Mês(es)" }];

    const [selectedMatrixEntry, setSelectedMatrixEntry] =
        useState<DocumentMatrixEntry | null>(null);
    const [editName, setEditName] = useState("");
    const [editExpirationUnit, setEditExpirationUnit] = useState("");
    const [editExpirationAmount, setEditExpirationAmount] = useState(0);
    const [editType, setEditType] = useState("");
    const [editIsDocumentUnique, setEditIsDocumentUnique] = useState(false);
    const [editDoesBlock, setEditDoesBlock] = useState(false);
    const [editIsRequired, setEditIsRequired] = useState(false);
    const [newDocName, setNewDocName] = useState("");
    const [newDocType, setNewDocType] = useState("");
    const [newDocExpirationAmount, setNewDocExpirationAmount] = useState(0);
    const [, setNewDocExpirationUnit] =
        useState("MONTHS");
    const [newDocDoesBlock, setNewDocDoesBlock] = useState(false);
    const [newDocIsUnique, setNewDocIsUnique] = useState(false);
    const [newDocIsRequired, setNewDocIsRequired] = useState(false);
    const [isCreatingDocument, setIsCreatingDocument] = useState(false);
    const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
    const [isFinalConfirmModalOpen, setIsFinalConfirmModalOpen] = useState(false);
    const [associatedUsers, setAssociatedUsers] = useState<User[]>([]);
    const [individualAssignments, setIndividualAssignments] = useState<Record<string, string>>({});
    const [profiles, setProfiles] = useState<SingleProfileItem[]>([]);
    const [isEditingMatrix, setIsEditingMatrix] = useState(false);

    const [editingService, setEditingService] = useState<Service | null>(null);
    const [editingServiceTitle, setEditingServiceTitle] = useState("");
    const [editingServiceRisk, setEditingServiceRisk] = useState("");

    const [editingActivity, setEditingActivity] = useState<Activity | null>(null);
    const [editingActivityTitle, setEditingActivityTitle] = useState("");
    const [editingActivityRisk, setEditingActivityRisk] = useState("");

    const [permissions, setPermissions] = useState<Permissions>({
        dashboard: {
            general: false,
            provider: false,
            document: false,
            documentDetail: false,
        },
        document: {
            view: {
                laboral: false,
                workplaceSafety: false,
                registrationAndCertificates: false,
                general: false,
                health: false,
                environment: false,
            },
            upload: {
                laboral: false,
                workplaceSafety: false,
                registrationAndCertificates: false,
                general: false,
                health: false,
                environment: false,
            },
            exempt: {
                laboral: false,
                workplaceSafety: false,
                registrationAndCertificates: false,
                general: false,
                health: false,
                environment: false,
            },
        },
        contract: {
            finish: false,
            suspend: false,
            create: false,
        },
        reception: false,
    });

    function handleSelectMatrixEntry(entry: DocumentMatrixEntry) {
        setSelectedMatrixEntry(entry);
        setEditName(entry.name);
        setEditExpirationUnit(entry.expirationDateUnit || "MONTHS");
        setEditExpirationAmount(entry.expirationDateAmount);
        setEditType(entry.type);
        setEditIsDocumentUnique(entry.isDocumentUnique);
        setEditDoesBlock(entry.doesBlock);
        setEditIsRequired(!!entry.required);
        setIsEditingMatrix(true);
    }

    function handleEditService(service: Service) {
        setEditingService(service);
        setEditingServiceTitle(service.title);
        setEditingServiceRisk(service.risk);
    }

    function handleCancelEditService() {
        setEditingService(null);
        setEditingServiceTitle("");
        setEditingServiceRisk("");
    }

    async function handleUpdateService() {
        if (!editingService || !editingService.id) {
            toast.error("ID do serviço não encontrado.");
            return;
        }
        setIsCreatingService(true);
        try {
            const url = `${ip}/contract/service-type/repository/${editingService.id}`;
            const payload = {
                title: editingServiceTitle,
                risk: editingServiceRisk,
            };
            const response = await axios.put(url, payload, authHeader);
            toast.success("Serviço atualizado com sucesso!");
            console.log('✅ Serviço atualizado:', response.data);
            handleCancelEditService();
            getServices();
        } catch (err) {
            console.error("❌ Erro ao atualizar serviço:", err);
            toast.error("Erro ao atualizar serviço. Tente novamente.");
        } finally {
            setIsCreatingService(false);
        }
    }

    async function handleDeleteService(id: string) {
        if (!id) {
            toast.error("ID do serviço não encontrado.");
            return;
        }
        try {
            const url = `${ip}/contract/service-type/repository/${id}`;
            await axios.delete(url, authHeader);
            toast.success("Serviço deletado com sucesso!");
            console.log('✅ Serviço deletado:', id);
            getServices();
        } catch (error) {
            console.error("❌ Erro ao deletar serviço:", error);
            toast.error("Erro ao deletar serviço. Tente novamente.");
        }
    }

    function handleEditActivity(activity: Activity) {
        console.log("✏️ Editar atividade:", activity);
        setEditingActivity(activity);
        setEditingActivityTitle(activity.title);
        setEditingActivityRisk(activity.risk);
    }


    function handleCancelEditActivity() {
        console.log('↩️ Cancelando edição de atividade.');
        setEditingActivity(null);
        setEditingActivityTitle("");
        setEditingActivityRisk("");
    }

    async function handleUpdateActivity() {
        console.log('Verificando se a atividade em edição tem um ID:', editingActivity?.id);
        if (!editingActivity || !editingActivity.id) {
            toast.error("ID da atividade não encontrado.");
            console.error("❌ ID da atividade ausente. Não é possível fazer a atualização.");
            return;
        }
        setIsCreatingActivity(true);
        try {
            const url = `${ip}/contract/activity-repo/${editingActivity.id}`;
            const payload = {
                title: editingActivityTitle,
                risk: editingActivityRisk,
            };
            console.log(`📤 Enviando requisição PUT para: ${url}`);
            console.log('Payload da requisição:', payload);

            const response = await axios.put(url, payload, authHeader);
            toast.success("Atividade atualizada com sucesso!");
            console.log('✅ Atividade atualizada:', response.data);
            handleCancelEditActivity();
            getActivities();
        } catch (err) {
            console.error("❌ Erro ao atualizar atividade:", err);
            toast.error("Erro ao atualizar atividade. Tente novamente.");
        } finally {
            setIsCreatingActivity(false);
        }
    }

    async function handleDeleteActivity(id: string) {
        if (!id) {
            toast.error("ID da atividade não encontrado.");
            return;
        }
        try {
            const url = `${ip}/contract/activity-repo/${id}`;
            await axios.delete(url, authHeader);
            toast.success("Atividade deletada com sucesso!");
            console.log('✅ Atividade deletada:', id);
            getActivities();
        } catch (error) {
            console.error("❌ Erro ao deletar atividade:", error);
            toast.error("Erro ao deletar atividade. Tente novamente.");
        }
    }


    useEffect(() => {
        getDocuments();
        getCbos();
        getPositions();
        getServices();
        getActivities();
        getProfilesRepo();
        getDocumentGroups();
    }, []);

    useEffect(() => {
        if (clientId) {
            fetchProfiles();
        }
    }, [clientId]);

    async function getDocuments() {
        setIsLoading(true);
        try {
            const url = `${ip}/prompt`;
            const { data } = await axios.get<Document[]>(url, authHeader);
            setDocuments(data);
            console.log('✅ Dados de documentos recebidos com sucesso:', data);
        } catch (error) {
            console.error('❌ Erro na requisição de documentos:', error);
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
        try {
            const url = `${ip}/prompt/${selectedDoc.id}`;
            const payload = { documentId: selectedDoc.documentId, description };
            const response = await axios.put(url, payload, authHeader);
            toast.success("Documento salvo com sucesso!");
            console.log('✅ Documento salvo com sucesso:', response.data);
            setDocuments((prev) =>
                prev.map((d) => (d.id === selectedDoc.id ? { ...d, description } : d))
            );
            setSelectedDoc(null);
        } catch (error) {
            console.error("❌ Erro ao salvar documento:", error);
            toast.error("Erro ao salvar documento. Tente novamente.");
        }
    }

    async function getCbos() {
        try {
            const url = `${ip}/cbo`;
            const { data } = await axios.get<CBO[]>(url, authHeader);
            setCbos(data || []);
            console.log('✅ Dados de CBOs recebidos com sucesso:', data);
        } catch (error) {
            console.error("❌ Erro na requisição de CBOs:", error);
            setCbos([]);
        }
    }

    async function handleSaveCBO() {
        try {
            if (selectedCBO) {
                const url = `${ip}/cbo/${selectedCBO.id}`;
                const payload = { code: cboCode, title: cboTitle };
                const response = await axios.put(url, payload, authHeader);
                toast.success("CBO atualizado com sucesso!");
                console.log('✅ CBO atualizado:', response.data);
            } else {
                const url = `${ip}/cbo`;
                const payload = { code: cboCode, title: cboTitle };
                const response = await axios.post(url, payload, authHeader);
                toast.success("CBO criado com sucesso!");
                console.log('✅ CBO criado:', response.data);
            }
            setCboCode("");
            setCboTitle("");
            setSelectedCBO(null);
            getCbos();
        } catch (error) {
            console.error("❌ Erro ao salvar CBO:", error);
            toast.error("Erro ao salvar CBO. Tente novamente.");
        }
    }

    async function handleDeleteCBO(id: string) {
        try {
            const url = `${ip}/cbo/${id}`;
            await axios.delete(url, authHeader);
            toast.success("CBO deletado com sucesso!");
            console.log('✅ CBO deletado:', id);
            getCbos();
        } catch (error) {
            console.error("❌ Erro ao deletar CBO:", error);
            toast.error("Erro ao deletar CBO. Tente novamente.");
        }
    }

    async function getPositions() {
        try {
            const url = `${ip}/position`;
            const { data } = await axios.get<Position[]>(url, authHeader);
            setPositions(data || []);
            console.log('✅ Dados de cargos recebidos com sucesso:', data);
        } catch (error) {
            console.error("❌ Erro na requisição de cargos:", error);
            setPositions([]);
        }
    }

    async function handleSavePosition() {
        try {
            if (selectedPosition) {
                const url = `${ip}/position/${selectedPosition.id}`;
                const payload = { title: positionName };
                const response = await axios.put(url, payload, authHeader);
                toast.success("Cargo atualizado com sucesso!");
                console.log('✅ Cargo atualizado:', response.data);
            } else {
                const url = `${ip}/position`;
                const payload = { title: positionName };
                const response = await axios.post(url, payload, authHeader);
                toast.success("Cargo criado com sucesso!");
                console.log('✅ Cargo criado:', response.data);
            }
            setPositionName("");
            setSelectedPosition(null);
            getPositions();
        } catch (error) {
            console.error("❌ Erro ao salvar cargo:", error);
            toast.error("Erro ao salvar cargo. Tente novamente.");
        }
    }

    async function handleDeletePosition(id: string) {
        try {
            const url = `${ip}/position/${id}`;
            await axios.delete(url, authHeader);
            toast.success("Cargo deletado com sucesso!");
            console.log('✅ Cargo deletado:', id);
            getPositions();
        } catch (error) {
            console.error("❌ Erro ao deletar cargo:", error);
            toast.error("Erro ao deletar cargo. Tente novamente.");
        }
    }

    async function getServices() {
        setIsLoadingServices(true);
        try {
            const url = `${ip}/contract/service-type`;
            const { data } = await axios.get(
                url,
                { params: { owner: "REPO", idOwner: "" }, ...authHeader }
            );

            const raw = Array.isArray(data) ? data : Array.isArray((data as any)?.content) ? (data as any).content : [];

            const normalized: Service[] = raw.map((s: ServiceResponse) => ({
                id: s.idServiceType,
                title: s.title,
                // Garantir que 'risk' existe, caso contrário, usa 'LOW'
                risk: s.risk || 'LOW'
            })).filter((s: Service) => !!s.id);

            setServices(normalized);
            console.log('✅ Dados de serviços recebidos e normalizados com sucesso:', normalized);
        } catch (e) {
            console.error("❌ Erro ao buscar serviços:", e);
            toast.error("Erro ao carregar serviços.");
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
            const url = `${ip}/contract/service-type/repository`;
            const payload = { title: newServiceTitle, risk: newServiceRisk };
            const response = await axios.post(url, payload, authHeader);
            toast.success("Serviço criado com sucesso!");
            console.log('✅ Serviço criado:', response.data);
            setNewServiceTitle("");
            setNewServiceRisk("LOW");
            getServices();
        } catch (err) {
            console.error("❌ Erro ao criar serviço:", err);
            toast.error("Erro ao criar serviço. Tente novamente.");
        } finally {
            setIsCreatingService(false);
        }
    }

    async function getActivities() {
        setIsLoadingActivities(true);
        try {
            const url = `${ip}/contract/activity-repo`;
            const { data } = await axios.get<{ content?: ActivityApi[] } | ActivityApi[]>(
                url,
                {
                    params: { page: 0, size: 100, sort: "title", direction: "ASC" },
                    ...authHeader,
                }
            );

            const raw: ActivityApi[] = Array.isArray(data)
                ? data
                : Array.isArray((data as any)?.content)
                    ? (data as any).content
                    : [];

            const normalized: Activity[] = raw
                .map((a) => ({
                    id: a.id ?? a.idActivity ?? a.idActivityRepo ?? "",
                    title: a.title,
                    risk: (a.risk || "LOW").toUpperCase(),
                }))
                .filter((a) => !!a.id);

            setActivities(normalized);
            console.log("✅ Atividades normalizadas:", normalized);
        } catch (err) {
            console.error("❌ Erro ao buscar atividades:", err);
            toast.error("Erro ao carregar atividades.");
            setActivities([]);
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
            const url = `${ip}/contract/activity-repo`;
            const payload = { title: newActivityTitle, risk: newActivityRisk };
            const response = await axios.post(url, payload, authHeader);
            toast.success("Atividade criada com sucesso!");
            console.log('✅ Atividade criada:', response.data);
            setNewActivityTitle("");
            setNewActivityRisk("LOW");
            getActivities();
        } catch (err) {
            console.error("❌ Erro ao criar atividade:", err);
            toast.error("Erro ao criar atividade. Tente novamente.");
        } finally {
            setIsCreatingActivity(false);
        }
    }

    const getProfilesRepo = async () => {
        setIsLoadingProfilesRepo(true);
        try {
            const url = `${ip}/profile/repo`;
            const response = await axios.get<SingleProfileItem[]>(
                url,
                {
                    headers: { Authorization: `Bearer ${token}` },
                }
            );
            console.log(
                "✅ Dados da requisição de perfis do repositório:",
                response.data
            );

            const sortedProfiles = response.data.sort((a, b) =>
                (a.name || "").localeCompare((b.name || ""), "pt-BR", { sensitivity: "base" })
            );

            setProfilesRepoItems(sortedProfiles);
        } catch (err) {
            console.error("❌ Erro ao buscar perfis do repositório:", err);
            toast.error("Erro ao carregar perfis do repositório.");
        } finally {
            setIsLoadingProfilesRepo(false);
        }
    };

    const handlePermissionChange = (path: string, type: 'view' | 'upload' | 'exempt' | null = null) => {
        setPermissions(prev => {
            let newState = { ...prev };
            const [mainKey, subKey] = path.split('.');

            if (mainKey === 'document' && subKey) {
                if (type) {
                    newState.document = {
                        ...newState.document,
                        [type]: {
                            ...newState.document[type],
                            [subKey]: !newState.document[type][subKey as keyof typeof newState.document.view]
                        }
                    };
                } else {
                    const documentKeys = ['view', 'upload', 'exempt'] as const;
                    if (documentKeys.includes(subKey as any)) {
                        (newState.document as any)[subKey] = !(newState.document as any)[subKey];
                    }
                }
            } else {
                let currentLevel = newState as any;
                const keys = path.split('.');
                for (let i = 0; i < keys.length - 1; i++) {
                    currentLevel[keys[i]] = { ...currentLevel[keys[i]] };
                    currentLevel = currentLevel[keys[i]];
                }
                const lastKey = keys[keys.length - 1];
                currentLevel[lastKey] = !currentLevel[lastKey];
            }

            return newState;
        });
    };

    const handleCreateProfile = async () => {
        if (!clientId || !name.trim()) {
            toast.warning("O nome do perfil é obrigatório.");
            return;
        }

        setIsCreatingProfile(true);

        let profilePermissions = permissions;

        if (admin) {
            profilePermissions = {
                dashboard: {
                    general: true,
                    provider: true,
                    document: true,
                    documentDetail: true,
                },
                document: {
                    view: {
                        laboral: true,
                        workplaceSafety: true,
                        registrationAndCertificates: true,
                        general: true,
                        health: true,
                        environment: true,
                    },
                    upload: {
                        laboral: true,
                        workplaceSafety: true,
                        registrationAndCertificates: true,
                        general: true,
                        health: true,
                        environment: true,
                    },
                    exempt: {
                        laboral: true,
                        workplaceSafety: true,
                        registrationAndCertificates: true,
                        general: true,
                        health: true,
                        environment: true,
                    },
                },
                contract: {
                    finish: true,
                    suspend: true,
                    create: true,
                },
                reception: true,
            };
        }

        const newProfile = {
            name,
            description: newProfileDescription,
            admin,
            ...profilePermissions,
            clientId,
            branchIds: [],
            contractIds: []
        };

        const tokenFromStorage = localStorage.getItem("tokenClient");
        try {
            console.log("Iniciando requisição POST para criar perfil com os dados:", newProfile);
            await axios.post(`${ip}/profile`, newProfile, {
                headers: { Authorization: `Bearer ${tokenFromStorage}` },
            });
            console.log("Perfil criado com sucesso.");
            toast.success("Perfil criado com sucesso!");
            setName("");
            setNewProfileDescription("");
            setAdmin(false);
            setPermissions({
                dashboard: { general: false, provider: false, document: false, documentDetail: false },
                document: {
                    view: { laboral: false, workplaceSafety: false, registrationAndCertificates: false, general: false, health: false, environment: false },
                    upload: { laboral: false, workplaceSafety: false, registrationAndCertificates: false, general: false, health: false, environment: false },
                    exempt: { laboral: false, workplaceSafety: false, registrationAndCertificates: false, general: false, health: false, environment: false },
                },
                contract: { finish: false, suspend: false, create: false },
                reception: false,
            });
            fetchProfiles();
        } catch (err: any) {
            console.error("Erro ao criar perfil:", err.response || err);
            toast.error("Erro ao criar o perfil.");
        } finally {
            setIsCreatingProfile(false);
        }
    };

    const fetchProfiles = async () => {
        if (!clientId) return;
        setIsLoadingProfilesRepo(true);
        const tokenFromStorage = localStorage.getItem("tokenClient");

        try {
            const { data } = await axios.get(`${ip}/profile/by-name/${clientId}`, {
                headers: { Authorization: `Bearer ${tokenFromStorage}` },
            });
            console.log("Resposta da requisição GET:", data);

            const normalized = (Array.isArray(data) ? data : []).map((p: any) => ({
                ...p,
                name: p.name ?? p.profileName ?? "",
            }));

            const sorted = normalized.sort((a: any, b: any) =>
                (a.name || "").localeCompare((b.name || ""), "pt-BR", { sensitivity: "base" })
            );

            setProfiles(sorted);
        } catch (err) {
            console.error("Erro ao buscar perfis:", err);
            toast.error("Erro ao carregar a lista de perfis.");
        } finally {
            setIsLoadingProfilesRepo(false);
        }
    };


    const handleAttemptDelete = async (profileId: string) => {
        setIndividualAssignments({});
        setIsLoadingProfilesRepo(true);
        const tokenFromStorage = localStorage.getItem("tokenClient");
        try {
            console.log(`Iniciando requisição GET para buscar detalhes do perfil ${profileId}...`);
            const detailsResponse = await axios.get(`${ip}/profile/${profileId}`, {
                headers: { Authorization: `Bearer ${tokenFromStorage}` },
            });
            console.log("Resposta da requisição GET de detalhes:", detailsResponse.data);
            setSelectedProfileDetails(detailsResponse.data);

            console.log(`Iniciando requisição GET para buscar usuários do perfil ${profileId}...`);
            const usersResponse = await axios.get(
                `${ip}/user/find-by-profile/${profileId}`,
                {
                    headers: { Authorization: `Bearer ${tokenFromStorage}` },
                }
            );
            console.log("Resposta da requisição GET de usuários:", usersResponse.data);
            setAssociatedUsers(usersResponse.data);

            setIsDeleteModalOpen(true);
        } catch (err) {
            console.error("Erro ao verificar usuários vinculados:", err);
            toast.error("Não foi possível verificar os usuários vinculados.");
        } finally {
            setIsLoadingProfilesRepo(false);
        }
    };

    const handleConfirmDelete = async () => {
        if (!selectedProfileDetails) return;
        setIsLoadingProfilesRepo(true);
        const tokenFromStorage = localStorage.getItem("tokenClient");
        try {
            console.log(`Iniciando requisição DELETE para o perfil ${selectedProfileDetails.id}...`);
            await axios.delete(`${ip}/profile/${selectedProfileDetails.id}`, {
                headers: { Authorization: `Bearer ${tokenFromStorage}` },
            });
            console.log("Perfil excluído com sucesso.");
            toast.success("Perfil excluído com sucesso!");
            setIsDeleteModalOpen(false);
            setSelectedProfileDetails(null);
            fetchProfiles();
        } catch (err) {
            console.error("Erro ao excluir o perfil:", err);
            toast.error("Erro ao excluir o perfil.");
        } finally {
            setIsLoadingProfilesRepo(false);
        }
    };

    const handleIndividualAssignmentChange = (userId: string, newProfileId: string) => {
        setIndividualAssignments(prevAssignments => ({
            ...prevAssignments,
            [userId]: newProfileId,
        }));
    };

    const handleOpenFinalConfirm = () => {
        if (Object.keys(individualAssignments).length !== associatedUsers.length) {
            toast.error("Por favor, atribua um novo perfil para cada usuário.");
            return;
        }
        setIsFinalConfirmModalOpen(true);
    };

    const handleReassignAndDelete = async () => {
        if (!selectedProfileDetails) return;
        setIsLoadingProfilesRepo(true);
        const token = localStorage.getItem("tokenClient");
        try {
            const reassignPromises = Object.entries(individualAssignments).map(([userId, profileId]) => {
                console.log(`Iniciando requisição POST para reatribuir usuário ${userId} para o perfil ${profileId}...`);
                return axios.post(`${ip}/user/${userId}/change-profile?profileId=${profileId}`, null, {
                    headers: { Authorization: `Bearer ${token}` },
                });
            });

            await Promise.all(reassignPromises);
            console.log("Todos os usuários foram reatribuídos.");
            toast.success(`${associatedUsers.length} usuário(s) foram reatribuídos.`);

            console.log(`Iniciando requisição DELETE para excluir o perfil ${selectedProfileDetails.id}...`);
            await axios.delete(`${ip}/profile/${selectedProfileDetails.id}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            console.log("Perfil excluído após reatribuição.");
            toast.success(`O perfil "${selectedProfileDetails.name}" foi excluído.`);

            setIsFinalConfirmModalOpen(false);
            setIsDeleteModalOpen(false);
            setSelectedProfileDetails(null);
            setIndividualAssignments({});
            fetchProfiles();

        } catch (error) {
            console.log("Erro ao reatribuir e excluir:", error);
            toast.error("Ocorreu um erro durante a operação.");
        } finally {
            setIsLoadingProfilesRepo(false);
        }
    };


    const filteredDocuments = useMemo(
        () =>
            documents
                .filter((d) =>
                    // CORREÇÃO do erro de 'toLowerCase' em undefined
                    (d.documentTitle || "").toLowerCase().includes((searchTerm || "").toLowerCase())
                )
                .sort((a, b) =>
                    (a.documentTitle || "").localeCompare((b.documentTitle || ""), "pt-BR", {
                        sensitivity: "base",
                    })
                ),
        [documents, searchTerm]
    );

    const filteredCbos = useMemo(
        () =>
            cbos
                .filter(
                    (cbo) =>
                        // CORREÇÃO do erro de 'toLowerCase' em undefined
                        (cbo.code || "").toLowerCase().includes((cboSearchTerm || "").toLowerCase()) ||
                        (cbo.title || "").toLowerCase().includes((cboSearchTerm || "").toLowerCase())
                )
                .sort((a, b) =>
                    (a.code || "").localeCompare((b.code || ""), "pt-BR", { sensitivity: "base" })
                ),
        [cbos, cboSearchTerm]
    );
    
    // NOVO useMemo PARA FILTRAR CARGOS
    const filteredPositions = useMemo(
        () =>
            positions
                .filter((pos) => 
                    // Garante que o title existe e faz a busca case-insensitive
                    (pos.title || "").toLowerCase().includes((positionSearchTerm || "").toLowerCase())
                )
                .sort((a, b) =>
                    (a.title || "").localeCompare((b.title || ""), "pt-BR", { sensitivity: "base" })
                ),
        [positions, positionSearchTerm]
    );

    const filteredServices = useMemo(
        () =>
            services
                .filter(
                    (s) => {
                        // CORREÇÃO: Usa uma string vazia se o risco for null/undefined para toUpperCase e para checagem da tradução.
                        const riskUpper = (s.risk || '').toUpperCase();
                        // CORREÇÃO: Usa uma string vazia se s.title for null/undefined
                        const titleMatch = (s.title || "").toLowerCase().includes((serviceSearchTerm || "").toLowerCase());
                        
                        // Garante que o toLowerCase só é chamado em uma string válida, se houver tradução.
                        const riskTranslationLower = riskTranslations[riskUpper]?.toLowerCase() || '';

                        const riskMatch = riskTranslationLower.includes((serviceSearchTerm || "").toLowerCase());
                        
                        return titleMatch || riskMatch;
                    }
                )
                .sort((a, b) =>
                    // CORREÇÃO: Usa uma string vazia se a.title for null/undefined
                    (a.title || "").localeCompare((b.title || ""), "pt-BR", { sensitivity: "base" })
                ),
        [services, serviceSearchTerm, riskTranslations]
    );

    const filteredActivities = useMemo(
        () =>
            activities
                .filter(
                    (a) => {
                        // CORREÇÃO: Usa uma string vazia se o risco for null/undefined para toUpperCase e para checagem da tradução.
                        const riskUpper = (a.risk || '').toUpperCase();
                        // CORREÇÃO: Usa uma string vazia se a.title for null/undefined
                        const titleMatch = (a.title || "").toLowerCase().includes((activitySearchTerm || "").toLowerCase());

                        // Garante que o toLowerCase só é chamado em uma string válida, se houver tradução.
                        const riskTranslationLower = riskTranslations[riskUpper]?.toLowerCase() || '';
                        
                        const riskMatch = riskTranslationLower.includes((activitySearchTerm || "").toLowerCase());

                        return titleMatch || riskMatch;
                    }
                )
                .sort((a, b) =>
                    // CORREÇÃO: Usa uma string vazia se a.title for null/undefined
                    (a.title || "").localeCompare((b.title || ""), "pt-BR", { sensitivity: "base" })
                ),
        [activities, activitySearchTerm, riskTranslations]
    );

    const filteredProfiles = useMemo(
        () =>
            profiles.filter((profile) =>
                // CORREÇÃO: Usa uma string vazia se profile.name for null/undefined
                (profile.name || "").toLowerCase().includes((profileSearchTerm || "").toLowerCase())
            ),
        [profiles, profileSearchTerm]
    );

    const [documentGroups, setDocumentGroups] = useState<any[]>([]);
    const [docsSearchTerm, setDocsSearchTerm] = useState("");

    async function getDocumentGroups() {
        try {
            const url = `${ip}/document/matrix/group`;
            const response = await axios.get(url, {
                headers: { Authorization: `Bearer ${token}` },
                params: {
                    page: 0,
                    size: 10000,
                },
            });

            setDocumentGroups(response.data.content || []);
            console.log('✅ Dados de grupos de documentos recebidos com sucesso:', response.data);
        } catch (error) {
            console.error("❌ Erro ao buscar grupos de documentos", error);
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
        setIsEditingMatrix(false);
    };

    const [docsList, setDocsList] = useState<DocumentMatrixEntry[]>([]);
    const [isLoadingDocsList, setIsLoadingDocsList] = useState(false);

    async function getDocsByGroup(idDocumentGroup: string) {
        setIsLoadingDocsList(true);
        try {
            const url = `${ip}/document/matrix/filtered-group`;
            const { data } = await axios.get(
                url,
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
            console.log('✅ Documentos do grupo recebidos com sucesso:', data);
        } catch (error) {
            console.error("❌ Erro axios em filtered-group:", error);
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

    const filteredDocsList = useMemo(() => {
        if (!docsSearchTerm) {
            return docsList;
        }
        const lowerCaseSearchTerm = docsSearchTerm.toLowerCase();
        return docsList.filter((doc) =>
            (doc.name || "").toLowerCase().includes(lowerCaseSearchTerm) ||
            (doc.type || "").toLowerCase().includes(lowerCaseSearchTerm)
        );
    }, [docsList, docsSearchTerm]);

    async function saveMatrixEntry() {
        if (!selectedMatrixEntry) return;

        const payload = {
            ...selectedMatrixEntry,
            name: editName,
            expirationDateUnit: editExpirationUnit,
            expirationDateAmount: editExpirationAmount,
            type: editType,
            isDocumentUnique: editIsDocumentUnique,
            doesBlock: editDoesBlock,
            required: !!editIsRequired,
        };

        try {
            const url = `${ip}/document/matrix/${selectedMatrixEntry.idDocumentMatrix}`;
            const response = await axios.put(
                url,
                payload,
                authHeader
            );
            toast.success("Documento atualizado com sucesso!");
            console.log('✅ Documento de matriz atualizado:', response.data);

            setMatrixEntries((prev) =>
                prev.map((e) =>
                    e.idDocumentMatrix === payload.idDocumentMatrix ? payload : e
                )
            );
            setDocsList((prev) =>
                prev.map((e) =>
                    e.idDocumentMatrix === (payload as DocumentMatrixEntry).idDocumentMatrix ? (payload as DocumentMatrixEntry) : e
                )
            );

            setSelectedMatrixEntry(null);
            setIsEditingMatrix(false);
        } catch (err) {
            console.error("❌ Erro ao atualizar documento de matriz:", err);
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

        const selectedGroupObject = documentGroups.find(
            (group) => group.idDocumentGroup === selectedGroup
        );

        if (!selectedGroupObject) {
            toast.error("O grupo selecionado é inválido. Por favor, recarregue a página.");
            console.error("Grupo selecionado não encontrado na lista de grupos carregada.");
            return;
        }

        setIsCreatingDocument(true);

        const payload = {
            name: newDocName,
            type: newDocType,
            doesBlock: !!newDocDoesBlock,
            isDocumentUnique: !!newDocIsUnique,
            expirationDateUnit: 'MONTHS',
            expirationDateAmount: newDocExpirationAmount,
            group: selectedGroup,
            required: !!newDocIsRequired,
        };

        console.log("Payload para criação de documento de matriz:", payload);

        try {
            const url = `${ip}/document/matrix`;
            const response = await axios.post(
                url,
                payload,
                authHeader
            );

            toast.success("Documento criado com sucesso! 🎉");
            console.log("✅ Novo documento criado:", response.data);

            setNewDocName("");
            setNewDocType("");
            setNewDocExpirationAmount(0);
            setNewDocExpirationUnit("MONTHS");
            setNewDocDoesBlock(false);
            setNewDocIsUnique(false);
            setNewDocIsRequired(false);

            if (selectedGroup) {
                getDocsByGroup(selectedGroup);
            }
        } catch (error) {
            console.error("❌ Erro ao criar documento de matriz:", error);
            if (axios.isAxiosError(error) && error.response) {
                toast.error(
                    `Erro ao criar: ${error.response?.data?.message || "Verifique os dados."}`
                );
            } else {
                toast.error("Erro ao criar documento.");
            }
        } finally {
            setIsCreatingDocument(false);
        }
    }

    const documentTypes = [
        { value: 'cadastro e certidões', label: 'Cadastro e Certidões' },
        { value: 'saude', label: 'Saúde' },
        { value: 'segurança do trabalho', label: 'Segurança do Trabalho' },
        { value: 'meio ambiente', label: 'Meio Ambiente' },
        { value: 'trabalhista', label: 'Trabalhista' },
        { value: 'geral', label: 'Geral' },
    ];

    const handleExportExcel = () => {
        if (!selectedGroup || docsList.length === 0) {
            toast.info("Nenhum documento para exportar.");
            return;
        }

        const selectedGroupInfo = documentGroups.find(g => g.idDocumentGroup === selectedGroup);
        const groupName = selectedGroupInfo ? selectedGroupInfo.groupName : "Grupo";

        const dataToExport = docsList.map(doc => {
            const typeLabel = documentTypes.find(t => t.value === doc.type)?.label || doc.type;
            return {
                "Nome do Documento": doc.name,
                "Tipo": typeLabel,
                "Validade (meses)": doc.expirationDateAmount > 0 ? doc.expirationDateAmount : "N/A",
                "Bloqueia": doc.doesBlock ? "Sim" : "Não",
                "Documento Único": doc.isDocumentUnique ? "Sim" : "Não",
            };
        });

        const worksheet = XLSX.utils.json_to_sheet(dataToExport);
        const workbook = XLSX.utils.book_new();
        XLSX.utils.book_append_sheet(workbook, worksheet, "Documentos");

        worksheet["!cols"] = [
            { wch: 50 },
            { wch: 25 },
            { wch: 20 },
            { wch: 10 },
            { wch: 20 },
        ];

        const fileName = `Documentos_${(groupName || '').replace(/[^a-zA-Z0-9]/g, '_')}.xlsx`;
        XLSX.writeFile(workbook, fileName);
        toast.success("Download do Excel iniciado.");
    };

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
                        "documents",
                    ].map((tab) => (
                        <Button
                            key={tab}
                            className={`${selectTab === tab
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
                            <input
                                type="text"
                                placeholder="Buscar por código ou título..."
                                className="w-full p-2 border rounded"
                                value={cboSearchTerm}
                                onChange={(e) => setCboSearchTerm(e.target.value)}
                            />
                            <ul className="max-h-[60vh] overflow-auto space-y-2">
                                {filteredCbos.length > 0 ? (
                                    filteredCbos.map((cbo) => (
                                        <li
                                            key={cbo.id}
                                            className="p-3 border rounded flex justify-between items-center"
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
                                                    <Pencil className="w-5 h-5" />
                                                </button>
                                                <button
                                                    onClick={() => handleDeleteCBO(cbo.id)}
                                                    className="text-red-600"
                                                >
                                                    <Trash2 className="w-5 h-5" />
                                                </button>
                                            </div>
                                        </li>
                                    ))
                                ) : (
                                    <p className="text-gray-400">
                                        {cboSearchTerm
                                            ? "Nenhum CBO encontrado com o termo de busca."
                                            : "Nenhum CBO cadastrado."
                                        }
                                    </p>
                                )}
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
                            {/* NOVO INPUT DE BUSCA PARA CARGOS */}
                            <input
                                type="text"
                                placeholder="Buscar por nome do cargo..."
                                className="w-full p-2 border rounded"
                                value={positionSearchTerm}
                                onChange={(e) => setPositionSearchTerm(e.target.value)}
                            />
                            {/* RENDERIZA OS CARGOS FILTRADOS */}
                            <ul className="max-h-[60vh] overflow-auto space-y-2">
                                {filteredPositions.length > 0 ? (
                                    filteredPositions.map((pos) => (
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
                                                    <Pencil className="w-5 h-5" />
                                                </button>
                                                <button
                                                    onClick={() => handleDeletePosition(pos.id)}
                                                    className="text-red-600"
                                                >
                                                    <Trash2 className="w-5 h-5" />
                                                </button>
                                            </div>
                                        </li>
                                    ))
                                ) : (
                                    <p className="text-gray-400">
                                        {positionSearchTerm
                                            ? "Nenhum cargo encontrado com o termo de busca."
                                            : "Nenhum cargo cadastrado."}
                                    </p>
                                )}
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
                                                    {riskTranslations[(service.risk || '').toUpperCase()] ||
                                                        service.risk}
                                                    )
                                                </div>
                                                <div className="flex gap-2">
                                                    <button onClick={() => handleEditService(service)} className="text-blue-600" title="Editar serviço">
                                                        <Pencil className="w-5 h-5" />
                                                    </button>
                                                    <button onClick={() => handleDeleteService(service.id)} className="text-red-600" title="Deletar serviço">
                                                        <Trash2 className="w-5 h-5" />
                                                    </button>
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
                            <h2 className="text-xl font-bold">{editingService ? "Editar Serviço" : "Novo Serviço"}</h2>
                            <input
                                className="w-full p-2 border rounded"
                                placeholder="Título do Serviço"
                                value={editingService ? editingServiceTitle : newServiceTitle}
                                onChange={(e) => editingService ? setEditingServiceTitle(e.target.value) : setNewServiceTitle(e.target.value)}
                                disabled={isCreatingService}
                            />
                            <select
                                className="w-full p-2 border rounded"
                                value={editingService ? editingServiceRisk : newServiceRisk}
                                onChange={(e) => editingService ? setEditingServiceRisk(e.target.value) : setNewServiceRisk(e.target.value)}
                                disabled={isCreatingService}
                            >
                                {Object.entries(riskTranslations).map(([key, label]) => (
                                    <option key={key} value={key}>
                                        {label}
                                    </option>
                                ))}
                            </select>
                            <div className="flex gap-2">
                                {editingService ? (
                                    <>
                                        <Button onClick={handleUpdateService} disabled={isCreatingService}>
                                            {isCreatingService ? "Salvando..." : "Salvar Alterações"}
                                        </Button>
                                        <Button onClick={handleCancelEditService} className="bg-gray-300 text-black" disabled={isCreatingService}>
                                            Cancelar
                                        </Button>
                                    </>
                                ) : (
                                    <>
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
                                    </>
                                )}
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
                                                    {riskTranslations[(activity.risk || '').toUpperCase()] ||
                                                        activity.risk}
                                                    )
                                                </div>
                                                <div className="flex gap-2">
                                                    <button onClick={() => handleEditActivity(activity)} className="text-blue-600" title="Editar atividade">
                                                        <Pencil className="w-5 h-5" />
                                                    </button>
                                                    <button onClick={() => handleDeleteActivity(activity.id)} className="text-red-600" title="Deletar atividade">
                                                        <Trash2 className="w-5 h-5" />
                                                    </button>
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
                            <h2 className="text-xl font-bold">{editingActivity ? "Editar Atividade" : "Nova Atividade"}</h2>
                            <input
                                className="w-full p-2 border rounded"
                                placeholder="Título da Atividade"
                                value={editingActivity ? editingActivityTitle : newActivityTitle}
                                onChange={(e) => editingActivity ? setEditingActivityTitle(e.target.value) : setNewActivityTitle(e.target.value)}
                                disabled={isCreatingActivity}
                            />
                            <select
                                className="w-full p-2 border rounded"
                                value={editingActivity ? editingActivityRisk : newActivityRisk}
                                onChange={(e) => editingActivity ? setEditingActivityRisk(e.target.value) : setNewActivityRisk(e.target.value)}
                                disabled={isCreatingActivity}
                            >
                                {Object.entries(riskTranslations).map(([key, label]) => (
                                    <option key={key} value={key}>
                                        {label}
                                    </option>
                                ))}
                            </select>
                            <div className="flex gap-2">
                                {editingActivity ? (
                                    <>
                                        <Button
                                            onClick={handleUpdateActivity}
                                            disabled={isCreatingActivity}
                                        >
                                            {isCreatingActivity ? "Salvando..." : "Salvar Alterações"}
                                        </Button>
                                        <Button
                                            onClick={handleCancelEditActivity}
                                            className="bg-gray-300 text-black"
                                            disabled={isCreatingActivity}
                                        >
                                            Cancelar
                                        </Button>
                                    </>
                                ) : (
                                    <>
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
                                    </>
                                )}
                            </div>
                        </div>
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
                                    {filteredProfiles.length > 0 ? (
                                        filteredProfiles.map((profile: any) => (
                                            <li
                                                key={profile.id}
                                                className="p-3 border rounded-md flex justify-between items-center"
                                            >
                                                <div>
                                                    <strong>{profile.name}</strong>
                                                </div>
                                                <button
                                                    onClick={() => handleAttemptDelete(profile.id)}
                                                    className="p-1 rounded-full hover:bg-red-100"
                                                    title="Excluir perfil"
                                                >
                                                    <Trash2 className="w-5 h-5 text-red-600" />
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
                            <h2 className="text-xl font-bold">Criar novo perfil</h2>
                            <div className="flex flex-col gap-4">
                                <input className="border border-gray-300 rounded px-3 py-2" placeholder="Nome do perfil" value={name} onChange={(e) => setName(e.target.value)} />
                                <input className="border border-gray-300 rounded px-3 py-2" placeholder="Descrição" value={newProfileDescription} onChange={(e) => setNewProfileDescription(e.target.value)} />
                                <div className="flex flex-col gap-2">
                                    <p className="font-medium">Tipo do perfil</p>
                                    <div className="flex gap-6 flex-wrap">
                                        <label className="flex items-center gap-2">
                                            <input type="radio" name="profileType" checked={admin} onChange={() => setAdmin(true)} /> Admin
                                        </label>
                                        <label className="flex items-center gap-2">
                                            <input type="radio" name="profileType" checked={!admin} onChange={() => setAdmin(false)} /> Outro
                                        </label>
                                    </div>
                                </div>
                                {!admin && (
                                    <div className="flex flex-col gap-2 mt-4">
                                        <p className="font-medium">Permissões</p>
                                        <div className="grid grid-cols-1 gap-2">
                                            <p className="font-medium">Dashboard</p>
                                            <label>
                                                <input type="checkbox" checked={permissions.dashboard.general} onChange={() => handlePermissionChange("dashboard.general")} /> Geral
                                            </label>
                                            <label>
                                                <input type="checkbox" checked={permissions.dashboard.provider} onChange={() => handlePermissionChange("dashboard.provider")} /> Fornecedores
                                            </label>
                                            <label>
                                                <input type="checkbox" checked={permissions.dashboard.document} onChange={() => handlePermissionChange("dashboard.document")} /> Documentos
                                            </label>
                                            <label>
                                                <input type="checkbox" checked={permissions.dashboard.documentDetail} onChange={() => handlePermissionChange("dashboard.documentDetail")} /> Detalhes de Documentos
                                            </label>
                                            <hr className="my-2" />
                                            <p className="font-medium">Documentos</p>
                                            <div className="ml-4 mt-2 border-l-2 pl-4">
                                                <p className="font-medium">Visualizar</p>
                                                <div className="grid grid-cols-1 gap-2">
                                                    <label>
                                                        <input type="checkbox" checked={permissions.document.view.laboral} onChange={() => handlePermissionChange("document.laboral", "view")} /> Trabalhista
                                                    </label>
                                                    <label>
                                                        <input type="checkbox" checked={permissions.document.view.workplaceSafety} onChange={() => handlePermissionChange("document.workplaceSafety", "view")} /> Segurança do Trabalho
                                                    </label>
                                                    <label>
                                                        <input type="checkbox" checked={permissions.document.view.registrationAndCertificates} onChange={() => handlePermissionChange("document.registrationAndCertificates", "view")} /> Cadastro e Certidões
                                                    </label>
                                                    <label>
                                                        <input type="checkbox" checked={permissions.document.view.general} onChange={() => handlePermissionChange("document.general", "view")} /> Geral
                                                    </label>
                                                    <label>
                                                        <input type="checkbox" checked={permissions.document.view.health} onChange={() => handlePermissionChange("document.health", "view")} /> Saúde
                                                    </label>
                                                    <label>
                                                        <input type="checkbox" checked={permissions.document.view.environment} onChange={() => handlePermissionChange("document.environment", "view")} /> Meio Ambiente
                                                    </label>
                                                </div>
                                            </div>
                                            <div className="ml-4 mt-2 border-l-2 pl-4">
                                                <p className="font-medium">Upload</p>
                                                <div className="grid grid-cols-1 gap-2">
                                                    <label>
                                                        <input type="checkbox" checked={permissions.document.upload.laboral} onChange={() => handlePermissionChange("document.laboral", "upload")} /> Trabalhista
                                                    </label>
                                                    <label>
                                                        <input type="checkbox" checked={permissions.document.upload.workplaceSafety} onChange={() => handlePermissionChange("document.workplaceSafety", "upload")} /> Segurança do Trabalho
                                                    </label>
                                                    <label>
                                                        <input type="checkbox" checked={permissions.document.upload.registrationAndCertificates} onChange={() => handlePermissionChange("document.registrationAndCertificates", "upload")} /> Cadastro e Certidões
                                                    </label>
                                                    <label>
                                                        <input type="checkbox" checked={permissions.document.upload.general} onChange={() => handlePermissionChange("document.general", "upload")} /> Geral
                                                    </label>
                                                    <label>
                                                        <input type="checkbox" checked={permissions.document.upload.health} onChange={() => handlePermissionChange("document.health", "upload")} /> Saúde
                                                    </label>
                                                    <label>
                                                        <input type="checkbox" checked={permissions.document.upload.environment} onChange={() => handlePermissionChange("document.environment", "upload")} /> Meio Ambiente
                                                    </label>
                                                </div>
                                            </div>
                                            <div className="ml-4 mt-2 border-l-2 pl-4">
                                                <p className="font-medium">Isentar</p>
                                                <div className="grid grid-cols-1 gap-2">
                                                    <label>
                                                        <input type="checkbox" checked={permissions.document.exempt.laboral} onChange={() => handlePermissionChange("document.laboral", "exempt")} /> Trabalhista
                                                    </label>
                                                    <label>
                                                        <input type="checkbox" checked={permissions.document.exempt.workplaceSafety} onChange={() => handlePermissionChange("document.workplaceSafety", "exempt")} /> Segurança do Trabalho
                                                    </label>
                                                    <label>
                                                        <input type="checkbox" checked={permissions.document.exempt.registrationAndCertificates} onChange={() => handlePermissionChange("document.registrationAndCertificates", "exempt")} /> Cadastro e Certidões
                                                    </label>
                                                    <label>
                                                        <input type="checkbox" checked={permissions.document.exempt.general} onChange={() => handlePermissionChange("document.general", "exempt")} /> Geral
                                                    </label>
                                                    <label>
                                                        <input type="checkbox" checked={permissions.document.exempt.health} onChange={() => handlePermissionChange("document.health", "exempt")} /> Saúde
                                                    </label>
                                                    <label>
                                                        <input type="checkbox" checked={permissions.document.exempt.environment} onChange={() => handlePermissionChange("document.environment", "exempt")} /> Meio Ambiente
                                                    </label>
                                                </div>
                                            </div>
                                            <hr className="my-2" />
                                            <p className="font-medium">Contrato</p>
                                            <label>
                                                <input type="checkbox" checked={permissions.contract.finish} onChange={() => handlePermissionChange("contract.finish")} /> Finalizar
                                            </label>
                                            <label>
                                                <input type="checkbox" checked={permissions.contract.suspend} onChange={() => handlePermissionChange("contract.suspend")} /> Suspender
                                            </label>
                                            <label>
                                                <input type="checkbox" checked={permissions.contract.create} onChange={() => handlePermissionChange("contract.create")} /> Criar
                                            </label>
                                            <hr className="my-2" />
                                            <p className="font-medium">Outros</p>
                                            <label>
                                                <input type="checkbox" checked={permissions.reception} onChange={() => handlePermissionChange("reception")} /> Portaria
                                            </label>
                                        </div>
                                    </div>
                                )}
                                <Button
                                    onClick={handleCreateProfile}
                                    className="bg-realizaBlue text-white px-4 py-2 rounded w-fit disabled:bg-realizaBlue/70 disabled:cursor-not-allowed"
                                    disabled={isCreatingProfile}
                                >
                                    {isCreatingProfile ? 'Criando...' : 'Criar perfil'}
                                </Button>
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
                            <h3 className="text-lg font-semibold mt-6 mb-2 flex justify-between items-center">
                                <span>Documentos do Grupo</span>
                                {selectedGroup && !isLoadingDocsList && docsList.length > 0 && (
                                    <Button
                                        onClick={handleExportExcel}
                                        variant="outline"
                                        size="sm"
                                        className="flex items-center gap-2"
                                    >
                                        <FileDown className="w-4 h-4" />
                                        Exportar Excel
                                    </Button>
                                )}
                            </h3>
                            {!selectedGroup ? (
                                <p className="text-gray-500">
                                    Selecione um grupo para visualizar os documentos.
                                </p>
                            ) : isLoadingDocsList ? (
                                <p className="text-gray-500">Carregando documentos...</p>
                            ) : (
                                <>
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
                                                            ? `Validade: ${doc.expirationDateAmount
                                                            } ${expirationUnits.find(
                                                                (u) => u.value
                                                            )?.label
                                                            }`
                                                            : "Sem validade padrão"}
                                                        {doc.type && ` (${doc.type})`}
                                                    </p>
                                                </li>
                                            ))}
                                        </ul>
                                    ) : (
                                        <p className="text-gray-400">
                                            Nenhum documento encontrado para este grupo ou filtro.
                                        </p>
                                    )}
                                </>
                            )}
                        </div>
                        {isEditingMatrix ? (
                            <div className="w-[45%] border-l pl-6 space-y-6">
                                <h2 className="text-xl font-bold">Editar Documento</h2>
                                <div className="p-4 bg-gray-50 rounded-lg shadow-inner">
                                    <h3 className="text-lg font-semibold mb-3">
                                        Editando: {selectedMatrixEntry?.name}
                                    </h3>
                                    <div className="space-y-4">
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
                                                Tipo de Documento
                                            </label>
                                            <select
                                                id="editType"
                                                className="w-full p-2 border rounded-md"
                                                value={editType}
                                                onChange={(e) => setEditType(e.target.value)}
                                            >
                                                <option value="">Selecione o tipo</option>
                                                {documentTypes.map((type) => (
                                                    <option key={type.value} value={type.value}>
                                                        {type.label}
                                                    </option>
                                                ))}
                                            </select>
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
                                                    checked={editIsRequired}
                                                    onChange={(e) => setEditIsRequired(e.target.checked)}
                                                />
                                                Documento Obrigatório
                                            </label>
                                            <p className="text-xs text-gray-500 ml-6 -mt-1">
                                                Documento solicitado independentemente da gestão controlada(Trabalhista ou SSMA)
                                            </p>
                                            <label className="flex items-center gap-2 text-sm font-medium">
                                                <input
                                                    type="checkbox"
                                                    checked={editDoesBlock}
                                                    onChange={(e) => setEditDoesBlock(e.target.checked)}
                                                />
                                                Bloqueia pendência
                                            </label>
                                            <p className="text-xs text-gray-500 ml-6 -mt-1">
                                                Se marcado, o documento precisa estar valido para permitir a
                                                entrada de um colaborador.
                                            </p>
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
                                            <p className="text-xs text-gray-500 ml-6 -mt-1">
                                                Marque se o documento se espelha em outros contratos.
                                            </p>
                                        </div>
                                        <div className="flex gap-2 pt-2">
                                            <Button onClick={saveMatrixEntry} className="w-full">
                                                Salvar Alterações
                                            </Button>
                                            <Button
                                                onClick={() => {
                                                    setSelectedMatrixEntry(null);
                                                    setIsEditingMatrix(false);
                                                }}
                                                className="w-full bg-gray-300 text-black hover:bg-gray-400"
                                            >
                                                Cancelar
                                            </Button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        ) : (
                            <div className="w-[45%] border-l pl-6 space-y-6">
                                <h2 className="text-xl font-bold">Criar novos documentos</h2>
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
                                                Tipo de Documento <span className="text-red-500">*</span>
                                            </label>
                                            <select
                                                id="newDocType"
                                                className="w-full p-2 border rounded-md"
                                                value={newDocType}
                                                onChange={(e) => setNewDocType(e.target.value)}
                                                disabled={!selectedGroup || isCreatingDocument}
                                            >
                                                <option value="">Selecione o tipo</option>
                                                {documentTypes.map((type) => (
                                                    <option key={type.value} value={type.value}>
                                                        {type.label}
                                                    </option>
                                                ))}
                                            </select>
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
                                                    <span className="block p-2 text-gray-700">Meses</span>
                                                </div>
                                            </div>
                                        </div>
                                        <div className="space-y-2">
                                            <label className="flex items-center gap-2 text-sm font-medium">
                                                <input
                                                    type="checkbox"
                                                    checked={newDocIsRequired}
                                                    onChange={(e) => setNewDocIsRequired(e.target.checked)}
                                                    disabled={!selectedGroup || isCreatingDocument}
                                                />
                                                Documento Obrigatório
                                            </label>
                                            <p className="text-xs text-gray-500 ml-6 -mt-1">
                                                Este documento não está vinculado a nenhum tipo de contrato.
                                            </p>
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
                        )}
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
                                {selectedProfileDetails.permissions && (
                                    <>
                                        <h4 className="font-semibold mt-2">Permissões:</h4>
                                        <ul className="list-disc list-inside space-y-1">
                                            <p className="font-medium">Dashboard:</p>
                                            <li>Geral: {selectedProfileDetails.permissions.dashboard.general ? "Sim" : "Não"}</li>
                                            <li>Fornecedores: {selectedProfileDetails.permissions.dashboard.provider ? "Sim" : "Não"}</li>
                                            <li>Documentos: {selectedProfileDetails.permissions.dashboard.document ? "Sim" : "Não"}</li>
                                            <li>Detalhes de Documentos: {selectedProfileDetails.permissions.dashboard.documentDetail ? "Sim" : "Não"}</li>
                                        </ul>
                                        <ul className="list-disc list-inside space-y-1 mt-2">
                                            <p className="font-medium">Documentos (Visualizar):</p>
                                            <li>Trabalhista: {selectedProfileDetails.permissions.document.view.laboral ? "Sim" : "Não"}</li>
                                            <li>Segurança do Trabalho: {selectedProfileDetails.permissions.document.view.workplaceSafety ? "Sim" : "Não"}</li>
                                            <li>Cadastro e Certidões: {selectedProfileDetails.permissions.document.view.registrationAndCertificates ? "Sim" : "Não"}</li>
                                            <li>Geral: {selectedProfileDetails.permissions.document.view.general ? "Sim" : "Não"}</li>
                                            <li>Saúde: {selectedProfileDetails.permissions.document.view.health ? "Sim" : "Não"}</li>
                                            <li>Meio Ambiente: {selectedProfileDetails.permissions.document.view.environment ? "Sim" : "Não"}</li>
                                        </ul>
                                        <ul className="list-disc list-inside space-y-1 mt-2">
                                            <p className="font-medium">Documentos (Upload):</p>
                                            <li>Trabalhista: {selectedProfileDetails.permissions.document.upload.laboral ? "Sim" : "Não"}</li>
                                            <li>Segurança do Trabalho: {selectedProfileDetails.permissions.document.upload.workplaceSafety ? "Sim" : "Não"}</li>
                                            <li>Cadastro e Certidões: {selectedProfileDetails.permissions.document.upload.registrationAndCertificates ? "Sim" : "Não"}</li>
                                            <li>Geral: {selectedProfileDetails.permissions.document.upload.general ? "Sim" : "Não"}</li>
                                            <li>Saúde: {selectedProfileDetails.permissions.document.upload.health ? "Sim" : "Não"}</li>
                                            <li>Meio Ambiente: {selectedProfileDetails.permissions.document.upload.environment ? "Sim" : "Não"}</li>
                                        </ul>
                                        <ul className="list-disc list-inside space-y-1 mt-2">
                                            <p className="font-medium">Documentos (Isentar):</p>
                                            <li>Trabalhista: {selectedProfileDetails.permissions.document.exempt.laboral ? "Sim" : "Não"}</li>
                                            <li>Segurança do Trabalho: {selectedProfileDetails.permissions.document.exempt.workplaceSafety ? "Sim" : "Não"}</li>
                                            <li>Cadastro e Certidões: {selectedProfileDetails.permissions.document.exempt.registrationAndCertificates ? "Sim" : "Não"}</li>
                                            <li>Geral: {selectedProfileDetails.permissions.document.exempt.general ? "Sim" : "Não"}</li>
                                            <li>Saúde: {selectedProfileDetails.permissions.document.exempt.health ? "Sim" : "Não"}</li>
                                            <li>Meio Ambiente: {selectedProfileDetails.permissions.document.exempt.environment ? "Sim" : "Não"}</li>
                                        </ul>
                                        <ul className="list-disc list-inside space-y-1 mt-2">
                                            <p className="font-medium">Contrato:</p>
                                            <li>Finalizar: {selectedProfileDetails.permissions.contract.finish ? "Sim" : "Não"}</li>
                                            <li>Suspender: {selectedProfileDetails.permissions.contract.suspend ? "Sim" : "Não"}</li>
                                            <li>Criar: {selectedProfileDetails.permissions.contract.create ? "Sim" : "Não"}</li>
                                        </ul>
                                        <ul className="list-disc list-inside space-y-1 mt-2">
                                            <p className="font-medium">Outros:</p>
                                            <li>Portaria: {selectedProfileDetails.permissions.reception ? "Sim" : "Não"}</li>
                                        </ul>
                                    </>
                                )}
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
                {isDeleteModalOpen && selectedProfileDetails && (
                    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                        <div className="bg-white p-6 rounded shadow-lg w-full max-w-2xl">
                            <h3 className="text-xl font-semibold mb-2 text-red-600">Excluir Perfil e Reatribuir Usuários</h3>
                            <p className="mb-4">Você está prestes a excluir o perfil <strong>"{selectedProfileDetails.name}"</strong>.</p>
                            {isLoadingProfilesRepo && !associatedUsers.length ? (<p>Verificando usuários...</p>) : associatedUsers.length > 0 ? (
                                <div className="mb-4">
                                    <p className="font-semibold text-orange-600">Atenção! Para prosseguir, reatribua cada usuário a um novo perfil.</p>
                                    <ul className="text-sm space-y-3 bg-gray-50 p-4 rounded max-h-60 overflow-y-auto my-4">
                                        {associatedUsers.map((user) => (
                                            <li key={user.id} className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2">
                                                <span className="font-medium text-gray-800">{user.fullName} <span className="text-gray-500 font-normal">({user.email})</span></span>
                                                <select value={individualAssignments[user.id] || ''} onChange={(e) => handleIndividualAssignmentChange(user.id, e.target.value)} className="p-2 border border-gray-300 rounded w-full sm:w-64" aria-label={`Novo perfil para ${user.fullName}`}>
                                                    <option value="" disabled>Selecione um novo perfil...</option>
                                                    {profiles.filter(p => p.id !== selectedProfileDetails.id).map(p => (<option key={p.id} value={p.id}>{p.name}</option>))}
                                                </select>
                                            </li>
                                        ))}
                                    </ul>
                                </div>
                            ) : (
                                <p className="mb-4 text-green-600 bg-green-50 p-3 rounded">Nenhum usuário está vinculado a este perfil.</p>
                            )}
                            <div className="flex justify-end gap-4 mt-6">
                                <button onClick={() => setIsDeleteModalOpen(false)} className="bg-gray-300 text-gray-800 px-4 py-2 rounded hover:bg-gray-400" disabled={isLoadingProfilesRepo}>Cancelar</button>
                                {associatedUsers.length > 0 ? (
                                    <button
                                        onClick={handleOpenFinalConfirm}
                                        className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 disabled:bg-blue-300 disabled:cursor-not-allowed"
                                        disabled={isLoadingProfilesRepo || Object.keys(individualAssignments).length !== associatedUsers.length}
                                    >
                                        Reatribuir e Excluir
                                    </button>
                                ) : (
                                    <button onClick={handleConfirmDelete} className="bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700" disabled={isLoadingProfilesRepo}>
                                        {isLoadingProfilesRepo ? "Excluindo..." : "Confirmar Exclusão"}
                                    </button>
                                )}
                            </div>
                        </div>
                    </div>
                )}
                {isFinalConfirmModalOpen && selectedProfileDetails && (
                    <div className="fixed inset-0 bg-black bg-opacity-60 flex items-center justify-center z-50 p-4">
                        <div className="bg-white p-6 rounded shadow-lg w-full max-w-md">
                            <h3 className="text-xl font-semibold mb-4">Confirmação Final</h3>
                            <p className="mb-1">Você está prestes a reatribuir <strong>{associatedUsers.length} usuário(s)</strong> para os perfis selecionados.</p>
                            <p className="mb-6">Após a reatribuição, o perfil <strong>"{selectedProfileDetails.name}"</strong> será <strong>excluído permanentemente</strong>.</p>
                            <p className="font-bold">Esta ação não pode ser desfeita. Deseja continuar?</p>
                            <div className="flex justify-end gap-4 mt-8">
                                <button
                                    onClick={() => setIsFinalConfirmModalOpen(false)}
                                    className="bg-gray-300 text-gray-800 px-4 py-2 rounded hover:bg-gray-400"
                                    disabled={isLoadingProfilesRepo}
                                >
                                    Cancelar
                                </button>
                                <button
                                    onClick={handleReassignAndDelete}
                                    className="bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700"
                                    disabled={isLoadingProfilesRepo}
                                >
                                    {isLoadingProfilesRepo ? "Processando..." : "Sim, excluir perfil"}
                                </button>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}