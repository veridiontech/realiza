import { ProfilePic } from "@/components/header/profile-pic";
import { Skeleton } from "@/components/ui/skeleton";
import { useUser } from "@/context/user-provider";

export function TopSection() {
  const { user } = useUser();

  console.log(user);
  

  return (
    <div className="flex items-center">
      <ProfilePic className="bg-realizaBlue rounded-full p-7 text-[30px] text-white" />
      <div className="ml-6 flex flex-col gap-2">
        <div className="text-lg font-bold">
          {user ? (
            <h2>
              {user.firstName} {user?.surname}
            </h2>
          ) : (
            <Skeleton className="h-[1vh] w-[5vw]" />
          )}
        </div>
        <span>
          {user ? (
            <h2>
              {user.email}
            </h2>
          ) : (
            <Skeleton className="h-[1vh] w-[10vw]" />
          )}
        </span>
      </div>
    </div>
  );
}
