import { useQuery } from '@tanstack/react-query';
import { billingAPI } from '../services/api';

interface Invoice {
  id: string;
  invoiceNumber: string;
  amount: number;
  status: string;
  dueDate: string;
}

export default function Billing() {
  const { data: invoices, isLoading } = useQuery({
    queryKey: ['invoices'],
    queryFn: () => billingAPI.getInvoices()
  });

  if (isLoading) return <div>Loading...</div>;

  return (
    <div>
      <h2>Billing</h2>
      <table>
        <thead>
          <tr>
            <th>Invoice #</th>
            <th>Amount</th>
            <th>Status</th>
            <th>Due Date</th>
          </tr>
        </thead>
        <tbody>
          {invoices?.map((invoice: Invoice) => (
            <tr key={invoice.id}>
              <td>{invoice.invoiceNumber}</td>
              <td>${invoice.amount}</td>
              <td>{invoice.status}</td>
              <td>{invoice.dueDate}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
