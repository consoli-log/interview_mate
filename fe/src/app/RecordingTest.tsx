"use client";

import React, { useState, useRef } from "react";
import { RecordingState } from "@/hooks/useRecording";
import RecordingButton from "@/components/Record/RecordingButton";

interface QuestionCardProps {
  question: string;
  questionId?: string;
  onAnswerRecorded?: (audioBlob: Blob) => void;
}

const QuestionCard: React.FC<QuestionCardProps> = ({
  question,
  questionId = "default",
  onAnswerRecorded,
}) => {
  const [recordingState, setRecordingState] = useState<RecordingState>("ready");
  const [audioUrl, setAudioUrl] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const audioBlobRef = useRef<Blob | null>(null);

  const handleRecordingStop = (audioBlob: Blob) => {
    if (onAnswerRecorded) {
      onAnswerRecorded(audioBlob);
    }

    audioBlobRef.current = audioBlob;

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
      audioBlobRef.current = null;
    }
  };

  const handleSaveRecording = async () => {
    if (!audioBlobRef.current) return;

    try {
      setSaving(true);

      console.log(`질문 ID ${questionId}에 대한 녹음 저장 중...`);

      alert("녹음이 저장되었습니다!");
    } catch (error) {
      console.error("저장 실패:", error);
      alert("저장 중 오류가 발생했습니다.");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="p-6 rounded-2xl bg-white shadow-sm mb-4 w-full">
      <h2 className="m-0 mb-6 text-2xl font-bold leading-tight text-black">
        {question}
      </h2>

      <div className="ml-2">
        <RecordingButton
          recordingState={recordingState}
          onStateChange={handleStateChange}
          onRecordingStop={handleRecordingStop}
          maxDuration={120}
        />
      </div>

      {audioUrl && recordingState === "completed" && (
        <div className="mt-4">
          <div className="flex items-center justify-between mb-2">
            <p className="text-sm text-gray-600">녹음된 답변:</p>
          </div>
          <audio controls src={audioUrl} className="w-full mb-2" />
          <div className="flex justify-end gap-2 mt-2">
            <button
              onClick={handleSaveRecording}
              disabled={saving}
              className="px-3 py-1 text-sm bg-yellow-200 text-white rounded-md hover:bg-yellow-100 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {saving ? "저장 중..." : "저장"}
            </button>
            <button
              onClick={() => handleStateChange("ready")}
              className="px-3 py-1 text-sm bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 transition-colors"
            >
              삭제
            </button>
          </div>
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
    <div className="max-w-4xl mx-auto p-5">
      <QuestionCard
        question="SSR, CSR, SSG에 대해 설명해 주세요."
        questionId="q1"
        onAnswerRecorded={(audioBlob) =>
          handleAnswerRecorded("SSR, CSR, SSG에 대해 설명해 주세요.", audioBlob)
        }
      />

      <QuestionCard
        question="React의 가상 DOM(Virtual DOM)에 대해 설명해 주세요."
        questionId="q2"
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
