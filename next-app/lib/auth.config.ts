// lib/auth.config.ts
import type { NextAuthConfig } from "next-auth";
import Keycloak from "next-auth/providers/keycloak";

async function refreshAccessToken(token: any) {
    try {
        const tokenUrl = `${process.env.AUTH_KEYCLOAK_INNER}/protocol/openid-connect/token`
        console.log("[DEBUG] Refresh token - URL:", tokenUrl)
        console.log("[DEBUG] Refresh token - Client ID:", process.env.AUTH_KEYCLOAK_ID)
        console.log("[DEBUG] Refresh token - Has secret:", !!process.env.AUTH_KEYCLOAK_SECRET)
        console.log("[DEBUG] Refresh token - Has refresh token:", !!token.refreshToken)

        const response = await fetch(tokenUrl, {
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: new URLSearchParams({
                client_id: process.env.AUTH_KEYCLOAK_ID!,
                client_secret: process.env.AUTH_KEYCLOAK_SECRET!,
                grant_type: "refresh_token",
                refresh_token: token.refreshToken,
            }),
            method: "POST",
        })

        console.log("[DEBUG] Refresh token - Response status:", response.status)
        
        const tokens = await response.json()
        console.log("[DEBUG] Refresh token - Response body:", JSON.stringify(tokens, null, 2))
        
        if (!response.ok) {
            console.error("[ERROR] Refresh token - Error response:", tokens)
            throw tokens
        }

        const result = {
            ...token,
            accessToken: tokens.access_token,
            expiresAt: Math.floor(Date.now() / 1000 + tokens.expires_in),
            refreshToken: tokens.refresh_token ?? token.refreshToken,
            idToken: tokens.id_token ?? token.idToken, 
        }
        console.log("[DEBUG] Refresh token - Success, new expiresAt:", result.expiresAt)
        return result
    } catch (error) {
        console.error("[ERROR] Refresh token - Catch error:", error)
        return { ...token, error: "RefreshAccessTokenError" }
    }
}

export const authConfig: NextAuthConfig = {
    secret: process.env.AUTH_SECRET,
    providers: [
        Keycloak({
            clientId: process.env.AUTH_KEYCLOAK_ID,
            clientSecret: process.env.AUTH_KEYCLOAK_SECRET,
            issuer: process.env.AUTH_KEYCLOAK_ISSUER,

            authorization: {
              url: `${process.env.AUTH_KEYCLOAK_ISSUER}/protocol/openid-connect/auth`,
              params: { scope: "openid email profile" },
            },

            token: `${process.env.AUTH_KEYCLOAK_INNER}/protocol/openid-connect/token`,
            userinfo: `${process.env.AUTH_KEYCLOAK_INNER}/protocol/openid-connect/userinfo`,
            jwks_endpoint: `${process.env.AUTH_KEYCLOAK_INNER}/protocol/openid-connect/certs`,
        }),
    ],
    trustHost: true,
    callbacks: {
        async jwt({ token, account }) {
            if (account) {
                return {
                    ...token,
                    accessToken: account.access_token,
                    expiresAt: account.expires_at,
                    refreshToken: account.refresh_token,
                    idToken: account.id_token,
                }
            }

            if (Date.now() < (token.expiresAt as number) * 1000 - 10000) {
                return token
            }

            return refreshAccessToken(token)
        },
        async session({ session, token }) {
            session.accessToken = token.accessToken as string
            session.idToken = token.idToken as string
            session.error = token.error as string
            return session
        },
    },
    debug: false,
};