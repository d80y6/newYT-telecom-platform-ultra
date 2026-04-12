import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { salesLeadAPI } from '../../../services/api';

export default function SalesLeads() {
  const queryClient = useQueryClient();

  const { data: leads, isLoading } = useQuery({ queryKey: ['salesLeads'], queryFn: () => salesLeadAPI.getAll() });

  const updateMutation = useMutation({ mutationFn: ({ id, status }: { id: string; status: string }) => salesLeadAPI.updateStatus(id, status), onSuccess: () => queryClient.invalidateQueries({ queryKey: ['salesLeads'] }) });

  if (isLoading) return <div className="loading">Loading...</div>;

  return (
    <div className="sales-leads-page">
      <h2>Sales Leads</h2>
      <div className="lead-filters">
        <button className="active">All</button>
        <button>New</button>
        <button>Qualified</button>
        <button>Closed</button>
      </div>
      <div className="leads-list">
        {leads?.map((lead: any) => (
          <div key={lead.id} className="lead-card">
            <div className="lead-header">
              <span className="name">{lead.name}</span>
              <span className={`status ${lead.status?.toLowerCase()}`}>{lead.status}</span>
            </div>
            <div className="lead-body">
              <p>{lead.description}</p>
              <p className="meta">Created: {lead.createdAt}</p>
            </div>
            <div className="lead-actions">
              <button onClick={() => updateMutation.mutate({ id: lead.id, status: 'QUALIFIED' })}>Qualify</button>
              <button onClick={() => updateMutation.mutate({ id: lead.id, status: 'CLOSED' })}>Close</button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
