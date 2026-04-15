import NextAuth, { type DefaultSession } from "next-auth"
import { type JWT } from "next-auth/jwt"

declare module "next-auth" {
  interface Session {
    accessToken?: string;
    idToken?: string;
    error?: string;
    user: {
    } & DefaultSession["user"]
  }

  interface User {
  }
}

declare module "next-auth/jwt" {
  interface JWT {
    accessToken?: string;
    refreshToken?: string;
    expiresAt?: number;
    idToken?: string;
    error?: string;
  }
}