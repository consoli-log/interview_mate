"use client";

import React from "react";
import { useRouter } from "next/navigation";
import { Sidebar } from "@/components";

// 목업 데이터
const mockPosts = [
  {
    id: "1",
    title: "React로 CMS를 작업하였고, ...",
    date: "오늘",
  },
  {
    id: "2",
    title: "React로 CMS를 작업하였고, ...",
    date: "오늘",
  },
  {
    id: "3",
    title: "Next.js와 Tailwind로 포트폴리오 사이트 만들기",
    date: "어제",
  },
  {
    id: "4",
    title: "TypeScript 타입 시스템 활용하기",
    date: "어제",
  },
  {
    id: "5",
    title: "반응형 디자인 구현하기",
    date: "지난주",
  },
];

export default function ClientSidebar() {
  const router = useRouter();

  const handleAddPost = () => {
    router.push("/portfolio/new");
  };

  const handlePostClick = (id: string) => {
    router.push(`/portfolio/${id}`);
  };

  return (
    <div className="w-64 flex-shrink-0">
      <Sidebar
        posts={mockPosts}
        onAddPost={handleAddPost}
        onPostClick={handlePostClick}
      />
    </div>
  );
}
