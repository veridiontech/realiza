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
            return 'bg-red-200';
        case 'ATENÇÃO':
            return 'bg-yellow-200';
        case 'OK':
            return 'bg-green-200';
    }
};

export function ConformityRankingTable() {
    return (
        <Card className="w-full">
            <CardContent className="pt-6 pb-4">
                <h2 className="text-gray-700 text-lg font-semibold mb-4">Ranking pendências</h2>
                <div className="overflow-x-auto">
                    <table className="min-w-[1000px] w-full text-sm text-left border-separate border-spacing-x-6 border-spacing-y-3">
                        <thead className="text-gray-500 border-b">
                            <tr>
                                <th className="py-2">Razão Social</th>
                                <th className="py-2">CNPJ</th>
                                <th className="py-2">Aderência %</th>
                                <th className="py-2">Conformidade %</th>
                                <th className="py-2">Docs Não Conformes</th>
                                <th className="py-2">Faixa de Conformidade</th>
                            </tr>
                        </thead>
                        <tbody>
                            {companies.map((c) => (
                                <tr key={c.cnpj} className="border-b last:border-0">
                                    <td className="py-2 text-blue-600 hover:underline cursor-pointer whitespace-nowrap">{c.name}</td>
                                    <td className="py-2 whitespace-nowrap">{c.cnpj}</td>
                                    <td className="py-2 whitespace-nowrap">{c.adherence}</td>
                                    <td className={`py-2 font-medium whitespace-nowrap ${getConformityColor(c.conformityLevel)}`}>{c.conformity}</td>
                                    <td className="py-2 whitespace-nowrap">{c.nonConformDocs}</td>
                                    <td className="py-2 whitespace-nowrap">{c.conformityLevel}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </CardContent>
        </Card>
    );
}