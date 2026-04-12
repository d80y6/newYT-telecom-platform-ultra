import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import axios from 'axios';

const client = axios.create({ baseURL: import.meta.env.VITE_API_URL || '/tmf-api' });

const agreementAPI = {
  getAll: () => client.get('/agreementManagement/v5/agreement').then(r => r.data),
  getById: (id: string) => client.get(`/agreementManagement/v5/agreement/${id}`).then(r => r.data),
  create: (data: any) => client.post('/agreementManagement/v5/agreement', data).then(r => r.data),
  update: (id: string, data: any) => client.patch(`/agreementManagement/v5/agreement/${id}`, data).then(r => r.data),
  terminate: (id: string) => client.post(`/agreementManagement/v5/agreement/${id}/terminate`).then(r => r.data)
};

export default function AgreementManagement() {
  const queryClient = useQueryClient();
  const { data: agreements, isLoading } = useQuery({ queryKey: ['agreements'], queryFn: agreementAPI.getAll });
  const mutate = useMutation({ mutationFn: (data: any) => agreementAPI.create(data), onSuccess: () => queryClient.invalidateQueries({ queryKey: ['agreements'] }) });

  if (isLoading) return <div className="loading">Loading...</div>;

  return (
    <div className="agreement-page">
      <h2>Agreement Management</h2>
      <button className="primary" onClick={() => mutate.mutate({ name: 'New Agreement', type: 'CONTRACT' })}>Create Agreement</button>
      <table className="data-table">
        <thead><tr><th>ID</th><th>Name</th><th>Type</th><th>Status</th><th>Start Date</th><th>End Date</th></tr></thead>
        <tbody>
          {agreements?.map((item: any) => (
            <tr key={item.id}>
              <td>{item.id}</td>
              <td>{item.name}</td>
              <td>{item.agreementType}</td>
              <td><span className={`status ${item.status?.toLowerCase()}`}>{item.status}</span></td>
              <td>{item.startDate}</td>
              <td>{item.endDate}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
