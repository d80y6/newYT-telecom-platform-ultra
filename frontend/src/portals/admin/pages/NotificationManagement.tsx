import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import axios from 'axios';

const client = axios.create({ baseURL: import.meta.env.VITE_API_URL || '/tmf-api' });

const notificationAPI = {
  getAll: () => client.get('/notificationManagement/v5/notification').then(r => r.data),
  getById: (id: string) => client.get(`/notificationManagement/v5/notification/${id}`).then(r => r.data),
  create: (data: any) => client.post('/notificationManagement/v5/notification', data).then(r => r.data),
  send: (id: string) => client.post(`/notificationManagement/v5/notification/${id}/send`).then(r => r.data),
  markRead: (id: string) => client.patch(`/notificationManagement/v5/notification/${id}`, { read: true }).then(r => r.data)
};

export default function NotificationManagement() {
  const queryClient = useQueryClient();
  const { data: notifications, isLoading } = useQuery({ queryKey: ['notifications'], queryFn: notificationAPI.getAll });
  const sendMutation = useMutation({ mutationFn: (id: string) => notificationAPI.send(id), onSuccess: () => queryClient.invalidateQueries({ queryKey: ['notifications'] }) });

  if (isLoading) return <div className="loading">Loading...</div>;

  return (
    <div className="notification-page">
      <h2>Notification Management</h2>
      <button className="primary" onClick={() => notificationAPI.create({ title: 'New Notification', message: 'Message' })}>Create Notification</button>
      <div className="notification-list">
        {notifications?.map((notif: any) => (
          <div key={notif.id} className={`notification-card ${notif.read ? 'read' : 'unread'}`}>
            <div className="notif-header">
              <span className="title">{notif.title}</span>
              <span className="time">{notif.createdAt}</span>
            </div>
            <div className="notif-body">
              <p>{notif.message}</p>
            </div>
            <div className="notif-actions">
              <button onClick={() => queryClient.invalidateQueries({ queryKey: ['notifications'] })}>Mark Read</button>
              <button onClick={() => sendMutation.mutate(notif.id)}>Send</button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
