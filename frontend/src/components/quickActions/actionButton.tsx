import React from "react";

interface ActionButtonProps {
  label: string;
  icon: React.ReactNode;
}

export const ActionButton: React.FC<ActionButtonProps> = ({ label, icon }) => {
  return (
    <button className="flex w-full items-center justify-between rounded-lg border border-gray-200 bg-white p-3 shadow-sm transition-colors hover:bg-blue-50">
      <span className="text-sm font-medium text-gray-700">{label}</span>
      <span className="text-blue-500">{icon}</span>
    </button>
  );
};
