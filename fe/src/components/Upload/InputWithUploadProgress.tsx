import React, {
  useState,
  forwardRef,
  TextareaHTMLAttributes,
  ReactNode,
  useCallback,
  useMemo,
  useEffect,
} from "react";
import UploadButton from "./UploadButton";
import ProgressBar from "./ProgressBar";

export type TextAreaWithUploadProgressState =
  | "default"
  | "focused"
  | "disabled";
export type FeedbackLevel = "low" | "medium" | "high" | "none";

export interface TextAreaWithUploadProgressProps
  extends Omit<TextareaHTMLAttributes<HTMLTextAreaElement>, "size"> {
  className?: string;
  uploadButtonVariant?: "primary" | "secondary" | "ghost";
  disabled?: boolean;
  onUploadClick?: () => void;
  showProgressBar?: boolean;
  lowThreshold?: number;
  mediumThreshold?: number;
  highThreshold?: number;
  customRightElement?: ReactNode;
  rows?: number;
  maxRows?: number;
}

const STATE_STYLES: Record<TextAreaWithUploadProgressState, string> = {
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

const TextAreaWithUploadProgress = forwardRef<
  HTMLTextAreaElement,
  TextAreaWithUploadProgressProps
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
      rows = 3,
      maxRows = 10,
      value: externalValue,
      onChange,
      onFocus,
      onBlur,
      ...rest
    },
    ref
  ) => {
    const [internalValue, setInternalValue] = useState<string>(
      (externalValue as string) || ""
    );
    const [isFocused, setIsFocused] = useState<boolean>(false);

    useEffect(() => {
      if (externalValue !== undefined) {
        setInternalValue(externalValue as string);
      }
    }, [externalValue]);

    const handleInput = (e: React.FormEvent<HTMLTextAreaElement>) => {
      const target = e.target as HTMLTextAreaElement;

      target.style.height = "auto";

      const lineHeight = 24;
      const maxHeight = maxRows * lineHeight;
      const newHeight = Math.min(target.scrollHeight, maxHeight);

      target.style.height = `${newHeight}px`;
    };

    const handleChange = useCallback(
      (e: React.ChangeEvent<HTMLTextAreaElement>) => {
        const newValue = e.target.value;
        setInternalValue(newValue);

        if (onChange) {
          onChange(e);
        }
      },
      [onChange]
    );

    const handleFocus = useCallback(
      (e: React.FocusEvent<HTMLTextAreaElement>) => {
        setIsFocused(true);
        if (onFocus) {
          onFocus(e);
        }
      },
      [onFocus]
    );

    const handleBlur = useCallback(
      (e: React.FocusEvent<HTMLTextAreaElement>) => {
        setIsFocused(false);
        if (onBlur) {
          onBlur(e);
        }
      },
      [onBlur]
    );

    const inputState: TextAreaWithUploadProgressState = useMemo(() => {
      return disabled ? "disabled" : isFocused ? "focused" : "default";
    }, [disabled, isFocused]);

    const textLength = internalValue.length;

    const feedbackLevel: FeedbackLevel = useMemo(() => {
      if (textLength === 0) return "none";
      if (textLength < lowThreshold) return "low";
      if (textLength < mediumThreshold) return "medium";
      return "high";
    }, [textLength, lowThreshold, mediumThreshold]);

    const containerStyle = STATE_STYLES[inputState];

    const { message, color } = FEEDBACK_INFO[feedbackLevel];

    const isButtonDisabled = disabled || textLength === 0;

    useEffect(() => {
      if (ref && "current" in ref && ref.current) {
        const textarea = ref.current;
        const lineHeight = 24;
        const maxHeight = maxRows * lineHeight;
        const newHeight = Math.min(textarea.scrollHeight, maxHeight);
        textarea.style.height = `${newHeight}px`;
      }
    }, [maxRows, ref, internalValue]);

    return (
      <div className={className}>
        <div
          className={`relative rounded-2xl border ${containerStyle} overflow-hidden`}
        >
          <div className="flex p-4">
            <div className="flex-1 relative">
              <textarea
                ref={ref}
                className="w-full bg-transparent outline-none text-gray-700 resize-none min-h-[72px]"
                placeholder={placeholder}
                disabled={disabled}
                value={internalValue}
                onChange={handleChange}
                onFocus={handleFocus}
                onBlur={handleBlur}
                onInput={handleInput}
                rows={rows}
                {...rest}
              />
            </div>

            {/* 업로드 버튼 또는 커스텀 요소 */}
            <div className="absolute bottom-3 right-4">
              {customRightElement || (
                <UploadButton
                  variant={uploadButtonVariant}
                  onClick={onUploadClick}
                  disabled={isButtonDisabled}
                  aria-label="Upload"
                />
              )}
            </div>
          </div>

          {/* 진행률 표시줄 */}
          {showProgressBar && textLength > 0 && (
            <div className="absolute bottom-0 left-0 right-0 h-1">
              <ProgressBar
                value={textLength}
                lowThreshold={lowThreshold}
                mediumThreshold={mediumThreshold}
                highThreshold={highThreshold}
              />
            </div>
          )}
        </div>

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

TextAreaWithUploadProgress.displayName = "TextAreaWithUploadProgress";

export default TextAreaWithUploadProgress;
