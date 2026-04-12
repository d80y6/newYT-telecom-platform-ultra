import { useQuery } from '@tanstack/react-query';
import { billingAPI } from '../services/api';

interface Bill {
  id: string;
  invoiceNumber: string;
  amount: number;
  dueDate: string;
  status: string;
}

export default function MyBills() {
  const { data: bills, isLoading } = useQuery({
    queryKey: ['myBills'],
    queryFn: () => billingAPI.getMyBills()
  });

  if (isLoading) return <div className="loading">Loading...</div>;

  const totalDue = bills?.reduce((sum: number, b: Bill) => sum + b.amount, 0) || 0;

  return (
    <div className="bills-page">
      <div className="balance-card">
        <h3>Total Due</h3>
        <p className="amount">${totalDue.toFixed(2)}</p>
        <button className="pay-btn">Pay Now</button>
      </div>
      <h2>Billing History</h2>
      <table className="bills-table">
        <thead>
          <tr>
            <th>Invoice #</th>
            <th>Amount</th>
            <th>Due Date</th>
            <th>Status</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {bills?.map((bill: Bill) => (
            <tr key={bill.id}>
              <td>{bill.invoiceNumber}</td>
              <td>${bill.amount.toFixed(2)}</td>
              <td>{bill.dueDate}</td>
              <td><span className={`status ${bill.status.toLowerCase()}`}>{bill.status}</span></td>
              <td><button>View</button></td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
