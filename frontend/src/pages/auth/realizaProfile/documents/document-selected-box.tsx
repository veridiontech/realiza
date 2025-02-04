import { ScrollArea } from "@radix-ui/react-scroll-area";
import { useState } from "react";

export function DocumentSelectedBox() {
    const [isLoading, setIsLoading] = useState(false)

    if(isLoading) {
        
    }

  return (
    <div className="w-[30vw] rounded-md border p-2 shadow-md">
      <div className="flex h-[3vh] w-full items-center gap-1 rounded-md border p-1">
        <select className="w-full border-none focus:border-none focus:outline-none focus:ring-0">
          <option value="" disabled>
            Selecionados
          </option>
        </select>
      </div>
      <ScrollArea className="h-[25vh] w-full"></ScrollArea>
    </div>
  );
}
