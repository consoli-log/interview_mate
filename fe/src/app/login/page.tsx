import React from "react";
import Image from "next/image";
import KakaoLoginButton from "@/components/Auth/KakaoLoginButton";
import GoogleLoginButton from "@/components/Auth/GoogleLoginButton";

export default function LoginPage() {
  return (
    <div className="flex flex-col items-center justify-center h-screen bg-gray-50">
      <div className="bg-white h-full w-full max-w-lg mx-auto flex flex-col items-center justify-center md:shadow-sm">
        <div className="mb-8 mt-4">
          <Image
            src="/images/icons/interview_mate_logo.svg"
            alt="Interview Mate 로고"
            width={150}
            height={80}
            className="mx-auto"
          />
        </div>

        <div className="w-full max-w-xs px-6">
          <KakaoLoginButton
            text="카카오로 시작하기"
            className="bg-yellow-400 hover:bg-yellow-300 text-black font-medium"
          />

          <div className="relative my-4">
            <div className="absolute inset-0 flex items-center">
              <div className="w-full border-t border-gray-200"></div>
            </div>
            <div className="relative flex justify-center">
              <span className="px-3 bg-white text-sm text-gray-500">또는</span>
            </div>
          </div>

          <GoogleLoginButton
            text="Google로 시작하기"
            className="border border-gray-200"
          />
        </div>
      </div>
    </div>
  );
}
