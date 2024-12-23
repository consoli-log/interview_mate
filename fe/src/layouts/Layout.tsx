"use client";
import { ReactNode, useState } from "react";

interface SidebarProps {
  isOpen: boolean;
  toggleSidebar: () => void;
}

type SidebarContent = ReactNode | ((props: SidebarProps) => ReactNode);

interface LayoutProps {
  children: ReactNode;
  header?: ReactNode;
  sidebar?: SidebarContent;
  className?: string;
  isOpen?: boolean;
  onToggle?: () => void;
}

export const Layout = ({
  children,
  header,
  sidebar,
  className = "",
  isOpen: propIsOpen,
  onToggle: propOnToggle,
}: LayoutProps) => {
  const [localIsOpen, setLocalIsOpen] = useState(true);
  const isOpen = propIsOpen ?? localIsOpen;
  const toggleSidebar = propOnToggle ?? (() => setLocalIsOpen(!localIsOpen));

  return (
    <div className={`flex min-h-screen relative ${className}`}>
      {sidebar && (
        <aside
          className={`
            fixed lg:relative
            min-h-screen
            transition-transform duration-300 ease-in-out
            border-r border-gray-200 bg-white
            transform-gpu
            ${isOpen ? "w-64" : "w-16"}
          `}
        >
          {typeof sidebar === "function"
            ? sidebar({ isOpen, toggleSidebar })
            : sidebar}
        </aside>
      )}
      <main className="flex-1">
        {header && <header className="h-16 border-b">{header}</header>}
        <div className="p-4">{children}</div>
      </main>
    </div>
  );
};
