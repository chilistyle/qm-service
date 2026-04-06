// auth.ts
import NextAuth from "next-auth"
import Keycloak from "next-auth/providers/keycloak"

async function refreshAccessToken(token: any) {
    try {
        const response = await fetch(`${process.env.AUTH_KEYCLOAK_INNER}/protocol/openid-connect/token`, {
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: new URLSearchParams({
                client_id: process.env.AUTH_KEYCLOAK_ID!,
                client_secret: process.env.AUTH_KEYCLOAK_SECRET!,
                grant_type: "refresh_token",
                refresh_token: token.refreshToken,
            }),
            method: "POST",
        })

        const tokens = await response.json()
        if (!response.ok) throw tokens

        return {
            ...token,
            accessToken: tokens.access_token,
            expiresAt: Math.floor(Date.now() / 1000 + tokens.expires_in),
            refreshToken: tokens.refresh_token ?? token.refreshToken,
            idToken: tokens.id_token ?? token.idToken, 
        }
    } catch (error) {
        console.error("Error refreshing access token", error)
        return { ...token, error: "RefreshAccessTokenError" }
    }
}

export const { handlers, auth, signIn, signOut } = NextAuth({
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
})