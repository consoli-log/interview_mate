"use client";

import React, { useState } from "react";
import { RecordingState } from "@/hooks/useRecording";
import RecordingButton from "@/components/Record/RecordingButton";

interface QuestionCardProps {
  question: string;
  onAnswerRecorded?: (audioBlob: Blob) => void;
}

const QuestionCard: React.FC<QuestionCardProps> = ({
  question,
  onAnswerRecorded,
}) => {
  const [recordingState, setRecordingState] = useState<RecordingState>("ready");
  const [audioUrl, setAudioUrl] = useState<string | null>(null);

  const handleRecordingStop = (audioBlob: Blob) => {
    if (onAnswerRecorded) {
      onAnswerRecorded(audioBlob);
    }

    // 오디오 미리보기 URL 생성
    const url = URL.createObjectURL(audioBlob);
    setAudioUrl(url);
  };

  const handleStateChange = (newState: RecordingState) => {
    setRecordingState(newState);

    // 새로운 녹음이 시작되면 이전 오디오 URL 정리
    if (newState === "ready" && audioUrl) {
      URL.revokeObjectURL(audioUrl);
      setAudioUrl(null);
    }
  };

  return (
    <div
      style={{
        padding: "24px",
        borderRadius: "16px",
        backgroundColor: "white",
        boxShadow: "0 2px 10px rgba(0, 0, 0, 0.05)",
        marginBottom: "16px",
        width: "100%",
      }}
    >
      <h2
        style={{
          margin: "0 0 24px 0",
          fontSize: "24px",
          fontWeight: "700",
          lineHeight: "1.4",
          color: "#1a1a1a",
        }}
      >
        {question}
      </h2>

      <div style={{ marginLeft: "8px" }}>
        <RecordingButton
          recordingState={recordingState}
          onStateChange={handleStateChange}
          onRecordingStop={handleRecordingStop}
          maxDuration={120}
        />
      </div>

      {audioUrl && recordingState === "completed" && (
        <div style={{ marginTop: "16px" }}>
          <p style={{ fontSize: "14px", color: "#666", marginBottom: "8px" }}>
            녹음된 답변:
          </p>
          <audio controls src={audioUrl} style={{ width: "100%" }} />
        </div>
      )}
    </div>
  );
};

const ExampleUsage: React.FC = () => {
  const handleAnswerRecorded = (question: string, audioBlob: Blob) => {
    console.log(`"${question}" 답변 녹음 완료:`, audioBlob);
  };

  return (
    <div style={{ maxWidth: "800px", margin: "0 auto", padding: "20px" }}>
      <QuestionCard
        question="SSR, CSR, SSG에 대해 설명해 주세요."
        onAnswerRecorded={(audioBlob) =>
          handleAnswerRecorded("SSR, CSR, SSG에 대해 설명해 주세요.", audioBlob)
        }
      />

      <QuestionCard
        question="React의 가상 DOM(Virtual DOM)에 대해 설명해 주세요."
        onAnswerRecorded={(audioBlob) =>
          handleAnswerRecorded(
            "React의 가상 DOM에 대해 설명해 주세요.",
            audioBlob
          )
        }
      />
    </div>
  );
};

export default ExampleUsage;
