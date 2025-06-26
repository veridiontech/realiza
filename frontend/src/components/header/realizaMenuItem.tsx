import { ReactNode } from "react";
import { Link } from "react-router-dom";
import { Button } from "../ui/button";

interface RealizaMenuItemProps {
  to: string;
  icon: ReactNode;
  label: string;
  menuKey: string;
  activeMenuKey?: string;
  setActiveMenuKey?: (key: string) => void;
  onClick?: () => void;
}

export function RealizaMenuItem({
  to,
  icon,
  label,
  menuKey,
  setActiveMenuKey,
  onClick,
}: RealizaMenuItemProps) {
  const isActive = location.pathname.includes(to);

  const handleClick = () => {
    setActiveMenuKey?.(menuKey); // Marca esse item como ativo
    onClick?.(); // Fecha o menu (onClose)
  };

  return (
    <Link to={to} onClick={handleClick}>
      <Button
        variant="ghost"
        className={`mt-2 w-full justify-start px-4 py-2 ${
          isActive ? "bg-[#C0B15B33]" : "hover:bg-neutral-500"
        }`}
      >
        <div className={"text-[#C0B15B]"}>{icon}</div>
        <span
          className={`ml-2 text-sm font-medium ${
            isActive ? "text-[#C0B15B]" : "text-white"
          }`}
        >
          {label}
        </span>
      </Button>
    </Link>
  );
}
