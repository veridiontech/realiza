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
    <div className="flex h-screen items-center justify-center">
      <div className="flex w-full max-w-md flex-col justify-center px-6">
        <h1 className="mb-4 text-center text-3xl font-bold text-white">
          Esqueceu sua senha?
        </h1>
        <span className="text-center text-white">
          Insira seu e-mail abaixo para que possamos enviar um código de 4
          dígitos para redefinir sua senha.
        </span>
        <Form className="mt-8 flex flex-col" onSubmit={handleSubmit(onSubmit)}>
          <label className="mb-2 text-white" htmlFor="email">
            E-mail
          </label>
          <input
            className="focus:ring-realizaBlue mb-6 block w-full rounded-md border border-gray-300 px-3 py-2 text-gray-900 focus:outline-none focus:ring-2"
            placeholder="email@gmail.com"
            type="email"
            {...register("email")}
          />
          {errors.email && (
            <span className="mb-4 text-sm text-red-500">
              {errors.email.message}
            </span>
          )}

          <button
            className="mt-4 w-full rounded bg-[#C0B15B] text-realizaBlue px-4 py-2 font-bold hover:bg-neutral-400"
            type="submit"
          >
            Enviar
          </button>
        </Form>
      </div>
    </div>
  );
}
