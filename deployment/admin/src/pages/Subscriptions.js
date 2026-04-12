import React, { useState, useEffect } from 'react';
import { subscriptionApi, customerApi } from '../services/api';

function Subscriptions() {
  const [subscriptions, setSubscriptions] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [subsData, custData] = await Promise.allSettled([
        subscriptionApi.getAll(),
        customerApi.getAll()
      ]);

      if (subsData.status === 'fulfilled' && subsData.value) {
        const data = subsData.value;
        if (Array.isArray(data)) {
          setSubscriptions(data);
        } else if (data.subscription) {
          setSubscriptions(data.subscription);
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

  const getCustomerName = (customerId) => {
    const customer = customers.find(c => c.id === customerId);
    return customer ? `${customer.firstName || ''} ${customer.lastName || ''}`.trim() : customerId;
  };

  const defaultSubscriptions = [
    { id: 'SUB-001', customer: 'أحمد محمد', product: 'باقة الأساسية', startDate: '2026-01-15', endDate: '2026-04-15', status: 'active' },
    { id: 'SUB-002', customer: 'سارة علي', product: 'باقة Premium', startDate: '2026-02-01', endDate: '2026-05-01', status: 'active' },
    { id: 'SUB-003', customer: 'محمد حسن', product: 'باقة العائلة', startDate: '2026-03-01', endDate: '2026-06-01', status: 'pending' },
    { id: 'SUB-004', customer: 'فاطمة اليمن', product: 'باقة الأعمال', startDate: '2025-12-01', endDate: '2026-03-01', status: 'expired' },
    { id: 'SUB-005', customer: 'علي سعيد', product: 'باقة الأساسية', startDate: '2026-04-01', endDate: '2026-07-01', status: 'active' }
  ];

  const displayData = subscriptions.length > 0 ? subscriptions : defaultSubscriptions;

  return (
    <div className="dashboard-content">
      <div className="card">
        <div className="card-header">
          <h3>الاشتراكات</h3>
        </div>
        <div className="card-body">
          {loading && <div className="loading">جاري التحميل...</div>}
          {error && <div className="error">خطأ: {error}</div>}
          
          <table className="data-table">
            <thead>
              <tr>
                <th>رقم الاشتراك</th>
                <th>العميل</th>
                <th>الخطة</th>
                <th>تاريخ البدء</th>
                <th>تاريخ الانتهاء</th>
                <th>الحالة</th>
              </tr>
            </thead>
            <tbody>
              {displayData.map((sub) => (
                <tr key={sub.id}>
                  <td>{sub.subscriptionNumber || sub.id}</td>
                  <td>{sub.customerId ? getCustomerName(sub.customerId) : '-'}</td>
                  <td>{sub.planName || '-'}</td>
                  <td>{sub.startDate ? sub.startDate.split('T')[0] : '-'}</td>
                  <td>{sub.endDate ? sub.endDate.split('T')[0] : '-'}</td>
                  <td>
                    <span className={`status-badge ${(sub.status || '').toLowerCase()}`}>
                      {sub.status === 'ACTIVE' ? 'نشط' : 
                       sub.status === 'SUSPENDED' ? 'موقف' : 
                       sub.status === 'TERMINATED' ? 'منتهي' : 'معلق'}
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

export default Subscriptions;