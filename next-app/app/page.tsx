export default function HomePage() {
  return (
    <main className="min-h-screen bg-black text-white flex flex-col items-center justify-center px-6">
      
      {/* HEADER */}
      <h1 className="text-5xl font-bold mb-4 text-center">
        🚀 qm-service
      </h1>

      <p className="text-lg text-gray-400 text-center max-w-xl mb-8">
        High-performance microservices gateway built with Spring WebFlux,
        designed for scalability, resilience and real-world load testing.
      </p>

      {/* STATS */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-10 w-full max-w-4xl">
        <Stat title="Throughput" value="~500 RPS" />
        <Stat title="Latency (p95)" value="< 130ms" />
        <Stat title="CPU" value="1 Core" />
      </div>

      {/* BUTTONS */}
      <div className="flex gap-4">
        <a
          href="/api/v1/books"
          className="px-6 py-3 bg-green-500 hover:bg-green-600 rounded-xl font-semibold transition"
        >
          Test API
        </a>

        <a
          href="/actuator/health"
          className="px-6 py-3 border border-gray-600 hover:border-white rounded-xl transition"
        >
          Health Check
        </a>
      </div>

      {/* FOOTER */}
      <p className="absolute bottom-6 text-gray-500 text-sm">
        Built with Spring + Next.js • Performance Lab
      </p>
    </main>
  );
}

function Stat({ title, value }: { title: string; value: string }) {
  return (
    <div className="bg-zinc-900 p-6 rounded-2xl border border-zinc-800 text-center">
      <p className="text-gray-400 text-sm">{title}</p>
      <p className="text-2xl font-bold mt-2">{value}</p>
    </div>
  );
}