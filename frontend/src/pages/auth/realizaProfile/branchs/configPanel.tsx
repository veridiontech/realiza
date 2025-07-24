import { useEffect, useState, useMemo } from "react";
import axios from "axios";
import { ip } from "@/utils/ip";
import { Button } from "@/components/ui/button";
import { toast } from "sonner";

//
// Interfaces principais
//
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

//
// Tipo para a matriz de documentos
//
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

export function ConfigPanel() {
  // aba ativa
  const [selectTab, setSelectedTab] = useState<
    "documents" | "cbos" | "positions" | "services" | "activities" | "validate"
  >("documents");

  //
  // Estados para cada aba
  //
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
  const [selectedPosition, setSelectedPosition] = useState<Position | null>(null);
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

  // aba Validate
  const [matrixEntries, setMatrixEntries] = useState<DocumentMatrixEntry[]>([]);
  const [isLoadingMatrix, setIsLoadingMatrix] = useState(false);
  const [searchMatrixTerm, setSearchMatrixTerm] = useState("");

  // auth
  const token = localStorage.getItem("tokenClient");
  const authHeader = { headers: { Authorization: `Bearer ${token}` } };

  // traduções e unidades
  const riskTranslations: Record<string, string> = {
    LOW: "Baixo",
    MEDIUM: "Médio",
    HIGH: "Alto",
    VERY_HIGH: "Muito Alto",
  };
  const expirationUnits = [

    { value: "MONTHS", label: "Meses" },

  ];

  // carregamento inicial
  useEffect(() => {
    getDocuments();
    getCbos();
    getPositions();
    getServices();
    getActivities();
    getMatrixEntries();
  }, []);

  //
  // ——— CRUD das outras abas ———
  //

  async function getDocuments() {
    setIsLoading(true);
    try {
      const { data } = await axios.get<Document[]>(
        `${ip}/prompt`,
        authHeader
      );
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
      prev.map((d) =>
        d.id === selectedDoc.id ? { ...d, description } : d
      )
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
    const { data } = await axios.get<Position[]>(
      `${ip}/position`,
      authHeader
    );
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
      await axios.post(
        `${ip}/position`,
        { title: positionName },
        authHeader
      );
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
    } finally {
      setIsLoadingServices(false);
    }
  }

  async function handleCreateService() {
    if (!newServiceTitle) return toast("Preencha o título");
    setIsCreatingService(true);
    await axios.post(
      `${ip}/contract/service-type/repository`,
      { title: newServiceTitle, risk: newServiceRisk },
      authHeader
    );
    setNewServiceTitle("");
    setNewServiceRisk("LOW");
    getServices();
    setIsCreatingService(false);
  }

  async function getActivities() {
    setIsLoadingActivities(true);
    try {
      const res = await axios.get<{ content?: Activity[] }>(
        `${ip}/contract/activity-repo`,
        {
          params: { page: 0, size: 100, sort: "title", direction: "ASC" },
          ...authHeader,
        }
      );
      setActivities(res.data.content || []);
    } finally {
      setIsLoadingActivities(false);
    }
  }

  async function handleCreateActivity() {
    if (!newActivityTitle) return toast("Preencha o título");
    setIsCreatingActivity(true);
    await axios.post(
      `${ip}/contract/activity-repo`,
      { title: newActivityTitle, risk: newActivityRisk },
      authHeader
    );
    setNewActivityTitle("");
    setNewActivityRisk("LOW");
    getActivities();
    setIsCreatingActivity(false);
  }

  //
  // ——— ABA VALIDATE ———
  //

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
    } catch {
      toast.error("Não foi possível carregar documentos");
    } finally {
      setIsLoadingMatrix(false);
    }
  }

  async function handleUpdateEntry(id: string) {
    const entry = matrixEntries.find((e) => e.idDocumentMatrix === id);
    if (!entry) return;
    await axios.put(
      `${ip}/document/matrix/${id}`,
      {
        expirationDateUnit: entry.expirationDateUnit,
        expirationDateAmount: entry.expirationDateAmount,
      },
      authHeader
    );
    toast.success("Validade atualizada");
  }

  //
  // ——— Filtragens ———
  //
  const filteredDocuments = useMemo(
    () =>
      documents
        .filter((d) =>
          d.documentTitle.toLowerCase().includes(searchTerm.toLowerCase())
        )
        .sort((a, b) =>
          a.documentTitle.localeCompare(b.documentTitle, "pt-BR")
        ),
    [documents, searchTerm]
  );

  const filteredServices = useMemo(
    () =>
      services
        .filter(
          (s) =>
            s.title.toLowerCase().includes(serviceSearchTerm.toLowerCase()) ||
            riskTranslations[s.risk]
              .toLowerCase()
              .includes(serviceSearchTerm.toLowerCase())
        )
        .sort((a, b) => a.title.localeCompare(b.title, "pt-BR")),
    [services, serviceSearchTerm]
  );

  const filteredActivities = useMemo(
    () =>
      activities
        .filter(
          (a) =>
            a.title.toLowerCase().includes(activitySearchTerm.toLowerCase()) ||
            riskTranslations[a.risk]
              .toLowerCase()
              .includes(activitySearchTerm.toLowerCase())
        )
        .sort((a, b) => a.title.localeCompare(b.title, "pt-BR")),
    [activities, activitySearchTerm]
  );

  const filteredMatrixEntries = useMemo(
    () =>
      matrixEntries.filter((e) =>
        e.name.toLowerCase().includes(searchMatrixTerm.toLowerCase())
      ),
    [matrixEntries, searchMatrixTerm]
  );

  return (
    <div className="p-6 md:p-10 flex flex-col gap-6">
      {/* Header de abas */}
      <div className="shadow-lg rounded-lg bg-white p-6 flex gap-2">
        {[
          "documents",
          "cbos",
          "positions",
          "services",
          "activities",
          "validate",
        ].map((tab) => (
          <Button
            key={tab}
            className={
              selectTab === tab
                ? "bg-realizaBlue text-white"
                : "bg-transparent border text-black border-black hover:bg-neutral-300"
            }
            onClick={() => setSelectedTab(tab as any)}
          >
            {{
              documents: "Documentos",
              cbos: "CBOs",
              positions: "Cargos",
              services: "Serviços",
              activities: "Atividades",
              validate: "Validade Padrão",
            }[tab]}
          </Button>
        ))}
      </div>

      {/* Conteúdo das abas */}
      <div className="shadow-lg rounded-lg bg-white p-6 flex flex-col gap-6">
        {/* Documentos */}
        {selectTab === "documents" && (
          <div className="flex gap-10">
            <div className="w-1/2 space-y-4">
              <h2 className="text-xl font-bold">Documentos</h2>
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

        {/* CBOs */}
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

        {/* Cargos */}
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

        {/* Serviços */}
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
                <p>Carregando serviços...</p>
              ) : filteredServices.length > 0 ? (
                <ul className="max-h-[60vh] overflow-auto space-y-2">
                  {filteredServices.map((s) => (
                    <li key={s.id} className="p-3 border rounded">
                      <strong>{s.title}</strong> — Risco:{" "}
                      {riskTranslations[s.risk]}
                    </li>
                  ))}
                </ul>
              ) : (
                <p>Nenhum serviço.</p>
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
                {expirationUnits.map((u) => (
                  <option key={u.value} value={u.value}>
                    {u.label}
                  </option>
                ))}
              </select>
              <div className="flex gap-2">
                <Button onClick={handleCreateService} disabled={isCreatingService}>
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

        {/* Atividades */}
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
                <p>Carregando atividades...</p>
              ) : filteredActivities.length > 0 ? (
                <ul className="max-h-[60vh] overflow-auto space-y-2">
                  {filteredActivities.map((a) => (
                    <li key={a.id} className="p-3 border rounded">
                      <strong>{a.title}</strong> — Risco:{" "}
                      {riskTranslations[a.risk]}
                    </li>
                  ))}
                </ul>
              ) : (
                <p>Nenhuma atividade.</p>
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
                <Button onClick={handleCreateActivity} disabled={isCreatingActivity}>
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

        {/* Validate */}
        {selectTab === "validate" && (
          <div className="flex flex-col gap-4">
            <h2 className="text-xl font-bold">Validação de Documentos</h2>
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
                        onClick={() => handleUpdateEntry(entry.idDocumentMatrix)}
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
      </div>
    </div>
  );
}
