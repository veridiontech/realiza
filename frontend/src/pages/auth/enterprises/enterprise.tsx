import ultraIcon from '@/assets/ultraIcon.png'

export function Enterprise() {
    return (
        <div className="flex flex-col w-full max-w-screen-2xl h-auto p-6 ml-4 bg-white">
            <h1 className="text-2xl font-semibold pb-6 text-blue-600 border-b-2">Empresa</h1>
            <div className="flex">
                <div className="flex flex-row mt-4 w-full h-80 items-center justify-center text-white text-lg ">
                    <div className="flex w-1/3 h-auto items-center justify-center">
                        <div className="w-40 h-40 rounded-full">
                            <img className='w-40 h-40 rounded-full' src={ultraIcon} alt="" />
                        </div>
                    </div> 
                    <div className="flex flex-col w-1/3 h-auto text-black">
                        <span>Ultragaz</span>
                        <span>Ultragaz | Distribuidora de gás nacional</span>
                        <span>Matriz</span>
                        <span>12.345.678/0001-91</span>
                        <span>12345678</span>
                        <span>email@email.com.br</span>
                    </div> 
                    <div className="flex items-center justify-center w-1/3 h-auto">
                    <button className="text-black border-2 border-blue-300 py-2 px-10 rounded-md hover:bg-blue-300 hover:text-white hover:border-blue-600">
                        Editar ✏️
                    </button>
                    </div> 
                </div>
            </div>
        </div>
        
    );
}
