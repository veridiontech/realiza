import { useUser } from "@/context/user-provider"

export function ProfilePic() {
    const{ user } = useUser()

    console.log(user);
    

    const getNameUser = user?.firstName?.[0]
    console.log(getNameUser);
    
    const getSurnameUser = user?.surname?.[0] 



    return(
        <div>
            <div className="bg-realizaBlue text-white p-2 rounded-full">
                <span>{getNameUser}</span>
                <span>{getSurnameUser}</span>
            </div>
        </div>
    )
}