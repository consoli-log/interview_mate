"use client";

import {
  useState,
  ReactNode,
  useContext,
  useCallback,
  ReactElement,
  createContext,
} from "react";

interface User {
  id: string;
  email: string;
  name: string;
}

interface LoginCredentials {
  email: string;
  password: string;
}

interface AuthContextType {
  isAuthenticated: boolean;
  user: User | null;
  checkAuthStatus: () => Promise<boolean>;
  login: (credentials: LoginCredentials) => Promise<boolean>;
  logout: () => Promise<void>;
}

const defaultAuthContext: AuthContextType = {
  isAuthenticated: false,
  user: null,
  checkAuthStatus: async () => false,
  login: async () => false,
  logout: async () => {},
};

const AuthContext = createContext<AuthContextType>(defaultAuthContext);

export function AuthProvider({
  children,
}: {
  children: ReactNode;
}): ReactElement {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  const [user, setUser] = useState<User | null>(null);

  const checkAuthStatus = useCallback(async (): Promise<boolean> => {
    if (typeof window === "undefined") return false;

    // TODO: 로컬 스토리지 -> 쿠키 / 세션
    try {
      const storedUser = localStorage.getItem("userInfo");
      if (!storedUser) {
        setUser(null);
        setIsAuthenticated(false);
        return false;
      }

      const parsedUser = JSON.parse(storedUser) as User;
      setUser(parsedUser);
      setIsAuthenticated(true);
      return true;
    } catch (error) {
      console.error("인증 상태 확인 중 오류 발생:", error);
      setUser(null);
      setIsAuthenticated(false);
      return false;
    }
  }, []);

  const login = useCallback(
    async (credentials: LoginCredentials): Promise<boolean> => {
      if (typeof window === "undefined") return false;

      // 목업 데이터
      try {
        const mockUser: User = {
          id: "user-1",
          email: credentials.email,
          name: "사용자",
        };

        localStorage.setItem("userInfo", JSON.stringify(mockUser));
        setUser(mockUser);
        setIsAuthenticated(true);
        return true;
      } catch (error) {
        console.error("로그인 중 오류 발생:", error);
        return false;
      }
    },
    []
  );

  const logout = useCallback(async (): Promise<void> => {
    if (typeof window === "undefined") return;

    try {
      localStorage.removeItem("userInfo");
      setUser(null);
      setIsAuthenticated(false);
    } catch (error) {
      console.error("로그아웃 중 오류 발생:", error);
    }
  }, []);

  return (
    <AuthContext.Provider
      value={{ isAuthenticated, user, checkAuthStatus, login, logout }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextType {
  return useContext(AuthContext);
}
