import React from 'react';

function Settings() {
  return (
    <div className="dashboard-content">
      <div className="card">
        <div className="card-header">
          <h3>الإعدادات العامة</h3>
        </div>
        <div className="card-body">
          <div className="form-group">
            <label>اسم المؤسسة</label>
            <input type="text" defaultValue=" Yemen Public Telecommunications Corporation" />
          </div>
          <div className="form-group">
            <label>البريد الإلكتروني</label>
            <input type="email" defaultValue="admin@yemenptc.ye" />
          </div>
          <div className="form-group">
            <label>رقم الهاتف</label>
            <input type="tel" defaultValue="+967-1-500000" />
          </div>
          <div className="form-group">
            <label>المنطقة الزمنية</label>
            <select defaultValue="Asia/Aden">
              <option value="Asia/Aden">آسيا/عدن (UTC+3)</option>
            </select>
          </div>
          <div className="form-group">
            <label>اللغة الافتراضية</label>
            <select defaultValue="ar">
              <option value="ar">العربية</option>
              <option value="en">English</option>
            </select>
          </div>
          <button className="btn btn-primary">حفظ الإعدادات</button>
        </div>
      </div>

      <div className="card">
        <div className="card-header">
          <h3>إعدادات الأمان</h3>
        </div>
        <div className="card-body">
          <div className="form-group">
            <label>مهلة تسجيل الدخول (دقائق)</label>
            <input type="number" defaultValue="30" />
          </div>
          <div className="form-group">
            <label>الحد الأقصى لمحاولات الدخول</label>
            <input type="number" defaultValue="5" />
          </div>
          <div className="form-group">
            <label>
              <input type="checkbox" defaultChecked /> تفعيل المصادقة الثنائية
            </label>
          </div>
          <button className="btn btn-primary">حفظ</button>
        </div>
      </div>
    </div>
  );
}

export default Settings;