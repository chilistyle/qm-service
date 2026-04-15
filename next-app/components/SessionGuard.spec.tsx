import { render, waitFor } from "@testing-library/react";
import { useSession, signOut } from "next-auth/react";
import { SessionGuard } from "./SessionGuard";
import { describe, it, expect, vi, beforeEach } from "vitest";

vi.mock("next-auth/react", () => ({
  useSession: vi.fn(),
  signOut: vi.fn(),
}));

describe("SessionGuard", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("не повинен викликати signOut, якщо сесія валідна", () => {
    (useSession as any).mockReturnValue({
      data: { user: { name: "John Doe" } },
      status: "authenticated",
    });

    render(<SessionGuard />);

    expect(signOut).not.toHaveBeenCalled();
  });

  it("має викликати signOut, коли сесія повертає RefreshAccessTokenError", async () => {
    (useSession as any).mockReturnValue({
      data: { error: "RefreshAccessTokenError" },
      status: "authenticated",
    });

    render(<SessionGuard />);

    await waitFor(() => {
      expect(signOut).toHaveBeenCalledWith({
        redirectTo: "/",
        redirect: true,
      });
    });
  });

  it("не повинен нічого рендерити (повертає null)", () => {
    (useSession as any).mockReturnValue({ data: null, status: "unauthenticated" });
    
    const { container } = render(<SessionGuard />);
    
    expect(container.firstChild).toBeNull();
  });
});