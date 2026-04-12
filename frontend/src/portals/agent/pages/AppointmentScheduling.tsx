import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { appointmentAPI } from '../services/api';

export default function AppointmentScheduling() {
  const queryClient = useQueryClient();

  const { data: appointments, isLoading } = useQuery({ queryKey: ['appointments'], queryFn: () => appointmentAPI.getAppointments() });

  const updateMutation = useMutation({ mutationFn: ({ id, status }: { id: string; status: string }) => appointmentAPI.updateStatus(id, status), onSuccess: () => queryClient.invalidateQueries({ queryKey: ['appointments'] }) });

  if (isLoading) return <div className="loading">Loading...</div>;

  return (
    <div className="appointment-scheduling-page">
      <h2>Appointment Scheduling</h2>
      <button className="primary">Schedule New</button>
      <div className="calendar-view">
        <div className="appointment-list">
          {appointments?.map((apt: any) => (
            <div key={apt.id} className="appointment-card">
              <div className="apt-header">
                <span className="time">{apt.startTime}</span>
                <span className={`status ${apt.status?.toLowerCase()}`}>{apt.status}</span>
              </div>
              <div className="apt-body">
                <h3>{apt.title}</h3>
                <p>Customer: {apt.customerName}</p>
                <p>Type: {apt.appointmentType}</p>
              </div>
              <div className="apt-actions">
                <button onClick={() => updateMutation.mutate({ id: apt.id, status: 'CONFIRMED' })}>Confirm</button>
                <button onClick={() => updateMutation.mutate({ id: apt.id, status: 'COMPLETED' })}>Complete</button>
                <button onClick={() => updateMutation.mutate({ id: apt.id, status: 'CANCELLED' })}>Cancel</button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
