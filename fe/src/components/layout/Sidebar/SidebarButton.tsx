import React from "react";
import { ButtonHTMLAttributes } from "react";
import Image from "next/image";
import { Button } from "@/components/ui/button";

/**
 * 사이드바 버튼 Props 타입
 */
interface SidebarButtonProps
  extends Omit<ButtonHTMLAttributes<HTMLButtonElement>, "children"> {
  text?: string;
  iconSrc?: string;
  className?: string;
}

export default function SidebarButton({
  text = "포폴 추가하기",
  iconSrc = "/images/icons/sidebar_icon.svg",
  disabled = false,
  className = "",
  ...rest
}: SidebarButtonProps) {
  return (
    <Button
      disabled={disabled}
      className={`w-full mb-3 text-lg ${className}`}
      {...rest}
    >
      <span className="mr-2 flex items-center">
        <Image src={iconSrc} alt="사이드바 아이콘" width={20} height={20} />
      </span>
      {text}
    </Button>
  );
}
