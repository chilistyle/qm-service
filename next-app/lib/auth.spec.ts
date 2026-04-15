import { describe, it, expect, vi, beforeEach } from 'vitest';
import { authConfig } from './auth.config';
import { type Session } from "next-auth";

describe('Auth Config Unit Tests', () => {
    const { callbacks } = authConfig;

    it('повинен коректно мапити дані account у JWT при вході', async () => {
        const mockToken = { name: 'Test User' };
        const mockAccount = {
            access_token: 'valid_access_token',
            expires_at: 999999,
            refresh_token: 'valid_refresh_token',
        };

        const result = await callbacks?.jwt?.({
            token: mockToken,
            account: mockAccount as any,
            user: {} as any
        });

        expect(result).toMatchObject({
            accessToken: 'valid_access_token',
            refreshToken: 'valid_refresh_token',
            expiresAt: 999999,
        });
    });

    it('повинен передавати токен у сесію через session() callback', async () => {
        const mockSession = {
            user: { id: '1', name: 'Test User' },
            expires: new Date().toISOString()
        };
        const mockToken = { accessToken: 'secret-token' };

        const result = await callbacks?.session?.({
            session: mockSession as any,
            token: mockToken as any,
            user: {} as any, 
            newSession: undefined,
            trigger: 'update'
        }) as Session;

        expect(result?.accessToken).toBe('secret-token');
    });
});