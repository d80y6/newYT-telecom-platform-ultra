import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';

export default function AgentDashboard() {
  const stats = {
    openTickets: 12,
    pendingOrders: 8,
    callsToday: 45,
    avgHandleTime: '4:30'
  };

  return (
    <div className="dashboard">
      <h2>Agent Dashboard</h2>
      <div className="stats-grid">
        <div className="stat-card warning">
          <h3>Open Tickets</h3>
          <p className="highlight">{stats.openTickets}</p>
          <Link to="/tickets">View All</Link>
        </div>
        <div className="stat-card info">
          <h3>Pending Orders</h3>
          <p className="highlight">{stats.pendingOrders}</p>
          <Link to="/orders">View All</Link>
        </div>
        <div className="stat-card success">
          <h3>Calls Today</h3>
          <p className="highlight">{stats.callsToday}</p>
        </div>
        <div className="stat-card">
          <h3>Avg Handle Time</h3>
          <p className="highlight">{stats.avgHandleTime}</p>
        </div>
      </div>
      <div className="quick-actions">
        <h3>Quick Actions</h3>
        <div className="actions-grid">
          <Link to="/search" className="action-btn">🔍 Search Customer</Link>
          <Link to="/orders" className="action-btn">📋 New Order</Link>
          <Link to="/tickets" className="action-btn">🎫 Create Ticket</Link>
          <Link to="/billing" className="action-btn">💰 Process Payment</Link>
        </div>
      </div>
    </div>
  );
}
