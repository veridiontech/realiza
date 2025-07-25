import { useState, useEffect, useCallback } from "react";
import axios from "axios";
import { ip } from "@/utils/ip";
import { DataGrid, GridColDef } from "@mui/x-data-grid";
import { toast } from "sonner";

interface DocumentViewerProps {
  documentId: string;
  onClose: () => void;
  onStatusChange?: (id: string, newStatus: string) => void;
  isOpen: boolean;
}

export function DocumentViewer({
  documentId,
  onClose,
  onStatusChange,
  isOpen,
}: DocumentViewerProps) {
  const [pdfUrl, setPdfUrl] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loadingPdf, setLoadingPdf] = useState(false);
  const [logs, setLogs] = useState<any[]>([]);
  const [showJustification, setShowJustification] = useState(false);
  const [justification, setJustification] = useState("");
  const [justificationError, setJustificationError] = useState<string | null>(null);
  const [loadingStatus, setLoadingStatus] = useState(false);

  const columns: GridColDef[] = [
    { field: "user", headerName: "Usuário", flex: 1 },
    { field: "action", headerName: "Ação", flex: 1 },
    { field: "date", headerName: "Data", flex: 1 },
    { field: "notes", headerName: "Histórico", flex: 2 },
  ];

  const changeStatus = useCallback(
    async (status: "APROVADO" | "REPROVADO", notes = "") => {
      if (status === "REPROVADO" && justification.length > 1000) {
        setJustificationError("Máximo de 1000 caracteres na justificativa.");
        return;
      }
      setJustificationError(null);
      setLoadingStatus(true);
      try {
        const token = localStorage.getItem("tokenClient");
        await axios.post(
          `${ip}/document/${documentId}/change-status`,
          { status, notes },
          { headers: { Authorization: `Bearer ${token}` } }
        );
        toast.success(
          `Documento ${status === "APROVADO" ? "aprovado" : "reprovado"} com sucesso!`
        );
        onStatusChange?.(documentId, status);
        onClose();
      } catch {
        toast.error("Erro ao atualizar o status do documento.");
      } finally {
        setLoadingStatus(false);
      }
    },
    [documentId, justification, onClose, onStatusChange]
  );

  useEffect(() => {
    if (!isOpen || !documentId) return;
    setLoadingPdf(true);
    setError(null);

    axios
      .get(`${ip}/document/supplier/${documentId}`, {
        headers: { Authorization: `Bearer ${localStorage.getItem("tokenClient")}` },
      })
      .then((res) => {
        const url = res.data.signedUrl;
        if (url) setPdfUrl(url);
        else setError("Nenhum arquivo encontrado.");
      })
      .catch(() => setError("Erro ao buscar o documento."))
      .finally(() => setLoadingPdf(false));
  }, [documentId, isOpen]);

  useEffect(() => {
    if (!isOpen || !documentId) return;
    axios
      .get(`${ip}/document/${documentId}/logs`, {
        headers: { Authorization: `Bearer ${localStorage.getItem("tokenClient")}` },
      })
      .then((res) => setLogs(res.data || []))
      .catch(() => {});
  }, [documentId, isOpen]);

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50" onClick={onClose}>
      <div
        className="relative max-w-4xl w-full h-[98vh] bg-white p-6 shadow-lg"
        onClick={(e) => e.stopPropagation()}
      >
        <button
          className="absolute right-4 top-4 text-gray-500 hover:text-gray-800"
          onClick={onClose}
        >
          ✖
        </button>
        <h2 className="mb-4 text-center text-xl font-bold">Visualizar Documento</h2>

        {(loadingPdf || loadingStatus) && (
          <div className="absolute inset-0 flex items-center justify-center bg-white bg-opacity-75">
            <div className="loader">Carregando...</div>
          </div>
        )}

        {error && <p className="mb-4 text-red-500">{error}</p>}

        {pdfUrl ? (
          <iframe src={pdfUrl} width="100%" height="750px" className="mb-6" />
        ) : (
          !loadingPdf && <p className="mb-6">Nenhum documento carregado.</p>
        )}

        <div className="flex gap-4 mb-6 justify-center">
          <button
            onClick={() => changeStatus("APROVADO")}
            disabled={loadingStatus}
            className={`flex-1 py-3 rounded-full font-bold text-white ${
              loadingStatus ? "bg-green-300" : "bg-green-500 hover:bg-green-400"
            }`}
          >
            Aprovar
          </button>
          <button
            onClick={() => setShowJustification(true)}
            disabled={loadingStatus}
            className="flex-1 py-3 rounded-full bg-red-500 font-bold text-white hover:bg-red-400"
          >
            Reprovar
          </button>
          <button
            className="flex-1 py-3 rounded-full border font-bold text-black hover:text-gray-600 "
          >
            Histórico
          </button>
        </div>

        {logs.length > 0 && (
          <div style={{ height: 300, width: "100%" }}>
            <DataGrid
              rows={logs.map((l, i) => ({ id: i, ...l }))}
              columns={columns}
           
            />
          </div>
        )}

        {showJustification && (
          <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
            <div className="relative w-[30rem] bg-white p-6 rounded-lg shadow-lg">
              <h3 className="mb-4 text-lg font-bold">Justificativa para Reprovação</h3>
              <textarea
                value={justification}
                onChange={(e) => setJustification(e.target.value)}
                maxLength={1000}
                rows={4}
                className="w-full border border-gray-300 p-2 rounded-md"
                placeholder="Informe a justificativa (até 1000 caracteres)"
              />
              {justificationError && (
                <p className="mt-1 text-sm text-red-500">{justificationError}</p>
              )}
              <div className="mt-4 flex justify-end gap-4">
                <button
                  onClick={() => changeStatus("REPROVADO", justification)}
                  disabled={loadingStatus}
                  className={`py-2 px-4 rounded-full font-bold text-white ${
                    loadingStatus ? "bg-red-300" : "bg-red-500 hover:bg-red-400"
                  }`}
                >
                  {loadingStatus ? "Processando..." : "Confirmar"}
                </button>
                <button
                  onClick={() => {
                    setShowJustification(false);
                    setJustification("");
                    setJustificationError(null);
                  }}
                  className="py-2 px-4 rounded-full bg-gray-400 font-bold text-white hover:bg-gray-300"
                >
                  Cancelar
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
