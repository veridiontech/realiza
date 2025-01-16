import { LabelWithInputProps } from "@/types/labelWithInput";

export const LabelWithInput: React.FC<LabelWithInputProps> = ({
  label,
  type,
  placeholder,
  register,
  name,
  error,
}) => {
  return (
    <div>
      <div className="mb-4">
        <label className="mb-1 block text-blue-600" htmlFor={name}>
          {label}
        </label>
        <input
          className={`w-full rounded border p-2 ${
            error ? "border-red-500" : "border-gray-300"
          }`}
          type={type}
          placeholder={placeholder}
          id={name}
          {...register(name)}
        />
        {error && <p className="mt-1 text-sm text-red-500">{error}</p>}
      </div>
    </div>
  );
};
