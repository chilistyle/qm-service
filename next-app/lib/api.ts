import axios from "axios";
import { getSession } from "next-auth/react";
import { handleSignIn } from "@/app/actions";

const api = axios.create({
    baseURL: "/",
});

api.interceptors.request.use(async (config) => {
    const session = await getSession();

    if (session?.error === "RefreshAccessTokenError") {
        handleSignIn();
        return config;
    }

    if (session?.accessToken) {
        config.headers.Authorization = `Bearer ${session.accessToken}`;
    }
    return config;
});

export default api;

/**
 * Fetches a resource with automatic Bearer token injection.
 * Handles session expiration by redirecting to sign-in if needed.
 */
export async function fetchWithAuth(
    url: string,
    options?: RequestInit
): Promise<Response> {
    const session = await getSession();

    if (session?.error === "RefreshAccessTokenError") {
        handleSignIn();
        throw new Error("Session expired, redirecting to sign-in");
    }

    const headers = new Headers(options?.headers);

    if (session?.accessToken) {
        headers.set("Authorization", `Bearer ${session.accessToken}`);
    }

    return fetch(url, {
        ...options,
        headers,
    });
}

/**
 * Helper for JSON API requests with auth.
 * Automatically sets Content-Type and parses JSON response.
 */
export async function apiRequest<T>(
    url: string,
    method: "GET" | "POST" | "PUT" | "PATCH" | "DELETE",
    body?: unknown
): Promise<T> {
    const options: RequestInit = {
        method,
        headers: {
            "Content-Type": "application/json",
        },
    };

    if (body && method !== "GET" && method !== "DELETE") {
        options.body = JSON.stringify(body);
    }

    const response = await fetchWithAuth(url, options);

    if (!response.ok) {
        const error = await response.text().catch(() => "Unknown error");
        throw new Error(`API request failed: ${response.status} - ${error}`);
    }

    if (method === "DELETE" || response.status === 204) {
        return {} as T;
    }

    return response.json();
}
