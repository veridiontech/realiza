import { Skeleton } from "@/components/ui/skeleton";
import { EditModalEnterprise } from "../../realizaProfile/profileEnterprise/edit-modal-enterprise";

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
      <div className="flex gap-4 w-[59vw]">
        <div className="dark:bg-primary flex w-full items-start justify-between rounded bg-white p-8 shadow md:flex-row">
          <div className="flex items-center gap-3">
            <div className="bg-red-600 p-5 rounded-md flex items-center justify-center h-20 w-20">
              <div className="text-[30px] text-white font-bold">
                {firstLetter}
                {lastLetter}
              </div>
            </div>
            <div className="flex flex-col gap-1">
              {isLoading ? (
                <h3 className="text-lg font-medium">{name}</h3>
              ) : (
                <Skeleton className="h-[18px] w-[200px] rounded-full bg-gray-200" />
              )}
              <div className="flex items-center gap-2">
                <strong className="md:font-md font-medium">Email:</strong>
                {isLoading ? (
                  <p>{email}</p>
                ) : (
                  <Skeleton className="h-[8px] w-[150px] rounded-full bg-gray-200" />
                )}
              </div>
              <div className="flex items-center gap-2">
                <strong className="font-medium">CNPJ:</strong>
                {isLoading ? (
                  <p>{cnpj}</p>
                ) : (
                  <Skeleton className="h-[8px] w-[120px] rounded-full bg-gray-200" />
                )}
              </div>
            </div>
          </div>
          <div className="flex items-center gap-2">
            <EditModalEnterprise />
          </div>
        </div>
      </div>
    </div>
  );
}