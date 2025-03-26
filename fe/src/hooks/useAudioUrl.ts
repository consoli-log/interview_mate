"use client";

import { useState, useCallback, useEffect, useRef } from "react";
import { RecordingState } from "@/hooks/useRecording";

export function useAudioUrl() {
  const [audioUrl, setAudioUrl] = useState<string | null>(null);
  const audioUrlRef = useRef<string | null>(null);

  const clearPreviousUrl = useCallback(() => {
    if (audioUrlRef.current) {
      URL.revokeObjectURL(audioUrlRef.current);
      audioUrlRef.current = null;
    }
  }, []);

  const createAudioUrl = useCallback(
    (audioBlob: Blob): string => {
      if (!audioBlob) {
        console.error("유효하지 않은 오디오 Blob입니다.");
        return "";
      }

      try {
        clearPreviousUrl();

        const url = URL.createObjectURL(audioBlob);
        setAudioUrl(url);
        audioUrlRef.current = url;

        return url;
      } catch (error) {
        console.error("오디오 URL 생성 중 오류 발생:", error);
        return "";
      }
    },
    [clearPreviousUrl]
  );

  const revokeAudioUrl = useCallback(() => {
    clearPreviousUrl();
    setAudioUrl(null);
  }, [clearPreviousUrl]);

  const handleStateChange = useCallback(
    (newState: RecordingState) => {
      if (newState === "ready") {
        revokeAudioUrl();
      }
    },
    [revokeAudioUrl]
  );

  useEffect(() => {
    return () => {
      clearPreviousUrl();
    };
  }, [clearPreviousUrl]);

  return {
    audioUrl,
    createAudioUrl,
    revokeAudioUrl,
    handleStateChange,
    isAudioAvailable: !!audioUrl,
  };
}

export default useAudioUrl;
