import React from 'react';

function Network() {
  const elements = [
    { id: 'NE-001', name: 'محطة صنعاء الرئيسية', type: 'MSC', location: 'صنعاء', status: 'active', capacity: '100%' },
    { id: 'NE-002', name: 'محطة تعز', type: 'BSC', location: 'تعز', status: 'active', capacity: '87%' },
    { id: 'NE-003', name: 'محطة حضرموت', type: 'MSC', location: 'حضرموت', status: 'warning', capacity: '92%' },
    { id: 'NE-004', name: 'محطة عدنية', type: 'RNC', location: 'عدن', status: 'active', capacity: '78%' },
    { id: 'NE-005', name: 'محطة إب', type: 'BSC', location: 'إب', status: 'inactive', capacity: '0%' }
  ];

  const stats = [
    { label: 'المحطات النشطة', value: '124', up: true },
    { label: 'المحطات المحملة', value: '87%', up: true },
    { label: 'التحذيرات', value: '3', up: false },
    { label: 'إجمالي السعة', value: '2.4M', up: true }
  ];

  return (
    <div className="dashboard-content">
      <div className="stats-grid">
        {stats.map((stat, index) => (
          <div key={index} className={`stat-card ${stat.up ? 'success' : 'warning'}`}>
            <h3>{stat.label}</h3>
            <div className="value">{stat.value}</div>
          </div>
        ))}
      </div>

      <div className="card">
        <div className="card-header">
          <h3>عناصر الشبكة</h3>
          <button className="btn btn-primary">إضافة عنصر</button>
        </div>
        <div className="card-body">
          <table className="data-table">
            <thead>
              <tr>
                <th>رقم العنصر</th>
                <th>الاسم</th>
                <th>النوع</th>
                <th>الموقع</th>
                <th>السعة</th>
                <th>الحالة</th>
              </tr>
            </thead>
            <tbody>
              {elements.map((el) => (
                <tr key={el.id}>
                  <td>{el.id}</td>
                  <td>{el.name}</td>
                  <td>{el.type}</td>
                  <td>{el.location}</td>
                  <td>{el.capacity}</td>
                  <td>
                    <span className={`status-badge ${el.status}`}>
                      {el.status === 'active' ? 'نشط' : 
                       el.status === 'warning' ? 'تحذير' : 'معطل'}
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

export default Network;