import { useParams, Link } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { customerAPI, orderAPI, billingAPI, troubleTicketAPI } from '../services/api';

export default function CustomerDetail() {
  const { id } = useParams();
  
  const { data: customer, isLoading } = useQuery({
    queryKey: ['customer', id],
    queryFn: () => customerAPI.getById(id!)
  });

  const { data: orders } = useQuery({
    queryKey: ['customerOrders', id],
    queryFn: () => orderAPI.getAll()
  });

  const { data: bills } = useQuery({
    queryKey: ['customerBills', id],
    queryFn: () => billingAPI.getInvoices()
  });

  const { data: tickets } = useQuery({
    queryKey: ['customerTickets', id],
    queryFn: () => troubleTicketAPI.getAll()
  });

  if (isLoading) return <div className="loading">Loading...</div>;

  return (
    <div className="customer-detail">
      <div className="detail-header">
        <h2>{customer?.name}</h2>
        <div className="actions">
          <button>Edit</button>
          <button>New Order</button>
          <button>Create Ticket</button>
        </div>
      </div>
      
      <div className="detail-grid">
        <div className="detail-card">
          <h3>Contact Info</h3>
          <p><strong>Phone:</strong> {customer?.phone}</p>
          <p><strong>Email:</strong> {customer?.email}</p>
          <p><strong>Status:</strong> <span className={`status ${customer?.status?.toLowerCase()}`}>{customer?.status}</span></p>
        </div>
        <div className="detail-card">
          <h3>Account Summary</h3>
          <p><strong>Balance:</strong> YER 1,250</p>
          <p><strong>Outstanding:</strong> YER 450</p>
          <p><strong>Last Payment:</strong> Apr 1, 2026</p>
        </div>
      </div>

      <div className="detail-tabs">
        <section>
          <h3>Recent Orders</h3>
          <table>
            <thead><tr><th>Order #</th><th>Status</th><th>Date</th><th>Amount</th></tr></thead>
            <tbody>
              {orders?.slice(0, 5).map((o: any) => (
                <tr key={o.id}><td>{o.orderNumber}</td><td>{o.status}</td><td>{o.createdAt}</td><td>YER {o.totalAmount}</td></tr>
              ))}
            </tbody>
          </table>
        </section>
        <section>
          <h3>Open Tickets</h3>
          <table>
            <thead><tr><th>Ticket</th><th>Status</th><th>Priority</th></tr></thead>
            <tbody>
              {tickets?.slice(0, 5).map((t: any) => (
                <tr key={t.id}><td>{t.title}</td><td>{t.status}</td><td>{t.priority}</td></tr>
              ))}
            </tbody>
          </table>
        </section>
      </div>
    </div>
  );
}
