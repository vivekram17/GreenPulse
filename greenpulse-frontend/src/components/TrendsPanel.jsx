import { useEffect, useState } from "react";
import { getTrends } from "../services/insightsApi";

function CountBar({ label, count, max }) {
  const width = max > 0 ? Math.max((count / max) * 100, 4) : 4;

  return (
    <div className="count-row">
      <span className="count-label">{label}</span>
      <div className="count-track">
        <div className="count-fill" style={{ width: `${width}%` }} />
      </div>
      <span className="count-value">{count}</span>
    </div>
  );
}

function TrendsPanel() {
  const [trends, setTrends] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function load() {
      try {
        setLoading(true);
        const data = await getTrends();
        setTrends(data);
      } catch (err) {
        setError("Unable to load trends. Make sure the backend is running.");
      } finally {
        setLoading(false);
      }
    }

    load();
  }, []);

  if (loading) {
    return <div className="state-message">Loading trends...</div>;
  }

  if (error) {
    return <div className="state-message error-message">{error}</div>;
  }

  const categoryEntries = Object.entries(trends.byCategory || {});
  const locationEntries = Object.entries(trends.byLocation || {});
  const maxCategory = Math.max(0, ...categoryEntries.map(([, n]) => n));
  const maxLocation = Math.max(0, ...locationEntries.map(([, n]) => n));

  return (
    <section className="trends-section">
      <div className="section-heading">
        <span>Insights</span>
        <h2>What the reports are telling us.</h2>
        <p>
          A rolling summary of every complaint GreenPulse has classified so
          far, grouped by category and location.
        </p>
      </div>

      {trends.summary && <p className="trends-summary">{trends.summary}</p>}

      <div className="trends-grid">
        <div className="trends-card">
          <h3>By category</h3>
          {categoryEntries.length === 0 ? (
            <p className="trends-empty">No category data yet.</p>
          ) : (
            categoryEntries.map(([category, count]) => (
              <CountBar
                key={category}
                label={category}
                count={count}
                max={maxCategory}
              />
            ))
          )}
        </div>

        <div className="trends-card">
          <h3>By location</h3>
          {locationEntries.length === 0 ? (
            <p className="trends-empty">No location data yet.</p>
          ) : (
            locationEntries.map(([location, count]) => (
              <CountBar
                key={location}
                label={location}
                count={count}
                max={maxLocation}
              />
            ))
          )}
        </div>
      </div>
    </section>
  );
}

export default TrendsPanel;
