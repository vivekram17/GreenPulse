// import { useState } from "react";
// import { submitComplaint } from "../services/complaintApi";

// function ComplaintForm({ onComplaintSubmitted }) {
//   const [form, setForm] = useState({
//     description: "",
//     locationHint: "",
//   });

//   const [loading, setLoading] = useState(false);
//   const [submitted, setSubmitted] = useState(null);
//   const [error, setError] = useState("");

//   function handleChange(event) {
//     const { name, value } = event.target;
//     setForm((prev) => ({ ...prev, [name]: value }));
//   }

//   async function handleSubmit(event) {
//     event.preventDefault();

//     setLoading(true);
//     setSubmitted(null);
//     setError("");

//     try {
//       const complaint = await submitComplaint(form);

//       setSubmitted(complaint);
//       setForm({ description: "", locationHint: "" });

//       if (onComplaintSubmitted) {
//         onComplaintSubmitted(complaint);
//       }
//     } catch (err) {
//       setError(err.message);
//     } finally {
//       setLoading(false);
//     }
//   }

//   return (
//     <section className="form-section">
//       <div className="section-heading">
//         <span>Report a problem</span>
//         <h2>Tell GreenPulse what needs attention.</h2>
//         <p>
//           Describe a waste, water, or sanitation issue in your own words —
//           GreenPulse's AI reads it, classifies it, and routes it to the right
//           department automatically.
//         </p>
//       </div>

//       <form onSubmit={handleSubmit} className="complaint-form">
//         <div className="form-group">
//           <label htmlFor="description">What happened?</label>
//           <textarea
//             id="description"
//             name="description"
//             placeholder="Example: Water has been leaking from a broken pipe near the bus stop on Lake Road for two days."
//             value={form.description}
//             onChange={handleChange}
//             rows="5"
//             required
//           />
//         </div>

//         <div className="form-group">
//           <label htmlFor="locationHint">Location</label>
//           <input
//             id="locationHint"
//             name="locationHint"
//             type="text"
//             placeholder="Example: Lake Road"
//             value={form.locationHint}
//             onChange={handleChange}
//             required
//           />
//         </div>

//         <button type="submit" disabled={loading} className="primary-button">
//           {loading ? "Submitting..." : "Report issue"}
//         </button>

//         {error && <p className="error-message">{error}</p>}
//       </form>

//       {submitted && (
//         <div className="ai-result">
//           <span className="ai-result-label">GreenPulse AI routed this as</span>

//           <div className="ai-result-grid">
//             <div>
//               <span>Category</span>
//               <strong>{submitted.category ?? "—"}</strong>
//             </div>
//             <div>
//               <span>Urgency</span>
//               <strong>{submitted.urgency ?? "—"}</strong>
//             </div>
//             <div>
//               <span>Department</span>
//               <strong>{submitted.department ?? "—"}</strong>
//             </div>
//           </div>

//           {submitted.aiReasoning && (
//             <p className="ai-reasoning">"{submitted.aiReasoning}"</p>
//           )}
//         </div>
//       )}
//     </section>
//   );
// }

// export default ComplaintForm;
import { useState } from "react";
import { submitComplaint } from "../services/complaintApi";

function ComplaintForm({ onComplaintSubmitted }) {

    const [form, setForm] = useState({
        title: "",
        description: "",
        location: "",
        photoUrl: "",
    });

    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState("");
    const [error, setError] = useState("");

    function handleChange(event) {
        const { name, value } = event.target;

        setForm({
            ...form,
            [name]: value,
        });
    }

    async function handleSubmit(event) {
        event.preventDefault();

        setLoading(true);
        setMessage("");
        setError("");

        try {
            const complaint = await submitComplaint(form);

            setMessage("Complaint submitted successfully.");

            setForm({
                title: "",
                description: "",
                location: "",
                photoUrl: "",
            });

            if (onComplaintSubmitted) {
                onComplaintSubmitted(complaint);
            }

        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    }

    return (
        <section className="form-section">

            <div className="section-heading">
                <span>Report a problem</span>
                <h2>Tell GreenPulse what needs attention.</h2>
                <p>
                    Report overflowing bins, missed collections,
                    illegal dumping, or other waste-related issues.
                </p>
            </div>

            <form onSubmit={handleSubmit} className="complaint-form">

                <div className="form-group">
                    <label htmlFor="title">
                        Complaint title
                    </label>

                    <input
                        id="title"
                        name="title"
                        type="text"
                        placeholder="Example: Overflowing waste bin"
                        value={form.title}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="description">
                        What happened?
                    </label>

                    <textarea
                        id="description"
                        name="description"
                        placeholder="Describe the waste problem..."
                        value={form.description}
                        onChange={handleChange}
                        rows="5"
                        required
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="location">
                        Location
                    </label>

                    <input
                        id="location"
                        name="location"
                        type="text"
                        placeholder="Example: Main gate, Block A"
                        value={form.location}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="photoUrl">
                        Photo URL (optional)
                    </label>

                    <input
                        id="photoUrl"
                        name="photoUrl"
                        type="url"
                        placeholder="https://example.com/photo.jpg"
                        value={form.photoUrl}
                        onChange={handleChange}
                    />
                </div>

                <button
                    type="submit"
                    disabled={loading}
                    className="primary-button"
                >
                    {loading ? "Submitting..." : "Report issue"}
                </button>

                {message && (
                    <p className="success-message">
                        {message}
                    </p>
                )}

                {error && (
                    <p className="error-message">
                        {error}
                    </p>
                )}

            </form>

        </section>
    );
}

export default ComplaintForm;