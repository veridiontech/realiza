export type Employee = {
  id: number;
  name: string;
  status: "Ativo" | "Desligado";
  idContracts?: number[];
};
