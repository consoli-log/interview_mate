"use client";

import React from "react";
import clsx from "clsx";
import { Button } from "../ui/button";
import { RecordingState, useRecording } from "@/hooks/useRecording";

interface RecordingButtonProps {
  maxDuration?: number;
  onRecordingStart?: () => void;
  onRecordingStop?: (audioBlob: Blob) => void;
  onRecordingCancel?: () => void;
  className?: string;
  recordingState?: RecordingState;
  onStateChange?: (state: RecordingState) => void;
}

const RecordingButton: React.FC<RecordingButtonProps> = ({
  maxDuration,
  onRecordingStart,
  onRecordingStop,
  onRecordingCancel,
  className = "",
  recordingState,
  onStateChange,
}) => {
  const {
    state,
    time,
    startRecording,
    stopRecording,
    cancelRecording,
    resetRecording,
    formatTime,
  } = useRecording({
    maxDuration,
    onRecordingStart,
    onRecordingStop,
    onRecordingCancel,
    initialState: recordingState,
    onStateChange,
  });

  const getIconColorClass = () => {
    switch (state) {
      case "ready":
        return "bg-green-100";
      case "recording":
        return "bg-red-100";
      case "completed":
        return "bg-gray-200";
      default:
        return "bg-green-100";
    }
  };

  const getButtonText = () => {
    switch (state) {
      case "ready":
        return "녹음 준비 중";
      case "recording":
        return `녹음중 ${formatTime(time)}`;
      case "completed":
        return "녹음 완료";
      default:
        return "녹음 준비 중";
    }
  };

  const handleClick = () => {
    switch (state) {
      case "ready":
        startRecording().catch(() => {});
        break;
      case "recording":
        stopRecording();
        break;
      case "completed":
        resetRecording();
        break;
    }
  };

  return (
    <div className="flex flex-col items-start">
      <Button
        variant="secondary"
        className={clsx(
          "flex items-center px-6 py-3 rounded-full",
          "cursor-pointer transition-all duration-200 ease-in-out",
          "font-medium text-base text-black-100",
          "bg-gray-100 border-none",
          className
        )}
        onClick={handleClick}
      >
        <div
          className={clsx("w-6 h-6 rounded-full mr-2", getIconColorClass())}
        />
        <span>{getButtonText()}</span>
      </Button>

      {state === "recording" && (
        <Button
          variant="secondary"
          onClick={cancelRecording}
          className="mt-2 ml-1 bg-transparent border-none text-red-100 cursor-pointer text-sm px-2 py-1 hover:bg-transparent"
        >
          취소
        </Button>
      )}
    </div>
  );
};

export default RecordingButton;
