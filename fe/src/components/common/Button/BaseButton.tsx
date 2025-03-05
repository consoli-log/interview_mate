import React from "react";
import { ButtonHTMLAttributes, ReactNode } from "react";

/**
 * 기본 버튼 Props 타입
 */
export interface BaseButtonProps
  extends ButtonHTMLAttributes<HTMLButtonElement> {
  children: ReactNode;
  className?: string;
}

export default function BaseButton({
  children,
  className = "",
  ...rest
}: BaseButtonProps) {
  return (
    <button
      className={`py-2 px-4 rounded-md transition-colors duration-200 flex items-center justify-center ${className}`}
      {...rest}
    >
      {children}
    </button>
  );
}
