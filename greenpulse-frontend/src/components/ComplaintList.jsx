import { useEffect, useState } from "react";
import { getComplaints } from "../services/complaintApi";
import ComplaintCard from "./ComplaintCard";

function ComplaintList() {
  const [complaints, setComplaints] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  async function loadComplaints() {
    try {
      setLoading(true);
      const data = await getComplaints();
      setComplaints(data);
    } catch (err) {
      setError("Unable to load complaints. Make sure the backend is running.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadComplaints();
  }, []);

  function handleStatusUpdated(updatedComplaint) {
    setComplaints((previous) =>
      previous.map((complaint) =>
        complaint.id === updatedComplaint.id ? updatedComplaint : complaint
      )
    );
  }

  if (loading) {
    return <div className="state-message">Loading complaints...</div>;
  }

  if (error) {
    return <div className="state-message error-message">{error}</div>;
  }

  return (
    <section className="complaints-section">
      <div className="section-heading">
        <span>Community reports</span>
        <h2>Waste issues that need attention.</h2>
        <p>
          Every report below was automatically classified and routed by
          GreenPulse AI. Track how each one moves from new to resolved.
        </p>
      </div>

      {complaints.length === 0 ? (
        <div className="empty-state">
          <h3>No complaints yet</h3>
          <p>When someone reports an issue, it will appear here.</p>
        </div>
      ) : (
        <div className="complaints-grid">
          {complaints.map((complaint) => (
            <ComplaintCard
              key={complaint.id}
              complaint={complaint}
              onStatusUpdated={handleStatusUpdated}
            />
          ))}
        </div>
      )}
    </section>
  );
}

export default ComplaintList;
