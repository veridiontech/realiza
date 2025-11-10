import React from "react";

interface SkeletonLoaderProps {
  type?: "card" | "table" | "chart" | "text";
  count?: number;
  className?: string;
}

/**
 * Componente de Skeleton Loader para feedback visual durante carregamento
 * Melhora a percepção de performance e UX
 */
export const SkeletonLoader: React.FC<SkeletonLoaderProps> = ({
  type = "card",
  count = 1,
  className = "",
}) => {
  const renderSkeleton = () => {
    switch (type) {
      case "card":
        return (
          <div
            className={`animate-pulse bg-gray-200 rounded-lg p-6 ${className}`}
          >
            <div className="h-4 bg-gray-300 rounded w-1/4 mb-4"></div>
            <div className="h-8 bg-gray-300 rounded w-1/2"></div>
          </div>
        );

      case "table":
        return (
          <div className={`animate-pulse bg-white rounded-lg ${className}`}>
            <div className="p-4 border-b border-gray-200">
              <div className="h-4 bg-gray-200 rounded w-1/3"></div>
            </div>
            {Array.from({ length: 5 }).map((_, i) => (
              <div key={i} className="p-4 border-b border-gray-100">
                <div className="flex gap-4">
                  <div className="h-4 bg-gray-200 rounded w-1/4"></div>
                  <div className="h-4 bg-gray-200 rounded w-1/4"></div>
                  <div className="h-4 bg-gray-200 rounded w-1/4"></div>
                  <div className="h-4 bg-gray-200 rounded w-1/4"></div>
                </div>
              </div>
            ))}
          </div>
        );

      case "chart":
        return (
          <div
            className={`animate-pulse bg-white rounded-lg p-6 ${className}`}
          >
            <div className="h-4 bg-gray-200 rounded w-1/3 mb-6"></div>
            <div className="space-y-3">
              {Array.from({ length: 4 }).map((_, i) => (
                <div key={i} className="flex items-end gap-2 h-32">
                  <div
                    className="bg-gray-200 rounded w-full"
                    style={{ height: `${Math.random() * 80 + 20}%` }}
                  ></div>
                </div>
              ))}
            </div>
          </div>
        );

      case "text":
        return (
          <div className={`animate-pulse space-y-2 ${className}`}>
            <div className="h-4 bg-gray-200 rounded w-full"></div>
            <div className="h-4 bg-gray-200 rounded w-5/6"></div>
            <div className="h-4 bg-gray-200 rounded w-4/6"></div>
          </div>
        );

      default:
        return null;
    }
  };

  return (
    <>
      {Array.from({ length: count }).map((_, index) => (
        <React.Fragment key={index}>{renderSkeleton()}</React.Fragment>
      ))}
    </>
  );
};

/**
 * Skeleton específico para cards de métricas do Dashboard
 */
export const MetricCardSkeleton: React.FC = () => {
  return (
    <div className="animate-pulse bg-white rounded-lg shadow p-6">
      <div className="flex items-center justify-between mb-4">
        <div className="h-4 bg-gray-200 rounded w-1/2"></div>
        <div className="h-8 w-8 bg-gray-200 rounded-full"></div>
      </div>
      <div className="h-8 bg-gray-300 rounded w-1/3 mb-2"></div>
      <div className="h-3 bg-gray-200 rounded w-1/4"></div>
    </div>
  );
};

/**
 * Skeleton para gráficos de pizza
 */
export const PieChartSkeleton: React.FC = () => {
  return (
    <div className="animate-pulse bg-white rounded-lg shadow p-6">
      <div className="h-4 bg-gray-200 rounded w-1/3 mb-6"></div>
      <div className="flex items-center justify-center">
        <div className="h-48 w-48 bg-gray-200 rounded-full"></div>
      </div>
      <div className="mt-6 space-y-2">
        {Array.from({ length: 4 }).map((_, i) => (
          <div key={i} className="flex items-center gap-2">
            <div className="h-3 w-3 bg-gray-300 rounded-full"></div>
            <div className="h-3 bg-gray-200 rounded flex-1"></div>
          </div>
        ))}
      </div>
    </div>
  );
};

/**
 * Skeleton para tabela de ranking
 */
export const RankingTableSkeleton: React.FC = () => {
  return (
    <div className="animate-pulse bg-white rounded-lg shadow">
      <div className="p-4 border-b border-gray-200">
        <div className="h-5 bg-gray-200 rounded w-1/4"></div>
      </div>
      <div className="overflow-x-auto">
        <table className="w-full">
          <thead className="bg-gray-50">
            <tr>
              {Array.from({ length: 5 }).map((_, i) => (
                <th key={i} className="p-4">
                  <div className="h-4 bg-gray-200 rounded"></div>
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {Array.from({ length: 8 }).map((_, i) => (
              <tr key={i} className="border-b border-gray-100">
                {Array.from({ length: 5 }).map((_, j) => (
                  <td key={j} className="p-4">
                    <div className="h-4 bg-gray-200 rounded"></div>
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
