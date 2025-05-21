import React, { useState } from "react";

export function ActiveContracts() {
  const count = 1126;
  const [showTooltip, setShowTooltip] = useState(false);

  return (
    <div
      style={{
        background: "#fff",
        borderRadius: 8,
        padding: 20,
        boxShadow: "0 2px 8px rgba(0,0,0,0.1)",
        width: 180,
        textAlign: "center",
        margin: "0 10px",
        fontFamily: "Arial, sans-serif",
        position: "relative",
      }}
    >
      <div
        style={{
          fontWeight: "600",
          fontSize: 14,
          color: "#333",
          marginBottom: 8,
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          gap: 6,
        }}
      >
        Total de Contratos Ativos
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
              Informação adicional
            </div>
          )}
        </span>
      </div>
      <div style={{ fontSize: 48, color: "#3b82f6", fontWeight: "bold" }}>
        {count.toLocaleString("pt-BR")}
      </div>
    </div>
  );
}
