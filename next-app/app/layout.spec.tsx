import { render, screen } from "@testing-library/react";
import RootLayout from "./layout"; 
import { auth } from "@/lib/auth";
import { describe, it, expect, vi, beforeEach } from "vitest";

vi.mock("@/lib/auth", () => ({
  auth: vi.fn(),
}));

vi.mock("next/font/google", () => ({
  Geist: () => ({ variable: "--font-geist-sans" }),
  Geist_Mono: () => ({ variable: "--font-geist-mono" }),
}));

vi.mock("@/components/Header", () => ({
  default: ({ session }: any) => <header data-testid="header">{session ? "Logged In" : "Logged Out"}</header>,
}));

vi.mock("@/components/SessionGuard", () => ({
  SessionGuard: () => <div data-testid="session-guard" />,
}));

vi.mock("next-auth/react", () => ({
  SessionProvider: ({ children }: any) => <div data-testid="session-provider">{children}</div>,
}));

describe("RootLayout", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("має рендерити дітей та передавати сесію в компоненти", async () => {
    const mockSession = { user: { name: "Admin" } };
    (auth as any).mockResolvedValue(mockSession);

    const Result = await RootLayout({ 
      children: <div data-testid="child-content">Hello World</div> 
    });
    
    render(Result);

    expect(screen.getByTestId("session-provider")).toBeInTheDocument();
    expect(screen.getByTestId("session-guard")).toBeInTheDocument();
    expect(screen.getByTestId("header")).toHaveTextContent("Logged In");
    expect(screen.getByTestId("child-content")).toBeInTheDocument();
  });

  it("має застосовувати правильні класи до тегу html", async () => {
    (auth as any).mockResolvedValue(null);

    const Result = await RootLayout({ children: <div /> });
    render(Result);

    const htmlElement = document.querySelector("html");
    expect(htmlElement).toHaveClass("--font-geist-sans");
    expect(htmlElement).toHaveClass("--font-geist-mono");
    expect(htmlElement).toHaveClass("antialiased");
  });
});