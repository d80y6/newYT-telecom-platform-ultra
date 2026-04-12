import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { identityAPI } from '../services/api';

export default function IdentityVerification() {
  const queryClient = useQueryClient();

  const { data: identities, isLoading } = useQuery({ queryKey: ['identities'], queryFn: () => identityAPI.getIdentities() });

  const verifyMutation = useMutation({ mutationFn: (id: string) => identityAPI.verifyIdentity(id), onSuccess: () => queryClient.invalidateQueries({ queryKey: ['identities'] }) });

  if (isLoading) return <div className="loading">Loading...</div>;

  return (
    <div className="identity-verification-page">
      <h2>Identity Verification</h2>
      <div className="filter-bar">
        <button className="active">All</button>
        <button>Pending</button>
        <button>Verified</button>
      </div>
      <table className="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Type</th>
            <th>Status</th>
            <th>Verified At</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {identities?.map((identity: any) => (
            <tr key={identity.id}>
              <td>{identity.id}</td>
              <td>{identity.name}</td>
              <td>{identity.identityType}</td>
              <td><span className={`status ${identity.status?.toLowerCase()}`}>{identity.status}</span></td>
              <td>{identity.verifiedAt || '-'}</td>
              <td>
                {identity.status !== 'VERIFIED' && (
                  <button className="primary" onClick={() => verifyMutation.mutate(identity.id)}>Verify</button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
