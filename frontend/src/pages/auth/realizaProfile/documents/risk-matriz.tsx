import { useState } from "react";

type Riscos = {
  [key: string]: boolean;
};

interface Document {
  idDocumentMatrix: string;
  name: string;
}

interface RiskRow {
  id: string;
  descricao: string;
  validade: number;
  riscos: Riscos;
}

function RiskMatriz() {
  // Dados falsos estáticos
  const fakeDocuments: Document[] = [
    { idDocumentMatrix: "1", name: "Documento A" },
    { idDocumentMatrix: "2", name: "Documento B" },
    { idDocumentMatrix: "3", name: "Documento C" },
  ];

  const [rows, setRows] = useState<RiskRow[]>(
    fakeDocuments.map((doc) => ({
      id: doc.idDocumentMatrix,
      descricao: doc.name,
      validade: 1,
      riscos: {
        baixo_8h: false,
        baixo_1m: false,
        baixo_6m: false,
        moderado_8h: false,
        moderado_1m: false,
        moderado_6m: false,
        alto_8h: false,
        alto_1m: false,
        alto_6m: false,
      },
    })),
  );

  // Atualiza o valor da validade
  const handleValidadeChange = (index: number, value: string) => {
    const newRows = [...rows];
    newRows[index].validade = Number(value);
    setRows(newRows);
  };

  // Alterna os checkboxes de risco
  const handleCheckboxChange = (index: number, riskType: string) => {
    const newRows = [...rows];
    newRows[index].riscos[riskType] = !newRows[index].riscos[riskType];
    setRows(newRows);
  };

  return (
    <div className="w-full overflow-auto bg-white p-6 shadow-lg">
      <table className="min-w-full border-collapse">
        {/* Cabeçalho */}
        <thead>
          <tr className="bg-blue-100 text-left">
            <th className="w-40 border p-2 text-center">Descrição</th>
            <th className="w-20 border p-2 text-center">Validade</th>
            <th className="border bg-green-100 p-2 text-center" colSpan={3}>
              Risco Baixo
            </th>
            <th className="border bg-yellow-100 p-2 text-center" colSpan={3}>
              Risco Moderado
            </th>
            <th className="border bg-red-100 p-2 text-center" colSpan={3}>
              Risco Alto
            </th>
          </tr>
          <tr className="bg-gray-50">
            <th colSpan={2}></th>
            <th className="border bg-green-100 p-2 text-center">{"< 8h"}</th>
            <th className="border bg-green-100 p-2 text-center">{"< 1m"}</th>
            <th className="border bg-green-100 p-2 text-center">{"< 6m"}</th>
            <th className="border bg-yellow-100 p-2 text-center">{"< 8h"}</th>
            <th className="border bg-yellow-100 p-2 text-center">{"< 1m"}</th>
            <th className="border bg-yellow-100 p-2 text-center">{"< 6m"}</th>
            <th className="border bg-red-100 p-2 text-center">{"< 8h"}</th>
            <th className="border bg-red-100 p-2 text-center">{"< 1m"}</th>
            <th className="border bg-red-100 p-2 text-center">{"< 6m"}</th>
          </tr>
        </thead>

        {/* Corpo da tabela */}
        <tbody>
          {rows.map((row, index) => (
            <tr key={row.id} className="border">
              <td className="w-40 border p-2">{row.descricao}</td>
              <td className="w-20 border p-2 text-center">
                <input
                  type="number"
                  min="1"
                  value={row.validade}
                  onChange={(e) => handleValidadeChange(index, e.target.value)}
                  className="w-16 border p-1 text-center"
                />
              </td>

              {/* Riscos Baixo */}
              <td className="border bg-green-50 p-2 text-center">
                <input
                  type="checkbox"
                  checked={!!row.riscos["baixo_8h"]}
                  onChange={() => handleCheckboxChange(index, "baixo_8h")}
                  className="h-5 w-5"
                />
              </td>
              <td className="border bg-green-50 p-2 text-center">
                <input
                  type="checkbox"
                  checked={!!row.riscos["baixo_1m"]}
                  onChange={() => handleCheckboxChange(index, "baixo_1m")}
                  className="h-5 w-5"
                />
              </td>
              <td className="border bg-green-50 p-2 text-center">
                <input
                  type="checkbox"
                  checked={!!row.riscos["baixo_6m"]}
                  onChange={() => handleCheckboxChange(index, "baixo_6m")}
                  className="h-5 w-5"
                />
              </td>

              {/* Riscos Moderado */}
              <td className="border bg-yellow-50 p-2 text-center">
                <input
                  type="checkbox"
                  checked={!!row.riscos["moderado_8h"]}
                  onChange={() => handleCheckboxChange(index, "moderado_8h")}
                  className="h-5 w-5"
                />
              </td>
              <td className="border bg-yellow-50 p-2 text-center">
                <input
                  type="checkbox"
                  checked={!!row.riscos["moderado_1m"]}
                  onChange={() => handleCheckboxChange(index, "moderado_1m")}
                  className="h-5 w-5"
                />
              </td>
              <td className="border bg-yellow-50 p-2 text-center">
                <input
                  type="checkbox"
                  checked={!!row.riscos["moderado_6m"]}
                  onChange={() => handleCheckboxChange(index, "moderado_6m")}
                  className="h-5 w-5"
                />
              </td>

              {/* Riscos Alto */}
              <td className="border bg-red-50 p-2 text-center">
                <input
                  type="checkbox"
                  checked={!!row.riscos["alto_8h"]}
                  onChange={() => handleCheckboxChange(index, "alto_8h")}
                  className="h-5 w-5"
                />
              </td>
              <td className="border bg-red-50 p-2 text-center">
                <input
                  type="checkbox"
                  checked={!!row.riscos["alto_1m"]}
                  onChange={() => handleCheckboxChange(index, "alto_1m")}
                  className="h-5 w-5"
                />
              </td>
              <td className="border bg-red-50 p-2 text-center">
                <input
                  type="checkbox"
                  checked={!!row.riscos["alto_6m"]}
                  onChange={() => handleCheckboxChange(index, "alto_6m")}
                  className="h-5 w-5"
                />
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function App() {
  return (
    <div className="min-h-screen bg-gray-100 p-4">
      <h1 className="mb-6 text-center text-3xl font-bold">Matriz de Riscos</h1>
      <RiskMatriz />
    </div>
  );
}

export default App;
