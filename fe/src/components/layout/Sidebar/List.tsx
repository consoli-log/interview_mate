"use client";

import React, { HTMLAttributes, ReactNode } from "react";

interface ListProps extends HTMLAttributes<HTMLUListElement> {
  children: ReactNode;
}

interface ListItemProps extends HTMLAttributes<HTMLLIElement> {
  children: ReactNode;
}

export const List = ({ children, className = "", ...rest }: ListProps) => {
  return (
    <ul className={`list-none p-0 m-0 ${className}`} {...rest}>
      {children}
    </ul>
  );
};

export const ListItem = ({
  children,
  className = "",
  onClick,
  ...rest
}: ListItemProps) => {
  return (
    <li
      className={`cursor-pointer truncate ${className}`}
      onClick={onClick}
      {...rest}
    >
      {children}
    </li>
  );
};
