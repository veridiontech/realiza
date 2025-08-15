import { useEffect, useMemo, useState } from "react";
import axios from "axios";

type Filters = {
  branchIds?: string[];
  providerIds?: string[];
  documentTypes?: string[];
  responsibleIds?: string[];
  activeContract?: string[]; 
  statuses?: string[];
  documentTitles?: string[];
};
type ApiItem = Record<string, any>;

type Row = {
  id: string;
  filial: string;
  cnpjFilial: string;
  fornecedor: string;
  cnpjFornecedor: string;
  documento: string;
  tipoDocumento: string;
  status: string;
  responsavel?: string;
  emailResponsavel?: string;
  conformidade?: string;
  criadoEm?: string;
  ultimaChecagem?: string;
  validade?: string;
};

function formatDate(v?: string) {
  if (!v) return "—";
  const d = new Date(v);
  if (isNaN(+d)) return v;
  return d.toLocaleDateString();
}

function mapItemToRow(it: ApiItem, idx: number): Row {
  return {
    id: String(it.id ?? idx),
    filial: it.branchName ?? "—",
    cnpjFilial: it.branchCnpj ?? "—",
    fornecedor: it.supplierName ?? "—",
    cnpjFornecedor: it.supplierCnpj ?? "—",
    documento: it.documentTitle ?? "—",
    tipoDocumento: it.documentType ?? it.contractType ?? "—",
    status: it.status ?? it.documentStatus ?? it.contractStatus ?? "—",
    responsavel: it.responsibleFullName ?? it.employeeFullName ?? "—",
    emailResponsavel: it.responsibleEmail ?? it.employeeEmail ?? "—",
    conformidade: typeof it.conform === "boolean" ? (it.conform ? "Conforme" : "Não conforme") : "—",
    criadoEm: formatDate(it.createdAt ?? it.contractAt ?? it.serviceStartAt),
    ultimaChecagem: formatDate(it.lastCheck ?? it.checkedAt),
    validade: formatDate(it.expirationDate ?? it.validUntil),
  };
}

async function fetchDocumentDetails(clientId: string, filters: Filters) {
  
  const body = {
    branchIds: filters.branchIds ?? [],
    providerIds: filters.providerIds ?? [],
    documentTypes: filters.documentTypes ?? [],
    responsibleIds: filters.responsibleIds ?? [],
    activeContract: filters.activeContract,   
    statuses: filters.statuses ?? [],
    documentTitles: filters.documentTitles ?? [],
  };




  console.log(body);


  console.log(`/dashboard/${clientId}/document/details`);

  const { data } = await axios.post(`/dashboard/${clientId}/document/details`, body);
  const content = Array.isArray(data?.content) ? data.content : Array.isArray(data) ? data : [];
  return content as ApiItem[];
}

type Props = {
  clientId: string;
  filters?: Filters;
};

export default function FornecedoresTable({ clientId, filters = {} }: Props) {
  const [rows, setRows] = useState<Row[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [sortKey, setSortKey] = useState<keyof Row>("fornecedor");
  const [sortDir, setSortDir] = useState<"asc" | "desc">("asc");

  useEffect(() => {
    let alive = true;
    (async () => {
      setLoading(true);
      setError(null);
      try {
        const items = await fetchDocumentDetails(clientId, filters);
        if (!alive) return;
        setRows(items.map(mapItemToRow));
      } catch (e: any) {
        if (!alive) return;
        setError(e?.message ?? "Erro ao carregar dados");
      } finally {
        if (alive) setLoading(false);
      }
    })();
    return () => { alive = false; };
  }, [clientId, JSON.stringify(filters)]);

  const sorted = useMemo(() => {
    const copy = [...rows];
    copy.sort((a, b) => {
      const va = (a[sortKey] ?? "").toString().toLowerCase();
      const vb = (b[sortKey] ?? "").toString().toLowerCase();
      if (va < vb) return sortDir === "asc" ? -1 : 1;
      if (va > vb) return sortDir === "asc" ?  1 : -1;
      return 0;
    });
    return copy;
  }, [rows, sortKey, sortDir]);

  function setSort(k: keyof Row) {
    if (k === sortKey) setSortDir(d => (d === "asc" ? "desc" : "asc"));
    else { setSortKey(k); setSortDir("asc"); }
  }

  return (
    <div style={{ width: "100%", overflowX: "auto" }}>
      <h3 style={{ margin: "12px 0" }}>Fornecedores — Documentos</h3>

      {loading && <div>Carregando…</div>}
      {error && <div style={{ color: "crimson" }}>{error}</div>}

      {!loading && !error && (
        <table style={{ width: "100%", borderCollapse: "collapse" }}>
          <thead>
            <tr>
              {[
                ["fornecedor","Fornecedor"],
                ["cnpjFornecedor","CNPJ Fornecedor"],
                ["filial","Filial"],
                ["cnpjFilial","CNPJ Filial"],
                ["documento","Documento"],
                ["tipoDocumento","Tipo"],
                ["status","Status"],
                ["responsavel","Responsável"],
                ["emailResponsavel","E-mail"],
                ["conformidade","Conformidade"],
                ["criadoEm","Criado em"],
                ["ultimaChecagem","Última checagem"],
                ["validade","Validade"],
              ].map(([key, label]) => (
                <th
                  key={key}
                  onClick={() => setSort(key as keyof Row)}
                  style={{ textAlign: "left", padding: "10px", borderBottom: "1px solid #ddd", cursor: "pointer", whiteSpace: "nowrap" }}
                  title="Clique para ordenar"
                >
                  {label} {sortKey === key ? (sortDir === "asc" ? "▲" : "▼") : ""}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {sorted.map(r => (
              <tr key={r.id} style={{ borderBottom: "1px solid #f0f0f0" }}>
                <td style={{ padding: "8px" }}>{r.fornecedor}</td>
                <td style={{ padding: "8px" }}>{r.cnpjFornecedor}</td>
                <td style={{ padding: "8px" }}>{r.filial}</td>
                <td style={{ padding: "8px" }}>{r.cnpjFilial}</td>
                <td style={{ padding: "8px" }}>{r.documento}</td>
                <td style={{ padding: "8px" }}>{r.tipoDocumento}</td>
                <td style={{ padding: "8px" }}>
                  <span style={{
                    padding: "2px 8px",
                    borderRadius: 12,
                    background: (r.status || "").toLowerCase().includes("pend") ? "#fff2e8"
                              : (r.status || "").toLowerCase().includes("aprov") ? "#e6ffed"
                              : "#eef2ff",
                    border: "1px solid #ddd"
                  }}>
                    {r.status}
                  </span>
                </td>
                <td style={{ padding: "8px" }}>{r.responsavel}</td>
                <td style={{ padding: "8px" }}>{r.emailResponsavel}</td>
                <td style={{ padding: "8px" }}>{r.conformidade}</td>
                <td style={{ padding: "8px", whiteSpace: "nowrap" }}>{r.criadoEm}</td>
                <td style={{ padding: "8px", whiteSpace: "nowrap" }}>{r.ultimaChecagem}</td>
                <td style={{ padding: "8px", whiteSpace: "nowrap" }}>{r.validade}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

