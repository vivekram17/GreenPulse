import { useState } from "react";

import ComplaintForm from "./components/ComplaintForm";
import ComplaintList from "./components/ComplaintList";
import TrendsPanel from "./components/TrendsPanel";
import PolicyAsk from "./components/PolicyAsk";

function App() {
  const [refreshKey, setRefreshKey] = useState(0);

  function handleComplaintSubmitted() {
    setRefreshKey((previous) => previous + 1);
  }

  return (
    <div className="app">
      <header className="navbar">
        <div className="brand">
          <div className="brand-mark">G</div>
          <span>GreenPulse</span>
        </div>

        <nav>
          <a href="#report">Report</a>
          <a href="#complaints">Community issues</a>
          <a href="#insights">Insights</a>
          <a href="#policy">Policy Q&amp;A</a>
        </nav>
      </header>

      <main>
        <section className="hero">
          <div className="hero-content">
            <p className="hero-kicker">Waste management, made visible</p>

            <h1>
              Cleaner places start with
              <br />
              better reports.
            </h1>

            <p className="hero-description">
              GreenPulse reads every citizen report, classifies it, routes it
              to the right department, and turns local observations into a
              clear trend picture — SDG 11, with SDG 12 and SDG 6 alongside.
            </p>

            <a href="#report" className="hero-button">
              Report a waste issue
            </a>
          </div>

          <div className="hero-visual">
            <div className="pulse-ring"></div>
            <div className="waste-symbol">♻</div>

            <div className="hero-note">
              <strong>Community signal</strong>
              <span>Every report helps identify where action is needed.</span>
            </div>
          </div>
        </section>

        <section id="report" className="content-layout">
          <ComplaintForm onComplaintSubmitted={handleComplaintSubmitted} />
        </section>

        <div id="complaints" key={refreshKey}>
          <ComplaintList />
        </div>

        <div id="insights" className="content-layout insights-layout">
          <TrendsPanel />
        </div>

        <div id="policy" className="content-layout policy-layout">
          <PolicyAsk />
        </div>
      </main>

      <footer>
        <div>GreenPulse</div>
        <p>Technology for cleaner communities.</p>
      </footer>
    </div>
  );
}

export default App;
