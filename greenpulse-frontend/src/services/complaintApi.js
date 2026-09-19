const API_BASE_URL = "http://localhost:8080/api/complaints";

export async function submitComplaint(complaint) {
    const response = await fetch(API_BASE_URL, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(complaint),
    });

    if (!response.ok) {
        throw new Error("Failed to submit complaint");
    }

    return response.json();
}

export async function getComplaints() {
    const response = await fetch(API_BASE_URL);

    if (!response.ok) {
        throw new Error("Failed to fetch complaints");
    }

    return response.json();
}

export async function getComplaintById(id) {
    const response = await fetch(`${API_BASE_URL}/${id}`);

    if (!response.ok) {
        throw new Error("Complaint not found");
    }

    return response.json();
}

export async function updateComplaintStatus(id, status) {
    const response = await fetch(`${API_BASE_URL}/${id}/status`, {
        method: "PATCH",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            status: status,
        }),
    });

    if (!response.ok) {
        throw new Error("Failed to update complaint status");
    }

    return response.json();
}
