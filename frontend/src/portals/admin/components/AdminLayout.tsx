import { Outlet, Link, useLocation } from 'react-router-dom';

export default function AdminLayout() {
  const location = useLocation();
  
  const navItems = [
    { path: '/', label: 'Dashboard', icon: '📊' },
    { path: '/users', label: 'User Management', icon: '👥' },
    { path: '/catalog', label: 'Product Catalog', icon: '📦' },
    { path: '/pricing', label: 'Product Pricing', icon: '💰' },
    { path: '/price-plans', label: 'Price Plans', icon: '📅' },
    { path: '/sales-leads', label: 'Sales Leads', icon: '🎯' },
    { path: '/quotes', label: 'Quotes', icon: '📝' },
    { path: '/analytics', label: 'Analytics', icon: '📈' },
    { path: '/agreements', label: 'Agreements', icon: '📜' },
    { path: '/notifications', label: 'Notifications', icon: '🔔' },
    { path: '/campaigns', label: 'Campaigns', icon: '📣' },
    { path: '/sla', label: 'SLA Management', icon: '✅' },
    { path: '/resources', label: 'Resources', icon: '🖥️' },
    { path: '/usage', label: 'Usage', icon: '📶' },
    { path: '/fraud', label: 'Fraud Detection', icon: '🚨' },
    { path: '/convergent-billing', label: 'Convergent Billing', icon: '💳' },
    { path: '/party', label: 'Party Management', icon: '👤' },
    { path: '/product-config', label: 'Product Config', icon: '⚙️' },
    { path: '/config', label: 'System Config', icon: '⚙️' },
    { path: '/reports', label: 'Reports', icon: '📊' },
    { path: '/audit', label: 'Audit Logs', icon: '📋' },
  ];

  return (
    <div className="admin-portal">
      <header className="portal-header">
        <div className="logo">PTC Admin Portal</div>
        <div className="user-info">
          <span>Admin: System</span>
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
