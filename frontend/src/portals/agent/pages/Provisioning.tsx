import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { provisioningAPI } from '../services/api';

export default function Provisioning() {
  const queryClient = useQueryClient();

  const { data: orders, isLoading } = useQuery({
    queryKey: ['provisioning'],
    queryFn: () => provisioningAPI.getAll()
  });

  const activateMutation = useMutation({
    mutationFn: (id: string) => provisioningAPI.activate(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['provisioning'] })
  });

  const deactivateMutation = useMutation({
    mutationFn: (id: string) => provisioningAPI.deactivate(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['provisioning'] })
  });

  return (
    <div className="provisioning-page">
      <h2>Service Provisioning</h2>
      <table>
        <thead>
          <tr>
            <th>Service Order</th>
            <th>Customer</th>
            <th>Service Type</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {orders?.map((o: any) => (
            <tr key={o.id}>
              <td>{o.id}</td>
              <td>{o.customerName}</td>
              <td>{o.serviceType}</td>
              <td><span className={`status ${o.status?.toLowerCase()}`}>{o.status}</span></td>
              <td>
                <button onClick={() => activateMutation.mutate(o.id)}>Activate</button>
                <button onClick={() => deactivateMutation.mutate(o.id)} className="danger">Deactivate</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
