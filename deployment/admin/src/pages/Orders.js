import React, { useState, useEffect } from 'react';
import { orderApi, customerApi, productApi } from '../services/api';

function Orders() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [customers, setCustomers] = useState([]);
  const [offerings, setOfferings] = useState([]);
  const [formData, setFormData] = useState({
    customerId: '',
    productId: '',
    quantity: 1
  });

  useEffect(() => {
    fetchOrders();
    fetchCustomers();
    fetchOfferings();
  }, []);

  const fetchOrders = async () => {
    try {
      setLoading(true);
      const data = await orderApi.getOrders();
      if (Array.isArray(data)) {
        setOrders(data);
      } else if (data.order) {
        setOrders(data.order);
      } else {
        setOrders([]);
      }
    } catch (err) {
      console.error('Fetch orders error:', err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const fetchCustomers = async () => {
    try {
      const data = await customerApi.getAll();
      if (Array.isArray(data)) {
        setCustomers(data);
      } else if (data.customer) {
        setCustomers(data.customer);
      }
    } catch (err) {
      console.error('Fetch customers error:', err);
    }
  };

  const fetchOfferings = async () => {
    try {
      const data = await productApi.getOfferings();
      if (Array.isArray(data)) {
        setOfferings(data);
      } else if (data.productOffering) {
        setOfferings(data.productOffering);
      }
    } catch (err) {
      console.error('Fetch offerings error:', err);
    }
  };

  const handleCreateOrder = async (e) => {
    e.preventDefault();
    try {
      await orderApi.create({
        orderDate: new Date().toISOString(),
        orderItem: [{
          action: 'add',
          product: {
            id: formData.productId
          },
          quantity: formData.quantity
        }],
        buyer: {
          id: formData.customerId
        },
        state: 'initial'
      });
      setShowModal(false);
      setFormData({ customerId: '', productId: '', quantity: 1 });
      fetchOrders();
      alert('تم إنشاء الطلب بنجاح');
    } catch (err) {
      console.error('Create order error:', err);
      alert('حدث خطأ في إنشاء الطلب');
    }
  };

  const handleCancelOrder = async (orderId) => {
    if (!confirm('هل أنت متأكد من إلغاء هذا الطلب؟')) return;
    try {
      await orderApi.cancel(orderId);
      fetchOrders();
      alert('تم إلغاء الطلب بنجاح');
    } catch (err) {
      console.error('Cancel order error:', err);
    }
  };

  const defaultOrders = [
    { id: 'ORD-001', customer: 'أحمد محمد', product: 'باقة الأساسية', date: '2026-04-01', amount: '150', status: 'pending' },
    { id: 'ORD-002', customer: 'سارة علي', product: 'باقة Premium', date: '2026-04-02', amount: '300', status: 'active' },
    { id: 'ORD-003', customer: 'محمد حسن', product: 'باقة العائلة', date: '2026-04-03', amount: '450', status: 'active' },
    { id: 'ORD-004', customer: 'فاطمة اليمن', product: 'باقة الأعمال', date: '2026-04-03', amount: '600', status: 'pending' },
    { id: 'ORD-005', customer: 'علي سعيد', product: 'باقة الأساسية', date: '2026-04-04', amount: '150', status: 'inactive' }
  ];

  const displayOrders = orders.length > 0 ? orders : defaultOrders;

  return (
    <div className="dashboard-content">
      <div className="card">
        <div className="card-header">
          <h3>الطلبات</h3>
          <button className="btn btn-primary" onClick={() => setShowModal(true)}>إنشاء طلب جديد</button>
        </div>
        <div className="card-body">
          {error && <div className="alert alert-danger">{error}</div>}
          
          {loading ? (
            <div className="loading">جاري التحميل...</div>
          ) : (
            <table className="data-table">
              <thead>
                <tr>
                  <th>رقم الطلب</th>
                  <th>العميل</th>
                  <th>المنتج</th>
                  <th>التاريخ</th>
                  <th>المبلغ</th>
                  <th>الحالة</th>
                  <th>إجراءات</th>
                </tr>
              </thead>
              <tbody>
                {displayOrders.map((order) => (
                  <tr key={order.id || order.orderId}>
                    <td>{order.id || order.orderId}</td>
                    <td>{order.buyer?.name || order.buyer?.id || '-'}</td>
                    <td>{order.orderItem?.[0]?.product?.name || order.productName || '-'}</td>
                    <td>{order.orderDate || order.createdDate || '-'}</td>
                    <td>{order.totalAmount || order.amount || '0'} ريال</td>
                    <td>
                      <span className={`status-badge ${order.state || order.status || 'pending'}`}>
                        {order.state === 'initial' || order.status === 'pending' ? 'معلق' : 
                         order.state === 'active' || order.status === 'active' ? 'نشط' : 
                         order.state === 'completed' ? 'مكتمل' : 'غير نشط'}
                      </span>
                    </td>
                    <td>
                      <button 
                        className="btn btn-secondary" 
                        style={{ padding: '6px 12px' }}
                        onClick={() => handleCancelOrder(order.id || order.orderId)}
                      >
                        إلغاء
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>

      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal" onClick={(e) => e.stopPropagation()}>
            <h3>إنشاء طلب جديد</h3>
            <form onSubmit={handleCreateOrder}>
              <div className="form-group">
                <label>العميل</label>
                <select 
                  value={formData.customerId}
                  onChange={(e) => setFormData({...formData, customerId: e.target.value})}
                  required
                >
                  <option value="">اختر العميل</option>
                  {customers.map(c => (
                    <option key={c.id || c.customerId} value={c.id || c.customerId}>
                      {c.name || c.fullName || c.id}
                    </option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>المنتج</label>
                <select 
                  value={formData.productId}
                  onChange={(e) => setFormData({...formData, productId: e.target.value})}
                  required
                >
                  <option value="">اختر المنتج</option>
                  {offerings.map(o => (
                    <option key={o.id} value={o.id}>
                      {o.name || o.productName}
                    </option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>الكمية</label>
                <input 
                  type="number" 
                  value={formData.quantity}
                  onChange={(e) => setFormData({...formData, quantity: parseInt(e.target.value)})}
                  min="1"
                  required
                />
              </div>
              <div className="modal-actions">
                <button type="submit" className="btn btn-primary">إنشاء</button>
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>إلغاء</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default Orders;