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
  const [showApprovalConfirmation, setShowApprovalConfirmation] = useState(false);
  const [showHistory, setShowHistory] = useState(false);

  const traduzirAcao = (acao: string) => {
    const traducoes: Record<string, string> = {
      CREATE: "Criado",
      UPDATE: "Atualizado",
      DELETE: "Deletado",
      UPLOAD: "Enviado",
      APPROVE: "Aprovado",
      REJECT: "Reprovado",
      EXEMPT: "Isento",
      STATUS_CHANGE: "Mudança de Status",
    };
    return traducoes[acao] ?? acao;
  };

  const formatDate = (dateString: string): string => {
    const date = new Date(dateString);
    return date.toLocaleString("pt-BR", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
      hour: "2-digit",
      minute: "2-digit",
      second: "2-digit",
    });
  };

  const fetchFileData = async () => {
    setLoadingPdf(true);
    try {
      const token = localStorage.getItem("tokenClient");
      const res = await axios.get(`${ip}/document/supplier/${documentId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      const url = res.data.signedUrl;

      if (url) {
        setPdfUrl(url);
      } else {
        setError("Nenhum arquivo encontrado.");
      }
    } catch (err) {
      console.error(err);
      setError("Erro ao buscar o documento.");
    } finally {
      setLoadingPdf(false);
    }
  };

  const fetchLogs = useCallback(async () => {
    try {
      const token = localStorage.getItem("tokenClient");
      const res = await axios.get(`${ip}/audit-log`, {
        headers: { Authorization: `Bearer ${token}` },
        params: {
          id: documentId,
          auditLogTypeEnum: "DOCUMENT",
        },
      });

      console.log("Logs Retornados:", res.data);
      setLogs(res.data.content || []);
    } catch (err) {
      console.error("Erro ao buscar logs:", err);
    }
  }, [documentId]);

  const columns: GridColDef[] = [
    { field: "userResponsibleFullName", headerName: "Usuário", flex: 1 },
    {
      field: "action",
      headerName: "Ação",
      flex: 1,
      renderCell: (params) => traduzirAcao(params.value),
    },
    {
      field: "createdAt",
      headerName: "Data",
      flex: 1,
      renderCell: (params) => formatDate(params.value),
    },
    { field: "description", headerName: "Histórico", flex: 2 }, // ALTERADO DE "notes" PARA "description"
  ];

  const changeStatus = useCallback(
    async (status: "APROVADO" | "REPROVADO", notes = "") => {
      if (status === "REPROVADO" && justification.length > 1000) {
        setJustificationError("Máximo de 1000 caracteres na justificativa.");
        return;
      }
      setJustificationError(null);
      setLoadingStatus(true);
      setShowApprovalConfirmation(false);
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
        await fetchLogs();
        onClose();
      } catch {
        toast.error("Erro ao atualizar o status do documento.");
      } finally {
        setLoadingStatus(false);
      }
    },
    [documentId, justification, onClose, onStatusChange, fetchLogs]
  );

  useEffect(() => {
    if (!isOpen || !documentId) return;
    fetchFileData();
    fetchLogs();
  }, [documentId, isOpen, fetchLogs]);

  if (!isOpen) return null;

  const isJustificationPanelOpen = showJustification;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50" onClick={onClose}>
      <div
        className={`relative ${isJustificationPanelOpen ? 'max-w-7xl' : 'max-w-4xl'} w-full h-[98vh] bg-white p-6 shadow-lg flex`}
        onClick={(e) => e.stopPropagation()}
      >
        <button
          className="absolute right-4 top-4 text-gray-500 hover:text-gray-800 z-30"
          onClick={onClose}
        >
          ✖
        </button>

        <div className={`flex-1 flex flex-col ${isJustificationPanelOpen ? 'pr-6' : ''}`}>
          <h2 className="mb-4 text-center text-xl font-bold">Visualizar Documento</h2>

          {(loadingPdf || loadingStatus) && (
            <div className="absolute inset-0 flex items-center justify-center bg-white bg-opacity-75 z-20">
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
              onClick={() => {
                setShowApprovalConfirmation(true);
                setShowJustification(false);
                setShowHistory(false);
              }}
              disabled={loadingStatus}
              className={`flex-1 py-3 rounded-full font-bold text-white ${
                loadingStatus ? "bg-green-300" : "bg-green-500 hover:bg-green-400"
              }`}
            >
              Aprovar
            </button>
            <button
              onClick={() => {
                setShowJustification(!showJustification);
                setShowHistory(false);
              }}
              disabled={loadingStatus}
              className={`flex-1 py-3 rounded-full font-bold text-white ${
                loadingStatus ? "bg-red-300" : "bg-red-500 hover:bg-red-400"
              }`}
            >
              Reprovar
            </button>
            <button
              onClick={() => {
                setShowHistory(true);
                setShowJustification(false);
              }}
              className="flex-1 py-3 rounded-full font-bold bg-gray-700 text-white hover:bg-gray-600"
            >
              Histórico
            </button>
          </div>
        </div>

        {showJustification && (
          <div className="w-[28rem] bg-white p-6 rounded-lg shadow-lg flex-shrink-0 ml-6">
            <button
              className="absolute right-4 top-4 text-gray-500 hover:text-gray-800 z-40"
              onClick={() => {
                setShowJustification(false);
                setJustification("");
                setJustificationError(null);
              }}
            >
              ✖
            </button>
            <h3 className="mb-4 text-lg font-bold">Justificativa para Reprovação</h3>
            <textarea
              value={justification}
              onChange={(e) => setJustification(e.target.value)}
              maxLength={1000}
              rows={8}
              className="w-full border border-gray-300 p-2 rounded-md resize-y"
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
        )}

        {showHistory && (
          <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50" onClick={() => setShowHistory(false)}>
            <div
              className="relative w-[55rem] h-[38rem] bg-white p-6 rounded-lg shadow-lg"
              onClick={(e) => e.stopPropagation()}
            >
              <button
                className="absolute right-4 top-4 text-gray-500 hover:text-gray-800"
                onClick={() => setShowHistory(false)}
              >
                ✖
              </button>
              <h3 className="mb-4 text-lg font-bold text-center">Histórico do Documento</h3>
              {logs.length > 0 ? (
                <div style={{ height: "calc(100% - 6rem)", width: "100%" }}>
                  <DataGrid
                    rows={logs.map((l) => ({
                      ...l,
                      id: l.id || l._id || Math.random(),
                    }))}
                    columns={columns}
                    pageSizeOptions={[5, 10, 15]}
                    disableRowSelectionOnClick
                    className="border-none"
                    sx={{
                      "& .MuiDataGrid-root": {
                        minWidth: "100%",
                        maxHeight: "100%",
                      },
                      "& .MuiDataGrid-cell": {
                        border: "none",
                      },
                    }}
                  />
                </div>
              ) : (
                <p className="text-center">Nenhum log disponível para este documento.</p>
              )}
            </div>
          </div>
        )}

        {showApprovalConfirmation && (
          <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
            <div className="relative w-[25rem] bg-white p-6 rounded-lg shadow-lg">
              <h3 className="mb-4 text-lg font-bold text-center">Confirmar Aprovação</h3>
              <p className="mb-6 text-center">Tem certeza de que deseja aprovar este documento?</p>
              <div className="mt-4 flex justify-center gap-4">
                <button
                  onClick={() => changeStatus("APROVADO")}
                  disabled={loadingStatus}
                  className={`py-2 px-4 rounded-full font-bold text-white ${
                    loadingStatus ? "bg-green-300" : "bg-green-500 hover:bg-green-400"
                  }`}
                >
                  {loadingStatus ? "Processando..." : "Sim"}
                </button>
                <button
                  onClick={() => setShowApprovalConfirmation(false)}
                  disabled={loadingStatus}
                  className="py-2 px-4 rounded-full bg-gray-400 font-bold text-white hover:bg-gray-300"
                >
                  Não
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}