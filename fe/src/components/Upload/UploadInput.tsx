import React, {
  useState,
  InputHTMLAttributes,
  ReactNode,
  forwardRef,
} from "react";

export type InputState = "default" | "focused" | "disabled";

export interface InputProps
  extends Omit<InputHTMLAttributes<HTMLInputElement>, "size"> {
  className?: string;
  rightElement?: ReactNode;
}

const STATE_STYLES: Record<InputState, string> = {
  default: "border-gray-200",
  focused: "border-yellow-200",
  disabled: "bg-gray-50 border-gray-200",
};

const Input = forwardRef<HTMLInputElement, InputProps>(
  (
    {
      placeholder = "포트폴리오를 입력해주세요.",
      className = "",
      rightElement,
      disabled = false,
      ...rest
    },
    ref
  ) => {
    const [isFocused, setIsFocused] = useState<boolean>(false);

    const getInputState = (): InputState => {
      if (disabled) return "disabled";
      if (isFocused) return "focused";
      return "default";
    };

    const handleFocus = (e: React.FocusEvent<HTMLInputElement>) => {
      setIsFocused(true);
      rest.onFocus?.(e);
    };

    const handleBlur = (e: React.FocusEvent<HTMLInputElement>) => {
      setIsFocused(false);
      rest.onBlur?.(e);
    };

    const currentState = getInputState();
    const containerStyle = STATE_STYLES[currentState];

    return (
      <div
        className={`relative rounded-2xl border ${containerStyle} overflow-hidden ${className}`}
      >
        <div className="flex items-center p-4">
          <input
            ref={ref}
            type="text"
            className="flex-1 bg-transparent outline-none text-gray-700"
            placeholder={placeholder}
            disabled={disabled}
            onFocus={handleFocus}
            onBlur={handleBlur}
            {...rest}
          />

          {rightElement && rightElement}
        </div>
      </div>
    );
  }
);

Input.displayName = "Input";

export default Input;
