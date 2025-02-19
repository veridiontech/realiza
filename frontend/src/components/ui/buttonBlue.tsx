import { ButtonProps } from "@/types/buttonBlue";

export const ButtonBlue: React.FC<ButtonProps> = ({ onClick, children }) => {
  return (
    <button
      className="border-realizaBlue hover:bg-realizaBlue hover:border-realizaBlue h-[3rem] rounded-md border-2 px-6 text-black hover:text-white"
      onClick={onClick}
    >
      {children}
    </button>
  );
};
