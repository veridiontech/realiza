import { ButtonProps } from "@/types/buttonBlue";

export const ButtonBlue: React.FC<ButtonProps> = ({ onClick, children }) => {
  return (
    <button
      className="h-[3rem] rounded-md border-2 border-blue-300 px-6 text-black hover:border-blue-600 hover:bg-blue-300 hover:text-white"
      onClick={onClick}
    >
      {children}
    </button>
  );
};
