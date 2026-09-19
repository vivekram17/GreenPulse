import { useState } from "react";
import { askPolicyQuestion } from "../services/policyApi";

function PolicyAsk() {
  const [question, setQuestion] = useState("");
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");

  async function handleSubmit(event) {
    event.preventDefault();

    if (!question.trim()) return;

    setLoading(true);
    setError("");
    setResult(null);

    try {
      const answer = await askPolicyQuestion(question);
      setResult(answer);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <section className="policy-section">
      <div className="section-heading">
        <span>Policy Q&amp;A</span>
        <h2>Ask about local waste and sanitation policy.</h2>
        <p>
          Answers are drawn from GreenPulse's local policy knowledge base —
          sample content for this demo, not official municipal policy.
        </p>
      </div>

      <form onSubmit={handleSubmit} className="policy-form">
        <input
          type="text"
          placeholder="Example: How long until someone responds to a water leak?"
          value={question}
          onChange={(event) => setQuestion(event.target.value)}
          required
        />

        <button type="submit" disabled={loading} className="primary-button">
          {loading ? "Asking..." : "Ask"}
        </button>
      </form>

      {error && <p className="error-message">{error}</p>}

      {result && (
        <div className="policy-answer">
          <p>{result.answer || "No answer was returned."}</p>

          {result.source && (
            <p className="policy-source">
              Source: {typeof result.source === "string"
                ? result.source
                : JSON.stringify(result.source)}
            </p>
          )}
        </div>
      )}
    </section>
  );
}

export default PolicyAsk;
