import React, { useState } from 'react';

export default function Support() {
  const [ticket, setTicket] = useState({ subject: '', message: '' });
  const [tickets] = useState([
    { id: 'TKT-001', subject: 'مشكلة في الإنترنت', status: 'OPEN', date: '2026-04-01' },
    { id: 'TKT-002', subject: 'استفسار عن الباقات', status: 'CLOSED', date: '2026-03-15' },
  ]);

  const handleSubmit = (e) => {
    e.preventDefault();
    alert('تم إرسال التذكرة بنجاح!');
    setTicket({ subject: '', message: '' });
  };

  return (
    <div className="support-page">
      <h3><i className="fas fa-headset"></i> الدعم الفني</h3>
      
      <div className="contact-options">
        <div className="contact-card">
          <i className="fas fa-phone"></i>
          <h5>اتصل بنا</h5>
          <p>123</p>
        </div>
        <div className="contact-card">
          <i className="fas fa-whatsapp"></i>
          <h5>واتساب</h5>
          <p>+967 777 123 456</p>
        </div>
        <div className="contact-card">
          <i className="fas fa-envelope"></i>
          <h5>البريد</h5>
          <p>support@yemenptc.ye</p>
        </div>
      </div>

      <div className="new-ticket">
        <h4>إنشاء تذكرة جديدة</h4>
        <form onSubmit={handleSubmit}>
          <input 
            type="text" 
            placeholder="الموضوع"
            value={ticket.subject}
            onChange={(e) => setTicket({...ticket, subject: e.target.value})}
          />
          <textarea 
            placeholder="الرسالة..."
            value={ticket.message}
            onChange={(e) => setTicket({...ticket, message: e.target.value})}
          />
          <button type="submit" className="btn btn-primary">إرسال</button>
        </form>
      </div>

      <div className="tickets-list">
        <h4>تذاكري السابقة</h4>
        {tickets.map((t, idx) => (
          <div key={idx} className="ticket-item">
            <div>
              <h5>{t.subject}</h5>
              <p>{t.date}</p>
            </div>
            <span className={`badge badge-${t.status.toLowerCase()}`}>{t.status}</span>
          </div>
        ))}
      </div>
    </div>
  );
}