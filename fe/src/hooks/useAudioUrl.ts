"use client";

import { useState, useCallback } from "react";
import { RecordingState } from "@/hooks/useRecording";

export function useAudioUrl() {
  const [audioUrl, setAudioUrl] = useState<string | null>(null);

  const createAudioUrl = useCallback(
    (audioBlob: Blob) => {
      if (audioUrl) {
        URL.revokeObjectURL(audioUrl);
      }

      const url = URL.createObjectURL(audioBlob);
      setAudioUrl(url);
      return url;
    },
    [audioUrl]
  );

  const revokeAudioUrl = useCallback(() => {
    if (audioUrl) {
      URL.revokeObjectURL(audioUrl);
      setAudioUrl(null);
    }
  }, [audioUrl]);

  const handleStateChange = useCallback(
    (newState: RecordingState) => {
      if (newState === "ready") {
        revokeAudioUrl();
      }
    },
    [revokeAudioUrl]
  );

  return {
    audioUrl,
    createAudioUrl,
    revokeAudioUrl,
    handleStateChange,
  };
}
