import React from 'react';

export default function Profile({ user }) {
  return (
    <div className="profile-page">
      <h3><i className="fas fa-user"></i> الملف الشخصي</h3>
      <div className="profile-card">
        <div className="avatar"><i className="fas fa-user-circle"></i></div>
        <h4>{user?.name || 'المستخدم'}</h4>
        <p>{user?.phone}</p>
      </div>
      
      <div className="profile-form">
        <div className="form-group">
          <label>الاسم</label>
          <input type="text" defaultValue={user?.name} />
        </div>
        <div className="form-group">
          <label>البريد الإلكتروني</label>
          <input type="email" placeholder="email@example.com" />
        </div>
        <div className="form-group">
          <label>اللغة المفضلة</label>
          <select><option>العربية</option><option>English</option></select>
        </div>
        <button className="btn btn-primary">حفظ التغييرات</button>
      </div>

      <div className="notifications-settings">
        <h5>إعدادات الإشعارات</h5>
        <label><input type="checkbox" /> إشعارات الرصيد</label>
        <label><input type="checkbox" /> إشعارات العروض</label>
        <label><input type="checkbox" /> رسائل SMS</label>
      </div>
    </div>
  );
}