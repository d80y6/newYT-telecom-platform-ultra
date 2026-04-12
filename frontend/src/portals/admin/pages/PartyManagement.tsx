import { useQuery } from '@tanstack/react-query';
import axios from 'axios';

const client = axios.create({ baseURL: import.meta.env.VITE_API_URL || '/tmf-api' });

export default function PartyManagement() {
  const { data, isLoading } = useQuery({ queryKey: ['parties'], queryFn: () => client.get('/partyManagement/v5/party').then(r => r.data) });

  if (isLoading) return <div className="loading">Loading...</div>;

  return (
    <div className="party-page">
      <h2>Party Management</h2>
      <button className="primary">Add Party</button>
      <table className="data-table">
        <thead><tr><th>ID</th><th>Name</th><th>Type</th><th>Status</th><th>Contact</th><th>Created</th></tr></thead>
        <tbody>
          {data?.map((item: any) => (
            <tr key={item.id}>
              <td>{item.id}</td>
              <td>{item.name}</td>
              <td>{item.partyType}</td>
              <td><span className={`status ${item.status?.toLowerCase()}`}>{item.status}</span></td>
              <td>{item.contactEmail}</td>
              <td>{item.createdAt}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
