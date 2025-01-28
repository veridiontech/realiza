export type Contract = {
  id: string;
  ref: string;
  project: string;
  clientFinal: string;
  client: string;
  providerSupplierName: string;
  serviceType: string;
  serviceDuration: string;
  serviceName: string;
  contractReference: string;
  description: string;
  allocatedLimit: number;
  responsible: string;
  expenseType: string;
  startDate: string;
  endDate: string;
  activities: any[]; // Ajuste o tipo se houver detalhes de atividades
  requirements: any[]; // Ajuste o tipo se houver detalhes de requisitos
};
