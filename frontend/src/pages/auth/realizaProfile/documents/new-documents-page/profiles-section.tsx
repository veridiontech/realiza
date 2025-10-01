import { useEffect, useState } from "react";
import axios from "axios";
import { toast } from "sonner";
import { ip } from "@/utils/ip";
import { useClient } from "@/context/Client-Provider";
import { Trash2, Pencil } from "lucide-react";

type User = {
  id: string;
  fullName: string;
  email: string;
};

type Profile = {
  id: string;
  profileName: string;
};

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
  permissions?: Permissions;
  clientId: string;
};

const endpoints = {
  listByClient: (clientId: string) => `${ip}/profile/by-name/${clientId}`,
  getOne: (id: string) => `${ip}/profile/${id}`,
  create: () => `${ip}/profile`,
  update: (id: string) => `${ip}/profile/${id}`, 
  remove: (id: string) => `${ip}/profile/${id}`,
  usersByProfile: (id: string) => `${ip}/user/find-by-profile/${id}`,
  changeUserProfile: (userId: string, newProfileId: string) =>
    `${ip}/user/${userId}/change-profile?profileId=${newProfileId}`,
};

export function ProfilesSection() {
  const { client } = useClient();
  const clientId = client?.idClient;

  const [profiles, setProfiles] = useState<Profile[]>([]);
  const [loading, setLoading] = useState(false);
  const [isCreating, setIsCreating] = useState(false);

  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [isFinalConfirmModalOpen, setIsFinalConfirmModalOpen] = useState(false);

  const [selectedProfileDetails, setSelectedProfileDetails] = useState<ProfileDetails | null>(null);
  const [associatedUsers, setAssociatedUsers] = useState<User[]>([]);
  const [individualAssignments, setIndividualAssignments] = useState<Record<string, string>>({});

  // FORM (create/edit)
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [admin, setAdmin] = useState(false);
  const [permissions, setPermissions] = useState<Permissions>({
    dashboard: { general: false, provider: false, document: false, documentDetail: false },
    document: {
      view: { laboral: false, workplaceSafety: false, registrationAndCertificates: false, general: false, health: false, environment: false },
      upload: { laboral: false, workplaceSafety: false, registrationAndCertificates: false, general: false, health: false, environment: false },
      exempt: { laboral: false, workplaceSafety: false, registrationAndCertificates: false, general: false, health: false, environment: false },
    },
    contract: { finish: false, suspend: false, create: false },
    reception: false,
  });

  // EDIÇÃO
  const [isEditing, setIsEditing] = useState(false);
  const [editingProfileId, setEditingProfileId] = useState<string | null>(null);

  const resetForm = () => {
    console.log("LOG: resetForm - Resetando o formulário.");
    setName("");
    setDescription("");
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
  };

  const fetchProfiles = async () => {
    console.log("LOG: fetchProfiles - Iniciando carregamento de perfis.");
    if (!clientId) {
      console.log("LOG: fetchProfiles - clientId não está definido. Abortando.");
      return;
    }
    setLoading(true);
    const token = localStorage.getItem("tokenClient");
    try {
      console.log(`LOG: fetchProfiles - Chamando API: ${endpoints.listByClient(clientId)}`);
      const { data } = await axios.get(endpoints.listByClient(clientId), {
        headers: { Authorization: `Bearer ${token}` },
      });
      const arr = Array.isArray(data) ? data : [];
      const sorted = arr.sort((a: Profile, b: Profile) => a.profileName.localeCompare(b.profileName));
      setProfiles(sorted);
      console.log(`LOG: fetchProfiles - Perfis carregados com sucesso: ${sorted.length} perfis.`);
    } catch (err) {
      console.error("LOG: fetchProfiles - Erro ao carregar perfis:", err);
      toast.error("Erro ao carregar a lista de perfis.");
    } finally {
      setLoading(false);
      console.log("LOG: fetchProfiles - Finalizado. Loading = false.");
    }
  };

  const handleAttemptDelete = async (profileId: string) => {
    console.log(`LOG: handleAttemptDelete - Tentativa de exclusão do Perfil ID: ${profileId}`);
    setIndividualAssignments({});
    setLoading(true);
    const token = localStorage.getItem("tokenClient");
    try {
      console.log(`LOG: handleAttemptDelete - Buscando detalhes do perfil: ${endpoints.getOne(profileId)}`);
      const details = await axios.get(endpoints.getOne(profileId), {
        headers: { Authorization: `Bearer ${token}` },
      });
      setSelectedProfileDetails(details.data);
      console.log("LOG: handleAttemptDelete - Detalhes do perfil carregados.");

      console.log(`LOG: handleAttemptDelete - Buscando usuários associados: ${endpoints.usersByProfile(profileId)}`);
      const users = await axios.get(endpoints.usersByProfile(profileId), {
        headers: { Authorization: `Bearer ${token}` },
      });
      setAssociatedUsers(users.data);
      console.log(`LOG: handleAttemptDelete - ${users.data.length} usuários encontrados.`);

      setIsDeleteModalOpen(true);
      console.log("LOG: handleAttemptDelete - Modal de deleção aberto.");
    } catch (err) {
      console.error("LOG: handleAttemptDelete - Erro na verificação de usuários:", err);
      toast.error("Não foi possível verificar os usuários vinculados.");
    } finally {
      setLoading(false);
      console.log("LOG: handleAttemptDelete - Finalizado. Loading = false.");
    }
  };

  const handleConfirmDelete = async () => {
    if (!selectedProfileDetails) return;
    console.log(`LOG: handleConfirmDelete - Confirmação de exclusão (sem usuários): Perfil ID: ${selectedProfileDetails.id}`);
    setLoading(true);
    const token = localStorage.getItem("tokenClient");
    try {
      console.log(`LOG: handleConfirmDelete - Chamando API DELETE: ${endpoints.remove(selectedProfileDetails.id)}`);
      await axios.delete(endpoints.remove(selectedProfileDetails.id), {
        headers: { Authorization: `Bearer ${token}` },
      });
      toast.success("Perfil excluído com sucesso!");
      console.log("LOG: handleConfirmDelete - Perfil excluído com sucesso. Fechando modal e recarregando.");
      setIsDeleteModalOpen(false);
      setSelectedProfileDetails(null);
      fetchProfiles();
    } catch (err) {
      console.error("LOG: handleConfirmDelete - Erro ao excluir perfil:", err);
      toast.error("Erro ao excluir o perfil.");
    } finally {
      setLoading(false);
      console.log("LOG: handleConfirmDelete - Finalizado. Loading = false.");
    }
  };

  const handleIndividualAssignmentChange = (userId: string, newProfileId: string) => {
    console.log(`LOG: handleIndividualAssignmentChange - Usuário ${userId} reatribuído para Perfil ${newProfileId}`);
    setIndividualAssignments((prev) => ({ ...prev, [userId]: newProfileId }));
  };

  const handleOpenFinalConfirm = () => {
    console.log("LOG: handleOpenFinalConfirm - Tentativa de abrir a confirmação final.");
    if (Object.keys(individualAssignments).length !== associatedUsers.length) {
      console.log("LOG: handleOpenFinalConfirm - Falha: Nem todos os usuários foram reatribuídos.");
      toast.error("Por favor, atribua um novo perfil para cada usuário.");
      return;
    }
    console.log("LOG: handleOpenFinalConfirm - Confirmação final aberta.");
    setIsFinalConfirmModalOpen(true);
  };

  const handleReassignAndDelete = async () => {
    if (!selectedProfileDetails) return;
    console.log(`LOG: handleReassignAndDelete - Iniciando reatribuição de ${associatedUsers.length} usuários e exclusão do perfil ID: ${selectedProfileDetails.id}`);
    setLoading(true);
    const token = localStorage.getItem("tokenClient");
    try {
      const reassigns = Object.entries(individualAssignments).map(([userId, profileId]) => {
        console.log(`LOG: Reatribuição - Usuário ${userId} -> Perfil ${profileId}`);
        return axios.post(endpoints.changeUserProfile(userId, profileId), null, { headers: { Authorization: `Bearer ${token}` } });
      });
      await Promise.all(reassigns);
      toast.success(`${associatedUsers.length} usuário(s) foram reatribuídos.`);

      console.log(`LOG: handleReassignAndDelete - Usuários reatribuídos. Chamando API DELETE para o perfil: ${endpoints.remove(selectedProfileDetails.id)}`);
      await axios.delete(endpoints.remove(selectedProfileDetails.id), {
        headers: { Authorization: `Bearer ${token}` },
      });
      toast.success(`O perfil "${selectedProfileDetails.name}" foi excluído.`);
      console.log("LOG: handleReassignAndDelete - Perfil excluído com sucesso. Fechando modais e recarregando.");

      setIsFinalConfirmModalOpen(false);
      setIsDeleteModalOpen(false);
      setSelectedProfileDetails(null);
      setIndividualAssignments({});
      fetchProfiles();
    } catch (err) {
      console.error("LOG: handleReassignAndDelete - Erro durante a reatribuição ou exclusão:", err);
      toast.error("Ocorreu um erro durante a operação.");
    } finally {
      setLoading(false);
      console.log("LOG: handleReassignAndDelete - Finalizado. Loading = false.");
    }
  };

  const handleCreateProfile = async () => {
    console.log("LOG: handleCreateProfile - Iniciando criação de perfil.");
    if (!clientId || !name.trim()) {
      console.log("LOG: handleCreateProfile - Validação falhou: Nome ou clientId ausente.");
      toast.warning("O nome do perfil é obrigatório.");
      return;
    }
    setIsCreating(true);

    let profilePermissions = permissions;
    if (admin) {
      console.log("LOG: handleCreateProfile - Perfil marcado como Admin: todas as permissões ativadas.");
      profilePermissions = {
        dashboard: { general: true, provider: true, document: true, documentDetail: true },
        document: {
          view: { laboral: true, workplaceSafety: true, registrationAndCertificates: true, general: true, health: true, environment: true },
          upload: { laboral: true, workplaceSafety: true, registrationAndCertificates: true, general: true, health: true, environment: true },
          exempt: { laboral: true, workplaceSafety: true, registrationAndCertificates: true, general: true, health: true, environment: true },
        },
        contract: { finish: true, suspend: true, create: true },
        reception: true,
      };
    }

    const body = {
      name,
      description,
      admin,
      ...profilePermissions,
      clientId,
      branchIds: [],
      contractIds: [],
    };

    console.log("LOG: handleCreateProfile - Payload da criação:", body);

    const token = localStorage.getItem("tokenClient");
    try {
      console.log(`LOG: handleCreateProfile - Chamando API POST: ${endpoints.create()}`);
      await axios.post(endpoints.create(), body, { headers: { Authorization: `Bearer ${token}` } });
      toast.success("Perfil criado com sucesso!");
      console.log("LOG: handleCreateProfile - Sucesso. Resetando formulário e recarregando perfis.");
      resetForm();
      fetchProfiles();
    } catch (err) {
      console.error("LOG: handleCreateProfile - Erro na criação do perfil:", err);
      toast.error("Erro ao criar o perfil.");
    } finally {
      setIsCreating(false);
    }
  };

  // ABRIR EDIÇÃO (GET /profile/{id})
  const handleOpenEdit = async (profileId: string) => {
    console.log(`LOG: handleOpenEdit - Iniciando abertura de edição para Perfil ID: ${profileId}`);
    const token = localStorage.getItem("tokenClient");
    try {
      setLoading(true);
      console.log(`LOG: handleOpenEdit - Chamando API GET: ${endpoints.getOne(profileId)}`);
      const { data } = await axios.get(endpoints.getOne(profileId), {
        headers: { Authorization: `Bearer ${token}` },
      });
      console.log("LOG: handleOpenEdit - Dados recebidos para edição:", data);

      setEditingProfileId(profileId);
      setIsEditing(true);
      setName(data.name ?? "");
      setDescription(data.description ?? "");
      
      const isAdmin = Boolean(data.admin);
      setAdmin(isAdmin);
      
      // ################### LÓGICA DE CARREGAMENTO DE PERMISSÕES CORRIGIDA ###################
      console.log("LOG: handleOpenEdit - Permissões do perfil na API (data.permissions):", data.permissions);
      
      if (!isAdmin) { 
          // 1. Tenta carregar de data.permissions (estrutura ideal)
          const permissionsFromAPI = data.permissions || {
              // 2. Ou tenta carregar as chaves de permissão diretamente de 'data',
              // que parece ser como a API está retornando para perfis não-admin.
              dashboard: data.dashboard,
              document: data.document,
              contract: data.contract,
              reception: data.reception
          };

          // 3. Verifica se as chaves principais de permissão existem (pelo menos 'dashboard' como teste)
          // Isso evita setar permissões se o objeto 'data' for apenas { id, name, admin: false }
          if (permissionsFromAPI.dashboard || permissionsFromAPI.document) {
              setPermissions(permissionsFromAPI as Permissions); 
              console.log("LOG: handleOpenEdit - Permissões carregadas com sucesso no estado 'permissions'.");
          } else {
              console.log("LOG: handleOpenEdit - FALHA: Nenhuma estrutura de permissão foi encontrada no objeto retornado pela API. Mantendo estado default.");
          }
      } else {
          console.log("LOG: handleOpenEdit - Perfil Admin, a UI de permissões não será usada.");
      }
      // ################### FIM DA LÓGICA DE CARREGAMENTO DE PERMISSÕES ###################

      toast.message("Editando perfil: " + (data.name ?? ""));
      console.log("LOG: handleOpenEdit - Edição iniciada com sucesso.");
    } catch (err) {
      console.error("LOG: handleOpenEdit - Erro ao carregar perfil para edição:", err);
      toast.error("Não foi possível carregar o perfil para edição.");
    } finally {
      setLoading(false);
      console.log("LOG: handleOpenEdit - Finalizado. Loading = false.");
    }
  };

  // SALVAR EDIÇÃO (PUT /profile/{id})
  const handleUpdateProfile = async () => {
    console.log(`LOG: handleUpdateProfile - Iniciando atualização para Perfil ID: ${editingProfileId}`);
    if (!editingProfileId) {
        console.log("LOG: handleUpdateProfile - Falha: editingProfileId ausente.");
        return;
    }
    if (!name.trim()) {
      console.log("LOG: handleUpdateProfile - Validação falhou: Nome ausente.");
      toast.warning("O nome do perfil é obrigatório.");
      return;
    }
    const token = localStorage.getItem("tokenClient");

    const payload = admin
      ? {
          name,
          description,
          admin: true,
          dashboard: { general: true, provider: true, document: true, documentDetail: true },
          document: {
            view: { laboral: true, workplaceSafety: true, registrationAndCertificates: true, general: true, health: true, environment: true },
            upload: { laboral: true, workplaceSafety: true, registrationAndCertificates: true, general: true, health: true, environment: true },
            exempt: { laboral: true, workplaceSafety: true, registrationAndCertificates: true, general: true, health: true, environment: true },
          },
          contract: { finish: true, suspend: true, create: true },
          reception: true,
        }
      : {
          name,
          description,
          admin: false,
          dashboard: permissions.dashboard,
          document: permissions.document,
          contract: permissions.contract,
          reception: permissions.reception,
        };
    
    console.log("LOG: handleUpdateProfile - Payload da atualização:", payload);

    try {
      setLoading(true);
      console.log(`LOG: handleUpdateProfile - Chamando API PUT: ${endpoints.update(editingProfileId)}`);
      await axios.put(endpoints.update(editingProfileId), payload, {
        headers: { Authorization: `Bearer ${token}` },
      });
      toast.success("Perfil atualizado com sucesso!");
      console.log("LOG: handleUpdateProfile - Sucesso. Fechando edição e recarregando perfis.");
      setIsEditing(false);
      setEditingProfileId(null);
      resetForm();
      fetchProfiles();
    } catch (err) {
      console.error("LOG: handleUpdateProfile - Erro ao atualizar o perfil:", err);
      toast.error("Erro ao atualizar o perfil.");
    } finally {
      setLoading(false);
      console.log("LOG: handleUpdateProfile - Finalizado. Loading = false.");
    }
  };

  const handleCancelEdit = () => {
    console.log("LOG: handleCancelEdit - Cancelamento de edição.");
    setIsEditing(false);
    setEditingProfileId(null);
    resetForm();
  };

  const handlePermissionChange = (path: string, type: "view" | "upload" | "exempt" | null = null) => {
    console.log(`LOG: handlePermissionChange - Alterando permissão: ${path}, Tipo: ${type || 'N/A'}`);
    setPermissions((prev) => {
      let newState = { ...prev };
      const [mainKey, subKey] = path.split(".");

      if (mainKey === "document" && subKey) {
        if (type) {
          newState.document = {
            ...newState.document,
            [type]: {
              ...newState.document[type],
              [subKey]: !newState.document[type][subKey as keyof typeof newState.document.view],
            },
          };
        } else {
          const documentKeys = ["view", "upload", "exempt"] as const;
          if (documentKeys.includes(subKey as any)) {
            (newState.document as any)[subKey] = !(newState.document as any)[subKey];
          }
        }
      } else {
        let currentLevel = newState as any;
        const keys = path.split(".");
        for (let i = 0; i < keys.length - 1; i++) {
          currentLevel[keys[i]] = { ...currentLevel[keys[i]] };
          currentLevel = currentLevel[keys[i]];
        }
        const lastKey = keys[keys.length - 1];
        currentLevel[lastKey] = !currentLevel[lastKey];
      }
      
      console.log("LOG: handlePermissionChange - Novo estado de permissões para:", path, (newState as any)[mainKey]);
      return newState;
    });
  };

  useEffect(() => {
    console.log(`LOG: useEffect - O componente montou. clientId: ${clientId}`);
    if (clientId) fetchProfiles();
  }, [clientId]);

  return (
    <div className="flex justify-center w-full mt-[-1.5rem]">
      <div className="flex flex-col md:flex-row w-full max-w-6xl gap-6 items-start">
        <div className="bg-white shadow-md rounded p-6 md:w-1/2">
          <h2 className="text-xl font-semibold mb-4">Perfis vinculados ao cliente</h2>
          {loading && !isDeleteModalOpen && <p className="text-gray-500">Carregando...</p>}
          {!loading && profiles.length === 0 && <p className="text-gray-500">Nenhum perfil encontrado para este cliente.</p>}
          {!loading && profiles.length > 0 && (
            <ul className="space-y-3 mb-6">
              {profiles.map((profile) => (
                <li
                  key={profile.id}
                  className="p-3 border border-gray-200 rounded bg-gray-50 hover:bg-gray-100 transition flex justify-between items-center"
                >
                  <span className="text-md text-gray-700">{profile.profileName}</span>
                  <div className="flex items-center gap-2">
                    <button
                      onClick={() => handleOpenEdit(profile.id)}
                      className="p-1 rounded-full hover:bg-blue-100"
                      title="Editar perfil"
                    >
                      <Pencil className="w-5 h-5 text-blue-600" />
                    </button>
                    <button
                      onClick={() => handleAttemptDelete(profile.id)}
                      className="p-1 rounded-full hover:bg-red-100"
                      title="Excluir perfil"
                    >
                      <Trash2 className="w-5 h-5 text-red-600" />
                    </button>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </div>

        <div className="bg-white shadow-md rounded p-6 md:w-1/2">
          <h3 className="text-lg font-medium mb-2">{isEditing ? "Editar perfil" : "Criar novo perfil"}</h3>
          <div className="flex flex-col gap-4">
            <input className="border border-gray-300 rounded px-3 py-2" placeholder="Nome do perfil" value={name} onChange={(e) => setName(e.target.value)} />
            <input className="border border-gray-300 rounded px-3 py-2" placeholder="Descrição" value={description} onChange={(e) => setDescription(e.target.value)} />
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
                  <label><input type="checkbox" checked={permissions.dashboard.general} onChange={() => handlePermissionChange("dashboard.general")} /> Geral</label>
                  <label><input type="checkbox" checked={permissions.dashboard.provider} onChange={() => handlePermissionChange("dashboard.provider")} /> Fornecedores</label>
                  <label><input type="checkbox" checked={permissions.dashboard.document} onChange={() => handlePermissionChange("dashboard.document")} /> Documentos</label>
                  <label><input type="checkbox" checked={permissions.dashboard.documentDetail} onChange={() => handlePermissionChange("dashboard.documentDetail")} /> Detalhes de Documentos</label>

                  <hr className="my-2" />
                  <p className="font-medium">Documentos</p>

                  <div className="ml-4 mt-2 border-l-2 pl-4">
                    <p className="font-medium">Visualizar</p>
                    <div className="grid grid-cols-1 gap-2">
                      <label><input type="checkbox" checked={permissions.document.view.laboral} onChange={() => handlePermissionChange("document.laboral", "view")} /> Trabalhista</label>
                      <label><input type="checkbox" checked={permissions.document.view.workplaceSafety} onChange={() => handlePermissionChange("document.workplaceSafety", "view")} /> Segurança do Trabalho</label>
                      <label><input type="checkbox" checked={permissions.document.view.registrationAndCertificates} onChange={() => handlePermissionChange("document.registrationAndCertificates", "view")} /> Cadastro e Certidões</label>
                      <label><input type="checkbox" checked={permissions.document.view.general} onChange={() => handlePermissionChange("document.general", "view")} /> Geral</label>
                      <label><input type="checkbox" checked={permissions.document.view.health} onChange={() => handlePermissionChange("document.health", "view")} /> Saúde</label>
                      <label><input type="checkbox" checked={permissions.document.view.environment} onChange={() => handlePermissionChange("document.environment", "view")} /> Meio Ambiente</label>
                    </div>
                  </div>

                  <div className="ml-4 mt-2 border-l-2 pl-4">
                    <p className="font-medium">Upload</p>
                    <div className="grid grid-cols-1 gap-2">
                      <label><input type="checkbox" checked={permissions.document.upload.laboral} onChange={() => handlePermissionChange("document.laboral", "upload")} /> Trabalhista</label>
                      <label><input type="checkbox" checked={permissions.document.upload.workplaceSafety} onChange={() => handlePermissionChange("document.workplaceSafety", "upload")} /> Segurança do Trabalho</label>
                      <label><input type="checkbox" checked={permissions.document.upload.registrationAndCertificates} onChange={() => handlePermissionChange("document.registrationAndCertificates", "upload")} /> Cadastro e Certidões</label>
                      <label><input type="checkbox" checked={permissions.document.upload.general} onChange={() => handlePermissionChange("document.general", "upload")} /> Geral</label>
                      <label><input type="checkbox" checked={permissions.document.upload.health} onChange={() => handlePermissionChange("document.health", "upload")} /> Saúde</label>
                      <label><input type="checkbox" checked={permissions.document.upload.environment} onChange={() => handlePermissionChange("document.environment", "upload")} /> Meio Ambiente</label>
                    </div>
                  </div>

                  <div className="ml-4 mt-2 border-l-2 pl-4">
                    <p className="font-medium">Isentar</p>
                    <div className="grid grid-cols-1 gap-2">
                      <label><input type="checkbox" checked={permissions.document.exempt.laboral} onChange={() => handlePermissionChange("document.laboral", "exempt")} /> Trabalhista</label>
                      <label><input type="checkbox" checked={permissions.document.exempt.workplaceSafety} onChange={() => handlePermissionChange("document.workplaceSafety", "exempt")} /> Segurança do Trabalho</label>
                      <label><input type="checkbox" checked={permissions.document.exempt.registrationAndCertificates} onChange={() => handlePermissionChange("document.registrationAndCertificates", "exempt")} /> Cadastro e Certidões</label>
                      <label><input type="checkbox" checked={permissions.document.exempt.general} onChange={() => handlePermissionChange("document.general", "exempt")} /> Geral</label>
                      <label><input type="checkbox" checked={permissions.document.exempt.health} onChange={() => handlePermissionChange("document.health", "exempt")} /> Saúde</label>
                      <label><input type="checkbox" checked={permissions.document.exempt.environment} onChange={() => handlePermissionChange("document.environment", "exempt")} /> Meio Ambiente</label>
                    </div>
                  </div>

                  <hr className="my-2" />
                  <p className="font-medium">Contrato</p>
                  <label><input type="checkbox" checked={permissions.contract.finish} onChange={() => handlePermissionChange("contract.finish")} /> Finalizar</label>
                  <label><input type="checkbox" checked={permissions.contract.suspend} onChange={() => handlePermissionChange("contract.suspend")} /> Suspender</label>
                  <label><input type="checkbox" checked={permissions.contract.create} onChange={() => handlePermissionChange("contract.create")} /> Criar</label>

                  <hr className="my-2" />
                  <p className="font-medium">Outros</p>
                  <label><input type="checkbox" checked={permissions.reception} onChange={() => handlePermissionChange("reception")}/> Portaria</label>
                </div>
              </div>
            )}

            <div className="flex gap-3">
              {isEditing ? (
                <>
                  <button
                    onClick={handleUpdateProfile}
                    className="bg-blue-600 text-white px-4 py-2 rounded disabled:bg-blue-300 disabled:cursor-not-allowed"
                    disabled={loading}
                  >
                    {loading ? "Salvando..." : "Salvar alterações"}
                  </button>
                  <button
                    onClick={handleCancelEdit}
                    className="bg-gray-200 text-gray-800 px-4 py-2 rounded hover:bg-gray-300"
                    disabled={loading}
                  >
                    Cancelar
                  </button>
                </>
              ) : (
                <button
                  onClick={handleCreateProfile}
                  className="bg-realizaBlue text-white px-4 py-2 rounded w-fit disabled:bg-realizaBlue/70 disabled:cursor-not-allowed"
                  disabled={isCreating}
                >
                  {isCreating ? "Criando..." : "Criar perfil"}
                </button>
              )}
            </div>
          </div>
        </div>
      </div>

      {isDeleteModalOpen && selectedProfileDetails && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded shadow-lg w-full max-w-2xl">
            <h3 className="text-xl font-semibold mb-2 text-red-600">Excluir Perfil e Reatribuir Usuários</h3>
            <p className="mb-4">Você está prestes a excluir o perfil <strong>"{selectedProfileDetails.name}"</strong>.</p>

            {loading && !associatedUsers.length ? (
              <p>Verificando usuários...</p>
            ) : associatedUsers.length > 0 ? (
              <div className="mb-4">
                <p className="font-semibold text-orange-600">Atenção! Para prosseguir, reatribua cada usuário a um novo perfil.</p>
                <ul className="text-sm space-y-3 bg-gray-50 p-4 rounded max-h-60 overflow-y-auto my-4">
                  {associatedUsers.map((user) => (
                    <li key={user.id} className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2">
                      <span className="font-medium text-gray-800">
                        {user.fullName} <span className="text-gray-500 font-normal">({user.email})</span>
                      </span>
                      <select
                        value={individualAssignments[user.id] || ""}
                        onChange={(e) => handleIndividualAssignmentChange(user.id, e.target.value)}
                        className="p-2 border border-gray-300 rounded w-full sm:w-64"
                        aria-label={`Novo perfil para ${user.fullName}`}
                      >
                        <option value="" disabled>Selecione um novo perfil...</option>
                        {profiles
                          .filter((p) => p.id !== selectedProfileDetails.id)
                          .map((p) => (
                            <option key={p.id} value={p.id}>{p.profileName}</option>
                          ))}
                      </select>
                    </li>
                  ))}
                </ul>
              </div>
            ) : (
              <p className="mb-4 text-green-600 bg-green-50 p-3 rounded">Nenhum usuário está vinculado a este perfil.</p>
            )}

            <div className="flex justify-end gap-4 mt-6">
              <button onClick={() => setIsDeleteModalOpen(false)} className="bg-gray-300 text-gray-800 px-4 py-2 rounded hover:bg-gray-400" disabled={loading}>
                Cancelar
              </button>
              {associatedUsers.length > 0 ? (
                <button
                  onClick={handleOpenFinalConfirm}
                  className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 disabled:bg-blue-300 disabled:cursor-not-allowed"
                  disabled={loading || Object.keys(individualAssignments).length !== associatedUsers.length}
                >
                  Reatribuir e Excluir
                </button>
              ) : (
                <button onClick={handleConfirmDelete} className="bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700" disabled={loading}>
                  {loading ? "Excluindo..." : "Confirmar Exclusão"}
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
              <button onClick={() => setIsFinalConfirmModalOpen(false)} className="bg-gray-300 text-gray-800 px-4 py-2 rounded hover:bg-gray-400" disabled={loading}>
                Cancelar
              </button>
              <button onClick={handleReassignAndDelete} className="bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700" disabled={loading}>
                {loading ? "Processando..." : "Sim, excluir perfil"}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}