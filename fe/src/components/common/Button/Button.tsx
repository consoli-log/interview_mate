import React from "react";
import { ButtonHTMLAttributes, ReactNode } from "react";
import BaseButton from "./BaseButton";

export type ButtonVariant = "primary" | "secondary";

export interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  children: ReactNode;
  variant?: ButtonVariant;
  disabled?: boolean;
  className?: string;
}

export default function Button({
  children,
  variant = "primary",
  disabled = false,
  className = "",
  ...rest
}: ButtonProps) {
  let variantClasses = "";
  if (disabled) {
    variantClasses = "bg-gray-200 text-gray-500 cursor-not-allowed";
  } else if (variant === "primary") {
    variantClasses =
      "bg-yellow-300 hover:bg-yellow-400 active:bg-yellow-500 text-black font-bold";
  } else if (variant === "secondary") {
    variantClasses =
      "bg-white hover:bg-gray-100 active:bg-gray-200 text-black font-bold border border-gray-300";
  }

  return (
    <BaseButton
      className={`${variantClasses} ${className}`}
      disabled={disabled}
      {...rest}
    >
      {children}
    </BaseButton>
  );
}
