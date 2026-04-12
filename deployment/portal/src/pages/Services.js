import React, { useState, useEffect } from 'react';
import { productApi, subscriptionApi, orderApi } from '../services/api';

export default function Services() {
  const [offerings, setOfferings] = useState([]);
  const [subscriptions, setSubscriptions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [subscribing, setSubscribing] = useState(null);

  useEffect(() => {
    async function fetchData() {
      try {
        setLoading(true);
        
        const [offeringsData, subsData] = await Promise.allSettled([
          productApi.getOfferings(),
          subscriptionApi.getAll()
        ]);

        if (offeringsData.status === 'fulfilled' && offeringsData.value) {
          const data = offeringsData.value;
          if (Array.isArray(data)) {
            setOfferings(data);
          } else if (data.productOffering) {
            setOfferings(data.productOffering);
          }
        }

        if (subsData.status === 'fulfilled' && subsData.value) {
          const data = subsData.value;
          if (Array.isArray(data)) {
            setSubscriptions(data);
          } else if (data.product) {
            setSubscriptions(data.product);
          }
        }

        setLoading(false);
      } catch (err) {
        console.error('Services fetch error:', err);
        setError(err.message);
        setLoading(false);
      }
    }

    fetchData();
  }, []);

  const handleSubscribe = async (offeringId) => {
    try {
      setSubscribing(offeringId);
      await orderApi.create({
        orderDate: new Date().toISOString(),
        orderItem: [{
          action: 'add',
          product: {
            id: offeringId
          }
        }],
        state: 'initial'
      });
      alert('تم تقديم طلب الاشتراك بنجاح!');
    } catch (err) {
      console.error('Subscribe error:', err);
      alert('حدث خطأ في تقديم الطلب');
    } finally {
      setSubscribing(null);
    }
  };

  const defaultPackages = [
    { name: 'باقة يومية', price: 100, data: '500MB', validity: 'يوم', type: 'daily' },
    { name: 'باقة أسبوعية', price: 500, data: '3GB', validity: '7 أيام', type: 'weekly' },
    { name: 'باقة شهرية', price: 1500, data: '15GB', validity: '30 يوم', type: 'monthly' },
    { name: 'باقة سنوية', price: 12000, data: '200GB', validity: '365 يوم', type: 'yearly' },
  ];

  const displayPackages = offerings.length > 0 ? offerings : defaultPackages;

  return (
    <div className="services-page">
      <h3><i className="fas fa-box"></i> الباقات المتاحة</h3>
      
      {loading && <div className="loading">جاري التحميل...</div>}
      {error && <div className="error">خطأ: {error}</div>}

      <div className="packages-grid">
        {displayPackages.map((pkg, idx) => (
          <div key={idx} className="package-card">
            <h5>{pkg.name || pkg.productName || 'باقة'}</h5>
            <div className="package-details">
              <p><i className="fas fa-wifi"></i> {pkg.data || pkg.description || 'بيانات'}</p>
              <p><i className="fas fa-clock"></i> صالح {pkg.validity || '30 يوم'}</p>
            </div>
            <h4 className="price">{pkg.price || pkg.price?.amount || '0'} ر.ي</h4>
            <button 
              className="btn btn-primary"
              onClick={() => handleSubscribe(pkg.id)}
              disabled={subscribing === pkg.id}
            >
              {subscribing === pkg.id ? 'جاري...' : 'اشتراك'}
            </button>
          </div>
        ))}
      </div>

      <h3 style={{marginTop: '30px'}}><i className="fas fa-mobile"></i> خدماتي</h3>
      {subscriptions.length > 0 ? (
        <div className="my-services">
          {subscriptions.map((sub, idx) => (
            <div key={idx} className="service-row">
              <span>{sub.name || sub.productName || 'خدمة'}</span>
              <span className="badge badge-success">نشط</span>
            </div>
          ))}
        </div>
      ) : (
        <div className="增值-services">
          <div className="service-row">
            <span>حظر المكالمات</span>
            <button className="btn btn-sm">تفعيل</button>
          </div>
          <div className="service-row">
            <span>تحويل المكالمات</span>
            <button className="btn btn-sm">إعداد</button>
          </div>
          <div className="service-row">
            <span>الانتظار</span>
            <button className="btn btn-sm">تفعيل</button>
          </div>
        </div>
      )}
    </div>
  );
}