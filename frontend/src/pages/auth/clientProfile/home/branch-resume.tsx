import { Skeleton } from "@/components/ui/skeleton";
import { EditModalEnterprise } from "../../realizaProfile/profileEnterprise/edit-modal-enterprise";
import { ChevronRight } from "lucide-react";

interface BranchResumeProps {
  firstLetter: string;
  lastLetter: string;
  name: string | undefined;
  email: string | undefined;
  cnpj: string | undefined;
  isLoading: boolean;
}

export function BranchResume({
  firstLetter,
  lastLetter,
  name,
  isLoading,
  cnpj,
  email,
}: BranchResumeProps) {
  return (
    <div className="flex flex-col items-center justify-center gap-5 p-10 relative bottom-[7vw]">
      <div className="flex gap-4">
        <div className="flex w-[60vw] items-start justify-between rounded-lg border bg-white p-10 shadow-lg">
          <div className="flex gap-3">
            <div className="bg-red-600 flex h-[16vh] w-[8vw] items-center justify-center rounded-lg p-7">
              <div className="text-[40px] text-white">
                {firstLetter}
                {lastLetter}
              </div>
            </div>
            <div className="flex flex-col gap-1">
              <div className="flex flex-col items-start gap-3">
                <div className="text-realizaBlue text-[30px] font-medium flex items-start gap-24">
                  {isLoading ? (
                    <h2>{name}</h2>
                  ) : (
                    <Skeleton className="h-[1.5vh] w-[15vw] rounded-full bg-gray-600" />
                  )}
                  <div className="flex items-center gap-4">
                    <EditModalEnterprise />
                    <div className="bg-neutral-400 p-2 rounded-full hover:bg-neutral-500 cursor-pointer ">
                      <ChevronRight className="text-white"/>
                    </div>
                  </div>
                </div>
                <div className="ml-1 text-sky-900 text-[15px]">
                  {isLoading ? (
                    <h3>Email: {email}</h3>
                  ) : (
                    <Skeleton className="h-[1.5vh] w-[8vw] rounded-full bg-gray-600" />
                  )}
                </div>
              </div>
              <div className="flex flex-col gap-1 text-[12px] text-sky-900 ml-1">
                <div>
                  {isLoading ? (
                    <p>CNPJ: {cnpj}</p>
                  ) : (
                    <Skeleton className="h-[0.8vh] w-[7vw] rounded-full bg-gray-600" />
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
