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
  children?: React.ReactNode;
}

export function Modal({
  title,
  fields = [],
  onSubmit,
  onClose,
  children,
}: ModalProps) {
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
        className="relative w-[90%] max-w-[40rem] overflow-hidden rounded-lg bg-cover bg-no-repeat p-6 text-white shadow-lg"
        style={{
          backgroundImage: `url(${bgImage})`,
          maxHeight: "90vh",
        }}
        onClick={(e) => e.stopPropagation()}
      >
        <div
          className="content-container"
          style={{
            maxHeight: "calc(90vh - 50px)",
            overflowY: "scroll",
            WebkitOverflowScrolling: "touch",
          }}
        >
          <style>
            {`
              .content-container::-webkit-scrollbar {
                width: 0px;
                background: transparent;
              }
              .content-container {
                scrollbar-width: none;
              }
            `}
          </style>
          <h2 className="mb-4 text-xl font-semibold text-yellow-400">
            {title}
          </h2>
          <form
            onSubmit={handleSubmit}
            className="space-y-4"
            style={{
              display: "flex",
              flexDirection: "column",
              gap: "1rem",
              alignItems: "stretch",
            }}
          >
            {fields.map((field) => (
              <div key={field.name} className="flex flex-col">
                <label
                  htmlFor={field.name}
                  className="mb-2 text-sm font-medium text-white"
                >
                  {field.label}
                </label>
                {field.type === "select" ? (
                  <select
                    id={field.name}
                    name={field.name}
                    value={formData[field.name]}
                    onChange={(e) => handleChange(field.name, e.target.value)}
                    className="rounded border border-gray-300 bg-white p-2 text-black"
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
                ) : field.type === "file" ? (
                  <input
                    id={field.name}
                    name={field.name}
                    type="file"
                    accept={field.accept}
                    onChange={(e) =>
                      handleChange(field.name, e.target.files?.[0])
                    }
                    className="hidden" // Esconde o input
                  />
                ) : field.render ? (
                  field.render({
                    value: formData[field.name],
                    onChange: (value: any) => handleChange(field.name, value),
                  })
                ) : (
                  <input
                    id={field.name}
                    name={field.name}
                    type={field.type}
                    placeholder={field.placeholder}
                    value={formData[field.name]}
                    onChange={(e) => handleChange(field.name, e.target.value)}
                    className="rounded border border-gray-300 bg-white p-2 text-black"
                    required={field.required}
                  />
                )}
              </div>
            ))}
            {children && <div>{children}</div>}
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
    </div>
  );
}
