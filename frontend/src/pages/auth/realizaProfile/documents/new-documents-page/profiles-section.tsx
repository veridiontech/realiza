import { useEffect, useState } from "react";
import axios from "axios";
import { toast } from "sonner";
import { ip } from "@/utils/ip";
import { useClient } from "@/context/Client-Provider";

type Profile = {
  id: string;
  profileName: string;
};

export function ProfilesSection() {
  const { client } = useClient();
  const clientId = client?.idClient;

  const [profiles, setProfiles] = useState<Profile[]>([]);
  const [loading, setLoading] = useState(false);

  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [admin, setAdmin] = useState(false);
  const [viewer, setViewer] = useState(false);
  const [manager, setManager] = useState(false);
  const [isInspector, setIsInspector] = useState(false);

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

      console.log("✅ Dados brutos dos perfis recebidos:", response.data);

      const data = Array.isArray(response.data) ? response.data : [];
      setProfiles(data);
    } catch (err) {
      console.error("❌ Erro ao buscar perfis:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (clientId) {
      console.log("📦 clientId do contexto:", clientId);
      fetchProfiles();
    }
  }, [clientId]);

  const handleCreateProfile = async () => {
    if (!clientId || !name.trim()) {
      console.warn("⚠️ Validação: Nome do perfil ou ID do cliente ausente.");
      return;
    }

    let permissions = {
      inspector: false,
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
        laboral: true,
        workplaceSafety: true,
        registrationAndCertificates: true,
        general: true,
        health: true,
        environment: true,
        concierge: true,
      };
    } else if (manager || isInspector) {
      permissions = {
        inspector: isInspector,
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

    console.log("📤 Enviando perfil para criação:", newProfile);
    const tokenFromStorage = localStorage.getItem("tokenClient");

    try {
      const result = await axios.post(`${ip}/profile`, newProfile, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` }
      });

      console.log("✅ Resposta da criação de perfil:", result.data);
      toast.success("Perfil criado com sucesso!");

      // Reset form
      setName("");
      setDescription("");
      setAdmin(false);
      setViewer(false);
      setManager(false);
      setIsInspector(false);
      setLaboral(false);
      setWorkplaceSafety(false);
      setRegistrationAndCertificates(false);
      setGeneral(false);
      setHealth(false);
      setEnvironment(false);
      setConcierge(false);

      fetchProfiles();
    } catch (err: any) {
      console.error("❌ Erro ao criar perfil:", err.response || err);
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
                className="p-3 border border-gray-200 rounded bg-gray-50 hover:bg-gray-100 transition"
              >
                <span className="text-md text-gray-700">{profile.profileName}</span>
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
                    }}
                  />
                  Inspetor
                </label>
              </div>
            </div>

            {(manager || isInspector) && (
              <div className="flex flex-col gap-2 mt-4">
                <p className="font-medium">Permissões</p>
                <div className="grid grid-cols-2 gap-2">
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
    </div>
  );
}
