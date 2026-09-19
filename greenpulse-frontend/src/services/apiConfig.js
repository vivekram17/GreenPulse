export const API_ROOT = "http://localhost:8080";

/**
 * Thin wrapper around fetch that throws with a readable message
 * and returns parsed JSON (or null for empty responses).
 */
export async function request(path, options = {}) {
  const response = await fetch(`${API_ROOT}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...(options.headers || {}),
    },
    ...options,
  });

  const text = await response.text();
  const data = text ? JSON.parse(text) : null;

  if (!response.ok) {
    const message =
      (data && (data.message || data.error)) ||
      `Request to ${path} failed (${response.status})`;
    throw new Error(message);
  }

  return data;
}
