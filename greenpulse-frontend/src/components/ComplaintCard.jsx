import { updateComplaintStatus } from "../services/complaintApi";

function ComplaintCard({ complaint, onStatusUpdated }) {

    async function changeStatus(event) {

        const newStatus = event.target.value;

        try {

            const updatedComplaint =
                await updateComplaintStatus(
                    complaint.id,
                    newStatus
                );

            onStatusUpdated(updatedComplaint);

        } catch (error) {

            alert("Could not update complaint status.");

        }
    }

    const statusClass =
        complaint.status?.toLowerCase().replace(/\s+/g, "-");

    return (
        <article className="complaint-card">

            <div className="complaint-top">

                <div>
                    <span className="complaint-id">
                        #{complaint.id}
                    </span>

                    <h3>
                        {complaint.title}
                    </h3>
                </div>

                <span className={`status ${statusClass}`}>
                    {complaint.status}
                </span>

            </div>

            <p className="complaint-description">
                {complaint.description}
            </p>

            <div className="complaint-location">
                <span>Location</span>
                <strong>
                    {complaint.location}
                </strong>
            </div>

            <div className="complaint-footer">

                <span>
                    Update status
                </span>

                <select
                    value={complaint.status}
                    onChange={changeStatus}
                >
                    <option value="SUBMITTED">
                        Submitted
                    </option>

                    <option value="IN_PROGRESS">
                        In progress
                    </option>

                    <option value="RESOLVED">
                        Resolved
                    </option>

                    <option value="REJECTED">
                        Rejected
                    </option>
                </select>

            </div>

        </article>
    );
}

export default ComplaintCard;
