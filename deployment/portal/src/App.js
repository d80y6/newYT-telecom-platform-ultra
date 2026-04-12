import React, { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import Dashboard from './pages/Dashboard';
import Services from './pages/Services';
import Billing from './pages/Billing';
import Recharge from './pages/Recharge';
import Profile from './pages/Profile';
import Support from './pages/Support';
import Login from './pages/Login';

const API_BASE = process.env.REACT_APP_API_URL || 'http://localhost:8080/api/v1';

function App() {
  const [user, setUser] = useState(null);
  const [balance, setBalance] = useState(1500);
  const [dataUsage, setDataUsage] = useState(5.2);
  const [dataLimit, setDataLimit] = useState(10);
  const [voiceMinutes, setVoiceMinutes] = useState(120);
  const [voiceLimit, setVoiceLimit] = useState(300);

  useEffect(() => {
    const savedUser = localStorage.getItem('customerUser');
    if (savedUser) setUser(JSON.parse(savedUser));
  }, []);

  const handleLogin = (userData) => {
    setUser(userData);
    localStorage.setItem('customerUser', JSON.stringify(userData));
  };

  const handleLogout = () => {
    setUser(null);
    localStorage.removeItem('customerUser');
  };

  if (!user) {
    return <Login onLogin={handleLogin} />;
  }

  return (
    <BrowserRouter>
      <div className="app">
        <nav className="navbar">
          <div className="nav-brand">
            <i className="fas fa-mobile-alt"></i> يمن موبايل
          </div>
          <div className="nav-links">
            <Link to="/">الرئيسية</Link>
            <Link to="/services">خدماتي</Link>
            <Link to="/billing">الفواتير</Link>
            <Link to="/support">الدعم</Link>
          </div>
          <div className="nav-user">
            <span>{user.name}</span>
            <button onClick={handleLogout} className="btn-logout">خروج</button>
          </div>
        </nav>

        <main className="main-content">
          <Routes>
            <Route path="/" element={<Dashboard 
              balance={balance} 
              dataUsage={dataUsage} 
              dataLimit={dataLimit}
              voiceMinutes={voiceMinutes}
              voiceLimit={voiceLimit}
            />} />
            <Route path="/services" element={<Services />} />
            <Route path="/billing" element={<Billing />} />
            <Route path="/recharge" element={<Recharge onRecharge={(amount) => setBalance(balance + amount)} />} />
            <Route path="/profile" element={<Profile user={user} />} />
            <Route path="/support" element={<Support />} />
          </Routes>
        </main>

        <footer className="footer">
          <p>هيئة الاتصالات - اليمن &copy; 2026</p>
          <p>للدعم: 123</p>
        </footer>
      </div>
    </BrowserRouter>
  );
}

export default App;