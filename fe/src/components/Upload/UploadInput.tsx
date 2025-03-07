import React, {
  useState,
  TextareaHTMLAttributes,
  ReactNode,
  forwardRef,
} from "react";

export type TextAreaState = "default" | "focused" | "disabled";

export interface TextAreaProps
  extends Omit<TextareaHTMLAttributes<HTMLTextAreaElement>, "size"> {
  className?: string;
  rightElement?: ReactNode;
  rows?: number;
  maxRows?: number;
}

const STATE_STYLES: Record<TextAreaState, string> = {
  default: "border-gray-200",
  focused: "border-yellow-200",
  disabled: "bg-gray-50 border-gray-200",
};

const TextArea = forwardRef<HTMLTextAreaElement, TextAreaProps>(
  (
    {
      placeholder = "포트폴리오를 입력해주세요.",
      className = "",
      rightElement,
      disabled = false,
      rows = 3,
      maxRows = 10,
      ...rest
    },
    ref
  ) => {
    const [isFocused, setIsFocused] = useState<boolean>(false);

    const getTextAreaState = (): TextAreaState => {
      if (disabled) return "disabled";
      if (isFocused) return "focused";
      return "default";
    };

    const handleFocus = (e: React.FocusEvent<HTMLTextAreaElement>) => {
      setIsFocused(true);
      rest.onFocus?.(e);
    };

    const handleBlur = (e: React.FocusEvent<HTMLTextAreaElement>) => {
      setIsFocused(false);
      rest.onBlur?.(e);
    };

    const handleInput = (e: React.FormEvent<HTMLTextAreaElement>) => {
      const target = e.target as HTMLTextAreaElement;

      target.style.height = "auto";

      const lineHeight = 24;
      const maxHeight = maxRows * lineHeight;
      const newHeight = Math.min(target.scrollHeight, maxHeight);

      target.style.height = `${newHeight}px`;
    };

    const currentState = getTextAreaState();
    const containerStyle = STATE_STYLES[currentState];

    return (
      <div
        className={`relative rounded-2xl border ${containerStyle} overflow-hidden ${className}`}
      >
        <div className="flex p-4">
          <div className="flex-1 relative">
            <textarea
              ref={ref}
              className="w-full bg-transparent outline-none text-gray-700 resize-none min-h-[72px]"
              placeholder={placeholder}
              disabled={disabled}
              onFocus={handleFocus}
              onBlur={handleBlur}
              onInput={handleInput}
              rows={rows}
              {...rest}
            />
          </div>

          {rightElement && (
            <div className="flex items-start ml-2">{rightElement}</div>
          )}
        </div>
      </div>
    );
  }
);

TextArea.displayName = "TextArea";

export default TextArea;
