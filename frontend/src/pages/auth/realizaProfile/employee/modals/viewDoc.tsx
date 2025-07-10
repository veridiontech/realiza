import { useState, useEffect } from "react";
import axios from "axios";
import { ip } from "@/utils/ip";
import { DataGrid, GridColDef } from "@mui/x-data-grid";
import { toast } from "sonner";

interface DocumentViewerProps {
  documentId: string;
  onClose: () => void;
  onStatusChange?: (id: string, newStatus: string) => void;
}

export function DocumentViewer({ documentId, onClose, onStatusChange }: DocumentViewerProps) {
  const [pdfUrl, setPdfUrl] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [logs, setLogs] = useState<any[]>([]);
  const [showJustification, setShowJustification] = useState(false);
  const [justification, setJustification] = useState("");
  const [justificationError, setJustificationError] = useState<string | null>(null);

  const fetchFileData = async () => {
    setLoading(true);
    try {
      const token = localStorage.getItem("tokenClient");
      const res = await axios.get(`${ip}/document/employee/${documentId}`, {
        headers: { Authorization: `Bearer ${token}` },
      });

      const fileData = res.data.fileData;

      if (fileData) {
        const binaryString = atob(fileData);
        const len = binaryString.length;
        const bytes = new Uint8Array(len);
        for (let i = 0; i < len; i++) {
          bytes[i] = binaryString.charCodeAt(i);
        }

        const pdfBlob = new Blob([bytes], { type: "application/pdf" });
        const pdfUrl = URL.createObjectURL(pdfBlob);
        setPdfUrl(pdfUrl);
      } else {
        setError("Nenhum dado de arquivo encontrado.");
      }
    } catch (err) {
      console.error(err);
      setError("Erro ao buscar o documento.");
    } finally {
      setLoading(false);
    }
  };

  const fetchLogs = async () => {
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
  };

  useEffect(() => {
    if (documentId) {
      fetchFileData();
      fetchLogs();
    }
  }, [documentId]);

  const handleChangeStatus = async (status: string, notes: string) => {
    try {
      const token = localStorage.getItem("tokenClient");
      await axios.post(
        `${ip}/document/${documentId}/change-status`,
        { status, notes },
        { headers: { Authorization: `Bearer ${token}` } }
      );

      toast(`Documento ${status === "APROVADO" ? "aprovado" : "reprovado"} com sucesso!`);

      if (onStatusChange) {
        onStatusChange(documentId, status);
      }

      await fetchLogs();
      onClose();
    } catch (err) {
      console.error(err);
      toast("Erro ao atualizar o status do documento.");
    }
  };

  const handleReprovar = async (notes: string) => {
    if (justification.length > 1000) {
      setJustificationError("A justificativa não pode ter mais de 1000 caracteres.");
      return;
    }

    await handleChangeStatus("REPROVADO", notes);
  };

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

  // Função para formatar a data
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

  const columns: GridColDef[] = [
    { field: "userResponsibleFullName", headerName: "Usuário", flex: 1 },
    { field: "action", headerName: "Ação", flex: 1 },
    { 
      field: "createdAt", 
      headerName: "Data", 
      flex: 1, 
      renderCell: (params) => formatDate(params.value)  // Formatar a data antes de exibi-la
    },
  ];

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center gap-8 bg-black bg-opacity-50">
      <div className="relative w-[50rem] bg-white p-4 shadow-lg rounded-lg">
        <button
          className="absolute right-2 top-2 text-gray-500 hover:text-gray-800"
          onClick={onClose}
        >
          ✖
        </button>

        <h2 className="mb-4 text-center text-xl font-bold">Visualizar Documento</h2>

        {loading && <p>Carregando...</p>}
        {error && <p className="text-red-500">{error}</p>}

        {pdfUrl ? (
          <iframe src={pdfUrl} width="100%" height="500px" />
        ) : (
          !loading && <p>Nenhum documento carregado.</p>
        )}
      </div>

      <div className="relative flex w-[50rem] flex-col bg-white p-6 shadow-lg rounded-lg">
        <button
          className="absolute right-2 top-2 text-gray-500 hover:text-gray-800"
          onClick={onClose}
        >
          ✖
        </button>

        <div className="mb-6 flex flex-col items-center">
          <h2 className="text-lg font-bold">Verificação do Documento</h2>
        </div>

        <div className="flex w-full flex-col items-start">
          <div className="w-full h-[350px] overflow-auto shadow-sm rounded-lg border border-gray-300">
            <DataGrid
              rows={logs.map(log => ({
                ...log,
                action: traduzirAcao(log.action),
              }))}
              columns={columns}
              pageSizeOptions={[5, 10, 15]}
              autoHeight
              disableRowSelectionOnClick
              className="border-none"
              getRowId={(row) => row.id || row._id || Math.random()}
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
        </div>

        <div className="mt-6 flex justify-center gap-6">
          <button
            onClick={() => handleChangeStatus("APROVADO", "")}
            className="h-12 w-[12rem] rounded-full bg-green-500 text-white hover:bg-green-400 transition-all"
          >
            Aprovar
          </button>
          <button
            onClick={() => setShowJustification(true)}
            className="h-12 w-[12rem] rounded-full bg-red-500 text-white hover:bg-red-400 transition-all"
          >
            Reprovar
          </button>
        </div>

        {showJustification && (
          <div className="mt-6 w-full">
            <textarea
              value={justification}
              onChange={(e) => {
                setJustification(e.target.value);
                setJustificationError(null);
              }}
              maxLength={1000}
              rows={4}
              className="w-full border border-gray-300 p-2 rounded-md"
              placeholder="Informe a justificativa para a reprovação"
            />
            {justificationError && (
              <p className="text-red-500 text-sm mt-2">{justificationError}</p>
            )}
            <div className="mt-4 flex gap-4">
              <button
                onClick={() => handleReprovar(justification)}
                className="h-12 w-[10rem] rounded-full bg-red-400 font-bold text-white hover:bg-red-300"
              >
                Confirmar Reprovação
              </button>
              <button
                onClick={() => setShowJustification(false)}
                className="h-12 w-[10rem] rounded-full bg-gray-400 font-bold text-white hover:bg-gray-300"
              >
                Cancelar
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
