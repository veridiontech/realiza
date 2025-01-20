// import { ButtonBlue } from "@/components/ui/buttonBlue";
// import { Pagination } from "@/components/ui/pagination";
// import { Settings2 } from "lucide-react";
// import { BranchType } from "@/types/branch";
// import { useEffect, useState } from "react";
// import { useBranches } from "@/hooks/gets/useBranch";
// import { Table } from "@/components/ui/table";

// const columns: {
//   key: keyof BranchType;
//   label: string;
//   render?: (value: any, row: BranchType) => JSX.Element;
//   className?: string;
// }[] = [
//   { key: "branchName", label: "Nome da Filial" },
//   { key: "cnpj", label: "CNPJ" },
//   { key: "adress", label: "Endereço" },
//   {
//     key: "id",
//     label: "Ações",
//     render: (value, row) => (
//       <button
//         className="ml-4 text-blue-500 hover:underline"
//         onClick={() => console.log(`Editar: ${row.branchName}`)}
//       >
//         <Settings2 />
//       </button>
//     ),
//   },
// ];

// export function Branch() {
//   const [currentPage, setCurrentPage] = useState(1);
//   const [isModalOpen, setIsModalOpen] = useState(false);
//   const itemsPerPage = 10;

//   const { branches, totalPages, loading, error, fetchBranches } = useBranches();

//   useEffect(() => {
//     fetchBranches(itemsPerPage, currentPage - 1);
//   }, [currentPage]);

//   const handlePageChange = (page: number) => {
//     if (page >= 1 && page <= totalPages) {
//       setCurrentPage(page);
//     }
//   };

//   return (
//     <div>
//       <div className="m-4 flex justify-center">
//         <div className="flex w-[90rem] flex-col rounded-lg bg-white p-4 shadow-md">
//           <div className="mb-6 flex items-center justify-between">
//             <h1 className="mb-6 text-xl font-semibold">Filiais</h1>
//             <ButtonBlue onClick={() => setIsModalOpen(true)}>
//               Adicionar Filial
//             </ButtonBlue>
//           </div>
//           {loading ? (
//             <div className="text-center text-gray-500">Carregando...</div>
//           ) : error ? (
//             <div className="text-center text-red-500">{error}</div>
//           ) : (
//             <Table<BranchType> data={branches} columns={columns} />
//           )}
//           <Pagination
//             currentPage={currentPage}
//             totalPages={totalPages}
//             onPageChange={handlePageChange}
//           />
//         </div>
//       </div>
//     </div>
//   );
// }
