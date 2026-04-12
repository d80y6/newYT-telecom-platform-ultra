import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { networkAPI } from '../services/api';

export default function Alarms() {
  const queryClient = useQueryClient();

  const { data: alarms, isLoading } = useQuery({ queryKey: ['alarms'], queryFn: () => networkAPI.getAlarms() });

  const ackMutation = useMutation({ mutationFn: (id: string) => networkAPI.acknowledgeAlarm(id), onSuccess: () => queryClient.invalidateQueries({ queryKey: ['alarms'] }) });
  const clearMutation = useMutation({ mutationFn: (id: string) => networkAPI.clearAlarm(id), onSuccess: () => queryClient.invalidateQueries({ queryKey: ['alarms'] }) });

  if (isLoading) return <div className="loading">Loading...</div>;

  return (
    <div className="alarms-page">
      <h2>Active Alarms</h2>
      <div className="alarm-filters">
        <button className="active">All</button>
        <button>Critical</button>
        <button>Major</button>
        <button>Minor</button>
      </div>
      <div className="alarms-list">
        {alarms?.map((alarm: any) => (
          <div key={alarm.id} className={`alarm-card ${alarm.severity.toLowerCase()}`}>
            <div className="alarm-header">
              <span className={`severity ${alarm.severity.toLowerCase()}`}>{alarm.severity}</span>
              <span className="id">{alarm.id}</span>
            </div>
            <div className="alarm-body">
              <h3>{alarm.title}</h3>
              <p>{alarm.location} - {alarm.timestamp}</p>
            </div>
            <div className="alarm-actions">
              <button onClick={() => ackMutation.mutate(alarm.id)}>Acknowledge</button>
              <button onClick={() => clearMutation.mutate(alarm.id)}>Clear</button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
