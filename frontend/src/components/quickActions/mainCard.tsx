import React from "react";

interface MainCardProps {
  title: string;
  value: number | string;
  icon: React.ReactNode;
}

export const MainCard: React.FC<MainCardProps> = ({ title, value, icon }) => {
  return (
    <div className="flex flex-col items-center justify-center rounded-lg border border-gray-200 bg-white p-4 shadow-sm transition-shadow hover:shadow-md">
      <div className="mb-4 flex h-12 w-12 items-center justify-center rounded-full bg-blue-100 text-blue-500">
        {icon}
      </div>
      <div className="text-center">
        <h2 className="text-sm font-medium text-gray-600">{title}</h2>
        <p className="text-2xl font-bold text-gray-900">{value}</p>
      </div>
    </div>
  );
};
