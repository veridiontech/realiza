import { ReactNode } from "react";

interface ColumnPanelControlProps {
  title: string;
  bgColor: string;
  textColor: string;
  icon: ReactNode;
  lenghtControl: number;
}

export function ColumnPanelControl({
  title,
  bgColor,
  textColor,
  icon,
  lenghtControl,
}: ColumnPanelControlProps) {
  return (
<div className={`rounded-md p-4 w-full ${bgColor}`}>
      <div className="flex items-center justify-between ">
        <h2 className={`font-semibold ${textColor}`}>{title}</h2>
        <div className={`flex items-center gap-2 rounded-lg p-2 ${bgColor}`}>

          {icon}
          <span className={`font-medium ${textColor}`}>{lenghtControl}</span>
        </div>
      </div>
    </div>
  );
}
