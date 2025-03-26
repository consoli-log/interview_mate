import React from "react";
import { ButtonHTMLAttributes } from "react";
import Image from "next/image";
import { Button } from "@/components/common";

/**
 * 카카오 로그인 버튼 Props 타입
 */
interface KakaoLoginButtonProps
  extends Omit<ButtonHTMLAttributes<HTMLButtonElement>, "children"> {
  text?: string;
  className?: string;
}

/**
 * 카카오 로그인 버튼 컴포넌트
 */
export default function KakaoLoginButton({
  text = "카카오로 시작하기",
  disabled = false,
  className = "",
  ...rest
}: KakaoLoginButtonProps) {
  return (
    <Button
      variant="primary"
      disabled={disabled}
      className={`w-full mb-3 ${className}`}
      {...rest}
    >
      <span className="mr-2 flex items-center">
        <Image
          src="/images/icons/kakao_icon.svg"
          alt="카카오 아이콘"
          width={20}
          height={20}
        />
      </span>
      {text}
    </Button>
  );
}
