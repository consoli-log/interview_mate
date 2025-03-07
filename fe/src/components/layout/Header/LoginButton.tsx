import React from "react";
import { ButtonHTMLAttributes } from "react";
import { Button } from "@/components/common";

/**
 * 로그인 CTA 버튼 Props 타입
 */
interface LoginButtonProps
  extends Omit<ButtonHTMLAttributes<HTMLButtonElement>, "children"> {
  className?: string;
  isLoading?: boolean;
}

/**
 * 로그인 CTA 버튼 컴포넌트
 */
export default function LoginButton({
  disabled = false,
  className = "",
  isLoading = false,
  ...rest
}: LoginButtonProps) {
  return (
    <Button
      variant="primary"
      disabled={disabled || isLoading}
      className={`px-6 mb-2 ${className}`}
      {...rest}
    >
      로그인
    </Button>
  );
}
