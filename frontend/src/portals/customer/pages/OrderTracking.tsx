import { useQuery } from '@tanstack/react-query';
import { orderAPI } from '../services/api';

export default function OrderTracking() {
  const { data: orders, isLoading } = useQuery({ queryKey: ['orders'], queryFn: () => orderAPI.getAll() });

  if (isLoading) return <div className="loading">Loading...</div>;

  return (
    <div className="order-tracking-page">
      <h2>Order Tracking</h2>
      <div className="order-list">
        {orders?.map((order: any) => (
          <div key={order.id} className="order-card">
            <div className="order-header">
              <span className="order-id">{order.orderId}</span>
              <span className={`status ${order.status?.toLowerCase()}`}>{order.status}</span>
            </div>
            <div className="order-timeline">
              <div className={`timeline-step ${order.status !== 'PENDING' ? 'completed' : ''}`}>Pending</div>
              <div className={`timeline-step ${['PROCESSING', 'COMPLETED'].includes(order.status) ? 'completed' : ''}`}>Processing</div>
              <div className={`timeline-step ${order.status === 'COMPLETED' ? 'completed' : ''}`}>Completed</div>
            </div>
            <div className="order-body">
              <p>Total: {order.totalAmount} {order.currency}</p>
              <p>Created: {order.orderDate}</p>
              <p>Expected: {order.expectedDeliveryDate}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
