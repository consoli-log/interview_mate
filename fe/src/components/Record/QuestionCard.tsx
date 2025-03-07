"use client";

import React, { useState, useRef } from "react";
import { RecordingState } from "@/hooks/useRecording";
import RecordingButton from "@/components/Record/RecordingButton";
import { useAudioUrl } from "@/hooks/useAudioUrl";

interface QuestionCardProps {
  question: string;
  onAnswerRecorded?: (audioBlob: Blob) => void;
  onSaveRecording?: (audioBlob: Blob, questionId: string) => Promise<void>;
  questionId?: string;
  className?: string;
}

const QuestionCard: React.FC<QuestionCardProps> = ({
  question,
  onAnswerRecorded,
  onSaveRecording,
  questionId = "",
  className,
}) => {
  const [recordingState, setRecordingState] = useState<RecordingState>("ready");
  const [saving, setSaving] = useState(false);
  const {
    audioUrl,
    createAudioUrl,
    handleStateChange: handleAudioUrlStateChange,
  } = useAudioUrl();

  const audioBlobRef = useRef<Blob | null>(null);

  const handleRecordingStop = (audioBlob: Blob) => {
    if (onAnswerRecorded) {
      onAnswerRecorded(audioBlob);
    }

    audioBlobRef.current = audioBlob;
    createAudioUrl(audioBlob);
  };

  const handleStateChange = (newState: RecordingState) => {
    setRecordingState(newState);
    handleAudioUrlStateChange(newState);

    if (newState === "ready") {
      audioBlobRef.current = null;
    }
  };

  const handleSaveRecording = async () => {
    if (!audioBlobRef.current || !onSaveRecording) return;

    try {
      setSaving(true);
      await onSaveRecording(audioBlobRef.current, questionId);
    } catch (error) {
      console.error("저장 실패:", error);
    } finally {
      setSaving(false);
    }
  };

  return (
    <div
      className={`p-6 rounded-2xl bg-white-100 shadow-sm mb-4 w-full ${className}`}
    >
      <h2 className="m-0 mb-6 text-2xl font-bold leading-tight text-black-100">
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
            <p className="text-sm text-gray-200">녹음된 답변:</p>
            <div className="flex gap-2">
              {onSaveRecording && (
                <button
                  onClick={handleSaveRecording}
                  disabled={saving}
                  className="px-3 py-1 text-sm bg-green-100 text-white-100 rounded-md hover:bg-green-200 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {saving ? "저장 중..." : "저장"}
                </button>
              )}
              <button
                onClick={() => handleStateChange("ready")}
                className="px-3 py-1 text-sm bg-gray-100 text-black-100 rounded-md hover:bg-gray-200 transition-colors"
              >
                삭제
              </button>
            </div>
          </div>
          <audio controls src={audioUrl} className="w-full" />
        </div>
      )}
    </div>
  );
};

export default QuestionCard;
