"use client";

import { useState, useRef, useCallback, useEffect } from "react";

export type RecordingState = "ready" | "recording" | "completed";

interface MediaError {
  name: string;
  message: string;
}

interface RecordingHookProps {
  maxDuration?: number;
  onRecordingStart?: () => void;
  onRecordingStop?: (audioBlob: Blob) => void;
  onRecordingCancel?: () => void;
  initialState?: RecordingState;
  onStateChange?: (state: RecordingState) => void;
}

interface RecordingHookResult {
  state: RecordingState;
  time: number;
  startRecording: () => Promise<void>;
  stopRecording: () => void;
  cancelRecording: () => void;
  resetRecording: () => void;
  formatTime: (seconds: number) => string;
}

export function useRecording({
  maxDuration = 180,
  onRecordingStart,
  onRecordingStop,
  onRecordingCancel,
  initialState = "ready",
  onStateChange,
}: RecordingHookProps = {}): RecordingHookResult {
  const [state, setState] = useState<RecordingState>(initialState);
  const [time, setTime] = useState<number>(0);

  const mediaRecorderRef = useRef<MediaRecorder | null>(null);
  const audioChunksRef = useRef<Blob[]>([]);
  const streamRef = useRef<MediaStream | null>(null);
  const timerRef = useRef<NodeJS.Timeout | null>(null);

  // 상태 변경 함수
  const updateState = useCallback(
    (newState: RecordingState) => {
      setState(newState);
      if (onStateChange) {
        onStateChange(newState);
      }
    },
    [onStateChange]
  );

  // 오류 처리 함수
  const handleRecordingError = useCallback((error: MediaError): string => {
    console.error("마이크 접근 권한 오류:", error);

    if (error.name === "NotAllowedError") {
      if (error.message.includes("Permission denied by system")) {
        return "시스템에서 마이크 접근을 차단했습니다. 운영체제 설정에서 브라우저의 마이크 접근 권한을 허용해주세요.";
      } else {
        return "마이크 접근 권한이 거부되었습니다. 브라우저 주소창에서 마이크 권한을 허용해주세요.";
      }
    } else if (error.name === "NotFoundError") {
      return "마이크를 찾을 수 없습니다. 마이크가 제대로 연결되어 있는지 확인해주세요.";
    } else {
      return `마이크 사용 오류: ${error.message}`;
    }
  }, []);

  // 타이머 관리 함수
  const startTimer = useCallback(() => {
    timerRef.current = setInterval(() => {
      setTime((prev) => {
        const newTime = prev + 1;
        if (newTime >= maxDuration) {
          stopRecording();
          return maxDuration;
        }
        return newTime;
      });
    }, 1000);
  }, [maxDuration]);

  const stopTimer = useCallback(() => {
    if (timerRef.current) {
      clearInterval(timerRef.current);
      timerRef.current = null;
    }
  }, []);

  // 녹음 시작 함수
  const startRecording = useCallback(async () => {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({
        audio: true,
      });
      streamRef.current = stream;

      const mediaRecorder = new MediaRecorder(stream);
      mediaRecorderRef.current = mediaRecorder;

      mediaRecorder.ondataavailable = (event) => {
        if (event.data.size > 0) {
          audioChunksRef.current.push(event.data);
        }
      };

      mediaRecorder.onstop = () => {
        if (audioChunksRef.current.length === 0) return;

        const audioBlob = new Blob(audioChunksRef.current, {
          type: "audio/webm",
        });

        if (streamRef.current) {
          streamRef.current.getTracks().forEach((track) => track.stop());
        }

        if (onRecordingStop) {
          onRecordingStop(audioBlob);
        }
      };

      // 녹음 시작
      audioChunksRef.current = [];
      mediaRecorder.start();
      updateState("recording");
      setTime(0);

      startTimer();

      if (onRecordingStart) {
        onRecordingStart();
      }
    } catch (error) {
      const errorMessage = handleRecordingError(error as MediaError);
      alert(errorMessage);
      throw error;
    }
  }, [
    handleRecordingError,
    onRecordingStart,
    onRecordingStop,
    startTimer,
    updateState,
  ]);

  // 녹음 중지 함수
  const stopRecording = useCallback(() => {
    if (
      mediaRecorderRef.current &&
      mediaRecorderRef.current.state !== "inactive"
    ) {
      mediaRecorderRef.current.stop();
      stopTimer();
      updateState("completed");
    }
  }, [stopTimer, updateState]);

  // 녹음 취소 함수
  const cancelRecording = useCallback(() => {
    if (mediaRecorderRef.current) {
      if (mediaRecorderRef.current.state !== "inactive") {
        mediaRecorderRef.current.stop();
      }

      if (streamRef.current) {
        streamRef.current.getTracks().forEach((track) => track.stop());
      }

      stopTimer();
      audioChunksRef.current = [];
      updateState("ready");
      setTime(0);

      if (onRecordingCancel) {
        onRecordingCancel();
      }
    }
  }, [onRecordingCancel, stopTimer, updateState]);

  const resetRecording = useCallback(() => {
    updateState("ready");
    setTime(0);
  }, [updateState]);

  const formatTime = useCallback((seconds: number): string => {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = seconds % 60;
    return `${minutes}:${remainingSeconds < 10 ? "0" : ""}${remainingSeconds}`;
  }, []);

  useEffect(() => {
    return () => {
      stopTimer();

      if (
        mediaRecorderRef.current &&
        mediaRecorderRef.current.state !== "inactive"
      ) {
        mediaRecorderRef.current.stop();
      }

      if (streamRef.current) {
        streamRef.current.getTracks().forEach((track) => track.stop());
      }
    };
  }, [stopTimer]);

  return {
    state,
    time,
    startRecording,
    stopRecording,
    cancelRecording,
    resetRecording,
    formatTime,
  };
}
