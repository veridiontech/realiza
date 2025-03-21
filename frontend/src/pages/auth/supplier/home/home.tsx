import { Skeleton } from "@/components/ui/skeleton"
import { useSupplier } from "@/context/Supplier-context"

export function HomeSupplier() {
    const{supplier} = useSupplier()

    const firstLetterBranch = supplier?.corporateName?.charAt(0) || ""
    const lastLetterBranch = supplier?.corporateName?.slice(-1) || "";

    return(
        <div className="flex flex-col items-center justify-center gap-5 p-10">
      <div className="flex gap-4">
        <div className="flex w-[50vw] items-start justify-between rounded-lg border bg-white p-10 shadow-lg">
          <div className="flex gap-3">
            <div className="bg-realizaBlue flex h-[16vh] w-[8vw] items-center justify-center rounded-full p-7">
              <div className="text-[40px] text-white">
                {firstLetterBranch}
                {lastLetterBranch}
              </div>
            </div>
            <div className="flex flex-col gap-10">
              <div className="flex flex-col items-start gap-3">
                <div className="text-realizaBlue text-[30px] font-medium">
                  {supplier ? (
                    <h2>{supplier?.corporateName}</h2>
                  ) : (
                    <Skeleton className="h-[1.5vh] w-[15vw] rounded-full bg-gray-600" />
                  )}
                </div>
                <div className="ml-1 text-sky-900">
                  {supplier ? (
                    <h3>{supplier?.email}</h3>
                  ) : (
                    <Skeleton className="h-[1.5vh] w-[8vw] rounded-full bg-gray-600" />
                  )}
                </div>
              </div>
              <div className="flex flex-col gap-1 text-[13px] text-sky-900">
                <div>
                  {supplier ? (
                    <p>{supplier?.cnpj}</p>
                  ) : (
                    <Skeleton className="h-[0.8vh] w-[7vw] rounded-full bg-gray-600" />
                  )}
                </div>
                <div>
                  {supplier ? (
                    <p>{supplier?.cep}</p>
                  ) : (
                    <Skeleton className="h-[0.6vh] w-[5vw] rounded-full bg-gray-600" />
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    )
}