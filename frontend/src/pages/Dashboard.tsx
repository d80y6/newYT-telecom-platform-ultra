import { useQuery } from '@tanstack/react-query';
import { customerAPI } from '../services/api';

interface Customer {
  id: string;
  name: string;
  email: string;
  status: string;
}

export default function Dashboard() {
  const { data, isLoading } = useQuery({
    queryKey: ['stats'],
    queryFn: async () => {
      const customers = await customerAPI.getAll();
      return {
        totalCustomers: customers.length,
        activeOrders: 0,
        pendingPayments: 0,
        revenue: 0
      };
    }
  });

  if (isLoading) return <div>Loading...</div>;

  return (
    <div>
      <h2>Dashboard</h2>
      <div className="stats-grid">
        <div className="stat-card">
          <h3>Total Customers</h3>
          <p>{data?.totalCustomers || 0}</p>
        </div>
        <div className="stat-card">
          <h3>Active Orders</h3>
          <p>{data?.activeOrders || 0}</p>
        </div>
        <div className="stat-card">
          <h3>Pending Payments</h3>
          <p>{data?.pendingPayments || 0}</p>
        </div>
        <div className="stat-card">
          <h3>Revenue</h3>
          <p>${data?.revenue || 0}</p>
        </div>
      </div>
    </div>
  );
}
