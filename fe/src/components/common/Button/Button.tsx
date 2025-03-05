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
      "bg-yellow-200 hover:bg-yellow-300 active:bg-yellow-100 text-black-100 font-bold";
  } else if (variant === "secondary") {
    variantClasses =
      "bg-white-200 hover:bg-gray-300 active:bg-gray-100 text-black-100 font-bold border border-gray-200";
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
