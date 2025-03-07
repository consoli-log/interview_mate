import React, {
  useState,
  forwardRef,
  InputHTMLAttributes,
  ReactNode,
  useCallback,
  useMemo,
} from "react";
import UploadButton from "./UploadButton";
import ProgressBar from "./ProgressBar";

export type InputWithUploadProgressState = "default" | "focused" | "disabled";
export type FeedbackLevel = "low" | "medium" | "high" | "none";

export interface InputWithUploadProgressProps
  extends Omit<InputHTMLAttributes<HTMLInputElement>, "size"> {
  className?: string;
  uploadButtonVariant?: "primary" | "secondary" | "ghost";
  disabled?: boolean;
  onUploadClick?: () => void;
  showProgressBar?: boolean;
  lowThreshold?: number;
  mediumThreshold?: number;
  highThreshold?: number;
  customRightElement?: ReactNode;
}

const STATE_STYLES: Record<InputWithUploadProgressState, string> = {
  default: "border-gray-200",
  focused: "border-yellow-200",
  disabled: "bg-gray-50 border-gray-200",
};

const FEEDBACK_INFO: Record<FeedbackLevel, { message: string; color: string }> =
  {
    low: {
      message: "입력이 너무 짧습니다. 더 많은 정보를 입력해주세요.",
      color: "#FF6F61",
    },
    medium: {
      message: "적절하지만, 좀 더 추가하면 좋습니다.",
      color: "#FECD4A",
    },
    high: {
      message: "충분한 정보가 입력되었습니다.",
      color: "#1DB954",
    },
    none: {
      message: "",
      color: "",
    },
  };

const InputWithUploadProgress = forwardRef<
  HTMLInputElement,
  InputWithUploadProgressProps
>(
  (
    {
      placeholder = "포트폴리오를 입력해주세요.",
      className = "",
      uploadButtonVariant = "secondary",
      disabled = false,
      onUploadClick,
      showProgressBar = true,
      lowThreshold = 20,
      mediumThreshold = 50,
      highThreshold = 100,
      customRightElement,
      ...rest
    },
    ref
  ) => {
    const [isFocused, setIsFocused] = useState<boolean>(false);
    const [value, setValue] = useState<string>((rest.value as string) || "");

    const handleChange = useCallback(
      (e: React.ChangeEvent<HTMLInputElement>) => {
        setValue(e.target.value);
        rest.onChange?.(e);
      },
      [rest.onChange]
    );

    const handleFocus = useCallback(
      (e: React.FocusEvent<HTMLInputElement>) => {
        setIsFocused(true);
        rest.onFocus?.(e);
      },
      [rest.onFocus]
    );

    const handleBlur = useCallback(
      (e: React.FocusEvent<HTMLInputElement>) => {
        setIsFocused(false);
        rest.onBlur?.(e);
      },
      [rest.onBlur]
    );

    const inputState: InputWithUploadProgressState = useMemo(() => {
      return disabled ? "disabled" : isFocused ? "focused" : "default";
    }, [disabled, isFocused]);

    const feedbackLevel: FeedbackLevel = useMemo(() => {
      const textLength = value.length;

      if (textLength === 0) return "none";
      if (textLength < lowThreshold) return "low";
      if (textLength < mediumThreshold) return "medium";
      return "high";
    }, [value.length, lowThreshold, mediumThreshold]);

    const containerStyle = STATE_STYLES[inputState];

    const { message, color } = FEEDBACK_INFO[feedbackLevel];

    const textLength = value.length;

    return (
      <div className={className}>
        <div
          className={`relative rounded-2xl border ${containerStyle} overflow-hidden`}
        >
          <div className="flex items-center p-4">
            {/* 텍스트 입력 필드 */}
            <input
              ref={ref}
              type="text"
              className="flex-1 bg-transparent outline-none text-gray-700"
              placeholder={placeholder}
              disabled={disabled}
              value={value}
              onChange={handleChange}
              onFocus={handleFocus}
              onBlur={handleBlur}
              {...rest}
            />

            {/* 업로드 버튼 또는 커스텀 요소 */}
            {customRightElement || (
              <UploadButton
                variant={uploadButtonVariant}
                onClick={onUploadClick}
                disabled={disabled}
                aria-label="Upload"
              />
            )}
          </div>
        </div>

        {/* 진행률 표시줄 */}
        {showProgressBar && textLength > 0 && (
          <div className="mt-2">
            <ProgressBar
              value={textLength}
              lowThreshold={lowThreshold}
              mediumThreshold={mediumThreshold}
              highThreshold={highThreshold}
            />
          </div>
        )}

        {/* 피드백 메시지 */}
        {textLength > 0 && feedbackLevel !== "none" && (
          <p className="mt-1 text-sm" style={{ color }}>
            {message}
          </p>
        )}
      </div>
    );
  }
);

InputWithUploadProgress.displayName = "InputWithUploadProgress";

export default InputWithUploadProgress;
