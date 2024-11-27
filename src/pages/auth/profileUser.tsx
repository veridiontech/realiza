import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"


export function ProfileUser () {
    return(
        <>
        
        <div className="flex flex-col w-full">
            <h1 className="max-h-5 m-8">Meu Perfil</h1>
            <div className="flex items-center w-full h-1/3 border-t border-b border-gray-300">
            <Avatar className="ml-12 w-36 h-36">
                <AvatarImage src="https://github.com/shadcn.png" />
                <AvatarFallback>CN</AvatarFallback>
            </Avatar>
            <div className="flex flex-col">
                <span className="ml-8">Jean de castro</span>
                <span>span1</span>
            </div>
            <div className="justify-end">
                <button>butao</button>
            </div>
            </div>
            <div className="w-full h-2/5 "></div>
            <div className="w-full h-1/3 border-t border-b border-gray-300"></div>
        </div>


        </>
    )
}