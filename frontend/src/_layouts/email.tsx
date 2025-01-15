import { Outlet } from "react-router-dom";
import bgImage from '@/assets/imageLogin.png'
import { MenuNav } from "@/components/menuNav/menuNav";

export function EmailLayout() {
    return(
        <div style={({backgroundImage: `url(${bgImage})`})} className="min-h-screen bg-no-repeat bg-cover bg-center flex justify-center py-10 ">
            <div className="bg-white rounded-sm flex flex-col gap-5 items-center px-10 py-10">
                <div>
                    <MenuNav />
                </div>
                <div>
                    <Outlet />
                </div>
            </div>
        </div>
    )
}