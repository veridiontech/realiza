// import { useState, useEffect } from "react";
// import axios from "axios";
// import { Ban, CheckCircle, Rotate3D } from "lucide-react";
// import { ip } from "@/utils/ip";
// import {
//   Dialog,
//   DialogContent,
//   DialogHeader,
//   DialogTitle,
//   DialogTrigger,
// } from "@/components/ui/dialog";
// import { Button } from "@/components/ui/button";
// import { ColumnPanelControl } from "@/components/column-panel-control";
// import { ScrollArea } from "@/components/ui/scroll-area";
// import { CardPanelControlProvider } from "@/components/cardPanelControlProvider";

// interface Requester {
//   idUser: string;
//   firstName: string;
//   surname: string;
// }

// export interface Solicitation {
//   idSolicitation: string;
//   title: string;
//   details: string;
//   creationDate: string;
//   requester: Requester;
//   newUser: {
//       idUser: string;
//       firstName: string;
//       surname: string;
//       nameEnterprise?: string | undefined ;
//       cpf?: string | undefined;
//       email:string | undefined;
//   };
//   status: string;
// }

// interface ApiResponse {
//   content: Solicitation[];
//   totalPages: number;
// }

// export function ControlPanel() {
//   const [solicitations, setSolicitations] = useState<Solicitation[]>([]);

//   const fetchSolicitations = async () => {
//     try {
//       const tokenFromStorage = localStorage.getItem("tokenClient");
//       const response = await axios.get<ApiResponse>(
//         `${ip}/item-management/new-provider`,
//         {
//           headers: { Authorization: `Bearer ${tokenFromStorage}` }
//         }
//       );
//       setSolicitations(response.data.content);
//     } catch (err) {
//       console.error("Erro ao buscar solicitações:", err);
//     }
//   };

//   useEffect(() => {
//     fetchSolicitations();
//   }, []);

//   const removeSolicitation = (idSolicitation: string) => {
//     setSolicitations((prev) =>
//       prev.filter((s) => s.idSolicitation !== idSolicitation),
//     );
//   };

//   const countStatus = (status: "APPROVED" | "DENIED" | "PENDING") => {
//     return solicitations.filter(
//       (solicitation) => solicitation.status === status,
//     ).length;
//   };

//   return (
//     <div className="flex h-full w-full flex-col items-center justify-center gap-9 p-4">
//       <div className="relative bottom-[3vw] flex w-full flex-col items-center justify-center gap-9 rounded-md bg-white p-4 shadow-sm">
//         <div className="flex w-full flex-row items-center justify-between gap-4">
//           <div>
//             <h2 className="text-center text-lg font-semibold">
//               Painel de Controle
//             </h2>
//             <p className="text-[#2563EB]">
//               {countStatus("PENDING")} Solicitações
//             </p>
//           </div>
//           <Dialog>
//             <DialogTrigger asChild>
//               <Button className="bg-realizaBlue hidden md:block">
//                 Todas solicitações
//               </Button>
//             </DialogTrigger>
//             <DialogContent>
//               <DialogHeader>
//                 <DialogTitle>Are you absolutely sure?</DialogTitle>
//               </DialogHeader>
//             </DialogContent>
//           </Dialog>
//         </div>
//       </div>

//       <div className="relative bottom-[4vw] flex h-full w-full flex-col gap-6 rounded-md bg-white p-4 pt-16 shadow-sm">
//         <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 md:grid-cols-3">
//           <div>
//             <ColumnPanelControl
//               lenghtControl={countStatus("PENDING")}
//               title="Solicitações pendentes"
//               bgColor="bg-[#F9731640]"
//               textColor="text-[#F97316]"
//               icon={<Rotate3D className="text-[#F97316]" />}
//             />
//             <div>
//               <div className="bg-gray-100 p-8">
//                 <ScrollArea className="h-[40vh]">
//                   {solicitations
//                     .filter((solicitation) => solicitation.status === "PENDING")
//                     .map((solicitation) => (
//                       <CardPanelControlProvider
//                         key={solicitation.idSolicitation}
//                         data={solicitation}
//                         onActionCompleted={removeSolicitation}
//                         status=""
//                       />
//                     ))}
//                 </ScrollArea>
//               </div>
//             </div>
//           </div>
//           <div>
//             <ColumnPanelControl
//               lenghtControl={countStatus("APPROVED")}
//               title="Solicitações Confirmadas"
//               bgColor="bg-[#2563EB40]"
//               textColor="text-[#2563EB]"
//               icon={<CheckCircle className="text-[#2563EB]" />}
//             />
//             <div>
//               <div className="bg-gray-100 p-8 ">
//                 <ScrollArea className="h-[40vh] ">
//                   <div className=" flex flex-col gap-2">
//                     {solicitations
//                       .filter(
//                         (solicitation) => solicitation.status === "APPROVED",
//                       )
//                       .map((solicitation) => (
//                         <CardPanelControl
//                           key={solicitation.idSolicitation}
//                           data={solicitation}
//                           onActionCompleted={removeSolicitation}
//                         />
//                       ))}
//                   </div>
//                 </ScrollArea>
//               </div>
//             </div>
//           </div>

//           <div>
//             <ColumnPanelControl
//               lenghtControl={countStatus("DENIED")}
//               title="Solicitações Negadas"
//               bgColor="bg-[#FF464640]"
//               textColor="text-[#FF4646]"
//               icon={<Ban className="text-[#FF4646]" />}
//             />
//             <div>
//               <div className="bg-gray-100 p-8">
//                 <ScrollArea className="h-[40vh]">
//                   {solicitations
//                     .filter((solicitation) => solicitation.status === "DENIED")
//                     .map((solicitation) => (
//                       <CardPanelControl
//                         key={solicitation.idSolicitation}
//                         data={solicitation}
//                         onActionCompleted={removeSolicitation}
//                       />
//                     ))}
//                 </ScrollArea>
//               </div>
//             </div>
//           </div>
//         </div>
//         {/* <div className="grid grid-cols-1 gap-5 rounded-md p-4 shadow-sm sm:grid-cols-2 lg:grid-cols-4">
//           {solicitations.map((solicitation) => (
//             <CardPanelControl
//               key={solicitation.idSolicitation}
//               data={solicitation}
//               onActionCompleted={removeSolicitation}
//             />
//           ))}
//         </div> */}

//         {/* Controles de Paginação */}
//       </div>
//     </div>
//   );
// }
