import { useQuery, useMutation } from '@tanstack/react-query';
import { orderAPI } from '../services/api';

interface Order {
  id: string;
  orderNumber: string;
  status: string;
  totalAmount: number;
  createdAt: string;
  customerName: string;
}

export default function OrderManagement() {
  const { data: orders, isLoading, refetch } = useQuery({
    queryKey: ['orders'],
    queryFn: () => orderAPI.getAll()
  });

  const approveMutation = useMutation({
    mutationFn: (id: string) => orderAPI.approve(id),
    onSuccess: () => refetch()
  });

  const rejectMutation = useMutation({
    mutationFn: (id: string) => orderAPI.reject(id, 'Rejected by agent'),
    onSuccess: () => refetch()
  });

  if (isLoading) return <div className="loading">Loading...</div>;

  return (
    <div className="orders-page">
      <h2>Order Management</h2>
      <div className="filter-bar">
        <select>
          <option value="">All Status</option>
          <option value="PENDING">Pending</option>
          <option value="APPROVED">Approved</option>
          <option value="COMPLETED">Completed</option>
          <option value="CANCELLED">Cancelled</option>
        </select>
      </div>
      <table>
        <thead>
          <tr>
            <th>Order #</th>
            <th>Customer</th>
            <th>Amount</th>
            <th>Status</th>
            <th>Date</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {orders?.map((order: Order) => (
            <tr key={order.id}>
              <td>{order.orderNumber}</td>
              <td>{order.customerName}</td>
              <td>YER {order.totalAmount}</td>
              <td><span className={`status ${order.status.toLowerCase()}`}>{order.status}</span></td>
              <td>{order.createdAt}</td>
              <td>
                {order.status === 'PENDING' && (
                  <>
                    <button onClick={() => approveMutation.mutate(order.id)}>Approve</button>
                    <button onClick={() => rejectMutation.mutate(order.id)} className="danger">Reject</button>
                  </>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
