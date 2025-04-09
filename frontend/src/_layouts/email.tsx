import { Outlet } from "react-router-dom";
import bgImage from '@/assets/imageLogin.png';
import { MenuNav } from "@/components/menuNav/menuNav";

export function EmailLayout() {
    return (
        <div className="min-h-screen flex">
            <div className="w-1/2 flex justify-center items-center bg-white p-10">
                <div className="flex flex-col gap-5 items-center">
                    <div>
                        <MenuNav />
                    </div>
                    <div>
                        <Outlet />
                    </div>
                </div>
            </div>
            <div style={{ backgroundImage: `url(${bgImage})` }} className="w-1/2 bg-no-repeat bg-cover bg-center">
            </div>
        </div>
    );
}