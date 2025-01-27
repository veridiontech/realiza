import { useState, useEffect } from "react";
import axios from "axios";
import { ip } from "@/utils/ip";

interface DocumentViewerProps {
  documentId: string;
  onClose: () => void;
}

export function DocumentViewer({ documentId, onClose }: DocumentViewerProps) {
  const [pdfUrl, setPdfUrl] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchDocument = async () => {
      setLoading(true);
      setError(null);

      try {
        const response = await axios.get(
          `${ip}/document/employee/${documentId}`,
          { responseType: "blob" }, // Recebe como Blob para exibição no iframe
        );

        const pdfBlob = new Blob([response.data], { type: "application/pdf" });
        const pdfUrl = URL.createObjectURL(pdfBlob);

        setPdfUrl(pdfUrl);
      } catch (err: any) {
        setError("Erro ao carregar o documento.");
      } finally {
        setLoading(false);
      }
    };

    fetchDocument();
  }, [documentId]);

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
      <div className="relative w-full max-w-3xl bg-white p-4 shadow-lg">
        <button
          className="absolute right-2 top-2 text-gray-500 hover:text-gray-800"
          onClick={onClose}
        >
          ✖
        </button>

        <h2 className="mb-4 text-lg font-bold">Visualizar Documento</h2>

        {loading && <p>Carregando...</p>}
        {error && <p className="text-red-500">{error}</p>}

        {pdfUrl ? (
          <iframe src={pdfUrl} width="100%" height="500px" />
        ) : (
          !loading && <p>Nenhum documento carregado.</p>
        )}
      </div>
    </div>
  );
}
