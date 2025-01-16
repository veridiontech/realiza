import { PaginationProps } from "@/types/pagination";

export const Pagination: React.FC<PaginationProps> = ({
  currentPage,
  totalPages,
  onPageChange,
}) => {
  return (
    <div className="mx-10 my-6 mt-4 flex items-center justify-between">
      <span className="text-sm text-gray-600">
        Página {currentPage} de {totalPages}
      </span>
      <div className="flex gap-2">
        <button
          onClick={() => onPageChange(currentPage - 1)}
          disabled={currentPage === 1}
          className={`rounded-md border px-4 py-2 ${
            currentPage === 1
              ? "cursor-not-allowed text-gray-400"
              : "text-blue-500 hover:bg-blue-100"
          }`}
        >
          {"<"}
        </button>
        <button
          onClick={() => onPageChange(currentPage + 1)}
          disabled={currentPage === totalPages}
          className={`rounded-md border px-4 py-2 ${
            currentPage === totalPages
              ? "cursor-not-allowed text-gray-400"
              : "text-blue-500 hover:bg-blue-100"
          }`}
        >
          {">"}
        </button>
      </div>
    </div>
  );
};
