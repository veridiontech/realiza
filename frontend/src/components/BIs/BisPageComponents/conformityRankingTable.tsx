import { Card, CardContent } from '@/components/ui/card';

interface Company {
  name: string;
  cnpj: string;
  adherence: string;
  conformity: string;
  nonConformDocs: number;
  conformityLevel: 'EM RISCO' | 'ATENÇÃO' | 'OK';
}

const companies: Company[] = [
  {
    name: 'RL REVESTIMENTOS E CONSTRUCOES LTDA.',
    cnpj: '56.677.537/0001-45',
    adherence: '88,21%',
    conformity: '51,13%',
    nonConformDocs: 605,
    conformityLevel: 'EM RISCO',
  },
  {
    name: 'WE MONT ENGENHARIA CONSTRUCOES E MONTAGENS LTDA',
    cnpj: '26.665.394/0001-90',
    adherence: '94,4%',
    conformity: '85,87%',
    nonConformDocs: 482,
    conformityLevel: 'ATENÇÃO',
  },
  {
    name: 'ABDALA ENGENHARIA E CONSTRUCOES LTDA',
    cnpj: '59.239.442/0001-38',
    adherence: '99,74%',
    conformity: '98,94%',
    nonConformDocs: 4,
    conformityLevel: 'OK',
  },
];

const getConformityColor = (level: Company['conformityLevel']) => {
  switch (level) {
    case 'EM RISCO':
      return 'bg-red-100 text-red-800 font-semibold';
    case 'ATENÇÃO':
      return 'bg-yellow-100 text-yellow-800 font-semibold';
    case 'OK':
      return 'bg-green-100 text-green-800 font-semibold';
    default:
      return '';
  }
};

export function ConformityRankingTable() {
  return (
    <Card className="w-full max-w-full rounded-lg shadow-md border border-gray-200">
      <CardContent className="pt-6 pb-4 px-6">
        <h2 className="text-gray-800 text-xl font-bold mb-6 select-none">
          Ranking Pendências
        </h2>
        {/* Aqui está a mágica para evitar overflow e garantir scroll horizontal */}
        <div className="overflow-x-auto w-full">
          <table className="min-w-[800px] w-full text-sm text-left">
            <thead className="bg-gray-50 border-b border-gray-300">
              <tr>
                <th className="py-3 px-4 font-semibold text-gray-600 whitespace-nowrap">
                  Razão Social
                </th>
                <th className="py-3 px-4 font-semibold text-gray-600 whitespace-nowrap">
                  CNPJ
                </th>
                <th className="py-3 px-4 font-semibold text-gray-600 whitespace-nowrap text-center">
                  Aderência %
                </th>
                <th className="py-3 px-4 font-semibold text-gray-600 whitespace-nowrap text-center">
                  Conformidade %
                </th>
                <th className="py-3 px-4 font-semibold text-gray-600 whitespace-nowrap text-center">
                  Docs Não Conformes
                </th>
                <th className="py-3 px-4 font-semibold text-gray-600 whitespace-nowrap text-center">
                  Faixa de Conformidade
                </th>
              </tr>
            </thead>
            <tbody>
              {companies.map((c) => (
                <tr
                  key={c.cnpj}
                  className="border-b border-gray-200 last:border-none hover:bg-gray-50 transition-colors"
                >
                  <td className="py-3 px-4 text-blue-600 hover:underline cursor-pointer whitespace-nowrap font-medium">
                    {c.name}
                  </td>
                  <td className="py-3 px-4 whitespace-nowrap">{c.cnpj}</td>
                  <td className="py-3 px-4 whitespace-nowrap text-center">{c.adherence}</td>
                  <td
                    className={`py-3 px-4 whitespace-nowrap text-center rounded-md ${getConformityColor(
                      c.conformityLevel
                    )}`}
                  >
                    {c.conformity}
                  </td>
                  <td className="py-3 px-4 whitespace-nowrap text-center">{c.nonConformDocs}</td>
                  <td className="py-3 px-4 whitespace-nowrap text-center font-semibold">
                    {c.conformityLevel}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </CardContent>
    </Card>
  );
}
