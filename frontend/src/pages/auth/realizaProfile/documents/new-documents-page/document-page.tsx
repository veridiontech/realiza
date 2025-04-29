import { Search } from "lucide-react";
import { NewDocumentBox } from "./document-box-new";

export function DocumentPageNew() {
    return (
        <div className="p-6 md:p-10 flex flex-col gap-6 md:gap-10">
            <div className="shadow-lg rounded-lg bg-white p-6 md:p-8 flex flex-col gap-6 md:gap-10">
                <h1 className="text-2xl md:text-[25px]">Matriz de Documentos</h1>
                <div className="bg-[#7CA1F3] w-full h-[1px]" />
                <div>
                    <div className="flex items-center gap-2 border rounded-md w-full md:w-[18vw] p-2">
                        <Search className="w-5 h-5" />
                        <input 
                            className="outline-none w-full text-sm" 
                            placeholder="Procure um documento" 
                        />
                    </div>
                </div>
            </div>
            <div>
                <NewDocumentBox />
            </div>
        </div>
    );
}
