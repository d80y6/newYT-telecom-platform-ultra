import { Outlet, Link, useLocation } from 'react-router-dom';

export default function CustomerLayout() {
  const location = useLocation();
  
  const navItems = [
    { path: '/', label: 'Dashboard', icon: '📊' },
    { path: '/services', label: 'My Services', icon: '📱' },
    { path: '/bills', label: 'Bills', icon: '📄' },
    { path: '/payment', label: 'Payment', icon: '💳' },
    { path: '/usage', label: 'Usage', icon: '📈' },
    { path: '/cart', label: 'Cart', icon: '🛒' },
    { path: '/orders', label: 'Orders', icon: '📦' },
    { path: '/support', label: 'Support', icon: '🎫' },
  ];

  return (
    <div className="customer-portal">
      <header className="portal-header">
        <div className="logo">PTC Customer Portal</div>
        <div className="user-info">
          <span>Welcome, John Doe</span>
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
