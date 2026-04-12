import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import axios from 'axios';

const client = axios.create({ baseURL: import.meta.env.VITE_API_URL || '/tmf-api' });

const campaignAPI = {
  getAll: () => client.get('/campaignManagement/v5/campaign').then(r => r.data),
  getById: (id: string) => client.get(`/campaignManagement/v5/campaign/${id}`).then(r => r.data),
  create: (data: any) => client.post('/campaignManagement/v5/campaign', data).then(r => r.data),
  launch: (id: string) => client.post(`/campaignManagement/v5/campaign/${id}/launch`).then(r => r.data),
  cancel: (id: string) => client.post(`/campaignManagement/v5/campaign/${id}/cancel`).then(r => r.data)
};

export default function CampaignManagement() {
  const queryClient = useQueryClient();
  const { data: campaigns, isLoading } = useQuery({ queryKey: ['campaigns'], queryFn: campaignAPI.getAll });
  const launchMutation = useMutation({ mutationFn: (id: string) => campaignAPI.launch(id), onSuccess: () => queryClient.invalidateQueries({ queryKey: ['campaigns'] }) });

  if (isLoading) return <div className="loading">Loading...</div>;

  return (
    <div className="campaign-page">
      <h2>Campaign Management</h2>
      <button className="primary" onClick={() => campaignAPI.create({ name: 'New Campaign', type: 'PROMOTION' })}>Create Campaign</button>
      <div className="campaign-grid">
        {campaigns?.map((camp: any) => (
          <div key={camp.id} className="campaign-card">
            <div className="camp-header">
              <h3>{camp.name}</h3>
              <span className={`status ${camp.status?.toLowerCase()}`}>{camp.status}</span>
            </div>
            <div className="camp-body">
              <p>Type: {camp.campaignType}</p>
              <p>Start: {camp.startDate}</p>
              <p>End: {camp.endDate}</p>
              <p>Target: {camp.targetAudience}</p>
            </div>
            <div className="camp-actions">
              {camp.status === 'DRAFT' && <button onClick={() => launchMutation.mutate(camp.id)}>Launch</button>}
              {camp.status === 'ACTIVE' && <button onClick={() => campaignAPI.cancel(camp.id)}>Cancel</button>}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
