import { useQuery } from '@tanstack/react-query';

export default function AdminDashboard() {
  const stats = {
    totalCustomers: 125000,
    activeSubscriptions: 98000,
    dailyRevenue: 450000,
    systemHealth: 99.9
  };

  return (
    <div className="dashboard">
      <h2>System Overview</h2>
      <div className="stats-grid">
        <div className="stat-card">
          <h3>Total Customers</h3>
          <p className="highlight">{stats.totalCustomers.toLocaleString()}</p>
        </div>
        <div className="stat-card">
          <h3>Active Subscriptions</h3>
          <p className="highlight">{stats.activeSubscriptions.toLocaleString()}</p>
        </div>
        <div className="stat-card success">
          <h3>Daily Revenue</h3>
          <p className="highlight">YER {stats.dailyRevenue.toLocaleString()}</p>
        </div>
        <div className="stat-card success">
          <h3>System Health</h3>
          <p className="highlight">{stats.systemHealth}%</p>
        </div>
      </div>
      <div className="admin-actions">
        <h3>Quick Actions</h3>
        <div className="actions-grid">
          <button>Create User</button>
          <button>Add Product</button>
          <button>Run Report</button>
          <button>View Logs</button>
        </div>
      </div>
    </div>
  );
}
