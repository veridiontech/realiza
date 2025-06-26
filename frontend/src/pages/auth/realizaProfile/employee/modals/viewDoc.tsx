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
  const [logs] = useState([]);
  const [showJustification, setShowJustification] = useState(false);
  const [justification, setJustification] = useState("");
  const [justificationError, setJustificationError] = useState<string | null>(null);

  const handleChangeStatus = async (status: string, notes: string) => {
    try {
      const token = localStorage.getItem("tokenClient");
      const response = await axios.post(
        `${ip}/document/${documentId}/change-status`,
        {
          status,
          notes
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      console.log("status:", response.data);
      toast(`Documento ${status === "APROVADO" ? "aprovado" : "reprovado"} com sucesso!`);
      if (onStatusChange) {
        onStatusChange(documentId, status);
      }
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

    try {
      const token = localStorage.getItem("tokenClient");
      const response = await axios.post(
        `${ip}/document/${documentId}/change-status`,
        {
          status: "REPROVADO",
          notes
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      console.log("status:", response.data);
      toast("Documento reprovado com sucesso!");
      if (onStatusChange) {
        onStatusChange(documentId, "REPROVADO");
      }
      onClose();
    } catch (err) {
      console.error(err);
      toast("Erro ao atualizar o status do documento.");
    }
  };

  useEffect(() => {
    const fetchFileData = async () => {
      setLoading(true);
      try {
        const tokenFromStorage = localStorage.getItem("tokenClient");
        const res = await axios.get(`${ip}/document/employee/${documentId}`,
          {
            headers: { Authorization: `Bearer ${tokenFromStorage}` }
          }
        );
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

    fetchFileData();
  }, [documentId]);

  const columns: GridColDef[] = [
    { field: "usuario", headerName: "Usuário", flex: 1 },
    { field: "acao", headerName: "Ação", flex: 1 },
    { field: "data", headerName: "Data", flex: 1 },
    { field: "historico", headerName: "Histórico", flex: 2 },
  ];

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center gap-8 bg-black bg-opacity-50">
      <div className="relative w-[50rem] bg-white p-4 shadow-lg">
        <button
          className="absolute right-2 top-2 text-gray-500 hover:text-gray-800"
          onClick={onClose}
        >
          ✖
        </button>

        <h2 className="mb-4 text-center text-lg font-bold">
          Visualizar Documento
        </h2>

        {loading && <p>Carregando...</p>}
        {error && <p className="text-red-500">{error}</p>}

        {pdfUrl ? (
          <iframe src={pdfUrl} width="100%" height="500px" />
        ) : (
          !loading && <p>Nenhum documento carregado.</p>
        )}
      </div>

      <div className="relative flex w-[50rem] flex-col bg-white p-4 shadow-lg">
        <button
          className="absolute right-2 top-2 text-gray-500 hover:text-gray-800"
          onClick={onClose}
        >
          ✖
        </button>

        <div className="mb-6 flex flex-col items-center">
          <h2 className="text-lg font-bold">Verificação do documento</h2>
        </div>

        <div className="flex h-[350px] w-full flex-col items-start">
          <div className="h-full w-full overflow-auto">
            <DataGrid
              rows={logs}
              columns={columns}
              pageSizeOptions={[5, 10]}
              autoHeight
              disableRowSelectionOnClick
              className="border border-gray-300"
              sx={{
                "& .MuiDataGrid-root": {
                  minWidth: "100%",
                  maxHeight: "100%",
                },
              }}
            />
          </div>
        </div>

        <div className="mt-6 flex justify-center">
          <div className="flex flex-row gap-6">
            <button
              onClick={() => handleChangeStatus("APROVADO", "")}
              className="h-12 w-[10rem] rounded-full bg-green-400 font-bold text-white hover:bg-green-300"
            >
              Aprovar
            </button>
            <button
              onClick={() => setShowJustification(true)}
              className="h-12 w-[10rem] rounded-full bg-red-400 font-bold text-white hover:bg-yellow-300"
            >
              Reprovar
            </button>
            {showJustification && (
              <div className="mt-4">
                <textarea
                  value={justification}
                  onChange={(e) => setJustification(e.target.value)}
                  maxLength={1000}
                  rows={4}
                  className="w-full border border-gray-300 p-2 rounded-md"
                  placeholder="Informe a justificativa para a reprovação"
                />
                {justificationError && (
                  <p className="text-red-500 text-sm">{justificationError}</p>
                )}
                <div className="mt-4 flex gap-4">
                  <button
                    onClick={() => handleReprovar(justification)}
                    className="h-12 w-[10rem] rounded-full bg-red-400 font-bold text-white hover:bg-yellow-300"
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
      </div>
    </div>
  );
}
