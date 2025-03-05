import React from "react";
import { ButtonHTMLAttributes } from "react";
import Image from "next/image";
import { Button } from "../common";

/**
 * 구글 로그인 버튼 Props 타입
 */
interface GoogleLoginButtonProps
  extends Omit<ButtonHTMLAttributes<HTMLButtonElement>, "children"> {
  text?: string;
  className?: string;
}

/**
 * 구글 로그인 버튼 컴포넌트
 */
export default function GoogleLoginButton({
  text = "Google로 시작하기",
  disabled = false,
  className = "",
  ...rest
}: GoogleLoginButtonProps) {
  return (
    <Button
      variant="secondary"
      disabled={disabled}
      className={`w-full mb-3 ${className}`}
      {...rest}
    >
      <span className="mr-2 flex items-center">
        <Image
          src="/images/icons/google_icon.svg"
          alt="구글 아이콘"
          width={20}
          height={20}
        />
      </span>
      {text}
    </Button>
  );
}
