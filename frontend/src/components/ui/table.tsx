import React from "react";
import { ScrollText } from "lucide-react";

type TableRow = {
  category: string;
  corporateReason: string;
  enterprise: string;
  cnpj: string;
  units: string;
  options: JSX.Element;
};

type TableProps = {
  data: TableRow[];
};

export const Table: React.FC<TableProps> = ({ data }) => {
  return (
    <div className="mx-10 mt-20 overflow-hidden rounded-lg border border-gray-300 shadow-md">
      <table className="w-full table-auto border-collapse">
        <thead className="bg-blue-100 text-blue-800">
          <tr>
            <th className="px-4 py-2 text-left">Categoria</th>
            <th className="px-4 py-2 text-left">Razão Social</th>
            <th className="px-4 py-2 text-left">Empresa</th>
            <th className="px-4 py-2 text-left">CNPJ</th>
            <th className="px-4 py-2 text-left">Unidades</th>
            <th className="px-4 py-2 text-center">Opções</th>
          </tr>
        </thead>
        <tbody>
          {data.map((row, index) => (
            <tr
              key={index}
              className={`border-t ${
                index % 2 === 0 ? "bg-white" : "bg-gray-50"
              }`}
            >
              <td className="px-4 py-2">{row.category}</td>
              <td className="px-4 py-2">{row.corporateReason}</td>
              <td className="px-4 py-2">{row.enterprise}</td>
              <td className="px-4 py-2">{row.cnpj}</td>
              <td className="px-4 py-2">{row.units}</td>
              <td className="px-4 py-2 text-center text-blue-500 hover:underline">
                <button>
                  <ScrollText />
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};
