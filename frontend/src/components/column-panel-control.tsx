import { ReactNode } from "react";
import { Oval } from "react-loader-spinner";

interface ColumnPanelControlProps {
  title: string;
  bgColor: string;
  textColor: string;
  icon: ReactNode;
  lenghtControl: number;
  isLoading: boolean;
}

export function ColumnPanelControl({
  title,
  bgColor,
  textColor,
  icon,
  lenghtControl,
  isLoading,
}: ColumnPanelControlProps) {
  if (isLoading) {
    return (
      <div className={`w-full rounded-md p-4 ${bgColor} `}>
        <div className="flex items-center justify-between">
          <h2 className={`font-semibold ${textColor}`}>{title}</h2>
          <div className={`flex items-center gap-2 rounded-lg p-2 ${bgColor}`}>
            {icon}
            <span className={`font-medium ${textColor}`}>
              <Oval
                visible={true}
                height="15"
                width="15"
                color="#4fa94d"
                ariaLabel="oval-loading"
                wrapperStyle={{}}
                wrapperClass=""
              />
            </span>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className={`w-full rounded-md p-4 ${bgColor} `}>
      <div className="flex items-center justify-between">
        <h2 className={`font-semibold ${textColor}`}>{title}</h2>
        <div className={`flex items-center gap-2 rounded-lg p-2 ${bgColor}`}>
          {icon}
          <span className={`font-medium ${textColor}`}>{lenghtControl}</span>
        </div>
      </div>
    </div>
  );
}
