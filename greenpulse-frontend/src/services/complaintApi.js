import { request } from "./apiConfig";

// Matches ComplaintStatus enum values from the backend.
export const COMPLAINT_STATUSES = [
  { value: "NEW", label: "New" },
  { value: "ROUTED", label: "Routed" },
  { value: "IN_PROGRESS", label: "In progress" },
  { value: "RESOLVED", label: "Resolved" },
];

/**
 * The backend's exact response field names weren't available at build time
 * (no entity/DTO source was provided) — this normalizes a few likely
 * variants so the UI still renders correctly either way. Swap this for a
 * direct pass-through once you confirm the real ComplaintResponse shape.
 */
function normalizeComplaint(raw) {
  if (!raw) return raw;

  return {
    id: raw.id,
    description: raw.description ?? "",
    locationHint: raw.locationHint ?? raw.location ?? "",
    category: raw.category ?? null,
    urgency: raw.urgency ?? null,
    department: raw.department ?? raw.routedDepartment ?? null,
    aiReasoning: raw.aiReasoning ?? raw.reasoning ?? null,
    photoUrl: raw.photoUrl ?? raw.photo_url ?? null,
    status: raw.status ?? "NEW",
    createdAt: raw.createdAt ?? raw.submittedAt ?? raw.timestamp ?? null,
    raw,
  };
}

export async function submitComplaint({ description, locationHint, photoUrl }) {
  const data = await request("/api/complaints", {
    method: "POST",
    body: JSON.stringify({ description, locationHint, photoUrl }),
  });

  return normalizeComplaint(data);
}

export async function getComplaints() {
  const data = await request("/api/complaints");
  return (data || []).map(normalizeComplaint);
}

export async function getComplaintById(id) {
  const data = await request(`/api/complaints/${id}`);
  return normalizeComplaint(data);
}

export async function updateComplaintStatus(id, status) {
  const data = await request(`/api/complaints/${id}/status`, {
    method: "PATCH",
    body: JSON.stringify({ status }),
  });

  return normalizeComplaint(data);
}