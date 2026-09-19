import { request } from "./apiConfig";

/**
 * Exact PolicyAnswer response field names weren't available at build
 * time — this reads a few likely variants for the answer text and the
 * supporting snippet/source.
 */
function normalizeAnswer(raw) {
  if (!raw) return { answer: "", source: null };

  const answer = raw.answer ?? raw.response ?? raw.text ?? "";
  const source =
    raw.source ?? raw.matchedSnippet ?? raw.snippet ?? raw.policy ?? null;

  return { answer, source, raw };
}

export async function askPolicyQuestion(question) {
  const data = await request("/api/policy/ask", {
    method: "POST",
    body: JSON.stringify({ question }),
  });

  return normalizeAnswer(data);
}
