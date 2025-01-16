import { api } from '../src/lib/axios'

export interface postClientInterface {
    cnpj: string
    nameEnterprise: string
    fantasyName: string
    socialReason: string
    email: string
    phone: string
    name: string,
    surname: string
    cpf: string
    position: string
    role: string
    passoword: string
}

export async function PostClient() {
    const res = await api.post('/client/user')
}