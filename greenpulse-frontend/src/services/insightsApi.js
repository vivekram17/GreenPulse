import { request } from "./apiConfig";

/**
 * The exact TrendSummary response shape wasn't available at build time.
 * The README only promises "counts by category/location + a narrative
 * summary", so this reads a few likely key names and falls back
 * gracefully if the real response uses different ones.
 */
function normalizeTrends(raw) {
  if (!raw) return { summary: "", byCategory: {}, byLocation: {} };

  const byCategory =
    raw.byCategory ?? raw.countsByCategory ?? raw.categoryCounts ?? {};

  const byLocation =
    raw.byLocation ?? raw.countsByLocation ?? raw.locationCounts ?? {};

  const summary =
    raw.summary ?? raw.narrative ?? raw.narrativeSummary ?? raw.text ?? "";

  const total =
    raw.total ??
    raw.totalComplaints ??
    Object.values(byCategory).reduce((sum, n) => sum + Number(n || 0), 0);

  return { summary, byCategory, byLocation, total, raw };
}

export async function getTrends() {
  const data = await request("/api/insights/trends");
  return normalizeTrends(data);
}
