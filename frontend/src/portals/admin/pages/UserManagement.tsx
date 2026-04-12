import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { adminAPI } from '../services/api';

export default function UserManagement() {
  const queryClient = useQueryClient();

  const { data: users, isLoading } = useQuery({
    queryKey: ['users'],
    queryFn: () => adminAPI.getUsers()
  });

  const deleteMutation = useMutation({
    mutationFn: (id: string) => adminAPI.deleteUser(id),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['users'] })
  });

  if (isLoading) return <div className="loading">Loading...</div>;

  return (
    <div className="users-page">
      <div className="page-header">
        <h2>User Management</h2>
        <button>+ Add User</button>
      </div>
      <table>
        <thead>
          <tr>
            <th>Username</th>
            <th>Email</th>
            <th>Role</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {users?.map((u: any) => (
            <tr key={u.id}>
              <td>{u.username}</td>
              <td>{u.email}</td>
              <td>{u.role}</td>
              <td><span className={`status ${u.status?.toLowerCase()}`}>{u.status}</span></td>
              <td>
                <button>Edit</button>
                <button onClick={() => deleteMutation.mutate(u.id)} className="danger">Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
