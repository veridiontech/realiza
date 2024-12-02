import { TopSection } from './topSection.tsx'
import { MiddleSection } from './middleSection.tsx'
import { BottomSection } from "./bottomSection.tsx";

export function ProfileUser() {
  return (
    <div className="flex flex-col w-full h-full p-6 bg-white">
      <h1 className="text-2xl font-semibold mb-6 text-blue-600">Meu Perfil</h1>
      <div className="flex flex-col md:flex-row items-center justify-between bg-white shadow rounded-lg p-6 mb-6">
        <TopSection />
        <button className="mt-4 md:mt-0 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600">
          Editar
        </button>
      </div>
      <MiddleSection />
      <BottomSection />
    </div>
  );
}
