import { useSupplier } from "@/context/Supplier-context"

export function HomeSupplier() {
    const{supplier} = useSupplier()

    return(
        <div>
            {supplier?.corporateName}
        </div>
    )
}