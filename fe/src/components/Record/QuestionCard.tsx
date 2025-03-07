"use client";

import React, { useState } from "react";
import { RecordingState } from "@/hooks/useRecording";
import RecordingButton from "@/components/Record/RecordingButton";
import { useAudioUrl } from "@/hooks/useAudioUrl";

interface QuestionCardProps {
  question: string;
  onAnswerRecorded?: (audioBlob: Blob) => void;
  className?: string;
}

const QuestionCard: React.FC<QuestionCardProps> = ({
  question,
  onAnswerRecorded,
  className,
}) => {
  const [recordingState, setRecordingState] = useState<RecordingState>("ready");
  const {
    audioUrl,
    createAudioUrl,
    handleStateChange: handleAudioUrlStateChange,
  } = useAudioUrl();

  const handleRecordingStop = (audioBlob: Blob) => {
    if (onAnswerRecorded) {
      onAnswerRecorded(audioBlob);
    }

    createAudioUrl(audioBlob);
  };

  const handleStateChange = (newState: RecordingState) => {
    setRecordingState(newState);
    handleAudioUrlStateChange(newState);
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
          <p className="text-sm text-gray-200 mb-2">녹음된 답변:</p>
          <audio controls src={audioUrl} className="w-full" />
        </div>
      )}
    </div>
  );
};

export default QuestionCard;
