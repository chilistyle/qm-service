import { describe, it, expect, vi, beforeEach } from "vitest";
import { handleSignIn, getLogoutUrl } from "./actions";
import { signIn, signOut, auth } from "@/lib/auth";

vi.mock("@/lib/auth", () => ({
  auth: vi.fn(),
  signIn: vi.fn(),
  signOut: vi.fn(),
}));

describe("Server Actions", () => {
  const OLD_ENV = process.env;

  beforeEach(() => {
    vi.clearAllMocks();
    process.env = { ...OLD_ENV };
    process.env.AUTH_KEYCLOAK_ISSUER = "https://keycloak.example.com";
    process.env.AUTH_KEYCLOAK_ID = "my-client-id";
    process.env.AUTH_HOST_URL = "http://localhost:3000";
  });

  describe("handleSignIn", () => {
    it("має викликати signIn з провайдером keycloak", async () => {
      await handleSignIn();
      expect(signIn).toHaveBeenCalledWith("keycloak");
    });
  });

  describe("getLogoutUrl", () => {
    it("має сформувати правильний URL та викликати signOut", async () => {
      const mockIdToken = "test-id-token";
      (auth as any).mockResolvedValue({ idToken: mockIdToken });

      await getLogoutUrl();

      const expectedLogoutUrl = 
        "https://keycloak.example.com/protocol/openid-connect/logout" +
        "?client_id=my-client-id" +
        "&post_logout_redirect_uri=http%3A%2F%2Flocalhost%3A3000" +
        `&id_token_hint=${mockIdToken}`;

      expect(signOut).toHaveBeenCalledWith({
        redirectTo: expectedLogoutUrl,
      });
    });

    it("має коректно працювати, якщо сесії або idToken немає", async () => {
      (auth as any).mockResolvedValue(null);

      await getLogoutUrl();

      expect(signOut).toHaveBeenCalledWith(
        expect.objectContaining({
          redirectTo: expect.stringContaining("id_token_hint=undefined"),
        })
      );
    });
  });
});