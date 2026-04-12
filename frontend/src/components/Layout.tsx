import { Outlet, Link } from 'react-router-dom';

export default function Layout() {
  return (
    <div className="layout">
      <nav className="sidebar">
        <h1>PTC Portal</h1>
        <ul>
          <li><Link to="/">Dashboard</Link></li>
          <li><Link to="/customers">Customers</Link></li>
          <li><Link to="/orders">Orders</Link></li>
          <li><Link to="/billing">Billing</Link></li>
        </ul>
      </nav>
      <main className="content">
        <Outlet />
      </main>
    </div>
  );
}
