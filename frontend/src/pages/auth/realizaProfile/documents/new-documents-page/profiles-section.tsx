import { useEffect, useState } from "react";
import axios from "axios";
import { toast } from "sonner";
import { ip } from "@/utils/ip";
import { useClient } from "@/context/Client-Provider";
import { Eye, Trash2 } from "lucide-react";

// Tipo para os usuários retornados pela verificação
type User = {
  id: string;
  fullName: string;
  email: string;
};

// Tipo para a lista de perfis
type Profile = {
  id: string;
  profileName: string;
};

// Tipo para os detalhes de um perfil
type ProfileDetails = {
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
};

export function ProfilesSection() {
  const { client } = useClient();
  const clientId = client?.idClient;

  // Estados principais
  const [profiles, setProfiles] = useState<Profile[]>([]);
  const [loading, setLoading] = useState(false);
  
  // Estados para os modais
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  // --- NOVO ESTADO para o modal de confirmação final ---
  const [isFinalConfirmModalOpen, setIsFinalConfirmModalOpen] = useState(false);

  // Estados para dados selecionados e associados
  const [selectedProfileDetails, setSelectedProfileDetails] = useState<ProfileDetails | null>(null);
  const [associatedUsers, setAssociatedUsers] = useState<User[]>([]);
  const [individualAssignments, setIndividualAssignments] = useState<Record<string, string>>({});

  // Estados para o formulário de criação de perfil
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [admin, setAdmin] = useState(false);
  const [viewer, setViewer] = useState(false);
  const [manager, setManager] = useState(false);
  const [isInspector, setIsInspector] = useState(false);
  const [documentViewer, setDocumentViewer] = useState(false);
  const [registrationUser, setRegistrationUser] = useState(false);
  const [registrationContract, setRegistrationContract] = useState(false);
  const [laboral, setLaboral] = useState(false);
  const [workplaceSafety, setWorkplaceSafety] = useState(false);
  const [registrationAndCertificates, setRegistrationAndCertificates] = useState(false);
  const [general, setGeneral] = useState(false);
  const [health, setHealth] = useState(false);
  const [environment, setEnvironment] = useState(false);
  const [concierge, setConcierge] = useState(false);

  // Função para buscar a lista de perfis
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

  // Função para buscar os detalhes de um perfil específico
  const fetchProfileDetails = async (profileId: string) => {
    setLoading(true);
    const tokenFromStorage = localStorage.getItem("tokenClient");
    try {
      const response = await axios.get(`${ip}/profile/${profileId}`, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });
      setSelectedProfileDetails(response.data);
      setIsModalOpen(true);
    } catch (err) {
      console.error("Erro ao buscar detalhes do perfil:", err);
      toast.error("Erro ao carregar detalhes do perfil.");
    } finally {
      setLoading(false);
    }
  };

  // Função para iniciar o processo de exclusão, verificando usuários
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
      setIsModalOpen(false);
    } catch (err) {
      console.error("Erro ao verificar usuários vinculados:", err);
      toast.error("Não foi possível verificar os usuários vinculados.");
    } finally {
      setLoading(false);
    }
  };

  // Função que executa a exclusão direta (quando não há usuários)
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
  
  // Função para lidar com a mudança em cada dropdown individual
  const handleIndividualAssignmentChange = (userId: string, newProfileId: string) => {
    setIndividualAssignments(prevAssignments => ({
      ...prevAssignments,
      [userId]: newProfileId,
    }));
  };
  
  // --- NOVA FUNÇÃO: Apenas valida e abre o modal de confirmação final ---
  const handleOpenFinalConfirm = () => {
    if (Object.keys(individualAssignments).length !== associatedUsers.length) {
      toast.error("Por favor, atribua um novo perfil para cada usuário.");
      return;
    }
    setIsFinalConfirmModalOpen(true);
  };

  // Função para reatribuir e excluir (agora chamada pelo modal final)
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

      // Fecha todos os modais e reseta os estados
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
  
  // Função para criar um novo perfil
  const handleCreateProfile = async () => {
    if (!clientId || !name.trim()) {
      toast.warning("O nome do perfil é obrigatório.");
      return;
    }
    let permissions = { inspector: false, documentViewer: false, registrationUser: false, registrationContract: false, laboral: false, workplaceSafety: false, registrationAndCertificates: false, general: false, health: false, environment: false, concierge: false };
    if (admin) {
      permissions = { inspector: true, documentViewer: true, registrationUser: true, registrationContract: true, laboral: true, workplaceSafety: true, registrationAndCertificates: true, general: true, health: true, environment: true, concierge: true };
    } else if (manager) {
      permissions = { ...permissions, documentViewer: true, registrationUser, registrationContract, laboral, workplaceSafety, registrationAndCertificates, general, health, environment, concierge };
    } else if (isInspector) {
      permissions = { ...permissions, inspector: true, documentViewer, laboral, workplaceSafety, registrationAndCertificates, general, health, environment, concierge };
    } else {
      permissions = { ...permissions, documentViewer, registrationUser, registrationContract, laboral, workplaceSafety, registrationAndCertificates, general, health, environment, concierge };
    }
    const newProfile = { name, description, admin, viewer, manager, ...permissions, clientId, branchIds: [], contractIds: [] };
    const tokenFromStorage = localStorage.getItem("tokenClient");
    try {
      await axios.post(`${ip}/profile`, newProfile, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });
      toast.success("Perfil criado com sucesso!");
      setName(""); setDescription(""); setAdmin(false); setViewer(false); setManager(false); setIsInspector(false); setDocumentViewer(false); setRegistrationUser(false); setRegistrationContract(false); setLaboral(false); setWorkplaceSafety(false); setRegistrationAndCertificates(false); setGeneral(false); setHealth(false); setEnvironment(false); setConcierge(false);
      fetchProfiles();
    } catch (err: any) {
      console.error("Erro ao criar perfil:", err.response || err);
      toast.error("Erro ao criar o perfil.");
    }
  };

  useEffect(() => {
    if (clientId) {
      fetchProfiles();
    }
  }, [clientId]);

  return (
    <div className="flex justify-center w-full mt-[-1.5rem]">
      <div className="flex flex-col md:flex-row w-full max-w-6xl gap-6 items-start">
        {/* Card de Perfis Vinculados */}
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
                      onClick={() => fetchProfileDetails(profile.id)}
                      className="p-1 rounded-full hover:bg-gray-200"
                      title="Ver detalhes do perfil"
                    >
                      <Eye className="w-5 h-5 text-gray-600" />
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

        {/* Card de Criar Novo Perfil */}
        <div className="bg-white shadow-md rounded p-6 md:w-1/2">
          <h3 className="text-lg font-medium mb-2">Criar novo perfil</h3>
          <div className="flex flex-col gap-4">
            <input className="border border-gray-300 rounded px-3 py-2" placeholder="Nome do perfil" value={name} onChange={(e) => setName(e.target.value)} />
            <input className="border border-gray-300 rounded px-3 py-2" placeholder="Descrição" value={description} onChange={(e) => setDescription(e.target.value)} />
            <div className="flex flex-col gap-2">
              <p className="font-medium">Tipo do perfil</p>
              <div className="flex gap-6 flex-wrap">
                <label className="flex items-center gap-2"><input type="radio" name="profileType" checked={admin} onChange={() => { setAdmin(true); setViewer(false); setManager(false); setIsInspector(false); setDocumentViewer(false); setRegistrationUser(false); setRegistrationContract(false); }} /> Admin</label>
                <label className="flex items-center gap-2"><input type="radio" name="profileType" checked={viewer} onChange={() => { setAdmin(false); setViewer(true); setManager(false); setIsInspector(false); setDocumentViewer(false); setRegistrationUser(false); setRegistrationContract(false); }} /> Visitante</label>
                <label className="flex items-center gap-2"><input type="radio" name="profileType" checked={manager} onChange={() => { setAdmin(false); setViewer(false); setManager(true); setIsInspector(false); setDocumentViewer(true); setRegistrationUser(false); setRegistrationContract(false); }} /> Gestor</label>
                <label className="flex items-center gap-2"><input type="radio" name="profileType" checked={isInspector} onChange={() => { setAdmin(false); setViewer(false); setManager(false); setIsInspector(true); setDocumentViewer(false); setRegistrationUser(false); setRegistrationContract(false); }} /> Fiscal de contrato</label>
              </div>
            </div>
            {(manager || isInspector) && (
              <div className="flex flex-col gap-2 mt-4">
                <p className="font-medium">Permissões</p>
                <div className="grid grid-cols-2 gap-2">
                  <label><input type="checkbox" checked={documentViewer} onChange={(e) => setDocumentViewer(e.target.checked)} disabled={manager} /> Visualizador de Documentos</label>
                  <label><input type="checkbox" checked={registrationUser} onChange={(e) => setRegistrationUser(e.target.checked)} disabled={isInspector} /> Cadastro de Usuários</label>
                  <label><input type="checkbox" checked={registrationContract} onChange={(e) => setRegistrationContract(e.target.checked)} disabled={isInspector} /> Cadastro de Contratos</label>
                  <label><input type="checkbox" checked={laboral} onChange={(e) => setLaboral(e.target.checked)} /> Trabalhista</label>
                  <label><input type="checkbox" checked={workplaceSafety} onChange={(e) => setWorkplaceSafety(e.target.checked)} /> Segurança do Trabalho</label>
                  <label><input type="checkbox" checked={registrationAndCertificates} onChange={(e) => setRegistrationAndCertificates(e.target.checked)} /> Cadastro e certidões</label>
                  <label><input type="checkbox" checked={general} onChange={(e) => setGeneral(e.target.checked)} /> Geral</label>
                  <label><input type="checkbox" checked={health} onChange={(e) => setHealth(e.target.checked)} /> Saúde</label>
                  <label><input type="checkbox" checked={environment} onChange={(e) => setEnvironment(e.target.checked)} /> Meio Ambiente</label>
                  <label><input type="checkbox" checked={concierge} onChange={(e) => setConcierge(e.target.checked)} /> Portaria</label>
                </div>
              </div>
            )}
            <button onClick={handleCreateProfile} className="bg-realizaBlue text-white px-4 py-2 rounded w-fit">Criar perfil</button>
          </div>
        </div>
      </div>

      {/* Modal de Detalhes do Perfil */}
      {isModalOpen && selectedProfileDetails && (
         <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
           <div className="bg-white p-6 rounded shadow-lg w-full max-w-md max-h-[90vh] overflow-y-auto">
             <h3 className="text-xl font-semibold mb-4">Detalhes do Perfil: {selectedProfileDetails.name}</h3>
             <div className="grid grid-cols-1 gap-2 text-sm">
                <p><strong>Descrição:</strong> {selectedProfileDetails.description || "Nenhuma descrição informada"}</p>
                <p><strong>Admin:</strong> {selectedProfileDetails.admin ? "Sim" : "Não"}</p>
                <p><strong>Visitante:</strong> {selectedProfileDetails.viewer ? "Sim" : "Não"}</p>
                <p><strong>Gestor:</strong> {selectedProfileDetails.manager ? "Sim" : "Não"}</p>
                <p><strong>Fiscal:</strong> {selectedProfileDetails.inspector ? "Sim" : "Não"}</p>
                <p><strong>Visualizador de Documentos:</strong> {selectedProfileDetails.documentViewer ? "Sim" : "Não"}</p>
                <p><strong>Cadastro de Usuários:</strong> {selectedProfileDetails.registrationUser ? "Sim" : "Não"}</p>
                <p><strong>Cadastro de Contratos:</strong> {selectedProfileDetails.registrationContract ? "Sim" : "Não"}</p>
                <p><strong>Trabalhista:</strong> {selectedProfileDetails.laboral ? "Sim" : "Não"}</p>
                <p><strong>Segurança do Trabalho:</strong> {selectedProfileDetails.workplaceSafety ? "Sim" : "Não"}</p>
                <p><strong>Cadastro e Certidões:</strong> {selectedProfileDetails.registrationAndCertificates ? "Sim" : "Não"}</p>
                <p><strong>Geral:</strong> {selectedProfileDetails.general ? "Sim" : "Não"}</p>
                <p><strong>Saúde:</strong> {selectedProfileDetails.health ? "Sim" : "Não"}</p>
                <p><strong>Meio Ambiente:</strong> {selectedProfileDetails.environment ? "Sim" : "Não"}</p>
                <p><strong>Portaria:</strong> {selectedProfileDetails.concierge ? "Sim" : "Não"}</p>
             </div>
             <div className="mt-6 flex justify-end gap-3">
               <button onClick={() => handleAttemptDelete(selectedProfileDetails.id)} className="bg-red-600 text-white px-4 py-2 rounded hover:bg-red-700 flex items-center gap-2"><Trash2 className="w-4 h-4" /> Excluir</button>
               <button onClick={() => setIsModalOpen(false)} className="bg-gray-300 text-gray-800 px-4 py-2 rounded hover:bg-gray-400">Fechar</button>
             </div>
           </div>
         </div>
      )}

      {/* Modal de Reatribuição (antigo modal de confirmação) */}
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

      {/* --- NOVO MODAL: Confirmação Final --- */}
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