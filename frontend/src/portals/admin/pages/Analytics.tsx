import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { analyticsAPI } from '../../../services/api';

export default function AnalyticsDashboard() {
  const queryClient = useQueryClient();

  const { data: dashboard, isLoading: loadingDash } = useQuery({ queryKey: ['dashboard'], queryFn: () => analyticsAPI.getDashboard() });
  const { data: kpis, isLoading: loadingKpis } = useQuery({ queryKey: ['kpis'], queryFn: () => analyticsAPI.getKpis() });
  const { data: metrics, isLoading: loadingMetrics } = useQuery({ queryKey: ['metrics'], queryFn: () => analyticsAPI.getMetrics() });

  if (loadingDash || loadingKpis || loadingMetrics) return <div className="loading">Loading analytics...</div>;

  return (
    <div className="analytics-page">
      <h2>Analytics Dashboard</h2>
      
      <div className="kpi-cards">
        <div className="kpi-card">
          <h3>ARPU</h3>
          <p className="value">{kpis?.arpu || 0}</p>
        </div>
        <div className="kpi-card">
          <h3>Churn Rate</h3>
          <p className="value">{kpis?.churnRate || 0}%</p>
        </div>
        <div className="kpi-card">
          <h3>Collection Rate</h3>
          <p className="value">{kpis?.collectionRate || 0}%</p>
        </div>
      </div>

      <div className="dashboard-summary">
        <div className="summary-card">
          <h3>Total Revenue (7 days)</h3>
          <p className="value">{dashboard?.totalRevenue || 0} YER</p>
        </div>
        <div className="summary-card">
          <h3>Active Services</h3>
          <p className="value">{dashboard?.activeServices || 0}</p>
        </div>
        <div className="summary-card">
          <h3>Total Customers</h3>
          <p className="value">{dashboard?.totalCustomers || 0}</p>
        </div>
      </div>

      <div className="metrics-table">
        <h3>Recent Metrics</h3>
        <table className="data-table">
          <thead>
            <tr>
              <th>Name</th>
              <th>Category</th>
              <th>Value</th>
              <th>Change %</th>
            </tr>
          </thead>
          <tbody>
            {metrics?.map((metric: any) => (
              <tr key={metric.id}>
                <td>{metric.metricName}</td>
                <td>{metric.metricCategory}</td>
                <td>{metric.value}</td>
                <td className={metric.changePercent > 0 ? 'positive' : 'negative'}>{metric.changePercent}%</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
