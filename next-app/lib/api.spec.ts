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

  it('should add Bearer token if session is valid', async () => {
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

  it('should call handleSignIn on RefreshAccessTokenError', async () => {
    (getSession as any).mockResolvedValue({
      error: "RefreshAccessTokenError",
    });

    const config = { headers: {} };
    const interceptor = getRequestInterceptor();
    
    await interceptor(config);

    expect(handleSignIn).toHaveBeenCalledTimes(1);
  });

  it('should not add header if session is missing', async () => {
    (getSession as any).mockResolvedValue(null);

    const config = { headers: {} };
    const interceptor = getRequestInterceptor();
    
    const result = await interceptor(config);

    expect(result.headers.Authorization).toBeUndefined();
    expect(handleSignIn).not.toHaveBeenCalled();
  });
});