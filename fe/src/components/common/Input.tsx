import { ReactNode } from "react";

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  icon?: ReactNode;
}

export const Input = ({
  label,
  error,
  icon,
  className = "",
  ...props
}: InputProps) => {
  return (
    <div className="space-y-1">
      {label && <label className="text-sm font-medium">{label}</label>}
      <div className="relative">
        <input
          className={`w-full rounded-lg border p-2 ${
            icon ? "pl-10" : ""
          } ${className}`}
          {...props}
        />
        {icon && (
          <span className="absolute left-3 top-1/2 -translate-y-1/2">
            {icon}
          </span>
        )}
      </div>
      {error && <p className="text-sm text-red-500">{error}</p>}
    </div>
  );
};
