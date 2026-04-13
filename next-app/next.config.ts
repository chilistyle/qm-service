import type { NextConfig } from "next";

const publicDns = process.env.PUBLIC_DNS ?? "localhost";

const nextConfig: NextConfig = {
  output: "standalone",
  experimental: {
    serverActions: {
      allowedOrigins: [
        `${publicDns}`,
        "next-app:3000",
      ],
    },
  },
};

export default nextConfig;