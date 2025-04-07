"use client";

import React, { useEffect } from "react";
import Link from "next/link";
import Image from "next/image";
import { useAuth } from "@/hooks/useAuth";
import LoginButton from "@/components/layout/Header/LoginButton";
import SignupButton from "@/components/layout/Header/SignupButton";

export default function Header() {
  const { isAuthenticated, checkAuthStatus } = useAuth();

  useEffect(() => {
    checkAuthStatus();
  }, [checkAuthStatus]);

  return (
    <header className="w-full flex items-center justify-between px-6 py-4 border-b border-gray-200">
      <div className="flex items-center">
        <Link href="/" aria-label="홈으로 이동">
          <Image
            src="/images/icons/interview_mate_logo.svg"
            alt="Interview Mate"
            width={120}
            height={24}
            priority
          />
        </Link>
      </div>

      {!isAuthenticated && (
        <nav className="flex items-center gap-2">
          <LoginButton
            onClick={() => console.log("로그인 버튼 클릭")}
            aria-label="로그인"
          />
          <SignupButton
            onClick={() => console.log("회원가입 버튼 클릭")}
            aria-label="회원가입"
          />
        </nav>
      )}
    </header>
  );
}
