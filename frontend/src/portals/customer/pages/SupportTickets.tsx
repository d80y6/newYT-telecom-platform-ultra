import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { troubleTicketAPI } from '../services/api';

interface Ticket {
  id: string;
  title: string;
  status: string;
  priority: string;
  createdAt: string;
}

export default function SupportTickets() {
  const queryClient = useQueryClient();
  const [showForm, setShowForm] = useState(false);
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');

  const { data: tickets, isLoading } = useQuery({
    queryKey: ['myTickets'],
    queryFn: () => troubleTicketAPI.getAll()
  });

  const createMutation = useMutation({
    mutationFn: (data: any) => troubleTicketAPI.create(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['myTickets'] });
      setShowForm(false);
      setTitle('');
      setDescription('');
    }
  });

  if (isLoading) return <div className="loading">Loading...</div>;

  return (
    <div className="tickets-page">
      <div className="page-header">
        <h2>Support Tickets</h2>
        <button onClick={() => setShowForm(!showForm)}>
          {showForm ? 'Cancel' : '+ New Ticket'}
        </button>
      </div>
      
      {showForm && (
        <form onSubmit={(e) => { e.preventDefault(); createMutation.mutate({ title, description }); }} className="ticket-form">
          <div className="form-group">
            <label>Subject</label>
            <input value={title} onChange={(e) => setTitle(e.target.value)} required />
          </div>
          <div className="form-group">
            <label>Description</label>
            <textarea value={description} onChange={(e) => setDescription(e.target.value)} rows={4} required />
          </div>
          <button type="submit" disabled={createMutation.isPending}>
            {createMutation.isPending ? 'Creating...' : 'Submit Ticket'}
          </button>
        </form>
      )}

      <div className="tickets-list">
        {tickets?.map((ticket: Ticket) => (
          <div key={ticket.id} className="ticket-card">
            <div className="ticket-header">
              <h3>{ticket.title}</h3>
              <span className={`priority ${ticket.priority.toLowerCase()}`}>{ticket.priority}</span>
            </div>
            <div className="ticket-meta">
              <span className={`status ${ticket.status.toLowerCase()}`}>{ticket.status}</span>
              <span>Created: {ticket.createdAt}</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
