import type { Config } from "tailwindcss";

export default {
  content: [
    "./src/pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/components/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/app/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      colors: {
        // Neutral Colors
        "black-100": "var(--black-100)",
        "white-100": "var(--white-100)",
        "gray-100": "var(--gray-100)",
        "gray-200": "var(--gray-200)",

        // Primary Colors
        "yellow-100": "var(--yellow-100)",
        "yellow-200": "var(--yellow-200)",
        "yellow-300": "var(--yellow-300)",

        // System Colors
        "red-100": "var(--red-100)",
        "green-100": "var(--green-100)",

        // Base Theme Colors
        background: "var(--background)",
        foreground: "var(--foreground)",
      },
    },
  },
  plugins: [],
} satisfies Config;
