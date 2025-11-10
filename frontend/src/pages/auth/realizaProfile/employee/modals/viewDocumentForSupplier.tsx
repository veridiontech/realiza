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
  const [justificationError, setJustificationError] = useState<string | null>(
    null
  );
  const [loadingStatus, setLoadingStatus] = useState(false);
  const [showApprovalConfirmation, setShowApprovalConfirmation] =
    useState(false);
  const [showHistory, setShowHistory] = useState(false);
  const [showOldDocumentViewer, setShowOldDocumentViewer] = useState(false);
  const [oldPdfUrl, setOldPdfUrl] = useState<string | null>(null);
  const [loadingOldPdf, setLoadingOldPdf] = useState(false);
  const [oldPdfError, setOldPdfError] = useState<string | null>(null);

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
      if (!token) {
        throw new Error("Token não encontrado");
      }

      // Fazer fetch do endpoint proxy com token JWT e criar blob URL
      const proxyUrl = `${ip}/document/supplier/${documentId}/proxy`;
      console.log("Fetching from proxy:", proxyUrl);

      const proxyRes = await fetch(proxyUrl, {
        headers: {
          Authorization: `Bearer ${token}`
        },
      });

      if (!proxyRes.ok) {
        throw new Error(`HTTP ${proxyRes.status}: ${proxyRes.statusText}`);
      }

      // Converter resposta em blob
      const blob = await proxyRes.blob();
      console.log("Blob created:", blob.size, "bytes");

      // Criar blob URL
      const blobUrl = URL.createObjectURL(blob);
      console.log("Blob URL created:", blobUrl);

      // Definir URL no iframe
      setPdfUrl(blobUrl);
    } catch (err) {
      console.error("Erro ao buscar documento:", err);
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
      setLogs(res.data.content || []);
    } catch (err) {
      console.error("Erro ao buscar logs:", err);
    }
  }, [documentId]);

  const fetchDocumentByAuditLogId = async (auditLogId: string) => {
    setLoadingOldPdf(true);
    setOldPdfError(null);
    try {
      const token = localStorage.getItem("tokenClient");
      if (!token) {
        setOldPdfError("Autenticação necessária. Por favor, faça login novamente.");
        return;
      }

      const requestUrl = `${ip}/document/find-by-audit-log/${auditLogId}`;

      const res = await axios.get(requestUrl, {
        headers: { Authorization: `Bearer ${token}` },
      });

      const url = res.data;

      if (url) {
        setOldPdfUrl(url);
        setShowOldDocumentViewer(true);
      } else {
        setOldPdfError("Nenhum documento encontrado para este log de auditoria. Resposta da API vazia.");
      }
    } catch (err) {
      console.log("id: " , auditLogId);
      console.error("Erro completo ao buscar documento por auditLogId:", err);
      if (axios.isAxiosError(err)) {
        setOldPdfError(`Erro ao carregar a versão do documento: ${err.response?.data?.message || err.message}.`);
      } else {
        setOldPdfError("Erro desconhecido ao carregar a versão do documento. Tente novamente.");
      }
    } finally {
      setLoadingOldPdf(false);
    }
  };

  const columns: GridColDef[] = [
    { field: "userResponsibleFullName", headerName: "Usuário", flex: 1 },
    {
      field: "action",
      headerName: "Ação",
      flex: 1,
      renderCell: (params) => {
        const row = params.row;
        const isRejected = params.value === "REJECT";

        if (isRejected && row.id) {
          return (
            <button
              onClick={() => {
                fetchDocumentByAuditLogId(row.id);
              }}
              className="font-bold text-red-600 hover:underline cursor-pointer text-left"
              title="Clique para visualizar a versão do documento reprovado"
            >
              {traduzirAcao(params.value)}
            </button>
          );
        }
        return (
          <span className={isRejected ? "font-bold text-red-600" : ""}>
            {traduzirAcao(params.value)}
          </span>
        );
      },
    },
    {
      field: "createdAt",
      headerName: "Data",
      flex: 1,
      renderCell: (params) => formatDate(params.value),
    },
    {
      field: "justification",
      headerName: "Motivo",
      flex: 2,
      renderCell: (params) => {
        if (params.row.action === "REJECT") {
          return params.row.justification;
        }
        return "";
      },
    },
  ];

  const changeStatus = useCallback(
    async (status: "APROVADO" | "REPROVADO", justification = "") => {
      if (status === "REPROVADO" && justification.length > 1000) {
        setJustificationError("Máximo de 1000 caracteres na justificativa.");
        return;
      }
      setJustificationError(null);
      setLoadingStatus(true);
      setShowApprovalConfirmation(false);

      try {
        const token = localStorage.getItem("tokenClient");
        const requestBody = { status, justification };
        
        await axios.post(
          `${ip}/document/${documentId}/change-status`,
          requestBody,
          { headers: { Authorization: `Bearer ${token}` } }
        );
        
        toast.success(
          `Documento ${
            status === "APROVADO" ? "aprovado" : "reprovado"
          } com sucesso!`
        );
        onStatusChange?.(documentId, status);
        await fetchLogs();
        onClose();
      } catch (error) {
        console.error("Erro na requisição changeStatus:", error);
        toast.error("Erro ao atualizar o status do documento.");
      } finally {
        setLoadingStatus(false);
      }
    },
    [documentId, onClose, onStatusChange, fetchLogs]
  );

  useEffect(() => {
    if (!isOpen || !documentId) return;
    fetchFileData();
    fetchLogs();
  }, [documentId, isOpen, fetchLogs]);

  if (!isOpen) return null;

  const isJustificationPanelOpen = showJustification;

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50"
      onClick={onClose}
    >
      <div
        className={`relative ${
          isJustificationPanelOpen ? "max-w-7xl" : "max-w-6xl" 
        } w-full h-[98vh] bg-white p-6 shadow-lg flex`}
        onClick={(e) => e.stopPropagation()}
      >
        <button
          className="absolute right-4 top-4 text-gray-500 hover:text-gray-800 z-30"
          onClick={onClose}
        >
          ✖
        </button>

        <div className={`flex-1 flex flex-col ${isJustificationPanelOpen ? "pr-6" : ""}`}>
          <h2 className="mb-4 text-center text-xl font-bold">
            Visualizar Documento
          </h2>

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
            <h3 className="mb-4 text-lg font-bold">
              Justificativa para Reprovação
            </h3>
            <textarea
              value={justification}
              onChange={(e) => {
                setJustification(e.target.value);
              }}
              maxLength={1000}
              rows={8}
              className="w-full border border-gray-300 p-2 rounded-md resize-y focus:outline-none focus:ring-2 focus:ring-red-500"
              placeholder="Informe a justificativa (até 1000 caracteres)"
            />
            {justificationError && (
              <p className="mt-1 text-sm text-red-500">
                {justificationError}
              </p>
            )}
            <div className="mt-4 flex justify-end gap-4">
              <button
                onClick={() => {
                  changeStatus("REPROVADO", justification);
                }}
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
          <div
            className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 backdrop-blur-sm"
            onClick={() => setShowHistory(false)}
          >
            <div
              className="relative w-[70rem] h-[45rem] bg-white p-8 rounded-xl shadow-2xl flex flex-col"
              onClick={(e) => e.stopPropagation()}
            >
              <button
                className="absolute right-5 top-5 text-gray-500 hover:text-gray-800 text-2xl"
                onClick={() => {
                  setShowHistory(false);
                }}
              >
                ✖
              </button>
              <h3 className="mb-6 text-2xl font-extrabold text-center text-gray-800">
                Histórico do Documento
              </h3>
              {logs.length > 0 ? (
                <div style={{ height: "calc(100% - 6rem)", width: "100%" }}>
                  <DataGrid
                    rows={logs.map((l) => ({
                      ...l,
                      id: l.id || l._id || Math.random(),
                    }))}
                    columns={columns}
                    initialState={{
                      pagination: {
                        paginationModel: { pageSize: 10, page: 0 },
                      },
                    }}
                    pageSizeOptions={[5, 10, 20]}
                    disableRowSelectionOnClick
                    disableColumnMenu
                    disableColumnSorting
                    disableColumnResize
                    className="border-none bg-white rounded-lg overflow-hidden"
                    sx={{
                      "& .MuiDataGrid-root": {
                        minWidth: "100%",
                        maxHeight: "100%",
                      },
                      "& .MuiDataGrid-cell": {
                        borderBottom: "1px solid #e5e7eb",
                        padding: "12px 16px",
                      },
                      "& .MuiDataGrid-columnHeaders": {
                        backgroundColor: "#f9fafb",
                        borderBottom: "1px solid #e5e7eb",
                        fontWeight: "bold",
                        fontSize: "1rem",
                        color: "#374151",
                      },
                      "& .MuiDataGrid-columnHeaderTitle": {
                        fontWeight: "bold",
                      },
                      "& .MuiDataGrid-row": {
                        "&:nth-of-type(odd)": {
                          backgroundColor: "#fcfcfc",
                        },
                        "&:hover": {
                          backgroundColor: "#f3f4f6",
                        },
                      },
                      "& .MuiDataGrid-footerContainer": {
                        borderTop: "1px solid #e5e7eb",
                        backgroundColor: "#f9fafb",
                      },
                      "& .MuiTablePagination-root": {
                        color: "#4b5563",
                      },
                      "& .MuiSvgIcon-root": {
                        color: "#4b5563",
                      },
                    }}
                  />
                </div>
              ) : (
                <p className="text-center text-gray-600 text-lg mt-8">
                  Nenhum histórico disponível para este documento.
                </p>
              )}
            </div>
          </div>
        )}
        {showOldDocumentViewer && (
          <div
            className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-75 backdrop-blur-sm"
            onClick={() => {
              setShowOldDocumentViewer(false);
              setOldPdfUrl(null);
              setOldPdfError(null);
            }}
          >
            <div
              className="relative w-[60rem] h-[90vh] bg-white p-6 rounded-lg shadow-xl flex flex-col"
              onClick={(e) => e.stopPropagation()}
            >
              <button
                className="absolute right-4 top-4 text-gray-500 hover:text-gray-800 text-2xl z-10"
                onClick={() => {
                  setShowOldDocumentViewer(false);
                  setOldPdfUrl(null);
                  setOldPdfError(null);
                }}
              >
                ✖
              </button>
              <h3 className="mb-4 text-xl font-bold text-center text-gray-800">
                Documento Reprovado (Histórico)
              </h3>
              {(loadingOldPdf || loadingStatus) && (
                <div className="absolute inset-0 flex items-center justify-center bg-white bg-opacity-75 z-20">
                  <div className="loader">Carregando...</div>
                </div>
              )}
              {oldPdfError && (
                <p className="mb-4 text-red-500 text-center">{oldPdfError}</p>
              )}
              {oldPdfUrl ? (
                <iframe
                  src={oldPdfUrl}
                  width="100%"
                  height="100%"
                  className="border border-gray-300 rounded-md"
                />
              ) : (
                !loadingOldPdf && !oldPdfError && (
                  <p className="text-center text-gray-600 mt-8">
                    Nenhum documento para esta versão.
                  </p>
                )
              )}
            </div>
          </div>
        )}

        {showApprovalConfirmation && (
          <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 backdrop-blur-sm">
            <div className="relative w-[25rem] bg-white p-6 rounded-lg shadow-lg">
              <h3 className="mb-4 text-lg font-bold text-center">
                Confirmar Aprovação
              </h3>
              <p className="mb-6 text-center">
                Tem certeza de que deseja aprovar este documento?
              </p>
              <div className="mt-4 flex justify-center gap-4">
                <button
                  onClick={() => {
                    changeStatus("APROVADO");
                  }}
                  disabled={loadingStatus}
                  className={`py-2 px-4 rounded-full font-bold text-white ${
                    loadingStatus
                      ? "bg-green-300"
                      : "bg-green-500 hover:bg-green-400"
                  }`}
                >
                  {loadingStatus ? "Processando..." : "Sim"}
                </button>
                <button
                  onClick={() => {
                    setShowApprovalConfirmation(false);
                  }}
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