import { useState } from "react";
import axios from "axios";

export function DocumentViewer() {
  const [pdfUrl, setPdfUrl] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const fetchDocument = async () => {
    setLoading(true);
    setError(null);
    try {
      // Simulação de requisição GET para obter um documento PDF
      const response = await axios.get(
        "https://www.soutocorrea.com.br/wp-content/uploads/2022/05/FakePDF-1.pdf",
        {
          responseType: "blob", // Importante para receber como Blob
        },
      );

      // Cria uma URL para o Blob (PDF)
      const pdfBlob = new Blob([response.data], { type: "application/pdf" });
      const pdfUrl = URL.createObjectURL(pdfBlob);

      setPdfUrl(pdfUrl); // Atualiza a URL do PDF no estado
    } catch (err: any) {
      setError("Erro ao carregar o documento.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex flex-col items-center p-4">
      <h1 className="mb-4 text-xl font-bold">Visualizador de Documentos</h1>

      <button
        onClick={fetchDocument}
        className="mb-4 rounded bg-blue-500 px-4 py-2 text-white hover:bg-blue-600"
      >
        {loading ? "Carregando..." : "Carregar Documento"}
      </button>

      {error && <p className="text-red-500">{error}</p>}

      {pdfUrl ? (
        <div className="h-[500px] w-full border">
          {/* Exibe o PDF usando um iframe */}
          <iframe src={pdfUrl} width="100%" height="100%" />
        </div>
      ) : (
        !loading && <p>Nenhum documento carregado.</p>
      )}
    </div>
  );
}
