import selectClientImage from '@/assets/selectClientImage.png'

export function SelectClient (){
    return(
        <div className="flex justify-center min-h-full m-10">
            
            <div className="flex justify-between bg-white w-[80rem] h-[30rem] rounded-lg">
            <div className="m-8">
                
                <h1>Escolha seu ambiente</h1>

                <div className=' w-[40rem] h-[23rem] mt-10 border-gray-600 border-2 p-6'>
                    <h2>Selecione um Cliente</h2>
                </div>
                
            </div>
            <div className="mx-8 my-4 w-[30rem] h-[28rem] rounded-lg bg-blue-50 ">
                <img src={selectClientImage} alt="imagem de seleção de cliente" />
            </div>
            </div>
        </div>   
    )
}