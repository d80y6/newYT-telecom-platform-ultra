import React from 'react';
import { useLocation } from 'react-router-dom';

const pageNames = {
  '/': 'لوحة التحكم',
  '/customers': 'إدارة العملاء',
  '/orders': 'إدارة الطلبات',
  '/products': 'إدارة المنتجات',
  '/billing': 'الفواتير والمدفوعات',
  '/subscriptions': 'الاشتراكات',
  '/network': 'إدارة الشبكة',
  '/tickets': 'التذاكر والدعم',
  '/reports': 'التقارير والإحصائيات',
  '/settings': 'الإعدادات'
};

function TopBar() {
  const location = useLocation();
  const pageName = pageNames[location.pathname] || 'لوحة التحكم';

  return (
    <div className="top-bar">
      <h2>{pageName}</h2>
      <div className="top-bar-actions">
        <button className="top-bar-btn secondary">
          <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor" style={{ marginLeft: '6px' }}>
            <path d="M12 22c1.1 0 2-.9 2-2h-4c0 1.1.89 2 2 2zm6-6v-5c0-3.07-1.64-5.64-4.5-6.32V4c0-.83-.67-1.5-1.5-1.5s-1.5.67-1.5 1.5v.68C7.63 5.36 6 7.92 6 11v5l-2 2v1h16v-1l-2-2z"/>
          </svg>
          الإشعارات
        </button>
        <button className="top-bar-btn primary">
          <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor" style={{ marginLeft: '6px' }}>
            <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
          </svg>
          الملف الشخصي
        </button>
      </div>
    </div>
  );
}

export default TopBar;