import { useUser } from "@/context/user-provider";
import { Skeleton } from "../ui/skeleton";

interface profilePicProps {
  className: string;
}

export function ProfilePic({ className }: profilePicProps) {
  const { user } = useUser();

  const getNameUser = user?.firstName?.[0];

  const getSurnameUser = user?.surname?.[0];

  return (
    <div>
      {user ? (
        <div className={className}>
          <span>{getNameUser}</span>
          <span>{getSurnameUser}</span>
        </div>
      ) : (
        <Skeleton className="w-[5vw] h-[10vh] rounded-full"/>
      )}
    </div>
  );
}
