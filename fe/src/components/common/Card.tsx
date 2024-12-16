import { ReactNode } from "react";

interface CardProps {
  children: ReactNode;
  className?: string;
  onClick?: () => void;
}

export const Card = ({ children, className = "", onClick }: CardProps) => {
  return (
    <div className={`rounded-lg border p-4 ${className}`} onClick={onClick}>
      {children}
    </div>
  );
};
