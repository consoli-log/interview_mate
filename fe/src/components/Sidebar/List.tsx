"use client";

import { ReactNode } from "react";

interface ListItemProps {
  children: ReactNode;
  className?: string;
  onClick?: () => void;
}

export const ListItem = ({
  children,
  className = "",
  onClick,
}: ListItemProps) => (
  <div
    className={`flex items-center p-2 hover:bg-gray-100 rounded-lg cursor-pointer ${className}`}
    onClick={onClick}
  >
    {children}
  </div>
);

export const List = ({
  children,
  className = "",
}: {
  children: ReactNode;
  className?: string;
}) => <div className={`space-y-1 ${className}`}>{children}</div>;
