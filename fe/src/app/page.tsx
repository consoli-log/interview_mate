"use client";
import { useState } from "react";
import { Menu, X, Search } from "lucide-react";
import { Layout } from "@/layouts/Layout";
import { Input, Sidebar } from "@/components";
import GoogleLoginButton from "@/components/Auth/GoogleLoginButton";
import KakaoLoginButton from "@/components/Auth/KakaoLoginButton";
import Button from "@/components/common/Button/Button";
import LoginButton from "@/components/Header/LoginButton";
import SidebarButton from "@/components/Sidebar/SidebarButton";

export default function Home() {
  const [isOpen, setIsOpen] = useState(true);

  const posts = [
    { id: "1", title: "Next.js로 랜딩페이지 만들기" },
    { id: "2", title: "React 컴포넌트 설계하기" },
  ];

  return (
    <Layout
      isOpen={isOpen}
      onToggle={() => setIsOpen(!isOpen)}
      sidebar={({ isOpen, toggleSidebar }) => (
        <div className="flex flex-col h-full">
          <div
            className={`
              flex flex-col
              transform-gpu transition-all duration-300 ease-in-out
              ${
                isOpen
                  ? "translate-x-0 opacity-100"
                  : "-translate-x-full opacity-0"
              }
              overflow-hidden
            `}
            style={{
              width: isOpen ? "100%" : "0",
              visibility: isOpen ? "visible" : "hidden",
              transitionProperty: "transform, opacity, width, visibility",
            }}
          >
            <div className="p-4 min-w-[256px]">
              <Input
                placeholder="검색하기"
                icon={<Search size={18} className="text-gray-400" />}
                className="mb-4"
              />
              <Sidebar
                posts={posts}
                onAddPost={() => console.log("add post")}
                onPostClick={(id) => console.log("post clicked:", id)}
              />
            </div>
          </div>
        </div>
      )}
    >
      <div className="max-w-2xl mx-auto">
        <h1 className="text-2xl font-bold mb-4">user님, 안녕하세요</h1>
        <Input placeholder="포트폴리오를 입력해요" className="mb-4" />
        <Button>f</Button> 
        <SidebarButton></SidebarButton>
      </div>
    </Layout>
  );
}
