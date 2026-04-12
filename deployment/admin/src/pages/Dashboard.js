import React from 'react';

function Dashboard() {
  const stats = [
    { label: 'إجمالي العملاء', value: '12,458', change: '+12%', up: true },
    { label: 'الطلبات النشطة', value: '847', change: '+5%', up: true },
    { label: 'الإيرادات الشهرية', value: '2.4M', change: '+8%', up: true },
    { label: 'التذاكر المفتوحة', value: '124', change: '-15%', up: false },
    { label: 'الاشتراكات النشطة', value: '9,234', change: '+3%', up: true },
    { label: 'معدل الاحتفاظ', value: '94.2%', change: '+1.5%', up: true }
  ];

  const recentOrders = [
    { id: 'ORD-001', customer: 'أحمد محمد', product: 'باقة الأساسية', amount: '150', status: 'pending' },
    { id: 'ORD-002', customer: 'سارة علي', product: 'باقة Premium', amount: '300', status: 'active' },
    { id: 'ORD-003', customer: 'محمد حسن', product: 'باقة العائلة', amount: '450', status: 'active' },
    { id: 'ORD-004', customer: 'فاطمة اليمن', product: 'باقة الأعمال', amount: '600', status: 'pending' },
    { id: 'ORD-005', customer: 'علي سعيد', product: 'باقة الأساسية', amount: '150', status: 'inactive' }
  ];

  return (
    <div className="dashboard-content">
      <div className="stats-grid">
        {stats.map((stat, index) => (
          <div key={index} className={`stat-card ${stat.up ? 'success' : 'warning'}`}>
            <h3>{stat.label}</h3>
            <div className="value">{stat.value}</div>
            <div className={`change ${stat.up ? 'up' : 'down'}`}>
              {stat.up ? '↑' : '↓'} {stat.change} من الشهر الماضي
            </div>
          </div>
        ))}
      </div>

      <div className="card">
        <div className="card-header">
          <h3>أحدث الطلبات</h3>
          <button className="btn btn-primary">عرض الكل</button>
        </div>
        <div className="card-body">
          <table className="data-table">
            <thead>
              <tr>
                <th>رقم الطلب</th>
                <th>العميل</th>
                <th>المنتج</th>
                <th>المبلغ</th>
                <th>الحالة</th>
              </tr>
            </thead>
            <tbody>
              {recentOrders.map((order) => (
                <tr key={order.id}>
                  <td>{order.id}</td>
                  <td>{order.customer}</td>
                  <td>{order.product}</td>
                  <td>{order.amount} ريال</td>
                  <td>
                    <span className={`status-badge ${order.status}`}>
                      {order.status === 'pending' ? 'معلق' : 
                       order.status === 'active' ? 'نشط' : 'غير نشط'}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' }}>
        <div className="card">
          <div className="card-header">
            <h3>توزيع العملاء</h3>
          </div>
          <div className="card-body" style={{ textAlign: 'center', padding: '40px' }}>
            <p style={{ color: 'var(--text-secondary)' }}>خريطة التوزيع الجغرافي</p>
          </div>
        </div>

        <div className="card">
          <div className="card-header">
            <h3>أداء الشبكة</h3>
          </div>
          <div className="card-body" style={{ textAlign: 'center', padding: '40px' }}>
            <p style={{ color: 'var(--text-secondary)' }}>مخططات الشبكة</p>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Dashboard;