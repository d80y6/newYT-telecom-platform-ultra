import React, { useState } from 'react';

export default function Login({ onLogin }) {
  const [phone, setPhone] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    onLogin({ name: 'أحمد محمد', phone, id: '12345' });
  };

  return (
    <div className="login-page">
      <div className="login-card">
        <h2><i className="fas fa-mobile-alt"></i> يمن موبايل</h2>
        <p>تسجيل الدخول للبوابة الذكية</p>
        <form onSubmit={handleSubmit}>
          <input 
            type="tel" 
            placeholder="رقم الهاتف" 
            value={phone}
            onChange={(e) => setPhone(e.target.value)}
            required
          />
          <input 
            type="password" 
            placeholder="كلمة المرور" 
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <button type="submit" className="btn btn-primary btn-block">دخول</button>
        </form>
        <div className="login-links">
          <a href="#">نسيت كلمة المرور؟</a>
          <a href="#">تسجيل جديد</a>
        </div>
      </div>
    </div>
  );
}