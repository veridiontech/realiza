import { useEffect, useState, useMemo } from "react";
import axios from "axios";
import { ip } from "@/utils/ip";

interface Document {
    id: string;
    description: string;
    documentId: string;
    documentTitle: string;
}

export function ConfigPanel() {
    const [documents, setDocuments] = useState<Document[]>([]);
    const [searchTerm, setSearchTerm] = useState("");
    const [selectedDoc, setSelectedDoc] = useState<Document | null>(null);
    const [description, setDescription] = useState("");
    const [isLoading, setIsLoading] = useState(false);

    const tokenFromStorage = localStorage.getItem("tokenClient");

    const getDocuments = async () => {
        setIsLoading(true);
        try {
            const response = await axios.get(`${ip}/prompt`, {
                headers: {
                    Authorization: `Bearer ${tokenFromStorage}`,
                },
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
                    headers: {
                        Authorization: `Bearer ${tokenFromStorage}`,
                    },
                }
            );

            setDocuments((prev) =>
                prev.map((d) =>
                    d.id === selectedDoc.id ? { ...d, description } : d
                )
            );

            setSelectedDoc(null);
        } catch (err) {
            console.error("Erro ao salvar descrição:", err);
        }
    };

    useEffect(() => {
        getDocuments();
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
        <div className="flex items-start justify-center gap-10 p-10">
            {/* Lista de documentos + busca */}
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

            {/* Editor lateral */}
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
    );
}
