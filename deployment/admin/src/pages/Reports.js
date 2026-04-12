import React from 'react';

function Reports() {
  const reports = [
    { id: 'RPT-001', name: 'تقرير الإيرادات الشهرية', type: 'financial', date: '2026-04-01', format: 'PDF' },
    { id: 'RPT-002', name: 'تقرير أداء الشبكة', type: 'network', date: '2026-04-01', format: 'PDF' },
    { id: 'RPT-003', name: 'تقرير العملاء الجدد', type: 'customers', date: '2026-03-31', format: 'Excel' },
    { id: 'RPT-004', name: 'تقرير استخدام الخدمة', type: 'usage', date: '2026-03-30', format: 'PDF' },
    { id: 'RPT-005', name: 'تقرير الشكاوى والتذاكر', type: 'support', date: '2026-03-29', format: 'Excel' }
  ];

  const stats = [
    { label: 'التقارير المتاحة', value: '24' },
    { label: 'التقارير هذا الشهر', value: '8' },
    { label: 'تنزيلات اليوم', value: '156' }
  ];

  return (
    <div className="dashboard-content">
      <div className="stats-grid">
        {stats.map((stat, index) => (
          <div key={index} className="stat-card">
            <h3>{stat.label}</h3>
            <div className="value">{stat.value}</div>
          </div>
        ))}
      </div>

      <div className="card">
        <div className="card-header">
          <h3>التقارير</h3>
          <button className="btn btn-primary">إنشاء تقرير</button>
        </div>
        <div className="card-body">
          <table className="data-table">
            <thead>
              <tr>
                <th>رقم التقرير</th>
                <th>اسم التقرير</th>
                <th>النوع</th>
                <th>التاريخ</th>
                <th>الصيغة</th>
                <th>إجراءات</th>
              </tr>
            </thead>
            <tbody>
              {reports.map((report) => (
                <tr key={report.id}>
                  <td>{report.id}</td>
                  <td>{report.name}</td>
                  <td>{report.type === 'financial' ? 'مالي' : 
                       report.type === 'network' ? 'شبكة' :
                       report.type === 'customers' ? 'عملاء' :
                       report.type === 'usage' ? 'استخدام' : 'دعم'}</td>
                  <td>{report.date}</td>
                  <td>{report.format}</td>
                  <td>
                    <button className="btn btn-secondary" style={{ padding: '6px 12px' }}>تنزيل</button>
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

export default Reports;