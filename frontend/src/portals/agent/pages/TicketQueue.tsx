import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { troubleTicketAPI } from '../services/api';

interface Ticket {
  id: string;
  title: string;
  status: string;
  priority: string;
  customerName: string;
  createdAt: string;
}

export default function TicketQueue() {
  const queryClient = useQueryClient();
  const [filter, setFilter] = useState('ALL');

  const { data: tickets, isLoading } = useQuery({
    queryKey: ['tickets', filter],
    queryFn: () => troubleTicketAPI.getAll()
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: string; data: any }) => troubleTicketAPI.update(id, data),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['tickets'] })
  });

  const filteredTickets = filter === 'ALL' 
    ? tickets 
    : tickets?.filter((t: Ticket) => t.status === filter);

  if (isLoading) return <div className="loading">Loading...</div>;

  return (
    <div className="tickets-page">
      <h2>Ticket Queue</h2>
      <div className="filter-bar">
        <button onClick={() => setFilter('ALL')} className={filter === 'ALL' ? 'active' : ''}>All</button>
        <button onClick={() => setFilter('OPEN')} className={filter === 'OPEN' ? 'active' : ''}>Open</button>
        <button onClick={() => setFilter('IN_PROGRESS')} className={filter === 'IN_PROGRESS' ? 'active' : ''}>In Progress</button>
        <button onClick={() => setFilter('RESOLVED')} className={filter === 'RESOLVED' ? 'active' : ''}>Resolved</button>
      </div>
      <div className="tickets-list">
        {filteredTickets?.map((ticket: Ticket) => (
          <div key={ticket.id} className="ticket-card">
            <div className="ticket-header">
              <h3>{ticket.title}</h3>
              <span className={`priority ${ticket.priority.toLowerCase()}`}>{ticket.priority}</span>
            </div>
            <div className="ticket-meta">
              <span>{ticket.customerName}</span>
              <span>{ticket.createdAt}</span>
            </div>
            <div className="ticket-actions">
              <select 
                value={ticket.status} 
                onChange={(e) => updateMutation.mutate({ id: ticket.id, data: { status: e.target.value } })}
              >
                <option value="OPEN">Open</option>
                <option value="IN_PROGRESS">In Progress</option>
                <option value="RESOLVED">Resolved</option>
                <option value="CLOSED">Closed</option>
              </select>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
