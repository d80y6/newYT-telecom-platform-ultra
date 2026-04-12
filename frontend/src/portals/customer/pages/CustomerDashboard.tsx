import { useQuery } from '@tanstack/react-query';
import { customerAPI } from '../services/api';

export default function CustomerDashboard() {
  const { data: customer } = useQuery({
    queryKey: ['customer'],
    queryFn: () => customerAPI.getCurrent()
  });

  return (
    <div className="dashboard">
      <h2>Welcome Back!</h2>
      <div className="summary-cards">
        <div className="card">
          <h3>Current Plan</h3>
          <p className="highlight">Unlimited Plus</p>
          <span className="status active">Active</span>
        </div>
        <div className="card">
          <h3>Data Used</h3>
          <p className="highlight">45.2 GB</p>
          <span className="sub">of 100 GB</span>
        </div>
        <div className="card">
          <h3>Next Bill</h3>
          <p className="highlight">$45.00</p>
          <span className="sub">Due May 1, 2026</span>
        </div>
        <div className="card">
          <h3>Account Status</h3>
          <p className="highlight">Good</p>
          <span className="status ok">No Due</span>
        </div>
      </div>
      <div className="quick-actions">
        <h3>Quick Actions</h3>
        <div className="actions-grid">
          <button>📱 Add Data</button>
          <button>💳 Pay Bill</button>
          <button>📞 Buy Airtime</button>
          <button>🎫 Open Ticket</button>
        </div>
      </div>
    </div>
  );
}
