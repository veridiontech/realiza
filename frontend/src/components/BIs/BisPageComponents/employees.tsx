import { useState } from "react";

export function Employees() {
  const count = 1134;
  const [showTooltip, setShowTooltip] = useState(false);

  return (
    <div
      style={{
        background: "#fff",
        borderRadius: 16,
        padding: 32,
        boxShadow: "0 4px 12px rgba(0,0,0,0.08)",
        width: 280,
        height: 160,
        textAlign: "center",
        margin: "0 10px",
        fontFamily: "Arial, sans-serif",
        position: "relative",
        display: "flex",
        flexDirection: "column",
        justifyContent: "center",
      }}
    >
      <div
        style={{
          fontWeight: "700",
          fontSize: 52,
          color: "#374151",
          marginBottom: 10,
        }}
      >
        {count.toLocaleString("pt-BR")}
      </div>

      <div
        style={{
          fontWeight: "500",
          fontSize: 16,
          color: "#374151",
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          gap: 6,
        }}
      >
        Colaboradores
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
              Número total de colaboradores ativos
            </div>
          )}
        </span>
      </div>
    </div>
  );
}
