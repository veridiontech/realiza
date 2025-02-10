import { useEffect, useState } from "react";
import axios from "axios";
import { RiskTable } from "@/components/riskTable"; // Importa o componente de tabela

export function RiskMatriz() {
  const [documents, setDocuments] = useState([]);

  useEffect(() => {
    axios.get("http://seu_ip/document/matrix").then((res) => {
      setDocuments(res.data.content);
    });
  }, []);

  return (
    <div className="flex flex-col items-center justify-center">
      {documents.length > 0 ? (
        <RiskTable documents={documents} />
      ) : (
        <p>Carregando documentos...</p>
      )}
    </div>
  );
}
