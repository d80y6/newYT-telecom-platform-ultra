import React, { useState, useEffect } from 'react';
import { productApi, orderApi, subscriptionApi, billingApi } from '../services/api';

export default function Dashboard({ balance: initialBalance, dataUsage: initialData, dataLimit: initialLimit, voiceMinutes: initialVoice, voiceLimit: initialVoiceLimit }) {
  const [balance, setBalance] = useState(initialBalance);
  const [dataUsage, setDataUsage] = useState(initialData);
  const [dataLimit] = useState(initialLimit);
  const [voiceMinutes, setVoiceMinutes] = useState(initialVoice);
  const [voiceLimit] = useState(initialVoiceLimit);
  const [offerings, setOfferings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function fetchData() {
      try {
        setLoading(true);
        
        const [productsData, ordersData, billsData] = await Promise.allSettled([
          productApi.getOfferings(),
          orderApi.getOrders(),
          billingApi.getBills()
        ]);

        if (productsData.status === 'fulfilled' && productsData.value) {
          const data = productsData.value;
          if (Array.isArray(data)) {
            setOfferings(data.slice(0, 6));
          } else if (data.productOffering) {
            setOfferings(data.productOffering.slice(0, 6));
          }
        }

        if (ordersData.status === 'fulfilled' && ordersData.value) {
          console.log('Orders:', ordersData.value);
        }

        if (billsData.status === 'fulfilled' && billsData.value) {
          console.log('Bills:', billsData.value);
        }

        setLoading(false);
      } catch (err) {
        console.error('Dashboard fetch error:', err);
        setError(err.message);
        setLoading(false);
      }
    }

    fetchData();
  }, []);

  const dataPercent = Math.round((dataUsage / dataLimit) * 100);
  const voicePercent = Math.round((voiceMinutes / voiceLimit) * 100);

  return (
    <div className="dashboard">
      {loading && <div className="loading">جاري التحميل...</div>}
      {error && <div className="error">خطأ: {error}</div>}
      
      <div className="balance-cards">
        <div className="card balance-card">
          <h6>الرصيد المتاح</h6>
          <h2 style={{color: 'white'}}>{balance.toLocaleString()} ر.ي</h2>
          <button className="btn btn-light btn-sm">
            <i className="fas fa-plus"></i> شحن
          </button>
        </div>
        
        <div className="card">
          <h6>رصيد الإنترنت</h6>
          <h2>{dataUsage.toFixed(1)} GB</h2>
          <div className="progress">
            <div className="progress-bar" style={{width: `${dataPercent}%`}}></div>
          </div>
          <small>{dataPercent}% مستخدم من {dataLimit} GB</small>
        </div>

        <div className="card">
          <h6>دقائق المكالمات</h6>
          <h2>{voiceMinutes} دقيقة</h2>
          <div className="progress">
            <div className="progress-bar" style={{width: `${voicePercent}%`}}></div>
          </div>
          <small>{voicePercent}% مستخدم من {voiceLimit} دقيقة</small>
        </div>
      </div>

      <div className="services-section">
        <h4><i className="fas fa-th-large"></i> خدماتنا</h4>
        <div className="services-grid">
          <div className="service-item">
            <i className="fas fa-wifi service-icon"></i>
            <h6>الإنترنت</h6>
          </div>
          <div className="service-item">
            <i className="fas fa-phone service-icon"></i>
            <h6>المكالمات</h6>
          </div>
          <div className="service-item">
            <i className="fas fa-sms service-icon"></i>
            <h6>الرسائل</h6>
          </div>
          <div className="service-item">
            <i className="fas fa-globe service-icon"></i>
            <h6>التجوال</h6>
          </div>
          <div className="service-item">
            <i className="fas fa-mobile service-icon"></i>
            <h6>باقات بيانات</h6>
          </div>
          <div className="service-item">
            <i className="fas fa-gift service-icon"></i>
            <h6>عروض خاصة</h6>
          </div>
        </div>
      </div>

      <div className="quick-actions">
        <div className="card">
          <h5><i className="fas fa-bolt"></i> إجراءات سريعة</h5>
          <div className="actions-grid">
            <button className="btn btn-outline"><i className="fas fa-receipt"></i> الفاتورة</button>
            <button className="btn btn-outline"><i className="fas fa-history"></i> السجل</button>
            <button className="btn btn-outline"><i className="fas fa-cog"></i> الإعدادات</button>
            <button className="btn btn-outline"><i className="fas fa-question-circle"></i> الدعم</button>
          </div>
        </div>

        <div className="card">
          <h5><i className="fas fa-tags"></i> العروض الحالية</h5>
          <div className="offers">
            {offerings.length > 0 ? (
              offerings.map((offer, idx) => (
                <div key={idx} className="alert alert-info">
                  <strong>{offer.name || 'عرض خاص'}</strong> - {offer.description || 'باقات مميزة'}
                </div>
              ))
            ) : (
              <>
                <div className="alert alert-success">
                  <strong>عرض خاص!</strong> 10GB إضافية بـ 500 ر.ي
                </div>
                <div className="alert alert-info">
                  <strong>باقات مازي</strong> باقات يومية وأسبوعية
                </div>
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}