import { useQuery } from '@tanstack/react-query';
import { usageAPI } from '../services/api';

interface UsageRecord {
  id: string;
  date: string;
  type: string;
  quantity: number;
  cost: number;
}

export default function UsageHistory() {
  const { data: usage, isLoading } = useQuery({
    queryKey: ['myUsage'],
    queryFn: () => usageAPI.getMyUsage()
  });

  if (isLoading) return <div className="loading">Loading...</div>;

  const totalCost = usage?.reduce((sum: number, u: UsageRecord) => sum + u.cost, 0) || 0;

  return (
    <div className="usage-page">
      <div className="usage-summary">
        <div className="summary-item">
          <h3>Total Data</h3>
          <p>45.2 GB</p>
        </div>
        <div className="summary-item">
          <h3>Total Voice</h3>
          <p>1,240 min</p>
        </div>
        <div className="summary-item">
          <h3>Total SMS</h3>
          <p>890</p>
        </div>
        <div className="summary-item">
          <h3>Total Cost</h3>
          <p>YER {totalCost.toFixed(2)}</p>
        </div>
      </div>
      <h2>Usage History</h2>
      <table className="usage-table">
        <thead>
          <tr>
            <th>Date</th>
            <th>Type</th>
            <th>Quantity</th>
            <th>Cost</th>
          </tr>
        </thead>
        <tbody>
          {usage?.map((record: UsageRecord) => (
            <tr key={record.id}>
              <td>{record.date}</td>
              <td><span className={`type ${record.type.toLowerCase()}`}>{record.type}</span></td>
              <td>{record.quantity}</td>
              <td>YER {record.cost.toFixed(2)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
