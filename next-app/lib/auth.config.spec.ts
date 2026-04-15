import { describe, it, expect, vi, beforeEach } from "vitest";
import { authConfig } from "./auth.config";
import { type Session, type User } from "next-auth";
import { type JWT } from "next-auth/jwt";

describe("authConfig", () => {
    const { callbacks } = authConfig;

    beforeEach(() => {
        vi.resetAllMocks();
        process.env.AUTH_KEYCLOAK_INNER = "http://keycloak-inner";
        process.env.AUTH_KEYCLOAK_ID = "test-id";
        process.env.AUTH_KEYCLOAK_SECRET = "test-secret";

        global.fetch = vi.fn();
    });

    describe("jwt callback", () => {
        it("повинен ініціалізувати токен при першому вході", async () => {
            const token = {} as any;
            const account = {
                access_token: "abc",
                expires_at: 123,
            } as any;

            const result = await callbacks!.jwt!({
                token,
                account,
                user: {} as any
            });

            expect(result).not.toBeNull();
            expect((result as any).accessToken).toBe("abc");
        });

        it("повинен повернути існуючий токен, якщо він ще валідний", async () => {
            const futureDate = Math.floor(Date.now() / 1000) + 3600;
            const token = {
                accessToken: "valid-token",
                expiresAt: futureDate,
            } as JWT;

            const result = await callbacks!.jwt!({ token, user: {} as User });

            expect(result).toEqual(token);
            expect(global.fetch).not.toHaveBeenCalled();
        });

        it("повинен викликати refreshAccessToken, якщо токен протермінований", async () => {
            const pastDate = Math.floor(Date.now() / 1000) - 100;
            const token = {
                refreshToken: "old-refresh-token",
                expiresAt: pastDate,
            } as any;

            (global.fetch as any).mockResolvedValue({
                ok: true,
                json: async () => ({
                    access_token: "new-access-token",
                    expires_in: 3600,
                    refresh_token: "new-refresh-token",
                    id_token: "new-id-token",
                }),
            });

            const result = await callbacks!.jwt!({ token, user: {} as any }) as any;

            expect(global.fetch).toHaveBeenCalledTimes(1);

            expect(result.accessToken).toBe("new-access-token");

            expect(result.error).toBeUndefined();
        });

        it("повинен додати помилку RefreshAccessTokenError, якщо fetch провалився", async () => {
            const token = {
                refreshToken: "some-token",
                expiresAt: 0,
            } as JWT;

            (global.fetch as any).mockResolvedValue({
                ok: false,
                json: async () => ({ error: "invalid_grant" }),
            });

            const result = await callbacks!.jwt!({ token, user: {} as User });

            expect(result.error).toBe("RefreshAccessTokenError");
        });
    });

    describe("session callback", () => {
        it("повинен перенести дані з JWT у об'єкт сесії", async () => {
            const mockSession = {
                user: { name: "Test" },
                expires: new Date().toISOString()
            };
            const mockToken = {
                accessToken: "secret-token",
                idToken: "id-token",
                error: undefined,
            };

            const result = await callbacks!.session!({
                session: mockSession as any,
                token: mockToken as any,
                user: {} as any, 
                newSession: undefined as any,
                trigger: "update"
            });

            const finalResult = result as any;

            expect(finalResult).not.toBeNull();
            expect(finalResult.accessToken).toBe("secret-token");
        });
    });
});