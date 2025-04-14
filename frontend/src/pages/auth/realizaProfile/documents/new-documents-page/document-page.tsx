import { Search } from "lucide-react";
import { NewDocumentBox } from "./document-box-new";

export function DocumentPageNew() {
    return(
        <div className="p-10 flex flex-col gap-10">
            <div className="shadow-lg rounded-lg bg-white p-8 flex flex-col gap-10">
                <h1 className="text-[25px]">Matriz de Documentos</h1>
                <div className="bg-[#7CA1F3] w-full h-[1px] "></div>
                <div>
                    <div className="flex items-center gap-2 border rounded-md w-[18vw] p-2">
                        <Search />
                        <input className="outline-none" placeholder="Procure um documento"/>
                    </div>
                </div>
            </div>
            <div>
                <NewDocumentBox />
            </div>
        </div>
    )
}