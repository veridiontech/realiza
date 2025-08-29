import { useEffect, useState } from "react";
import axios from "axios";
import { toast } from "sonner";
import { ip } from "@/utils/ip";
import { useClient } from "@/context/Client-Provider";
import { Trash2 } from "lucide-react";

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
    providers: boolean;
    document: boolean;
    documentDetails: boolean;
  };
  document: {
    view: boolean;
    upload: boolean;
    exempt: boolean;
  };
  contract: {
    finish: boolean;
    suspend: boolean;
    create: boolean;
  };
  reception: boolean;
  types: {
    registrationAndCertificates: boolean;
    health: boolean;
    workplaceSafety: boolean;
    environment: boolean;
    laboral: boolean;
    general: boolean;
  };
};

type ProfileDetails = {
  id: string;
  name: string;
  description: string;
  admin: boolean;
  permissions: Permissions;
  clientId: string;
};

export function ProfilesSection() {
  const { client } = useClient();
  const clientId = client?.idClient;

  const [profiles, setProfiles] = useState<Profile[]>([]);
  const [loading, setLoading] = useState(false);
  
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [isFinalConfirmModalOpen, setIsFinalConfirmModalOpen] = useState(false);

  const [selectedProfileDetails, setSelectedProfileDetails] = useState<ProfileDetails | null>(null);
  const [associatedUsers, setAssociatedUsers] = useState<User[]>([]);
  const [individualAssignments, setIndividualAssignments] = useState<Record<string, string>>({});

  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [admin, setAdmin] = useState(false);
  const [permissions, setPermissions] = useState<Permissions>({
    dashboard: {
      general: false,
      providers: false,
      document: false,
      documentDetails: false,
    },
    document: {
      view: false,
      upload: false,
      exempt: false,
    },
    contract: {
      finish: false,
      suspend: false,
      create: false,
    },
    reception: false,
    types: {
      registrationAndCertificates: false,
      health: false,
      workplaceSafety: false,
      environment: false,
      laboral: false,
      general: false,
    },
  });

  const fetchProfiles = async () => {
    if (!clientId) return;
    setLoading(true);
    const tokenFromStorage = localStorage.getItem("tokenClient");
    try {
      const response = await axios.get(`${ip}/profile/by-name/${clientId}`, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });
      const data = Array.isArray(response.data) ? response.data : [];
      const sortedProfiles = data.sort((a, b) =>
        a.profileName.localeCompare(b.profileName)
      );
      setProfiles(sortedProfiles);
    } catch (err) {
      console.error("Erro ao buscar perfis:", err);
      toast.error("Erro ao carregar a lista de perfis.");
    } finally {
      setLoading(false);
    }
  };

  const handleAttemptDelete = async (profileId: string) => {
    setIndividualAssignments({});
    setLoading(true);
    const tokenFromStorage = localStorage.getItem("tokenClient");
    try {
      const detailsResponse = await axios.get(`${ip}/profile/${profileId}`, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });
      setSelectedProfileDetails(detailsResponse.data);

      const usersResponse = await axios.get(
        `${ip}/user/find-by-profile/${profileId}`,
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        }
      );
      setAssociatedUsers(usersResponse.data);
      
      setIsDeleteModalOpen(true);
    } catch (err) {
      console.error("Erro ao verificar usuários vinculados:", err);
      toast.error("Não foi possível verificar os usuários vinculados.");
    } finally {
      setLoading(false);
    }
  };

  const handleConfirmDelete = async () => {
    if (!selectedProfileDetails) return;
    setLoading(true);
    const tokenFromStorage = localStorage.getItem("tokenClient");
    try {
      await axios.delete(`${ip}/profile/${selectedProfileDetails.id}`, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });
      toast.success("Perfil excluído com sucesso!");
      setIsDeleteModalOpen(false);
      setSelectedProfileDetails(null);
      fetchProfiles();
    } catch (err) {
      console.error("Erro ao excluir o perfil:", err);
      toast.error("Erro ao excluir o perfil.");
    } finally {
      setLoading(false);
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
    setLoading(true);
    const token = localStorage.getItem("tokenClient");
    try {
      const reassignPromises = Object.entries(individualAssignments).map(([userId, profileId]) =>
        axios.post(`${ip}/user/${userId}/change-profile?profileId=${profileId}`, null, {
          headers: { Authorization: `Bearer ${token}` },
        })
      );
      
      await Promise.all(reassignPromises);
      toast.success(`${associatedUsers.length} usuário(s) foram reatribuídos.`);

      await axios.delete(`${ip}/profile/${selectedProfileDetails.id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      toast.success(`O perfil "${selectedProfileDetails.name}" foi excluído.`);

      setIsFinalConfirmModalOpen(false);
      setIsDeleteModalOpen(false);
      setSelectedProfileDetails(null);
      setIndividualAssignments({});
      fetchProfiles();

    } catch (error) {
      console.error("Erro ao reatribuir e excluir:", error);
      toast.error("Ocorreu um erro durante a operação.");
    } finally {
      setLoading(false);
    }
  };
  
  const handleCreateProfile = async () => {
    if (!clientId || !name.trim()) {
      toast.warning("O nome do perfil é obrigatório.");
      return;
    }

    let profilePermissions = permissions;

    if (admin) {
      profilePermissions = {
        dashboard: {
          general: true,
          providers: true,
          document: true,
          documentDetails: true,
        },
        document: {
          view: true,
          upload: true,
          exempt: true,
        },
        contract: {
          finish: true,
          suspend: true,
          create: true,
        },
        reception: true,
        types: {
          registrationAndCertificates: true,
          health: true,
          workplaceSafety: true,
          environment: true,
          laboral: true,
          general: true,
        },
      };
    }

    const newProfile = { 
      name, 
      description, 
      admin, 
      permissions: profilePermissions,
      clientId, 
      branchIds: [], 
      contractIds: [] 
    };

    const tokenFromStorage = localStorage.getItem("tokenClient");
    try {
      await axios.post(`${ip}/profile`, newProfile, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });
      toast.success("Perfil criado com sucesso!");
      setName("");
      setDescription("");
      setAdmin(false);
      setPermissions({
        dashboard: { general: false, providers: false, document: false, documentDetails: false },
        document: { view: false, upload: false, exempt: false },
        contract: { finish: false, suspend: false, create: false },
        reception: false,
        types: { registrationAndCertificates: false, health: false, workplaceSafety: false, environment: false, laboral: false, general: false },
      });
      fetchProfiles();
    } catch (err: any) {
      console.error("Erro ao criar perfil:", err.response || err);
      toast.error("Erro ao criar o perfil.");
    }
  };

  const handlePermissionChange = (path: string) => {
    const keys = path.split('.');
    
    setPermissions(prev => {
      let newState: any = { ...prev };
      let currentLevel = newState;
      
      for (let i = 0; i < keys.length - 1; i++) {
        currentLevel[keys[i]] = { ...currentLevel[keys[i]] };
        currentLevel = currentLevel[keys[i]];
      }
      
      const lastKey = keys[keys.length - 1];
      currentLevel[lastKey] = !currentLevel[lastKey];
      
      return newState;
    });
  };

  useEffect(() => {
    if (clientId) {
      fetchProfiles();
    }
  }, [clientId]);

  return (
    <div className="flex justify-center w-full mt-[-1.5rem]">
      <div className="flex flex-col md:flex-row w-full max-w-6xl gap-6 items-start">
        <div className="bg-white shadow-md rounded p-6 md:w-1/2">
          <h2 className="text-xl font-semibold mb-4">Perfis vinculados ao cliente</h2>
          {loading && !isDeleteModalOpen && <p className="text-gray-500">Carregando...</p>}
          {!loading && profiles.length === 0 && (
            <p className="text-gray-500">Nenhum perfil encontrado para este cliente.</p>
          )}
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
          <h3 className="text-lg font-medium mb-2">Criar novo perfil</h3>
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
                  <label>
                    <input type="checkbox" checked={permissions.dashboard.general} onChange={() => handlePermissionChange("dashboard.general")} /> Geral
                  </label>
                  <label>
                    <input type="checkbox" checked={permissions.dashboard.providers} onChange={() => handlePermissionChange("dashboard.providers")} /> Fornecedores
                  </label>
                  <label>
                    <input type="checkbox" checked={permissions.dashboard.document} onChange={() => handlePermissionChange("dashboard.document")} /> Documentos
                  </label>
                  <label>
                    <input type="checkbox" checked={permissions.dashboard.documentDetails} onChange={() => handlePermissionChange("dashboard.documentDetails")} /> Detalhes de Documentos
                  </label>
                  <hr className="my-2" />
                  <p className="font-medium">Documentos</p>
                  <label>
                    <input type="checkbox" checked={permissions.document.view} onChange={() => handlePermissionChange("document.view")} /> Visualizar
                  </label>
                  <label>
                    <input type="checkbox" checked={permissions.document.upload} onChange={() => handlePermissionChange("document.upload")} /> Upload
                  </label>
                  <label>
                    <input type="checkbox" checked={permissions.document.exempt} onChange={() => handlePermissionChange("document.exempt")} /> Isentar
                  </label>
                  <div className="ml-4 mt-2 border-l-2 pl-4">
                    <p className="font-medium">Tipos de Documento</p>
                    <div className="grid grid-cols-1 gap-2">
                      <label>
                        <input type="checkbox" checked={permissions.types.registrationAndCertificates} onChange={() => handlePermissionChange("types.registrationAndCertificates")} /> Cadastro e Certidões
                      </label>
                      <label>
                        <input type="checkbox" checked={permissions.types.health} onChange={() => handlePermissionChange("types.health")} /> Saúde
                      </label>
                      <label>
                        <input type="checkbox" checked={permissions.types.workplaceSafety} onChange={() => handlePermissionChange("types.workplaceSafety")} /> Segurança do Trabalho
                      </label>
                      <label>
                        <input type="checkbox" checked={permissions.types.environment} onChange={() => handlePermissionChange("types.environment")} /> Meio Ambiente
                      </label>
                      <label>
                        <input type="checkbox" checked={permissions.types.laboral} onChange={() => handlePermissionChange("types.laboral")} /> Trabalhista
                      </label>
                      <label>
                        <input type="checkbox" checked={permissions.types.general} onChange={() => handlePermissionChange("types.general")} /> Geral
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
            <button onClick={handleCreateProfile} className="bg-realizaBlue text-white px-4 py-2 rounded w-fit">Criar perfil</button>
          </div>
        </div>
      </div>

      {isDeleteModalOpen && selectedProfileDetails && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded shadow-lg w-full max-w-2xl">
            <h3 className="text-xl font-semibold mb-2 text-red-600">Excluir Perfil e Reatribuir Usuários</h3>
            <p className="mb-4">Você está prestes a excluir o perfil <strong>"{selectedProfileDetails.name}"</strong>.</p>
            {loading && !associatedUsers.length ? (<p>Verificando usuários...</p>) : associatedUsers.length > 0 ? (
              <div className="mb-4">
                <p className="font-semibold text-orange-600">Atenção! Para prosseguir, reatribua cada usuário a um novo perfil.</p>
                <ul className="text-sm space-y-3 bg-gray-50 p-4 rounded max-h-60 overflow-y-auto my-4">
                  {associatedUsers.map((user) => (
                    <li key={user.id} className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-2">
                      <span className="font-medium text-gray-800">{user.fullName} <span className="text-gray-500 font-normal">({user.email})</span></span>
                      <select value={individualAssignments[user.id] || ''} onChange={(e) => handleIndividualAssignmentChange(user.id, e.target.value)} className="p-2 border border-gray-300 rounded w-full sm:w-64" aria-label={`Novo perfil para ${user.fullName}`}>
                        <option value="" disabled>Selecione um novo perfil...</option>
                        {profiles.filter(p => p.id !== selectedProfileDetails.id).map(p => (<option key={p.id} value={p.id}>{p.profileName}</option>))}
                      </select>
                    </li>
                  ))}
                </ul>
              </div>
            ) : (
              <p className="mb-4 text-green-600 bg-green-50 p-3 rounded">Nenhum usuário está vinculado a este perfil.</p>
            )}
            <div className="flex justify-end gap-4 mt-6">
              <button onClick={() => setIsDeleteModalOpen(false)} className="bg-gray-300 text-gray-800 px-4 py-2 rounded hover:bg-gray-400" disabled={loading}>Cancelar</button>
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
              <button
                onClick={() => setIsFinalConfirmModalOpen(false)}
                className="bg-gray-300 text-gray-800 px-4 py-2 rounded hover:bg-gray-400"
                disabled={loading}
              >
                Cancelar
              </button>
              <button
                onClick={handleReassignAndDelete}
                className="bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700"
                disabled={loading}
              >
                {loading ? "Processando..." : "Sim, excluir perfil"}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}