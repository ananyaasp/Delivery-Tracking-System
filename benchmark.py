import time
import urllib.request
import urllib.parse
import json
import statistics
import concurrent.futures

BASE_URL = "http://localhost:8081"

def create_shipment():
    start = time.perf_counter()
    req = urllib.request.Request(
        f"{BASE_URL}/shipments",
        data=json.dumps({"sourceAddress": "Mumbai", "destinationAddress": "Delhi"}).encode('utf-8'),
        headers={'Content-Type': 'application/json'},
        method='POST'
    )
    with urllib.request.urlopen(req) as response:
        data = json.loads(response.read().decode('utf-8'))
        duration_ms = (time.perf_counter() - start) * 1000
        return (data["id"], duration_ms)

def track_shipment(shipment_id):
    start = time.perf_counter()
    req = urllib.request.Request(f"{BASE_URL}/tracking/{shipment_id}", method='GET')
    with urllib.request.urlopen(req) as response:
        response.read()
    duration_ms = (time.perf_counter() - start) * 1000
    return duration_ms

def update_status(shipment_id, status):
    start = time.perf_counter()
    url = f"{BASE_URL}/shipments/{shipment_id}?status={status}"
    req = urllib.request.Request(url, method='PUT')
    with urllib.request.urlopen(req) as response:
        response.read()
    duration_ms = (time.perf_counter() - start) * 1000
    return duration_ms

def run_benchmark(name, fn_args_list, concurrency):
    latencies = []
    errors = 0
    t0 = time.perf_counter()

    with concurrent.futures.ThreadPoolExecutor(max_workers=concurrency) as executor:
        future_to_req = {executor.submit(fn, *args): (fn, args) for fn, args in fn_args_list}
        for future in concurrent.futures.as_completed(future_to_req):
            try:
                res = future.result()
                if isinstance(res, tuple):
                    lat = res[1]
                else:
                    lat = res
                latencies.append(lat)
            except Exception as e:
                errors += 1

    total_time = time.perf_counter() - t0
    rps = len(latencies) / total_time if total_time > 0 else 0

    latencies.sort()
    n = len(latencies)
    mean_lat = statistics.mean(latencies) if latencies else 0
    p50 = latencies[int(n * 0.50)] if latencies else 0
    p90 = latencies[int(n * 0.90)] if latencies else 0
    p95 = latencies[int(n * 0.95)] if latencies else 0
    p99 = latencies[int(n * 0.99)] if latencies else 0
    min_lat = min(latencies) if latencies else 0
    max_lat = max(latencies) if latencies else 0

    results = {
        "benchmark": name,
        "concurrency": concurrency,
        "total_requests": len(fn_args_list),
        "successful_requests": len(latencies),
        "errors": errors,
        "total_time_sec": round(total_time, 2),
        "throughput_rps": round(rps, 2),
        "latency_min_ms": round(min_lat, 2),
        "latency_mean_ms": round(mean_lat, 2),
        "latency_p50_ms": round(p50, 2),
        "latency_p90_ms": round(p90, 2),
        "latency_p95_ms": round(p95, 2),
        "latency_p99_ms": round(p99, 2),
        "latency_max_ms": round(max_lat, 2)
    }
    return results

if __name__ == "__main__":
    print("Pre-seeding shipments for benchmark...")
    seed_results = [create_shipment() for _ in range(100)]
    shipment_ids = [r[0] for r in seed_results]

    # Benchmark 1: GET Tracking with history (500 requests @ 10 concurrency)
    track_tasks = [(track_shipment, (shipment_ids[i % len(shipment_ids)],)) for i in range(500)]
    res_track_10 = run_benchmark("GET /tracking/{id} (Full History)", track_tasks, concurrency=10)

    # Benchmark 2: GET Tracking with history (500 requests @ 50 concurrency)
    res_track_50 = run_benchmark("GET /tracking/{id} (Full History)", track_tasks, concurrency=50)

    # Benchmark 3: POST /shipments (Create + Auto Audit Log @ 10 concurrency)
    create_tasks = [(create_shipment, ()) for _ in range(500)]
    res_create_10 = run_benchmark("POST /shipments (Create + Audit Log)", create_tasks, concurrency=10)

    # Benchmark 4: POST /shipments (Create + Auto Audit Log @ 50 concurrency)
    create_tasks_50 = [(create_shipment, ()) for _ in range(500)]
    res_create_50 = run_benchmark("POST /shipments (Create + Audit Log)", create_tasks_50, concurrency=50)

    print("\n" + "="*80)
    print("REAL BENCHMARK METRICS SUMMARY")
    print("="*80)
    for r in [res_track_10, res_track_50, res_create_10, res_create_50]:
        print(f"Endpoint: {r['benchmark']:<36} | Concurrency: {r['concurrency']:<2} | RPS: {r['throughput_rps']:<7.1f} | Mean: {r['latency_mean_ms']:<5.2f}ms | P95: {r['latency_p95_ms']:<5.2f}ms | P99: {r['latency_p99_ms']:<5.2f}ms")
    print("="*80)
