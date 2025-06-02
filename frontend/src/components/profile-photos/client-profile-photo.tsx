import { useClient } from "@/context/Client-Provider";

export function ClientProfilePhoto() {
  const { client } = useClient();

  const firstLetter = client?.corporateName[0];
  const lastLetter = client?.corporateName?.slice(-1);

  return (
    <div >
      <div className="bg-red-700 p-5 w-[4vw] h-[8vh] rounded-md flex items-center justify-center">
        <span className="font-medium text-white text-[20px]">{firstLetter}</span>
        <span className="font-medium text-white text-[20px]">{lastLetter}</span>
      </div>
    </div>
  );
}
