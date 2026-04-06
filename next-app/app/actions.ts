// app/actions.ts
'use server'

import { auth, signIn, signOut } from "@/lib/auth"

export async function handleSignIn() {
    await signIn("keycloak")
}

export async function getLogoutUrl() {
    const issuer = process.env.AUTH_KEYCLOAK_ISSUER;
    const clientId = process.env.AUTH_KEYCLOAK_ID;
    const redirectUri = process.env.AUTH_HOST_URL!;

    const session = await auth();
    const idToken = session?.idToken; 

    const logoutUrl = `${process.env.AUTH_KEYCLOAK_ISSUER}/protocol/openid-connect/logout` +
        `?client_id=${process.env.AUTH_KEYCLOAK_ID}` +
        `&post_logout_redirect_uri=${encodeURIComponent(process.env.AUTH_HOST_URL!)}` +
        `&id_token_hint=${idToken}`; 

    await signOut({
        redirectTo: logoutUrl
    });
}
