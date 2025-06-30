import { TopSection } from "./topSection.tsx";
import { MiddleSection } from "./middleSection.tsx";
import { BottomSection } from "./bottomSection.tsx";

export function ProfileUser() {
  return (
    <div className="flex h-full w-full flex-col bg-white p-6 relative bottom-[5vw] rounded-md">
      <h1 className="text-black text-realizaBlue mb-6 text-2xl font-semibold">
        Meu Perfil
      </h1>
      <div className="mb-6 flex flex-col items-center justify-between rounded-lg bg-white p-6 shadow md:flex-row">
        <TopSection />
      </div>
      <MiddleSection />
      <BottomSection />
    </div>
  );
}
