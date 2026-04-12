import React, { useState, useEffect } from 'react';
import { troubleTicketApi, customerApi } from '../services/api';

function Tickets() {
  const [tickets, setTickets] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [ticketsData, custData] = await Promise.allSettled([
        troubleTicketApi.getAll(),
        customerApi.getAll()
      ]);

      if (ticketsData.status === 'fulfilled' && ticketsData.value) {
        const data = ticketsData.value;
        if (Array.isArray(data)) {
          setTickets(data);
        } else if (data.troubleTicket) {
          setTickets(data.troubleTicket);
        }
      }

      if (custData.status === 'fulfilled' && custData.value) {
        const data = custData.value;
        const customers = Array.isArray(data) ? data : data.customer || [];
        setCustomers(customers);
      }
    } catch (err) {
      console.error('Fetch error:', err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const getCustomerName = (partyId) => {
    if (!partyId) return '-';
    const customer = customers.find(c => c.id === partyId);
    return customer ? `${customer.firstName || ''} ${customer.lastName || ''}`.trim() : partyId;
  };

  const defaultTickets = [
    { id: 'TKT-001', customer: 'أحمد محمد', subject: 'مشكلة في الإنترنت', date: '2026-04-01', priority: 'high', status: 'open' },
    { id: 'TKT-002', customer: 'سارة علي', subject: 'استفسار حول الفاتورة', date: '2026-04-02', priority: 'low', status: 'closed' },
    { id: 'TKT-003', customer: 'محمد حسن', subject: 'طلب ترقية الباقة', date: '2026-04-03', priority: 'medium', status: 'pending' },
  ];

  const displayData = tickets.length > 0 ? tickets : defaultTickets;

  return (
    <div className="dashboard-content">
      <div className="card">
        <div className="card-header">
          <h3>التذاكر والدعم</h3>
          <button className="btn btn-primary">إنشاء تذكرة جديدة</button>
        </div>
        <div className="card-body">
          {loading && <div className="loading">جاري التحميل...</div>}
          {error && <div className="error">خطأ: {error}</div>}
          
          <table className="data-table">
            <thead>
              <tr>
                <th>رقم التذكرة</th>
                <th>العميل</th>
                <th>الموضوع</th>
                <th>التاريخ</th>
                <th>الأولوية</th>
                <th>الحالة</th>
              </tr>
            </thead>
            <tbody>
              {displayData.map((ticket) => (
                <tr key={ticket.id}>
                  <td>{ticket.ticketNumber || ticket.id}</td>
                  <td>{ticket.partyId ? getCustomerName(ticket.partyId) : '-'}</td>
                  <td>{ticket.description || '-'}</td>
                  <td>{ticket.createdAt ? ticket.createdAt.split('T')[0] : '-'}</td>
                  <td>
                    <span className={`status-badge ${(ticket.severity || '').toLowerCase()}`}>
                      {ticket.severity === 'CRITICAL' ? 'حرجة' : 
                       ticket.severity === 'MAJOR' ? 'كبيرة' : 
                       ticket.severity === 'MINOR' ? 'صغيرة' : 'منخفضة'}
                    </span>
                  </td>
                  <td>
                    <span className={`status-badge ${ticket.status === 'IN_PROGRESS' || ticket.status === 'SUBMITTED' ? 'active' : ticket.status === 'CLOSED' ? 'inactive' : 'pending'}`}>
                      {ticket.status === 'IN_PROGRESS' ? 'قيد التنفيذ' : 
                       ticket.status === 'SUBMITTED' ? 'مقدمة' : 
                       ticket.status === 'CLOSED' ? 'مغلقة' : 'معلقة'}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

export default Tickets;