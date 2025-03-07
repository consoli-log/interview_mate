"use client";

import { List, ListItem } from "./List";

interface SidebarProps {
  onAddPost?: () => void;
  posts: Array<{ id: string; title: string }>;
  onPostClick?: (id: string) => void;
}

export const Sidebar = ({ onAddPost, posts, onPostClick }: SidebarProps) => {
  return (
    <div className="p-4 flex flex-col gap-4">
      {onAddPost && (
        <button
          onClick={onAddPost}
          className="w-full p-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600"
        >
          포스트 추가하기
        </button>
      )}
      <List>
        {posts.map((post) => (
          <ListItem key={post.id} onClick={() => onPostClick?.(post.id)}>
            <span className="text-sm">{post.title}</span>
          </ListItem>
        ))}
      </List>
    </div>
  );
};
