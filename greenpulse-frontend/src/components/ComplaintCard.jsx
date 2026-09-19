// import { updateComplaintStatus, COMPLAINT_STATUSES } from "../services/complaintApi";

// function ComplaintCard({ complaint, onStatusUpdated }) {
//   async function changeStatus(event) {
//     const newStatus = event.target.value;

//     try {
//       const updated = await updateComplaintStatus(complaint.id, newStatus);
//       onStatusUpdated(updated);
//     } catch (error) {
//       alert("Could not update complaint status.");
//     }
//   }

//   const statusClass = complaint.status?.toLowerCase().replace(/_/g, "-");

//   return (
//     <article className="complaint-card">
//       <div className="complaint-top">
//         <div>
//           <span className="complaint-id">#{complaint.id}</span>
//           <h3>{complaint.category ?? "Uncategorized report"}</h3>
//         </div>

//         <span className={`status ${statusClass}`}>
//           {COMPLAINT_STATUSES.find((s) => s.value === complaint.status)
//             ?.label ?? complaint.status}
//         </span>
//       </div>

//       <p className="complaint-description">{complaint.description}</p>

//       <div className="complaint-meta">
//         <div>
//           <span>Location</span>
//           <strong>{complaint.locationHint || "—"}</strong>
//         </div>
//         <div>
//           <span>Urgency</span>
//           <strong>{complaint.urgency || "—"}</strong>
//         </div>
//         <div>
//           <span>Department</span>
//           <strong>{complaint.department || "—"}</strong>
//         </div>
//       </div>

//       {complaint.aiReasoning && (
//         <p className="complaint-ai-note">{complaint.aiReasoning}</p>
//       )}

//       <div className="complaint-footer">
//         <span>Update status</span>

//         <select value={complaint.status} onChange={changeStatus}>
//           {COMPLAINT_STATUSES.map((s) => (
//             <option key={s.value} value={s.value}>
//               {s.label}
//             </option>
//           ))}
//         </select>
//       </div>
//     </article>
//   );
// }

// export default ComplaintCard;
import { updateComplaintStatus, COMPLAINT_STATUSES } from "../services/complaintApi";

function ComplaintCard({ complaint, onStatusUpdated }) {
  async function changeStatus(event) {
    const newStatus = event.target.value;

    try {
      const updated = await updateComplaintStatus(complaint.id, newStatus);
      onStatusUpdated(updated);
    } catch (error) {
      alert("Could not update complaint status.");
    }
  }

  const statusClass = complaint.status?.toLowerCase().replace(/_/g, "-");

  return (
    <article className="complaint-card">
      <div className="complaint-top">
        <div>
          <span className="complaint-id">#{complaint.id}</span>
          <h3>{complaint.category ?? "Uncategorized report"}</h3>
        </div>

        <span className={`status ${statusClass}`}>
          {COMPLAINT_STATUSES.find((s) => s.value === complaint.status)
            ?.label ?? complaint.status}
        </span>
      </div>

      {complaint.photoUrl && (
        <img
          src={complaint.photoUrl}
          alt={`Photo for complaint #${complaint.id}`}
          className="complaint-photo"
        />
      )}

      <p className="complaint-description">{complaint.description}</p>

      <div className="complaint-meta">
        <div>
          <span>Location</span>
          <strong>{complaint.locationHint || "—"}</strong>
        </div>
        <div>
          <span>Urgency</span>
          <strong>{complaint.urgency || "—"}</strong>
        </div>
        <div>
          <span>Department</span>
          <strong>{complaint.department || "—"}</strong>
        </div>
      </div>

      {complaint.aiReasoning && (
        <p className="complaint-ai-note">{complaint.aiReasoning}</p>
      )}

      <div className="complaint-footer">
        <span>Update status</span>

        <select value={complaint.status} onChange={changeStatus}>
          {COMPLAINT_STATUSES.map((s) => (
            <option key={s.value} value={s.value}>
              {s.label}
            </option>
          ))}
        </select>
      </div>
    </article>
  );
}

export default ComplaintCard;