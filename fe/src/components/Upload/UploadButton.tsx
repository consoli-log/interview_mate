import React from "react";
import { ButtonHTMLAttributes } from "react";
import Image from "next/image";
import { Button } from "../ui/button";

export type UploadButtonVariant = "primary" | "secondary" | "ghost";

export interface UploadButtonProps
  extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: UploadButtonVariant;
  className?: string;
  disabled?: boolean;
}

const VARIANT_STYLES: Record<UploadButtonVariant, string> = {
  primary: "bg-yellow-200 hover:bg-yellow-100 active:bg-yellow-300 text-white",
  secondary:
    "bg-yellow-200 hover:bg-yellow-100 active:bg-yellow-300 text-black",
  ghost: "bg-gray-100 hover:bg-gray-100 active:bg-gray-100 text-gray-600",
};

const DISABLED_STYLE = "bg-gray-100 text-gray-500 cursor-not-allowed";
const ICON_SIZE = 24;

export default function UploadButton({
  variant = "secondary",
  className = "",
  disabled = false,
  ...rest
}: UploadButtonProps) {
  const buttonStyle = disabled ? DISABLED_STYLE : VARIANT_STYLES[variant];

  return (
    <Button
      className={`w-12 h-12 rounded-full p-0 flex items-center justify-center ${buttonStyle} ${className}`}
      disabled={disabled}
      {...rest}
    >
      <Image
        src="/images/icons/Upload_icon.svg"
        alt="Upload"
        width={ICON_SIZE}
        height={ICON_SIZE}
      />
    </Button>
  );
}
