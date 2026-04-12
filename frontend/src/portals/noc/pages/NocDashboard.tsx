import { useQuery } from '@tanstack/react-query';

export default function NocDashboard() {
  const metrics = {
    criticalAlarms: 2,
    warnings: 8,
    networkHealth: 98.5,
    activeCalls: 45200,
    dataThroughput: '45 Gbps',
    smsPerSec: 1200
  };

  return (
    <div className="noc-dashboard">
      <h2>Network Operations Center</h2>
      <div className="kpi-grid">
        <div className="kpi-card critical">
          <h3>Critical Alarms</h3>
          <p className="value">{metrics.criticalAlarms}</p>
        </div>
        <div className="kpi-card warning">
          <h3>Warnings</h3>
          <p className="value">{metrics.warnings}</p>
        </div>
        <div className="kpi-card success">
          <h3>Network Health</h3>
          <p className="value">{metrics.networkHealth}%</p>
        </div>
        <div className="kpi-card">
          <h3>Active Calls</h3>
          <p className="value">{metrics.activeCalls.toLocaleString()}</p>
        </div>
        <div className="kpi-card">
          <h3>Data Throughput</h3>
          <p className="value">{metrics.dataThroughput}</p>
        </div>
        <div className="kpi-card">
          <h3>SMS/sec</h3>
          <p className="value">{metrics.smsPerSec}</p>
        </div>
      </div>
      <div className="charts-row">
        <div className="chart-card">
          <h3>Network Traffic</h3>
          <div className="chart-placeholder">[Traffic Chart]</div>
        </div>
        <div className="chart-card">
          <h3>Active Incidents</h3>
          <div className="incident-list">
            <div className="incident critical">CORE-001: Router failure - Sana'a</div>
            <div className="incident warning">CORE-002: High latency - Aden</div>
          </div>
        </div>
      </div>
    </div>
  );
}
