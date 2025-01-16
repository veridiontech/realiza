import React from "react";
import bgImage from "@/assets/modalBG.jpeg";
import { FieldType } from "@/types/fieldModal";

interface Field {
  name: string;
  label: string;
  type: FieldType;
  placeholder?: string;
  options?: string[];
  required?: boolean;
  defaultValue?: any;
  accept?: string;
  render?: (props: {
    value: any;
    onChange: (value: any) => void;
  }) => React.ReactNode;
}

interface ModalProps {
  title: string;
  fields?: Field[];
  onSubmit?: (formData: Record<string, any>) => void;
  onClose: () => void;
}

export function Modal({ title, fields = [], onSubmit, onClose }: ModalProps) {
  const [formData, setFormData] = React.useState<Record<string, any>>(
    fields.reduce(
      (acc, field) => ({
        ...acc,
        [field.name]: field.defaultValue || "",
      }),
      {},
    ),
  );

  React.useEffect(() => {
    document.body.style.overflow = "hidden";
    return () => {
      document.body.style.overflow = "auto";
    };
  }, []);

  const handleChange = (name: string, value: any) => {
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleFileChange = (name: string, file: File | null) => {
    setFormData((prev) => ({ ...prev, [name]: file }));
  };

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    if (onSubmit) {
      onSubmit(formData);
    }
  };

  return (
    <div
      className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50"
      onClick={() => onClose()}
    >
      <div
        className="scrollbar-hide relative max-h-[90%] w-[90%] max-w-[40rem] overflow-y-scroll rounded-lg bg-cover bg-no-repeat p-8 text-white shadow-lg"
        style={{ backgroundImage: `url(${bgImage})` }}
        onClick={(e) => e.stopPropagation()}
      >
        <h2 className="mb-4 text-xl font-semibold text-yellow-400">{title}</h2>
        <form onSubmit={handleSubmit} className="space-y-4">
          {fields.map((field) => (
            <div key={field.name} className="flex flex-col text-black">
              <label
                htmlFor={field.name}
                className="mb-2 text-sm font-medium text-white"
              >
                {field.label}
              </label>
              {field.type === "custom" && field.render ? (
                field.render({
                  value: formData[field.name],
                  onChange: (value) => handleChange(field.name, value),
                })
              ) : field.type === "select" ? (
                <select
                  id={field.name}
                  name={field.name}
                  value={formData[field.name]}
                  onChange={(e) => handleChange(field.name, e.target.value)}
                  className="rounded border border-gray-300 p-2"
                  required={field.required}
                >
                  <option value="">Selecione</option>
                  {field.options?.map((option) => (
                    <option key={option} value={option}>
                      {option}
                    </option>
                  ))}
                </select>
              ) : field.type === "checkbox" ? (
                <input
                  id={field.name}
                  name={field.name}
                  type="checkbox"
                  checked={formData[field.name]}
                  onChange={(e) => handleChange(field.name, e.target.checked)}
                  className="h-5 w-5"
                  required={field.required}
                />
              ) : field.type === "file" ? (
                <div className="flex items-center gap-2 rounded-lg border border-gray-300 bg-gray-100 p-2">
                  {/* Botão de escolher arquivo */}
                  <label
                    htmlFor={field.name}
                    className="cursor-pointer rounded-md bg-blue-500 px-4 py-2 text-sm font-medium text-white transition hover:bg-blue-600"
                  >
                    Escolher arquivo
                  </label>

                  {/* Nome do arquivo selecionado ou placeholder */}
                  <span className="truncate text-sm text-gray-500">
                    {formData[field.name]?.name || "Nenhum arquivo selecionado"}
                  </span>

                  {/* Input de arquivo oculto */}
                  <input
                    id={field.name}
                    name={field.name}
                    type="file"
                    accept={field.accept}
                    onChange={(e) =>
                      handleFileChange(field.name, e.target.files?.[0] || null)
                    }
                    className="hidden"
                    required={field.required}
                  />
                </div>
              ) : (
                <input
                  id={field.name}
                  name={field.name}
                  type={field.type}
                  placeholder={field.placeholder}
                  value={formData[field.name]}
                  onChange={(e) => handleChange(field.name, e.target.value)}
                  className="rounded border border-gray-300 p-2"
                  required={field.required}
                />
              )}
            </div>
          ))}
          <div className="flex justify-end space-x-4">
            <button
              type="button"
              className="rounded bg-gray-300 px-4 py-2 text-black hover:bg-gray-400"
              onClick={onClose}
            >
              Cancelar
            </button>
            <button
              type="submit"
              className="rounded bg-blue-600 px-4 py-2 text-white hover:bg-blue-700"
            >
              Enviar
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
