import axios from "axios";
import { getSession } from "next-auth/react";
import { handleSignIn} from "@/app/actions"

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