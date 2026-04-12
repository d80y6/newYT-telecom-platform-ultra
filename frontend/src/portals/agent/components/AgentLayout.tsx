import { Outlet, Link, useLocation } from 'react-router-dom';

export default function AgentLayout() {
  const location = useLocation();
  
  const navItems = [
    { path: '/', label: 'Dashboard', icon: '📊' },
    { path: '/search', label: 'Customer Search', icon: '🔍' },
    { path: '/orders', label: 'Orders', icon: '📋' },
    { path: '/tickets', label: 'Tickets', icon: '🎫' },
    { path: '/billing', label: 'Billing', icon: '💰' },
    { path: '/provisioning', label: 'Provisioning', icon: '⚡' },
    { path: '/identity', label: 'Identity Verification', icon: '🆔' },
    { path: '/appointments', label: 'Appointments', icon: '📅' },
    { path: '/rating', label: 'Rating & Charging', icon: '💳' },
    { path: '/customer360', label: 'Customer 360', icon: '👁️' },
  ];

  return (
    <div className="agent-portal">
      <header className="portal-header">
        <div className="logo">PTC Agent Portal</div>
        <div className="user-info">
          <span>Agent: Sarah Khan</span>
          <span className="status online">Online</span>
          <button>Logout</button>
        </div>
      </header>
      <div className="portal-body">
        <nav className="portal-nav">
          <ul>
            {navItems.map(item => (
              <li key={item.path}>
                <Link to={item.path} className={location.pathname === item.path ? 'active' : ''}>
                  <span className="icon">{item.icon}</span>
                  <span>{item.label}</span>
                </Link>
              </li>
            ))}
          </ul>
        </nav>
        <main className="portal-content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
