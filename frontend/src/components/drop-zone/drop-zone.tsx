import { Button } from "@/components/ui/button";
import { Copy, CopyPlus, Trash2 } from "lucide-react";
import { useCallback, useEffect, useState } from "react";
import { FileRejection, useDropzone } from "react-dropzone";

// Definir um tipo estendido de File
interface ExtendedFile extends File {
  preview: string;
}

interface DropzoneProps {
  className?: string;
  onFilesChange: (files: ExtendedFile[]) => void;
}

export const Dropzone = ({ className, onFilesChange }: DropzoneProps) => {
  const [files, setFiles] = useState<ExtendedFile[]>([]);
  const [rejectedFiles, setRejectedFiles] = useState<FileRejection[]>([]);

  const onDrop = useCallback(
    (acceptedFiles: File[], rejectedFiles: FileRejection[]) => {
      if (acceptedFiles?.length) {
        const newFiles = acceptedFiles.map((file) =>
          Object.assign(file, { preview: URL.createObjectURL(file) })
        );
        setFiles((previousFiles) => [...previousFiles, ...newFiles]);
      }

      if (rejectedFiles?.length) {
        setRejectedFiles((previousFiles) => [
          ...previousFiles,
          ...rejectedFiles,
        ]);
      }

      console.log(rejectedFiles);
    },
    []
  );

  useEffect(() => {
    onFilesChange(files);
  }, [files, onFilesChange]);

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    maxSize: 1024 * 100000000, 
  });

  const removeFile = (name: string) => {
    setFiles((files) => files.filter((file) => file.name !== name));
  };

  return (
    <div>
      <div
        {...getRootProps({
          className: className,
        })}
      >
        <input {...getInputProps()} />
        {isDragActive ? (
          <div className="flex flex-col gap-2 items-center justify-center">
            <Copy className="opacity-60" />
          </div>
        ) : (
          <div className="flex flex-col gap-2 items-center justify-center">
            <CopyPlus />
            <p className="text-sm font-medium">
              Arraste aqui ou clique para adicionar imagens, PDFs ou Excel
            </p>
          </div>
        )}
      </div>

      {/* Accepted files */}
      {files.length >= 1 && (
        <>
          <h3 className="text-lg py-4 font-medium">Arquivos aceitos</h3>
          <ul className="flex flex-col space-y-2">
            {files.map((file) => (
              <li
                key={file.name}
                className="grid grid-cols-2 gap-6 items-center p-4 border border-zinc-200"
              >
                <div className="flex items-center gap-4">
                  {file.type.startsWith("image/") ? (
                    <img
                      src={file.preview}
                      alt={file.name}
                      width={100}
                      height={100}
                      onLoad={() => {
                        URL.revokeObjectURL(file.preview);
                      }}
                      className="size-20 object-cover"
                    />
                  ) : (
                    <p className="text-neutral-500 text-[12px] font-medium">
                      {file.name} ({file.type})
                    </p>
                  )}
                </div>
                <div className="flex justify-end">
                  <Button
                    variant={"ghost"}
                    type="button"
                    onClick={() => removeFile(file.name)}
                  >
                    <Trash2 size={16} className="text-red-600" />
                  </Button>
                </div>
              </li>
            ))}
          </ul>
        </>
      )}

      {/* Rejected files */}
      {rejectedFiles.length >= 1 && (
        <>
          <h3 className="text-lg py-4 font-medium">Arquivos negados</h3>
          <ul className="flex flex-col space-y-2">
            {rejectedFiles.map(({ file }) => (
              <li
                key={file.name}
                className="p-4 border flex flex-col gap-1 border-zinc-200"
              >
                {file.name}
                <span className="text-xs text-red-500 font-medium">
                  Arquivo não aceito
                </span>
              </li>
            ))}
          </ul>
        </>
      )}
    </div>
  );
};