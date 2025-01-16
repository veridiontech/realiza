import { ReactNode } from "react";

export type TableProps<T> = {
  data: T[];
  columns: {
    key: keyof T;
    label: string;
    className?: string;
    render?: (value: T[keyof T], row: T) => ReactNode;
  }[];
};
