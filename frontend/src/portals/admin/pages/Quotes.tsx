import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { quoteAPI } from '../../../services/api';

export default function Quotes() {
  const queryClient = useQueryClient();

  const { data: quotes, isLoading } = useQuery({ queryKey: ['quotes'], queryFn: () => quoteAPI.getAll() });

  const updateMutation = useMutation({ mutationFn: ({ id, status }: { id: string; status: string }) => quoteAPI.updateStatus(id, status), onSuccess: () => queryClient.invalidateQueries({ queryKey: ['quotes'] }) });

  if (isLoading) return <div className="loading">Loading...</div>;

  return (
    <div className="quotes-page">
      <h2>Quotes</h2>
      <table className="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Total</th>
            <th>Status</th>
            <th>Valid Until</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {quotes?.map((quote: any) => (
            <tr key={quote.id}>
              <td>{quote.id}</td>
              <td>{quote.name}</td>
              <td>{quote.totalAmount} {quote.currency}</td>
              <td><span className={`status ${quote.status?.toLowerCase()}`}>{quote.status}</span></td>
              <td>{quote.validUntil}</td>
              <td>
                <button onClick={() => updateMutation.mutate({ id: quote.id, status: 'ACCEPTED' })}>Accept</button>
                <button onClick={() => updateMutation.mutate({ id: quote.id, status: 'REJECTED' })}>Reject</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
