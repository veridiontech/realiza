import React from "react";
import bgImage from "@/assets/modalBG.jpeg";
import { FieldType } from "@/types/fieldModal";

interface Field {
  name: string;
  label: string;
  type: FieldType;
  placeholder?: string;
  options?: string[] | { label: string; value: string }[];
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
            <div key={field.name} className="flex flex-col text-white">
              <label
                htmlFor={field.name}
                className="mb-2 text-sm font-medium text-white"
              >
                {field.label}
              </label>
              {field.type === "radio" ? (
                <div className="flex items-center space-x-4">
                  {Array.isArray(field.options) &&
                    field.options.map(
                      (option) =>
                        typeof option !== "string" && (
                          <label
                            key={option.value}
                            className="flex items-center space-x-2 text-white"
                          >
                            <input
                              type="radio"
                              name={field.name}
                              value={option.value}
                              checked={formData[field.name] === option.value}
                              onChange={(e) =>
                                handleChange(field.name, e.target.value)
                              }
                              required={field.required}
                              className="h-4 w-4 text-blue-500 focus:ring focus:ring-blue-500"
                            />
                            <span>{option.label}</span>
                          </label>
                        ),
                    )}
                </div>
              ) : field.type === "select" ? (
                <select
                  id={field.name}
                  name={field.name}
                  value={formData[field.name]}
                  onChange={(e) => handleChange(field.name, e.target.value)}
                  className="rounded border border-gray-300 p-2 text-black"
                  required={field.required}
                >
                  <option value="">Selecione</option>
                  {Array.isArray(field.options) &&
                    field.options.map((option) =>
                      typeof option === "string" ? (
                        <option key={option} value={option}>
                          {option}
                        </option>
                      ) : (
                        <option key={option.value} value={option.value}>
                          {option.label}
                        </option>
                      ),
                    )}
                </select>
              ) : field.type === "custom" && field.render ? (
                field.render({
                  value: formData[field.name],
                  onChange: (value) => handleChange(field.name, value),
                })
              ) : (
                <input
                  id={field.name}
                  name={field.name}
                  type={field.type}
                  placeholder={field.placeholder}
                  value={formData[field.name]}
                  onChange={(e) => handleChange(field.name, e.target.value)}
                  className="rounded border border-gray-300 p-2 text-black"
                  required={field.required}
                />
              )}
            </div>
          ))}
          <div className="flex justify-end space-x-4">
            <button
              type="button"
              className="rounded bg-gray-300 px-4 py-2 text-black hover:bg-gray-400 focus:ring focus:ring-gray-500"
              onClick={onClose}
            >
              Cancelar
            </button>
            <button
              type="submit"
              className="rounded bg-blue-600 px-4 py-2 text-white hover:bg-blue-700 focus:ring focus:ring-blue-500"
            >
              Enviar
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default function App() {
  const [showModal, setShowModal] = React.useState(false);

  const fields: Field[] = [
    {
      name: "confirmation",
      label: "* Você confirma que deseja continuar?",
      type: "radio",
      options: [
        { label: "Sim", value: "yes" },
        { label: "Não", value: "no" },
      ],
      required: true,
    },
  ];

  const handleSubmit = (formData: Record<string, any>) => {
    console.log("Form Submitted:", formData);
    setShowModal(false);
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-100">
      <button
        onClick={() => setShowModal(true)}
        className="rounded bg-blue-500 px-6 py-2 text-white hover:bg-blue-600 focus:ring focus:ring-blue-500"
      >
        Abrir Modal
      </button>
      {showModal && (
        <Modal
          title="Confirmação"
          fields={fields}
          onSubmit={handleSubmit}
          onClose={() => setShowModal(false)}
        />
      )}
    </div>
  );
}
