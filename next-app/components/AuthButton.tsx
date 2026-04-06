// components/AuthButton.tsx
'use client'

import { getLogoutUrl, handleSignIn } from "@/app/actions"

export default function AuthButton({ session }: { session: any }) {
    if (!session?.user) {
        return (
            <div className="flex gap-2">
                <form action={handleSignIn}>
                    <button
                        className="bg-indigo-600 text-white px-4 py-2 rounded-full text-sm font-bold hover:bg-indigo-700 transition-all shadow-md shadow-indigo-100 active:scale-95">
                        Sign in
                    </button>
                </form>
            </div>
        )
    }

    return (
        <div className="flex gap-2">
            <button className="bg-red-600 text-white px-4 py-2 rounded-full text-sm font-bold hover:bg-red-700 transition-all shadow-md shadow-red-100 active:scale-95" onClick={async () => {
                await getLogoutUrl();
            }}>Sign out
            </button>
        </div>
    )
}