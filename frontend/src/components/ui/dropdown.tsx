import { useState } from "react";

type Option = {
  id: number;
  name: string;
};

interface DropdownProps {
  options: Option[];
  selectedOption: string | null;
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

  return (
    <div className="relative">
      <button
        onClick={() => setIsDropdownOpen(!isDropdownOpen)}
        className="flex w-full items-center justify-between rounded-lg bg-blue-100 p-3 font-medium text-blue-600 focus:outline-none"
      >
        {selectedOption || placeholder}
        <span>{isDropdownOpen ? "▲" : "▼"}</span>
      </button>

      {isDropdownOpen && (
        <div className="absolute z-10 mt-2 max-h-40 w-full overflow-y-auto rounded-lg border border-gray-300 bg-white shadow-lg">
          {options.length > 0 ? (
            options.map((option, index) => (
              <div
                key={option.id}
                onClick={() => {
                  onSelect(option);
                  setIsDropdownOpen(false);
                }}
                className="cursor-pointer p-2 hover:bg-blue-100"
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
