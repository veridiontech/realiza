import { useState, useEffect } from "react";
import axios from "axios";
import { ip } from "@/utils/ip";
import { DataGrid, GridColDef } from "@mui/x-data-grid";

interface DocumentViewerProps {
  documentId: string;
  onClose: () => void;
}

export function DocumentViewer({ documentId, onClose }: DocumentViewerProps) {
  const [pdfUrl, setPdfUrl] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [logs] = useState([
    {
      id: 1,
      usuario: "monica",
      acao: "create",
      data: "27/02/2012 - 13:02:01",
      historico: "subiu o documento",
    },
    {
      id: 2,
      usuario: "gabriela",
      acao: "update",
      data: "27/02/2012 - 13:02:51",
      historico: "bloqueou o documento",
    },
    {
      id: 3,
      usuario: "monica",
      acao: "update",
      data: "27/02/2012 - 17:02:57",
      historico: "corrigiu o documento",
    },
    {
      id: 4,
      usuario: "larissa",
      acao: "update",
      data: "27/02/2012 - 17:02:45",
      historico: "aprovou o documento",
    },
  ]);

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

  // Definição das colunas para o DataGrid
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
                  maxHeight: "100%", // Define um tamanho máximo
                },
              }}
            />
          </div>
        </div>

        <div className="mt-6 flex justify-center">
          <div className="flex flex-row gap-6">
            <button className="h-12 w-[10rem] rounded-full bg-green-400 font-bold text-white hover:bg-green-300">
              Aprovar
            </button>
            <button className="h-12 w-[10rem] rounded-full bg-red-400 font-bold text-white hover:bg-yellow-300">
              Reprovar
            </button>
            {/* <button className="h-12 w-[10rem] rounded-full bg-red-400 font-bold text-white hover:bg-red-300">
              Bloquear
            </button> */}
          </div>
        </div>
      </div>
    </div>
  );
}
