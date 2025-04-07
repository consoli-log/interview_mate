"use client";

import React from "react";
import { List, ListItem } from "./List";
import SidebarButton from "./SidebarButton";

interface Post {
  id: string;
  title: string;
  date: string;
}

interface SidebarProps {
  onAddPost?: () => void;
  posts: Array<Post>;
  onPostClick?: (id: string) => void;
}

export const Sidebar = ({ onAddPost, posts, onPostClick }: SidebarProps) => {
  const groupedPosts = posts.reduce<Record<string, Post[]>>((groups, post) => {
    const { date } = post;
    if (!groups[date]) {
      groups[date] = [];
    }
    groups[date].push(post);
    return groups;
  }, {});

  return (
    <div className="border-r border-gray-200 h-screen flex flex-col">
      <div className="p-4">
        {onAddPost && (
          <SidebarButton
            onClick={onAddPost}
            text="포폴 추가하기"
            iconSrc="/images/icons/sidebar_icon.svg"
            className="bg-yellow-300 hover:bg-yellow-400 text-white"
          />
        )}
      </div>

      <div className="flex-1 overflow-y-auto px-4">
        {Object.entries(groupedPosts).map(([date, datePosts]) => (
          <div key={date} className="mb-4">
            <h3 className="text-sm font-medium text-gray-600 mb-2">{date}</h3>
            <List>
              {datePosts.map((post) => (
                <ListItem
                  key={post.id}
                  onClick={() => onPostClick?.(post.id)}
                  className="py-2 px-0 hover:bg-gray-100"
                >
                  <span className="text-sm text-gray-700 truncate">
                    {post.title}
                  </span>
                </ListItem>
              ))}
            </List>
          </div>
        ))}
      </div>
    </div>
  );
};
