import { TableProps } from "@/types/table";
import { ReactNode } from "react";

export const Table = <T,>({ data, columns }: TableProps<T>) => {
  return (
    <div className="mx-10 mt-20 overflow-hidden rounded-lg border border-gray-300 shadow-md">
      <table className="w-full table-auto border-collapse">
        <thead className="bg-blue-100 text-blue-800">
          <tr>
            {columns.map((col) => (
              <th key={String(col.key)} className="px-4 py-2 text-left">
                {col.label}
              </th>
            ))}
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
              {columns.map((col) => (
                <td
                  key={String(col.key)}
                  className={`px-4 py-2 ${col.className || ""}`}
                >
                  {col.render
                    ? col.render(row[col.key], row)
                    : (row[col.key] as ReactNode)}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};
