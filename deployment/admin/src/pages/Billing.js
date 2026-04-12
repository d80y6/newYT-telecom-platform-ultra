import React, { useState, useEffect } from 'react';
import { billingApi, accountApi, customerApi } from '../services/api';

function Billing() {
  const [invoices, setInvoices] = useState([]);
  const [accounts, setAccounts] = useState([]);
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [invoicesData, accountsData, custData] = await Promise.allSettled([
        billingApi.getBills(),
        accountApi.getAll(),
        customerApi.getAll()
      ]);

      if (invoicesData.status === 'fulfilled' && invoicesData.value) {
        const data = invoicesData.value;
        if (Array.isArray(data)) {
          setInvoices(data);
        } else if (data.invoice) {
          setInvoices(data.invoice);
        }
      }

      if (accountsData.status === 'fulfilled' && accountsData.value) {
        const data = accountsData.value;
        if (Array.isArray(data)) {
          setAccounts(data);
        } else if (data.account) {
          setAccounts(data.account);
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

  const getAccountCustomer = (accountId) => {
    const account = accounts.find(a => a.id === accountId);
    if (!account) return '-';
    const customer = customers.find(c => c.id === account.customerId);
    return customer ? `${customer.firstName || ''} ${customer.lastName || ''}`.trim() : '-';
  };

  const defaultInvoices = [
    { id: 'INV-001', customer: 'أحمد محمد', amount: '150', date: '2026-04-01', status: 'paid' },
    { id: 'INV-002', customer: 'سارة علي', amount: '300', date: '2026-04-01', status: 'paid' },
    { id: 'INV-003', customer: 'محمد حسن', amount: '450', date: '2026-04-02', status: 'pending' },
  ];

  const displayInvoices = invoices.length > 0 ? invoices : defaultInvoices;

  const getTotalAmount = () => {
    return displayInvoices.reduce((sum, inv) => sum + (inv.totalAmount || 0), 0);
  };

  const getPaidCount = () => {
    return displayInvoices.filter(inv => inv.status === 'PAID').length;
  };

  const getPendingCount = () => {
    return displayInvoices.filter(inv => inv.status === 'FINALIZED' || inv.status === 'OVERDUE').length;
  };

  const stats = [
    { label: 'إجمالي الإيرادات', value: `${getTotalAmount().toLocaleString()}`, change: '+8%', up: true },
    { label: 'الفواتير المدفوعة', value: getPaidCount(), change: '+12%', up: true },
    { label: 'الفواتير المستحقة', value: getPendingCount(), change: '-5%', up: false },
    { label: 'عدد الفواتير', value: displayInvoices.length, change: '+3%', up: true }
  ];

  return (
    <div className="dashboard-content">
      <div className="stats-grid">
        {stats.map((stat, index) => (
          <div key={index} className={`stat-card ${stat.up ? 'success' : 'warning'}`}>
            <h3>{stat.label}</h3>
            <div className="value">{stat.value}</div>
            <div className={`change ${stat.up ? 'up' : 'down'}`}>
              {stat.up ? '↑' : '↓'} {stat.change}
            </div>
          </div>
        ))}
      </div>

      <div className="card">
        <div className="card-header">
          <h3>الفواتير</h3>
          <button className="btn btn-primary">إنشاء فاتورة</button>
        </div>
        <div className="card-body">
          {loading && <div className="loading">جاري التحميل...</div>}
          {error && <div className="error">خطأ: {error}</div>}
          
          <table className="data-table">
            <thead>
              <tr>
                <th>رقم الفاتورة</th>
                <th>العميل</th>
                <th>المبلغ</th>
                <th>التاريخ</th>
                <th>الحالة</th>
                <th>إجراءات</th>
              </tr>
            </thead>
            <tbody>
              {displayInvoices.map((invoice) => (
                <tr key={invoice.id}>
                  <td>{invoice.invoiceNumber || invoice.id}</td>
                  <td>{invoice.accountId ? getAccountCustomer(invoice.accountId) : '-'}</td>
                  <td>{invoice.totalAmount || '0'} {invoice.currency || 'ر.ي'}</td>
                  <td>{invoice.issueDate || '-'}</td>
                  <td>
                    <span className={`status-badge ${(invoice.status || '').toLowerCase()}`}>
                      {invoice.status === 'PAID' ? 'مدفوعة' : 
                       invoice.status === 'FINALIZED' ? 'مستحقة' : 
                       invoice.status === 'OVERDUE' ? 'متأخرة' : invoice.status}
                    </span>
                  </td>
                  <td>
                    <button className="btn btn-secondary btn-sm">تفاصيل</button>
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

export default Billing;