import React, { useState } from 'react';
import { orderApi } from '../services/api';

export default function Recharge({ onRecharge }) {
  const [amount, setAmount] = useState('');
  const [cardNumber, setCardNumber] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleRecharge = async (e) => {
    e.preventDefault();
    if (!amount || parseInt(amount) <= 0) {
      setError('الرجاء إدخال مبلغ صحيح');
      return;
    }

    try {
      setLoading(true);
      setError(null);

      await orderApi.create({
        orderDate: new Date().toISOString(),
        orderItem: [{
          action: 'add',
          product: {
            id: 'RECHARGE',
            name: 'شحن رصيد',
            description: `شحن رصيد بقيمة ${amount} ريال`
          }
        }],
        state: 'initial',
        buyer: {
          id: localStorage.getItem('customerId') || 'default'
        }
      });

      if (onRecharge) {
        onRecharge(parseInt(amount));
      }
      
      setAmount('');
      setCardNumber('');
      alert('تم تقديم طلب الشحن بنجاح!');
    } catch (err) {
      console.error('Recharge error:', err);
      setError('حدث خطأ في عملية الشحن');
    } finally {
      setLoading(false);
    }
  };

  const quickAmounts = [500, 1000, 2000, 5000];

  return (
    <div className="recharge-page">
      <h3><i className="fas fa-credit-card"></i> شحن الرصيد</h3>
      
      {error && <div className="alert alert-danger">{error}</div>}
      
      <div className="quick-charges">
        {quickAmounts.map((amt) => (
          <button 
            key={amt} 
            className="btn btn-outline"
            onClick={() => setAmount(amt.toString())}
          >
            {amt} ر.ي
          </button>
        ))}
      </div>

      <form onSubmit={handleRecharge} className="recharge-form">
        <label>المبلغ</label>
        <input 
          type="number" 
          value={amount}
          onChange={(e) => setAmount(e.target.value)}
          placeholder="أدخل المبلغ"
          min="100"
          max="50000"
        />
        
        <label>رقم البطاقة</label>
        <input 
          type="text" 
          value={cardNumber}
          onChange={(e) => setCardNumber(e.target.value)}
          placeholder="XXXX XXXX XXXX XXXX"
          maxLength="19"
        />
        
        <button type="submit" className="btn btn-primary btn-block" disabled={loading}>
          {loading ? 'جاري المعالجة...' : 'شحن الآن'}
        </button>
      </form>

      <div className="recharge-methods">
        <h5>طرق الشحن المتاحة</h5>
        <div className="methods-list">
          <div className="method"><i className="fas fa-credit-card"></i> بطاقات Visa/Master</div>
          <div className="method"><i className="fas fa-university"></i> تحويل بنكي</div>
          <div className="method"><i className="fas fa-wallet"></i> محفظة إلكترونية</div>
        </div>
      </div>
    </div>
  );
}