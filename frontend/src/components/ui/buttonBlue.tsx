import { ButtonProps } from "@/types/buttonBlue";

export const ButtonBlue: React.FC<ButtonProps> = ({ onClick, children }) => {
  return (
    <button
      className="bg-realizaBlue text-white font-medium hover:bg-gray-600 hover:border-realizaBlue h-[3rem] rounded-md border-2 px-6 hover:text-white"
      onClick={onClick}
    >
      {children}
    </button>
  );
};
