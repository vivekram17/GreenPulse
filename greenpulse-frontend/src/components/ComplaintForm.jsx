import { useState } from "react";
import { submitComplaint } from "../services/complaintApi";

function ComplaintForm({ onComplaintSubmitted }) {

    const [form, setForm] = useState({
        title: "",
        description: "",
        location: "",
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
