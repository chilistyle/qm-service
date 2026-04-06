"use client";
import { useSession, signOut } from "next-auth/react";
import { useEffect } from "react";

export function SessionGuard() {
    const { data: session } = useSession();

    useEffect(() => {
        if (session?.error === "RefreshAccessTokenError") {
            console.log("Session expired, signing out...");
            signOut({ redirectTo: "/", redirect: true });
        }
    }, [session]);

    return null;
}