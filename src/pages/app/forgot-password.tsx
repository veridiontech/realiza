import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { Form } from "react-router-dom";

const forgotFormSchema = z.object({
  email: z
    .string()
    .nonempty("O email é obrigatório")
    .email("Formato de email inválido"),
});

type forgotFormData = z.infer<typeof forgotFormSchema>;

export function ForgotPassword() {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<forgotFormData>({
    resolver: zodResolver(forgotFormSchema),
  });

  const onSubmit = (data: forgotFormData) => {
    console.log("Dados do formulário:", data);
  };

  return (
    <div className="flex justify-center items-center h-screen">
      <div className="flex flex-col justify-center max-w-md w-full px-6">
        <h1 className="text-center font-bold text-3xl mb-4">
          Esqueceu sua senha?
        </h1>
        <span className="text-center text-gray-600">
          Insira seu e-mail abaixo para que possamos enviar um código de 4 dígitos para redefinir sua senha.
        </span>
        <Form
          className="flex flex-col mt-8"
          onSubmit={handleSubmit(onSubmit)}
        >
          <label className="mb-2" htmlFor="email">
            E-mail
          </label>
          <input
            className="mb-6 block w-full border border-gray-300 rounded-md py-2 px-3 text-gray-900 focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="email@gmail.com"
            type="email"
            {...register("email")}
          />
          {errors.email && <span className="text-red-500 text-sm mb-4">{errors.email.message}</span>}

          <button
            className="bg-realizaBlue hover:bg-blue-700 text-white font-bold py-2 px-4 rounded"
            type="submit"
          >
            Enviar
          </button>
        </Form>
      </div>
    </div>
  );
}
