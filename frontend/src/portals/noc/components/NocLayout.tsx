import { Outlet, Link, useLocation } from 'react-router-dom';

export default function NocLayout() {
  const location = useLocation();
  
  const navItems = [
    { path: '/', label: 'Dashboard', icon: '📊' },
    { path: '/network', label: 'Network Map', icon: '🌐' },
    { path: '/alarms', label: 'Alarms', icon: '🚨' },
    { path: '/performance', label: 'Performance', icon: '📈' },
    { path: '/sla', label: 'SLA Monitoring', icon: '📋' },
  ];

  return (
    <div className="noc-portal dark-theme">
      <header className="portal-header">
        <div className="logo">NOC Dashboard</div>
        <div className="status-bar">
          <span className="status-indicator healthy"></span>
          <span>System Healthy</span>
          <span className="time">12:34:56 AST</span>
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
