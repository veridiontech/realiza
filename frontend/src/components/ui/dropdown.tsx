import { useState } from "react";
import { Option } from "@/types/dropDown";

interface DropdownProps {
  options: Option[]; // Presume que Option possui id: number
  selectedOption: number | null; // Ajustado para ser consistente com o tipo de option.id
  onSelect: (option: Option) => void;
  placeholder: string;
}

export function Dropdown({
  options,
  selectedOption,
  onSelect,
  placeholder,
}: DropdownProps) {
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);

  const getSelectedOptionName = (): string => {
    const selected = options.find((option) => option.id === selectedOption);
    return selected ? selected.name : placeholder;
  };

  return (
    <div className="relative">
      <button
        onClick={() => setIsDropdownOpen(!isDropdownOpen)}
        className="bg-realizaBlue text-realizaBlue flex w-full items-center justify-between rounded-lg p-3 font-medium focus:outline-none"
      >
        {getSelectedOptionName()}
        <span>{isDropdownOpen ? "▲" : "▼"}</span>
      </button>

      {isDropdownOpen && (
        <div className="absolute z-10 mt-2 max-h-40 w-full overflow-y-auto rounded-lg border border-gray-300 bg-white shadow-lg">
          {options.length > 0 ? (
            options.map((option) => (
              <div
                key={option.id}
                onClick={() => {
                  onSelect(option);
                  setIsDropdownOpen(false);
                }}
                className="hover:bg-realizaBlue cursor-pointer p-2"
              >
                {option.name}
              </div>
            ))
          ) : (
            <div className="p-2 text-gray-500">Nenhuma opção encontrada</div>
          )}
        </div>
      )}
    </div>
  );
}
