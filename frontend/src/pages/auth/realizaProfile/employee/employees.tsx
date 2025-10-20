import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import axios from "axios";
import { ip } from "@/utils/ip";
import { propsSupplier } from "@/types/interfaces";
import { TableEmployee } from "./tableEmployee";
import { useBranch } from "@/context/Branch-provider";
import { useSupplier } from "@/context/Supplier-context";
import { useUser } from "@/context/user-provider";
import { NewModalCreateEmployee } from "./modals/newModalCreateEmployee";
import { ManageEmployeesModal } from "./modals/ManageEmployeesModal";
import { BriefcaseBusiness, Cog, Users2Icon } from "lucide-react";

export const EmployeesTable = (): JSX.Element => {
  const [selectedTab, setSelectedTab] = useState<"fornecedor" | "subcontratado">("fornecedor");
  const [loading, setLoading] = useState(false);
  const [error] = useState<string | null>(null);

  const { selectedBranch } = useBranch();
  const { user } = useUser();
  const { supplier, setSupplier } = useSupplier();

  const [suppliersList, setSuppliersList] = useState<propsSupplier[]>([]);
  const [selectedSupplier, setSelectedSupplier] = useState<string | null>(null);
  const [getUniqueSupplier, setGetUniqueSupplier] = useState<propsSupplier | null>(null);

  const [getSubcontractorList, setGetSubcontractorList] = useState<any[]>([]);
  const [selectedSubcontractor, setSelectedSubcontractor] = useState<string | null>(null);
  const [selectedSubcontractorName, setSelectedSubcontractorName] = useState<string>("");

  const isSupplierResponsible = user?.role === "ROLE_SUPPLIER_RESPONSIBLE";

  // ------ API calls
  const suppliers = async () => {
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res = await axios.get(
        `${ip}/supplier/filtered-client?idSearch=${selectedBranch?.idBranch}`,
        { headers: { Authorization: `Bearer ${tokenFromStorage}` } }
      );
      setSuppliersList(res.data.content);
    } catch (error) {
      console.error("Erro ao encontrar fornecedor:", error);
    }
  };

  const uniqueSupplier = async () => {
    if (!selectedSupplier) return;
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const res = await axios.get(`${ip}/supplier/${selectedSupplier}`, {
        headers: { Authorization: `Bearer ${tokenFromStorage}` },
      });
      setGetUniqueSupplier(res.data);
    } catch (err) {
      console.log("erro ao buscar unico supplier:", err);
    }
  };

  const getSubcontractor = async () => {
    // Log 1: Verifica se a função foi chamada e se selectedSupplier existe
    console.log("getSubcontractor chamado. selectedSupplier:", selectedSupplier);
    
    if (!selectedSupplier) {
      // Log 2: Caso não haja selectedSupplier
      console.log("selectedSupplier é nulo. Não dispara a requisição de subcontratados.");
      setGetSubcontractorList([]);
      return;
    }
    
    setLoading(true);
      
    try {
      const tokenFromStorage = localStorage.getItem("tokenClient");
      const url = `${ip}/subcontractor/filtered-supplier?idSearch=${selectedSupplier}`;
      
      // Log 3: Loga a URL e o token (parcialmente) para debug
      console.log("URL da Requisição de Subcontratados:", url);
      console.log("Token Presente:", !!tokenFromStorage);
      
      const res = await axios.get(url, { 
        headers: { Authorization: `Bearer ${tokenFromStorage}` } 
      });
      
      // Log 4: Loga o status e os dados da resposta
      console.log("Resposta da API de Subcontratados - Status:", res.status);
      console.log("Resposta da API de Subcontratados - Data:", res.data);
      console.log("Conteúdo da lista (res.data.content):", res.data.content);
      
      if (res.data && Array.isArray(res.data.content)) {
        setGetSubcontractorList(res.data.content);
        // Log 5: Sucesso ao carregar a lista
        console.log(`Subcontratados carregados com sucesso: ${res.data.content.length} itens.`);
      } else {
        // Log 6: Se o formato dos dados não for o esperado (não tem 'content' ou não é array)
        console.warn("Formato de dados inesperado para subcontratados. Resposta:", res.data);
        setGetSubcontractorList([]);
      }

    } catch (err) {
      // Log 7: Captura e loga o erro da requisição
      console.error("ERRO ao buscar subcontratados:", err);
      if (axios.isAxiosError(err) && err.response) {
        console.error("Detalhes do Erro (Status/Data):", err.response.status, err.response.data);
      }
      setGetSubcontractorList([]); // Limpa a lista em caso de erro

    } finally {
      setLoading(false);
      console.log("Finalizando requisição de subcontratados.");
    }
  };

  // ------ Effects
  useEffect(() => {
    if (selectedBranch?.idBranch) {
      suppliers();
    }
  }, [selectedBranch?.idBranch]);

  useEffect(() => {
    // Log 8: Disparado ao selecionar ou mudar o Fornecedor
    console.log("useEffect [selectedSupplier] disparado. Novo selectedSupplier:", selectedSupplier);
    
    if (selectedSupplier) {
      uniqueSupplier();
      getSubcontractor();
      // reset seleção de subcontratado quando trocar fornecedor
      setSelectedSubcontractor(null);
      setSelectedSubcontractorName("");
    } else {
      // Limpa a lista se não houver fornecedor selecionado
      setGetSubcontractorList([]);
    }
  }, [selectedSupplier]);

  // ------ Helpers
  // ... restante do código ...

  const targetSupplierId = isSupplierResponsible ? (supplier?.idProvider ?? null) : selectedSupplier;
  const targetSupplierName = isSupplierResponsible
    ? supplier?.corporateName
    : suppliersList.find((s) => s.idProvider === selectedSupplier)?.corporateName;

  // Refresh inteligente (chamado após cadastro)
  const refreshAfterCreate = () => {
    if (selectedTab === "fornecedor") {
      // Se quiser, recarregue a lista do fornecedor (ex.: emitir evento para TableEmployee)
    } else {
      // Na aba de subcontratado recarrega a lista de subcontratados
      console.log("Disparando getSubcontractor para refresh.");
      getSubcontractor();
    }
  };

  return (
    <div className="m-4 flex justify-center">
      <div className="flex flex-col">
        <div className="dark:bg-primary relative bottom-[8vw] flex h-[30vh] w-[95vw] flex-col rounded-lg bg-white p-10 shadow-md">
          {/* ... cabeçalho ... */}
          <div className="mb-6 flex items-center justify-between rounded-md bg-realizaBlue p-5">
            <div className="flex items-center gap-1">
              <Users2Icon className="text-[#FFCE50]" />
              <h1 className="text-2xl font-medium text-white">Colaboradores</h1>
            </div>

            <div className="flex gap-2">
              <Button
                variant="ghost"
                className={`px-4 py-2 transition-all duration-300 ${
                  selectedTab === "fornecedor"
                    ? "scale-110 font-bold shadow-lg bg-white text-realizaBlue"
                    : "text-white border border-white"
                }`}
                onClick={() => setSelectedTab("fornecedor")}
              >
                <Cog className="mr-2 h-4 w-4" /> Fornecedor
              </Button>

              <Button
                variant="ghost"
                className={`px-4 py-2 transition-all duration-300 ${
                  selectedTab === "subcontratado"
                    ? "scale-110 font-bold shadow-lg bg-white text-realizaBlue"
                    : "text-white border border-white"
                }`}
                onClick={() => setSelectedTab("subcontratado")}
              >
                <BriefcaseBusiness className="mr-2 h-4 w-4" /> Subcontratado
              </Button>
            </div>
          </div>

          <div className="p-4">
            {loading ? (
              <div className="text-center text-gray-600">Carregando colaboradores...</div>
            ) : error ? (
              <div className="text-center text-red-500">{error}</div>
            ) : selectedTab === "fornecedor" ? (
              <div>
                {/* ... lógica de Fornecedor ... */}
                {isSupplierResponsible ? (
                  <div>
                    <h2 className="mb-4 text-xl">{supplier?.corporateName}</h2>
                    {/* Lista colaboradores do fornecedor do usuário responsável */}
                    <TableEmployee idTarget={supplier?.idProvider ?? null} targetType="supplier" />
                  </div>
                ) : (
                  <div>
                    <h2 className="mb-4 text-xl">Selecione um Fornecedor</h2>
                    <div>
                      <span className="text-realizaBlue text-[14px]">Fornecedor: </span>
                      <select
                        value={selectedSupplier || ""}
                        onChange={(e) => {
                          const selectedId = e.target.value;
                          setSelectedSupplier(selectedId);
                          const supplierData = suppliersList.find((sup) => sup.idProvider === selectedId);
                          if (supplierData) setSupplier(supplierData);
                        }}
                        className="rounded-lg border p-2 text-[12px]"
                      >
                        <option value="">Selecione um fornecedor</option>
                        {suppliersList.map((s) => (
                          <option key={s.idProvider} value={s.idProvider}>
                            {s.corporateName}
                          </option>
                        ))}
                      </select>
                    </div>

                    {/* Lista colaboradores do fornecedor selecionado */}
                    <TableEmployee idTarget={selectedSupplier} targetType="supplier" />
                  </div>
                )}
              </div>
            ) : (
              <div className="flex flex-col gap-5">
                <div className="flex items-center gap-2">
                  <strong>Fornecedor: </strong>
                  {getUniqueSupplier ? (
                    <div className="flex items-center gap-3">
                      <p>{getUniqueSupplier?.corporateName}</p> - <p>{getUniqueSupplier?.cnpj}</p>
                    </div>
                  ) : (
                    <p>Nenhum fornecedor selecionado</p>
                  )}
                </div>

                {!selectedSupplier && (
                  <div className="text-sm text-red-600">
                    Primeiro selecione um fornecedor (na aba Fornecedor) para carregar os subcontratados.
                  </div>
                )}

                <div className="flex items-center gap-2">
                  <span className="text-realizaBlue text-[14px]">Subcontratado: </span>
                  <select
                    value={selectedSubcontractor || ""}
                    onChange={(e) => {
                      const id = e.target.value;
                      setSelectedSubcontractor(id);
                      const sc = getSubcontractorList.find((x: any) => x.idProvider === id);
                      setSelectedSubcontractorName(sc?.corporateName || "");
                    }}
                    className="rounded-lg border p-2 text-[12px]"
                    disabled={!selectedSupplier}
                  >
                    <option value="">Selecione um subcontratado</option>
                    {getSubcontractorList.map((sub: any) => (
                      <option key={sub.idProvider} value={sub.idProvider}>
                        {sub.corporateName}
                      </option>
                    ))}
                  </select>
                </div>

                {/* Lista colaboradores do subcontratado selecionado */}
                {selectedSubcontractor ? (
                  <div className="mt-2">
                    <TableEmployee idTarget={selectedSubcontractor} targetType="subcontractor" />
                  </div>
                ) : (
                  <p className="text-sm text-gray-600">
                    Selecione um subcontratado para listar os colaboradores.
                  </p>
                )}
              </div>
            )}
          </div>
        </div>

        <div className="relative bottom-[18vh] flex justify-end gap-2 2xl:bottom-[16vh] xl:bottom-[14vh] lg:bottom-[10vh] md:bottom-[9vh]">
          {selectedTab === "fornecedor" ? (
            <NewModalCreateEmployee
              onEmployeeCreated={refreshAfterCreate}
              targetType="supplier"
              supplierId={targetSupplierId}
              targetName={targetSupplierName || "Fornecedor"}
            />
          ) : (
            <NewModalCreateEmployee
              onEmployeeCreated={refreshAfterCreate}
              targetType="subcontractor"
              subcontractId={selectedSubcontractor}
              targetName={selectedSubcontractorName || "Subcontratado"}
            />
          )}

          <ManageEmployeesModal
            idProvider={selectedTab === "fornecedor" ? targetSupplierId : selectedSubcontractor}
          />
        </div>
      </div>
    </div>
  );
};

// (opcional) export default também, se você quiser importar por default em outro lugar
export default EmployeesTable;