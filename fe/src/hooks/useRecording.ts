"use client";

import { useState, useRef, useCallback, useEffect } from "react";

export type RecordingState = "ready" | "recording" | "completed";
export type AudioMimeType =
  | "audio/webm"
  | "audio/mp4"
  | "audio/ogg"
  | "audio/wav";

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
  audioType?: AudioMimeType;
  timeFormatter?: (seconds: number) => string;
  onError?: (error: Error, errorMessage: string) => void;
}

interface RecordingHookResult {
  state: RecordingState;
  time: number;
  startRecording: () => Promise<void>;
  stopRecording: () => void;
  cancelRecording: () => void;
  resetRecording: () => void;
  formatTime: (seconds: number) => string;
  isRecording: boolean;
  isCompleted: boolean;
  isMicrophoneAvailable: boolean | null;
}

export function useRecording({
  maxDuration = 180,
  onRecordingStart,
  onRecordingStop,
  onRecordingCancel,
  initialState = "ready",
  onStateChange,
  audioType,
  timeFormatter,
  onError,
}: RecordingHookProps = {}): RecordingHookResult {
  const [state, setState] = useState<RecordingState>(initialState);
  const [time, setTime] = useState<number>(0);
  const [isMicrophoneAvailable, setIsMicrophoneAvailable] = useState<
    boolean | null
  >(null);

  const mediaRecorderRef = useRef<MediaRecorder | null>(null);
  const audioChunksRef = useRef<Blob[]>([]);
  const streamRef = useRef<MediaStream | null>(null);
  const timerRef = useRef<NodeJS.Timeout | null>(null);

  const isRecording = state === "recording";
  const isCompleted = state === "completed";

  const updateState = useCallback(
    (newState: RecordingState) => {
      setState(newState);
      if (onStateChange) {
        onStateChange(newState);
      }
    },
    [onStateChange]
  );

  const stopTimer = useCallback(() => {
    if (timerRef.current) {
      clearInterval(timerRef.current);
      timerRef.current = null;
    }
  }, []);

  const cleanupStream = useCallback(() => {
    if (streamRef.current) {
      streamRef.current.getTracks().forEach((track) => track.stop());
      streamRef.current = null;
    }
  }, []);

  const cancelRecording = useCallback(() => {
    if (mediaRecorderRef.current) {
      try {
        if (mediaRecorderRef.current.state !== "inactive") {
          mediaRecorderRef.current.stop();
        }
      } catch (error) {
        console.error("녹음 취소 오류:", error);
      }

      cleanupStream();
      stopTimer();
      audioChunksRef.current = [];
      updateState("ready");
      setTime(0);

      if (onRecordingCancel) {
        onRecordingCancel();
      }
    }
  }, [onRecordingCancel, stopTimer, updateState, cleanupStream]);

  const stopRecording = useCallback(() => {
    if (
      mediaRecorderRef.current &&
      mediaRecorderRef.current.state !== "inactive"
    ) {
      try {
        mediaRecorderRef.current.stop();
        stopTimer();
        updateState("completed");
      } catch (error) {
        console.error("녹음 중지 오류:", error);
        cancelRecording();
      }
    }
  }, [stopTimer, updateState, cancelRecording]);

  const formatTime = useCallback(
    (seconds: number): string => {
      if (timeFormatter) {
        return timeFormatter(seconds);
      }

      const minutes = Math.floor(seconds / 60);
      const remainingSeconds = seconds % 60;
      return `${minutes}:${
        remainingSeconds < 10 ? "0" : ""
      }${remainingSeconds}`;
    },
    [timeFormatter]
  );

  const startTimer = useCallback(() => {
    stopTimer();

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
  }, [maxDuration, stopRecording, stopTimer]);

  const getBestAudioType = useCallback((): AudioMimeType => {
    if (audioType) return audioType;

    const types: AudioMimeType[] = [
      "audio/webm",
      "audio/mp4",
      "audio/ogg",
      "audio/wav",
    ];
    if (typeof MediaRecorder !== "undefined" && MediaRecorder.isTypeSupported) {
      for (const type of types) {
        if (MediaRecorder.isTypeSupported(type)) {
          return type;
        }
      }
    }
    return "audio/webm";
  }, [audioType]);

  const handleRecordingError = useCallback(
    (error: MediaError): string => {
      console.error("마이크 접근 권한 오류:", error);
      let errorMessage = "";

      switch (error.name) {
        case "NotAllowedError":
          errorMessage = error.message.includes("Permission denied by system")
            ? "시스템에서 마이크 접근을 차단했습니다. 운영체제 설정에서 브라우저의 마이크 접근 권한을 허용해주세요."
            : "마이크 접근 권한이 거부되었습니다. 브라우저 주소창에서 마이크 권한을 허용해주세요.";
          break;
        case "NotFoundError":
          errorMessage =
            "마이크를 찾을 수 없습니다. 마이크가 제대로 연결되어 있는지 확인해주세요.";
          break;
        case "NotReadableError":
        case "AbortError":
          errorMessage =
            "마이크에 접근할 수 없습니다. 다른 앱에서 사용 중인지 확인해주세요.";
          break;
        case "OverconstrainedError":
          errorMessage = "요청한 미디어 형식이 지원되지 않습니다.";
          break;
        case "TypeError":
          errorMessage = "잘못된 제약 조건이 지정되었습니다.";
          break;
        default:
          errorMessage = `마이크 사용 오류: ${error.message}`;
      }

      if (onError) {
        onError(error as unknown as Error, errorMessage);
      }

      return errorMessage;
    },
    [onError]
  );

  const startRecording = useCallback(async () => {
    try {
      if (isRecording) {
        return;
      }

      const stream = await navigator.mediaDevices.getUserMedia({
        audio: true,
      });

      setIsMicrophoneAvailable(true);
      streamRef.current = stream;

      const bestAudioType = getBestAudioType();
      const mediaRecorder = new MediaRecorder(stream, {
        mimeType: bestAudioType,
      });
      mediaRecorderRef.current = mediaRecorder;

      mediaRecorder.ondataavailable = (event) => {
        if (event.data && event.data.size > 0) {
          audioChunksRef.current.push(event.data);
        }
      };

      mediaRecorder.onstop = () => {
        if (audioChunksRef.current.length === 0) {
          return;
        }

        const audioBlob = new Blob(audioChunksRef.current, {
          type: bestAudioType,
        });

        cleanupStream();

        if (onRecordingStop) {
          onRecordingStop(audioBlob);
        }
      };

      mediaRecorder.onerror = (event) => {
        console.error("MediaRecorder 오류:", event);
        cancelRecording();

        if (onError) {
          onError(
            new Error("녹음 중 오류가 발생했습니다."),
            "녹음 중 오류가 발생했습니다."
          );
        }
      };

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

      if (onError) {
        onError(error as Error, errorMessage);
      } else {
        alert(errorMessage);
      }

      throw error;
    }
  }, [
    isRecording,
    handleRecordingError,
    onRecordingStart,
    onRecordingStop,
    startTimer,
    updateState,
    cleanupStream,
    getBestAudioType,
    onError,
    cancelRecording,
  ]);

  const resetRecording = useCallback(() => {
    updateState("ready");
    setTime(0);
  }, [updateState]);

  useEffect(() => {
    return () => {
      stopTimer();

      if (
        mediaRecorderRef.current &&
        mediaRecorderRef.current.state !== "inactive"
      ) {
        try {
          mediaRecorderRef.current.stop();
        } catch (error) {
          console.error("MediaRecorder 정리 오류:", error);
        }
      }

      cleanupStream();
    };
  }, [stopTimer, cleanupStream]);

  return {
    state,
    time,
    startRecording,
    stopRecording,
    cancelRecording,
    resetRecording,
    formatTime,
    isRecording,
    isCompleted,
    isMicrophoneAvailable,
  };
}

export default useRecording;
