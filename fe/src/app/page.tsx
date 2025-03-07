"use client";

import React, { useState } from "react";
import InputWithUploadProgress from "@/components/Upload/InputWithUploadProgress";

export default function Main() {
  const [inputValue, setInputValue] = useState("");

  const handleInputChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setInputValue(e.target.value);
  };

  const handleUploadClick = () => {
    console.log("업로드 버튼 클릭:", inputValue);
  };

  return (
    <div className="p-6">
      <h1 className="text-2xl font-bold mb-4">업로드 컴포넌트 테스트</h1>

      <InputWithUploadProgress
        placeholder="포트폴리오를 입력하세요..."
        value={inputValue}
        onChange={handleInputChange}
        onUploadClick={handleUploadClick}
      />
    </div>
  );
}
