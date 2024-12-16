import { Avatar, AvatarImage } from "@/components/ui/avatar";


export function TopSection () {
    return (
        <div className="flex items-center">
          <Avatar className="w-24 h-24">
            <AvatarImage src="https://github.com/shadcn.png" />
          </Avatar>
          <div className="ml-6">
            <h2 className="text-lg font-bold">Jean de Castro</h2>
            <span>email@email.com</span>
            <p className="text-blue-600">Usuário Comum</p>
          </div>
        </div>
    )
}