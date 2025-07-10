import { useState } from "react";
import { HousePlus } from "lucide-react";

interface AllocatedEmployeesProps {
  count?: number;
}

export function AllocatedEmployees({ count }: AllocatedEmployeesProps) {
  const [showTooltip, setShowTooltip] = useState(false);

  console.log("count dentro do componente:", count);

  return (
    <div
      style={{
        background: "#fff",
        borderRadius: 16,
        padding: 20,
        boxShadow: "0 4px 12px rgba(0,0,0,0.08)",
        width: 300,
        height: 160,
        textAlign: "center",
        margin: "0 10px",
        fontFamily: "Arial, sans-serif",
        position: "relative",
        display: "flex",
        flexDirection: "row",
        justifyContent: "space-evenly",
      }}
    >
      <div
        style={{
          justifyContent: "start",
          alignContent: "center",
        }}
      >
        <HousePlus height={80} width={70} color="#cccccc" />
      </div>
      <div
        style={{
          justifyContent: "start",
          alignContent: "center",
        }}
      >
        <div
          style={{
            fontWeight: "500",
            fontSize: 12,
            color: "#374151",
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            gap: 6,
          }}
        >
          Funcionários Alocados
          <span
            onMouseEnter={() => setShowTooltip(true)}
            onMouseLeave={() => setShowTooltip(false)}
            style={{
              background: "#ccc",
              borderRadius: "50%",
              width: 16,
              height: 16,
              display: "inline-flex",
              alignItems: "center",
              justifyContent: "center",
              fontSize: 12,
              cursor: "default",
              userSelect: "none",
              position: "relative",
            }}
          >
            i
            {showTooltip && (
              <div
                style={{
                  position: "absolute",
                  bottom: "130%",
                  left: "50%",
                  transform: "translateX(-50%)",
                  backgroundColor: "#333",
                  color: "#fff",
                  padding: "6px 8px",
                  borderRadius: 4,
                  whiteSpace: "nowrap",
                  fontSize: 12,
                  zIndex: 10,
                  boxShadow: "0 0 5px rgba(0,0,0,0.3)",
                  pointerEvents: "none",
                  userSelect: "none",
                }}
              >
                Funcionários atualmente alocados em contratos
              </div>
            )}
          </span>
        </div>

        <div
          style={{
            fontWeight: "700",
            fontSize: 40,
            color: "#374151", // azul acinzentado
            display: "flex",
          }}
        >
          {count?.toLocaleString("pt-BR")}
        </div>
      </div>
    </div>
  );
}
