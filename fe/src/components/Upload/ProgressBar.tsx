import React from "react";

export type ProgressLevel = "low" | "medium" | "high" | "empty";

export interface ProgressBarProps {
  value: number;
  lowThreshold?: number;
  mediumThreshold?: number;
  highThreshold?: number;
  className?: string;
  textLengthMode?: boolean;
}

export default function ProgressBar({
  value,
  lowThreshold = 20,
  mediumThreshold = 50,
  highThreshold = 100,
  className = "",
  textLengthMode = true,
}: ProgressBarProps) {
  const calculateProgress = (): number => {
    if (!textLengthMode) {
      return Math.min(Math.max(value, 0), 100);
    }

    if (value === 0) return 0;
    if (value < lowThreshold) {
      return (value / lowThreshold) * 20;
    } else if (value < mediumThreshold) {
      return (
        20 + ((value - lowThreshold) / (mediumThreshold - lowThreshold)) * 30
      );
    } else {
      const remainingProgress =
        Math.min(value, highThreshold) - mediumThreshold;
      const maxRemainingProgress = highThreshold - mediumThreshold;
      return 50 + (remainingProgress / maxRemainingProgress) * 50;
    }
  };

  const getProgressLevel = (): ProgressLevel => {
    if (value === 0) return "empty";
    if (textLengthMode) {
      if (value < lowThreshold) return "low";
      if (value < mediumThreshold) return "medium";
      return "high";
    } else {
      if (value < 20) return "low";
      if (value < 50) return "medium";
      return "high";
    }
  };

  const getProgressColor = (level: ProgressLevel): string => {
    switch (level) {
      case "low":
        return "#FF6F61";
      case "medium":
        return "#FECD4A";
      case "high":
        return "#1DB954";
      case "empty":
        return "#E5E5E5";
      default:
        return "#E5E5E5";
    }
  };

  const progress = calculateProgress();
  const level = getProgressLevel();
  const color = getProgressColor(level);

  return (
    <div
      className={`w-full h-2 bg-gray-100 rounded-full overflow-hidden ${className}`}
    >
      <div
        className="h-full transition-all duration-300 ease-out"
        style={{
          width: `${progress}%`,
          backgroundColor: color,
        }}
      />
    </div>
  );
}
