import api from './api';
import { getSession } from "next-auth/react";
import { handleSignIn } from "@/app/actions";
import { vi, describe, it, expect, beforeEach } from 'vitest';

vi.mock("next-auth/react", () => ({
  getSession: vi.fn(),
}));

vi.mock("@/app/actions", () => ({
  handleSignIn: vi.fn(),
}));

describe('Axios Client API Interceptor', () => {
  
  beforeEach(() => {
    vi.clearAllMocks();
  });

  const getRequestInterceptor = () => {
    return (api.interceptors.request as any).handlers[0].fulfilled;
  };

  it('повинен додати Bearer токен, якщо сесія валідна', async () => {
    const mockToken = 'super-secret-token';
    (getSession as any).mockResolvedValue({
      accessToken: mockToken,
    });

    const config = { headers: {} };
    const interceptor = getRequestInterceptor();
    
    const result = await interceptor(config);

    expect(result.headers.Authorization).toBe(`Bearer ${mockToken}`);
    expect(handleSignIn).not.toHaveBeenCalled();
  });

  it('повинен викликати handleSignIn при помилці RefreshAccessTokenError', async () => {
    (getSession as any).mockResolvedValue({
      error: "RefreshAccessTokenError",
    });

    const config = { headers: {} };
    const interceptor = getRequestInterceptor();
    
    await interceptor(config);

    expect(handleSignIn).toHaveBeenCalledTimes(1);
  });

  it('не повинен додавати заголовок, якщо сесія відсутня', async () => {
    (getSession as any).mockResolvedValue(null);

    const config = { headers: {} };
    const interceptor = getRequestInterceptor();
    
    const result = await interceptor(config);

    expect(result.headers.Authorization).toBeUndefined();
    expect(handleSignIn).not.toHaveBeenCalled();
  });
});