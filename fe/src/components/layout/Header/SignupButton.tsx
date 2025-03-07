import React from "react";
import { ButtonHTMLAttributes } from "react";
import { Button } from "@/components/common";

/**
 * 회원가입 CTA 버튼 Props 타입
 */
interface SignupButtonProps
  extends Omit<ButtonHTMLAttributes<HTMLButtonElement>, "children"> {
  className?: string;
  isLoading?: boolean;
}

/**
 * 회원가입 CTA 버튼 컴포넌트
 */
export default function SignupButton({
  disabled = false,
  isLoading = false,
  className = "",
  ...rest
}: SignupButtonProps) {
  return (
    <Button
      variant="secondary"
      disabled={disabled || isLoading}
      className={`px-6 mb-2 border-yellow-400 text-yellow-500 hover:bg-yellow-50 active:bg-yellow-100 ${className}`}
      {...rest}
    >
      회원가입
    </Button>
  );
}
