import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";
import Header from '@/components/Header';
import { auth } from "@/lib/auth"
import { SessionProvider } from "next-auth/react"
import { SessionGuard } from "@/components/SessionGuard";

const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export const metadata: Metadata = {
  title: "qm-service",
  description: "High-performance microservices gateway",
};

export default async function RootLayout({ children, }: Readonly<{ children: React.ReactNode; }>) {
  const session = await auth()
  return (
    <html lang="en" className={`${geistSans.variable} ${geistMono.variable} h-full antialiased`}>
      <body className="min-h-full flex flex-col">
        <SessionProvider session={session} refetchOnWindowFocus={true} refetchInterval={5 * 60}>
          <SessionGuard />
          <Header session={session} />
          {children}
        </SessionProvider>
      </body>
    </html>
  );
}

