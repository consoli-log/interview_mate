"use client";

import React, { useState, useRef, useCallback, useMemo } from "react";
import clsx from "clsx";
import { RecordingState } from "@/hooks/useRecording";
import RecordingButton from "@/components/Record/RecordingButton";
import { useAudioUrl } from "@/hooks/useAudioUrl";

type QuestionCardProps = {
  question: string;
  onAnswerRecorded?: (audioBlob: Blob) => void;
  onSaveRecording?: (audioBlob: Blob, questionId: string) => Promise<void>;
  questionId?: string;
  className?: string;
  maxRecordingDuration?: number;
};

const QuestionCard = ({
  question,
  onAnswerRecorded,
  onSaveRecording,
  questionId = "",
  className,
  maxRecordingDuration = 120,
}: QuestionCardProps) => {
  const [recordingState, setRecordingState] = useState<RecordingState>("ready");
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const {
    audioUrl,
    createAudioUrl,
    handleStateChange: handleAudioUrlStateChange,
  } = useAudioUrl();

  const audioBlobRef = useRef<Blob | null>(null);

  const handleRecordingStop = useCallback(
    (audioBlob: Blob) => {
      if (!audioBlob) {
        setError("녹음된 오디오를 처리할 수 없습니다.");
        return;
      }

      try {
        if (onAnswerRecorded) {
          onAnswerRecorded(audioBlob);
        }

        audioBlobRef.current = audioBlob;
        createAudioUrl(audioBlob);
        setError(null);
      } catch (err) {
        console.error("오디오 처리 오류:", err);
        setError("오디오 처리 중 오류가 발생했습니다.");
      }
    },
    [onAnswerRecorded, createAudioUrl]
  );

  const handleStateChange = useCallback(
    (newState: RecordingState) => {
      setRecordingState(newState);
      handleAudioUrlStateChange(newState);

      if (newState === "ready") {
        audioBlobRef.current = null;
        setError(null);
      }
    },
    [handleAudioUrlStateChange]
  );

  const handleSaveRecording = useCallback(async () => {
    if (!audioBlobRef.current || !onSaveRecording) {
      setError("저장할 녹음이 없습니다.");
      return;
    }

    try {
      setSaving(true);
      setError(null);
      await onSaveRecording(audioBlobRef.current, questionId);
    } catch (error) {
      console.error("저장 실패:", error);
      setError("녹음 저장에 실패했습니다. 다시 시도해주세요.");
    } finally {
      setSaving(false);
    }
  }, [onSaveRecording, questionId]);

  const handleDelete = useCallback(() => {
    handleStateChange("ready");
  }, [handleStateChange]);

  const buttonStyles = useMemo(
    () => ({
      save: clsx(
        "px-3 py-1 text-sm rounded-md transition-colors",
        "bg-green-100 text-white-100 hover:bg-green-200",
        "disabled:opacity-50 disabled:cursor-not-allowed"
      ),
      delete: clsx(
        "px-3 py-1 text-sm rounded-md transition-colors",
        "bg-gray-100 text-black-100 hover:bg-gray-200"
      ),
    }),
    []
  );

  const containerStyle = useMemo(
    () => clsx("p-6 rounded-2xl bg-white-100 shadow-sm mb-4 w-full", className),
    [className]
  );

  return (
    <div className={containerStyle} data-testid="question-card">
      <h2 className="m-0 mb-6 text-2xl font-bold leading-tight text-black-100">
        {question}
      </h2>

      <div className="ml-2">
        <RecordingButton
          recordingState={recordingState}
          onStateChange={handleStateChange}
          onRecordingStop={handleRecordingStop}
          maxDuration={maxRecordingDuration}
        />
      </div>

      {error && (
        <div className="mt-2 p-2 text-sm text-red-500 bg-red-50 rounded-md">
          {error}
        </div>
      )}

      {audioUrl && recordingState === "completed" && (
        <div className="mt-4 border-t pt-4">
          <div className="flex items-center justify-between mb-2">
            <p className="text-sm text-gray-200 font-medium">녹음된 답변:</p>
            <div className="flex gap-2">
              {onSaveRecording && (
                <button
                  onClick={handleSaveRecording}
                  disabled={saving}
                  className={buttonStyles.save}
                  aria-label="녹음 저장하기"
                >
                  {saving ? "저장 중..." : "저장"}
                </button>
              )}
              <button
                onClick={handleDelete}
                className={buttonStyles.delete}
                aria-label="녹음 삭제하기"
              >
                삭제
              </button>
            </div>
          </div>
          <audio
            controls
            src={audioUrl}
            className="w-full"
            aria-label={`${question}에 대한 녹음된 답변`}
          />
        </div>
      )}
    </div>
  );
};

export default QuestionCard;
