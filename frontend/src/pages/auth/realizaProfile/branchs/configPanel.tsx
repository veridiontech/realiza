import { useEffect, useState, useMemo } from "react";
import axios from "axios";
import { ip } from "@/utils/ip";
import { Button } from "@/components/ui/button"; // Importe o componente Button

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

export function ConfigPanel() {
  // Estado para controlar a aba selecionada
  const [selectTab, setSelectedTab] = useState("documents"); // Valor inicial para a primeira aba

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

  const tokenFromStorage = localStorage.getItem("tokenClient");

  // ---------- DOCUMENTOS ----------
  const getDocuments = async () => {
    setIsLoading(true);
    try {
      const response = await axios.get(`${ip}/prompt`, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });
      setDocuments(response.data);
    } catch (err) {
      console.error("Erro ao buscar documentos:", err);
    } finally {
      setIsLoading(false);
    }
  };

  const handleSelect = (doc: Document) => {
    setSelectedDoc(doc);
    setDescription(doc.description || "");
  };

  const handleSave = async () => {
    if (!selectedDoc) return;
    try {
      await axios.put(
        `${ip}/prompt/${selectedDoc.id}`,
        {
          documentId: selectedDoc.documentId,
          description,
        },
        {
          headers: { Authorization: `Bearer ${tokenFromStorage}` },
        }
      );

      setDocuments((prev) =>
        prev.map((d) => (d.id === selectedDoc.id ? { ...d, description } : d))
      );
      setSelectedDoc(null);
    } catch (err) {
      console.error("Erro ao salvar descrição:", err);
    }
  };

  // ---------- CBO ----------
  const getCbos = async () => {
    try {
      const res = await axios.get(`${ip}/cbo`, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });
      setCbos(res.data || []);
    } catch (err) {
      console.error("Erro ao buscar CBOs:", err);
    }
  };

  const handleSaveCBO = async () => {
    try {
      if (selectedCBO) {
        await axios.put(
          `${ip}/cbo/${selectedCBO.id}`,
          { code: cboCode, title: cboTitle },
          { headers: { Authorization: `Bearer ${tokenFromStorage}` } }
        );
      } else {
        await axios.post(
          `${ip}/cbo`,
          { code: cboCode, title: cboTitle },
          { headers: { Authorization: `Bearer ${tokenFromStorage}` } }
        );
      }
      setCboCode("");
      setCboTitle("");
      setSelectedCBO(null);
      getCbos();
    } catch (err) {
      console.error("Erro ao salvar CBO:", err);
    }
  };

  const handleDeleteCBO = async (id: string) => {
    try {
      await axios.delete(`${ip}/cbo/${id}`, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });
      getCbos();
    } catch (err) {
      console.error("Erro ao deletar CBO:", err);
    }
  };

  // ---------- CARGOS ----------
  const getPositions = async () => {
    try {
      const res = await axios.get(`${ip}/position`, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });
      setPositions(res.data || []);
    } catch (err) {
      console.error("Erro ao buscar cargos:", err);
    }
  };

  const handleSavePosition = async () => {
    try {
      if (selectedPosition) {
        await axios.put(
          `${ip}/position/${selectedPosition.id}`,
          { title: positionName },
          { headers: { Authorization: `Bearer ${tokenFromStorage}` } }
        );
      } else {
        await axios.post(
          `${ip}/position`,
          { title: positionName },
          { headers: { Authorization: `Bearer ${tokenFromStorage}` } }
        );
      }
      setPositionName("");
      setSelectedPosition(null);
      getPositions();
    } catch (err) {
      console.error("Erro ao salvar cargo:", err);
    }
  };

  const handleDeletePosition = async (id: string) => {
    try {
      await axios.delete(`${ip}/position/${id}`, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });
      getPositions();
    } catch (err) {
      console.error("Erro ao deletar cargo:", err);
    }
  };

  // ---------- USE EFFECT ----------
  useEffect(() => {
    getDocuments();
    getCbos();
    getPositions();
  }, []);

  const filteredDocuments = useMemo(() => {
    return documents
      .filter((doc) =>
        doc.documentTitle.toLowerCase().includes(searchTerm.toLowerCase())
      )
      .sort((a, b) =>
        a.documentTitle.localeCompare(b.documentTitle, "pt-BR", {
          sensitivity: "base",
        })
      );
  }, [documents, searchTerm]);

  return (
    // Reduzindo o gap entre os elementos flexíveis (caixas brancas)
    <div className="p-6 md:p-10 flex flex-col gap-0 md:gap-0">
      <div className="shadow-lg rounded-lg bg-white p-6 md:p-8 flex flex-col gap-6 md:gap-10 relative bottom-[8vw]">
        <h1 className="text-2xl md:text-[25px]">Configurações gerais</h1>
        <div className="bg-[#7CA1F3] w-full h-[1px]" />
        <div className="flex items-center gap-5">
          {/* Botões de navegação para as abas */}
          {["documents", "cbos", "positions"].map((tab) => (
            <Button
              key={tab}
              className={`${
                selectTab === tab
                  ? "bg-realizaBlue text-white" // Cor de fundo para aba selecionada
                  : "bg-transparent border text-black border-black hover:bg-neutral-300" // Estilo para abas não selecionadas
              }`}
              onClick={() => setSelectedTab(tab)}
            >
              {{
                documents: "Documentos",
                cbos: "CBOs",
                positions: "Cargos",
              }[tab]}
            </Button>
          ))}
        </div>
      </div>

      {/* Conteúdo condicional com base na aba selecionada */}
      {/* Aplicando o mesmo design de fundo branco para as seções de conteúdo */}
      <div className="shadow-lg rounded-lg bg-white p-6 md:p-8 flex flex-col gap-6 md:gap-10">
        {selectTab === "documents" && (
          // Conteúdo da aba de Documentos
          <div className="flex items-start justify-center gap-10 w-full">
            <div className="w-[45%] space-y-4">
              <h1 className="text-2xl font-bold mb-2">Lista de documentos</h1>
              <input
                type="text"
                placeholder="Buscar por título..."
                className="w-full p-2 border rounded-md"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
              />
              {isLoading ? (
                <p className="text-gray-500">Carregando documentos...</p>
              ) : (
                <ul className="space-y-2 max-h-[65vh] overflow-y-auto pr-1">
                  {filteredDocuments.length > 0 ? (
                    filteredDocuments.map((doc) => (
                      <li
                        key={doc.id}
                        onClick={() => handleSelect(doc)}
                        className="cursor-pointer p-4 border rounded-md hover:bg-gray-100 transition-all"
                      >
                        <h2 className="font-semibold">{doc.documentTitle}</h2>
                        <p className="text-sm text-gray-700 line-clamp-1">
                          {doc.description || "Sem descrição definida"}
                        </p>
                      </li>
                    ))
                  ) : (
                    <p className="text-gray-400">Nenhum documento encontrado.</p>
                  )}
                </ul>
              )}
            </div>

            {selectedDoc && (
              <div className="w-[45%] border-l pl-6 space-y-4">
                <h2 className="text-xl font-bold">
                  Editar descrição: {selectedDoc.documentTitle}
                </h2>
                <textarea
                  className="w-full h-40 p-3 border rounded-md resize-none"
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  maxLength={1000}
                  placeholder="Descreva como o documento deve ser avaliado..."
                />
                <div className="text-sm text-gray-500 text-right">
                  {description.length} / 1000 caracteres
                </div>
                <div className="flex gap-4 pt-2">
                  <button
                    onClick={handleSave}
                    className="bg-realizaBlue text-white px-6 py-2 rounded-md transition-all hover:opacity-90"
                  >
                    Salvar
                  </button>
                  <button
                    onClick={() => setSelectedDoc(null)}
                    className="bg-gray-300 text-gray-800 px-6 py-2 rounded-md"
                  >
                    Cancelar
                  </button>
                </div>
              </div>
            )}
          </div>
        )}

        {selectTab === "cbos" && (
          // Conteúdo da aba de CBOs
          <div className="flex items-start justify-center gap-10 w-full">
            <div className="w-[45%] space-y-4">
              <h1 className="text-2xl font-bold mb-2">Lista de CBOs</h1>
              <ul className="space-y-2 max-h-[65vh] overflow-y-auto pr-1">
                {cbos.map((cbo) => (
                  <li
                    key={cbo.id}
                    className="p-3 border rounded-md flex justify-between items-center"
                  >
                    <div>
                      <strong>{cbo.code}</strong> - {cbo.title}
                    </div>
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
            <div className="w-[45%] border-l pl-6 space-y-4">
              <h2 className="text-xl font-bold">
                {selectedCBO ? "Editar CBO" : "Novo CBO"}
              </h2>
              <input
                className="w-full p-2 border rounded-md"
                placeholder="Código"
                value={cboCode}
                onChange={(e) => setCboCode(e.target.value)}
              />
              <input
                className="w-full p-2 border rounded-md"
                placeholder="Título"
                value={cboTitle}
                onChange={(e) => setCboTitle(e.target.value)}
              />
              <div className="flex gap-2">
                <button
                  onClick={handleSaveCBO}
                  className="bg-green-600 text-white px-4 py-2 rounded-md"
                >
                  {selectedCBO ? "Salvar alterações" : "Criar CBO"}
                </button>
                {selectedCBO && (
                  <button
                    onClick={() => {
                      setSelectedCBO(null);
                      setCboCode("");
                      setCboTitle("");
                    }}
                    className="bg-gray-400 text-white px-4 py-2 rounded-md"
                  >
                    Cancelar
                  </button>
                )}
              </div>
            </div>
          </div>
        )}

        {selectTab === "positions" && (
          // Conteúdo da aba de Cargos
          <div className="flex items-start justify-center gap-10 w-full">
            <div className="w-[45%] space-y-4">
              <h1 className="text-2xl font-bold mb-2">Lista de Cargos</h1>
              <ul className="space-y-2 max-h-[65vh] overflow-y-auto pr-1">
                {positions.map((pos) => (
                  <li
                    key={pos.id}
                    className="p-3 border rounded-md flex justify-between items-center"
                  >
                    <div>{pos.title}</div>
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
            <div className="w-[45%] border-l pl-6 space-y-4">
              <h2 className="text-xl font-bold">
                {selectedPosition ? "Editar Cargo" : "Novo Cargo"}
              </h2>
              <input
                className="w-full p-2 border rounded-md"
                placeholder="Nome do cargo"
                value={positionName}
                onChange={(e) => setPositionName(e.target.value)}
              />
              <div className="flex gap-2">
                <button
                  onClick={handleSavePosition}
                  className="bg-green-600 text-white px-4 py-2 rounded-md"
                >
                  {selectedPosition ? "Salvar alterações" : "Criar Cargo"}
                </button>
                {selectedPosition && (
                  <button
                    onClick={() => {
                      setSelectedPosition(null);
                      setPositionName("");
                    }}
                    className="bg-gray-400 text-white px-4 py-2 rounded-md"
                  >
                    Cancelar
                  </button>
                )}
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}