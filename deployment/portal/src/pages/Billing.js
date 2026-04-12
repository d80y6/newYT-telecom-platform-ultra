import React, { useState, useEffect } from 'react';
import { billingApi, accountApi } from '../services/api';

export default function Billing() {
  const [bills, setBills] = useState([]);
  const [accounts, setAccounts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function fetchBillingData() {
      try {
        setLoading(true);
        
        const [billsData, accountsData] = await Promise.allSettled([
          billingApi.getBills(),
          billingApi.getAccounts()
        ]);

        if (billsData.status === 'fulfilled' && billsData.value) {
          const data = billsData.value;
          if (Array.isArray(data)) {
            setBills(data);
          } else if (data.bill) {
            setBills(data.bill);
          }
        }

        if (accountsData.status === 'fulfilled' && accountsData.value) {
          const data = accountsData.value;
          if (Array.isArray(data)) {
            setAccounts(data);
          } else if (data.billingAccount) {
            setAccounts(data.billingAccount);
          }
        }

        setLoading(false);
      } catch (err) {
        console.error('Billing fetch error:', err);
        setError(err.message);
        setLoading(false);
      }
    }

    fetchBillingData();
  }, []);

  const defaultInvoices = [
    { id: 'INV-001', date: '2026-03-01', amount: 1500, status: 'PAID' },
    { id: 'INV-002', date: '2026-02-01', amount: 1200, status: 'PAID' },
    { id: 'INV-003', date: '2026-01-01', amount: 1800, status: 'PAID' },
  ];

  const displayInvoices = bills.length > 0 ? bills : defaultInvoices;

  return (
    <div className="billing-page">
      <h3><i className="fas fa-file-invoice"></i> الفواتير</h3>
      
      {loading && <div className="loading">جاري التحميل...</div>}
      {error && <div className="error">خطأ: {error}</div>}

      <div className="invoices-list">
        {displayInvoices.map((inv, idx) => (
          <div key={idx} className="invoice-card">
            <div className="invoice-info">
              <h5>{inv.id || inv.billId || `INV-${idx + 1}`}</h5>
              <p>{inv.date || inv.billDate || inv.createdDate || '2026-03-01'}</p>
            </div>
            <div className="invoice-amount">
              <h4>{inv.amount || inv.totalAmount || inv.amountDue || '0'} ر.ي</h4>
              <span className={`badge badge-${(inv.status || 'PAID').toLowerCase()}`}>
                {inv.status || 'PAID'}
              </span>
            </div>
            <button className="btn btn-sm">عرض</button>
          </div>
        ))}
      </div>

      <h3 style={{marginTop: '30px'}}><i className="fas fa-history"></i> سجل المدفوعات</h3>
      <table className="table">
        <thead>
          <tr>
            <th>التاريخ</th>
            <th>المبلغ</th>
            <th>طريقة الدفع</th>
            <th>الحالة</th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>2026-03-15</td>
            <td>500 ر.ي</td>
            <td>بطاقة</td>
            <td><span className="badge badge-success">ناجح</span></td>
          </tr>
          <tr>
            <td>2026-03-10</td>
            <td>1000 ر.ي</td>
            <td>محفظة</td>
            <td><span className="badge badge-success">ناجح</span></td>
          </tr>
        </tbody>
      </table>
    </div>
  );
}