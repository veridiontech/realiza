import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, LabelList, ResponsiveContainer } from 'recharts';
import { Card, CardContent } from '@/components/ui/card';

const mockData = [
    { category: 'geral', value: 8 },
    { category: 'segurança', value: 7 },
    { category: 'saude', value: 1 },
    { category: 'trabalhista', value: 1 },
];

export function ExemptionPendingChart() {
    return (
        <Card className="w-[300px] ml-auto shadow-md">
            <CardContent className="pt-6 pb-4">
                <h2 className="text-gray-700 text-lg font-semibold mb-4">Isenções Aguardando Aprovações</h2>
                <ResponsiveContainer width="100%" height={400}>
                    <BarChart
                        data={mockData}
                        margin={{ top: 5, right: 10, left: 10, bottom: 20 }}
                        barSize={30}
                        barGap={20}
                    >
                        <CartesianGrid strokeDasharray="3 3" vertical={false} />
                        <XAxis dataKey="category" tick={{ fill: '#2f3a59' }} dy={10} />
                        <YAxis domain={[0, 100]} tick={{ fill: '#2f3a59' }} />
                        <Tooltip cursor={{ fill: 'transparent' }} />
                        <Bar dataKey="value" fill="#fcd34d">
                            <LabelList dataKey="value" position="top" fill="#2f3a59" />
                        </Bar>
                    </BarChart>
                </ResponsiveContainer>
            </CardContent>
        </Card>
    );
}