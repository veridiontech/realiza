import { useEffect, useState } from "react";
import axios from "axios";
import { toast } from "sonner";
import { ip } from "@/utils/ip";
import { useClient } from "@/context/Client-Provider";
import { Eye } from "lucide-react";

type Profile = {
  id: string;
  profileName: string;
};

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

  const [profiles, setProfiles] = useState<Profile[]>([]);
  const [loading, setLoading] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedProfileDetails, setSelectedProfileDetails] = useState<ProfileDetails | null>(null);

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

  const fetchProfiles = async () => {
    if (!clientId) return;
    setLoading(true);
    const tokenFromStorage = localStorage.getItem("tokenClient");
    try {
      const response = await axios.get(`${ip}/profile/by-name/${clientId}`, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` }
      });

      console.log("Retorno:", response.data);

      const data = Array.isArray(response.data) ? response.data : [];
      setProfiles(data);
    } catch (err) {
      console.error("Erro ao buscar perfis:", err);
    } finally {
      setLoading(false);
    }
  };

  const fetchProfileDetails = async (profileId: string) => {
    setLoading(true);
    const tokenFromStorage = localStorage.getItem("tokenClient");
    try {
      const response = await axios.get(`${ip}/profile/${profileId}`, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` }
      });
      console.log("Detalhes:", response.data);
      setSelectedProfileDetails(response.data);
      setIsModalOpen(true);
    } catch (err) {
      console.error("Erro ao buscar detalhes do perfil:", err);
      toast.error("Erro ao carregar detalhes do perfil.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (clientId) {
      fetchProfiles();
    }
  }, [clientId]);

  const handleCreateProfile = async () => {
    if (!clientId || !name.trim()) {
      return;
    }

    let permissions = {
      inspector: false,
      documentViewer: false,
      registrationUser: false,
      registrationContract: false,
      laboral: false,
      workplaceSafety: false,
      registrationAndCertificates: false,
      general: false,
      health: false,
      environment: false,
      concierge: false,
    };

    if (admin) {
      permissions = {
        inspector: true,
        documentViewer: true,
        registrationUser: true,
        registrationContract: true,
        laboral: true,
        workplaceSafety: true,
        registrationAndCertificates: true,
        general: true,
        health: true,
        environment: true,
        concierge: true,
      };
    } else if (manager) {
      permissions = {
        inspector: false,
        documentViewer: true,
        registrationUser,
        registrationContract,
        laboral,
        workplaceSafety,
        registrationAndCertificates,
        general,
        health,
        environment,
        concierge,
      };
    } else if (isInspector) {
      permissions = {
        inspector: true,
        documentViewer,
        registrationUser: false,
        registrationContract: false,
        laboral,
        workplaceSafety,
        registrationAndCertificates,
        general,
        health,
        environment,
        concierge,
      };
    } else {
        permissions = {
            inspector: false,
            documentViewer,
            registrationUser,
            registrationContract,
            laboral,
            workplaceSafety,
            registrationAndCertificates,
            general,
            health,
            environment,
            concierge,
        };
    }

    const newProfile = {
      name,
      description,
      admin,
      viewer,
      manager,
      ...permissions,
      clientId: clientId,
      branchIds: [],
      contractIds: [],
    };

    console.log("PAYLOAD:", JSON.stringify(newProfile, null, 2));
    const tokenFromStorage = localStorage.getItem("tokenClient");

    try {
        await axios.post(`${ip}/profile`, newProfile, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` }
      });

      toast.success("Perfil criado com sucesso!");

      setName("");
      setDescription("");
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

      fetchProfiles();
    } catch (err: any) {
      console.error("Erro ao criar perfil:", err.response || err);
    }
  };

  return (
    <div className="flex justify-center w-full mt-[-1.5rem]">
      <div className="bg-white shadow-md rounded p-6 w-full max-w-2xl">
        <h2 className="text-xl font-semibold mb-4">Perfis vinculados ao cliente</h2>

        {loading && <p className="text-gray-500">Carregando perfis...</p>}

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
                <button
                  onClick={() => fetchProfileDetails(profile.id)}
                  className="p-1 rounded-full hover:bg-gray-200"
                  title="Ver detalhes do perfil"
                >
                  <Eye className="w-5 h-5 text-gray-600" />
                </button>
              </li>
            ))}
          </ul>
        )}

        <div className="border-t pt-4">
          <h3 className="text-lg font-medium mb-2">Criar novo perfil</h3>

          <div className="flex flex-col gap-4">
            <input
              className="border border-gray-300 rounded px-3 py-2"
              placeholder="Nome do perfil"
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
            <input
              className="border border-gray-300 rounded px-3 py-2"
              placeholder="Descrição"
              value={description}
              onChange={(e) => setDescription(e.target.value)}
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
                    }}
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
                    }}
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
                    }}
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
                    }}
                  />
                  Fiscal de contrato
                </label>
              </div>
            </div>

            {(manager || isInspector) && (
              <div className="flex flex-col gap-2 mt-4">
                <p className="font-medium">Permissões</p>
                <div className="grid grid-cols-2 gap-2">
                  <label>
                    <input
                      type="checkbox"
                      checked={documentViewer}
                      onChange={(e) => setDocumentViewer(e.target.checked)}
                      disabled={manager}
                    />{" "}
                    Visualizador de Documentos
                  </label>
                  <label>
                    <input
                      type="checkbox"
                      checked={registrationUser}
                      onChange={(e) => setRegistrationUser(e.target.checked)}
                      disabled={isInspector}
                    />{" "}
                    Cadastro de Usuários
                  </label>
                  <label>
                    <input
                      type="checkbox"
                      checked={registrationContract}
                      onChange={(e) => setRegistrationContract(e.target.checked)}
                      disabled={isInspector}
                    />{" "}
                    Cadastro de Contratos
                  </label>
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

            <button
              onClick={handleCreateProfile}
              className="bg-realizaBlue text-white px-4 py-2 rounded w-fit"
            >
              Criar perfil
            </button>
          </div>
        </div>
      </div>

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
            <button
              onClick={() => setIsModalOpen(false)}
              className="mt-6 bg-gray-300 text-gray-800 px-4 py-2 rounded hover:bg-gray-400"
            >
              Fechar
            </button>
          </div>
        </div>
      )}
    </div>
  );
}