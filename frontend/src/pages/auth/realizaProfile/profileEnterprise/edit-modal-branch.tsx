import { useState } from "react";

export function EditModalBranch({ branch, onClose, onSave }: any) {
  const [editedBranch, setEditedBranch] = useState(branch);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setEditedBranch({ ...editedBranch, [name]: value });
  };

  const handleSave = () => {
    onSave(editedBranch); // Envia as alterações para a página principal
  };

  return (
    <div className="modal">
      <h2>Editando Filial</h2>
      <input
        type="text"
        name="name"
        value={editedBranch?.name || ""}
        onChange={handleChange}
        placeholder="Nome"
      />
      <input
        type="text"
        name="cnpj"
        value={editedBranch?.cnpj || ""}
        onChange={handleChange}
        placeholder="CNPJ"
      />
      <input
        type="text"
        name="address"
        value={editedBranch?.address || ""}
        onChange={handleChange}
        placeholder="Endereço"
      />
      <input
        type="text"
        name="email"
        value={editedBranch?.email || ""}
        onChange={handleChange}
        placeholder="E-mail"
      />
      <input
        type="text"
        name="phone"
        value={editedBranch?.phone || ""}
        onChange={handleChange}
        placeholder="Telefone"
      />
      <button onClick={handleSave}>Salvar</button>
      <button onClick={onClose}>Cancelar</button>
    </div>
  );
}
