import React, { useState, useEffect } from 'react';
import { customerApi } from '../services/api';

function Customers() {
  const [searchTerm, setSearchTerm] = useState('');
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [editingCustomer, setEditingCustomer] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    status: 'active'
  });

  useEffect(() => {
    fetchCustomers();
  }, []);

  const fetchCustomers = async () => {
    try {
      setLoading(true);
      const data = await customerApi.getAll();
      if (Array.isArray(data)) {
        setCustomers(data);
      } else if (data.customer) {
        setCustomers(data.customer);
      } else {
        setCustomers([]);
      }
    } catch (err) {
      console.error('Fetch customers error:', err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    if (!searchTerm) {
      fetchCustomers();
      return;
    }
    try {
      setLoading(true);
      const data = await customerApi.getById(searchTerm);
      setCustomers(data ? [data] : []);
    } catch (err) {
      console.error('Search error:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const nameParts = formData.name.split(' ');
      const customerData = {
        firstName: nameParts[0] || '',
        lastName: nameParts.slice(1).join(' ') || '',
        email: formData.email,
        primaryPhone: formData.phone,
        status: formData.status === 'active' ? 'ACTIVE' : formData.status === 'pending' ? 'PENDING' : 'SUSPENDED',
        customerType: 'RESIDENTIAL',
        nationalId: 'N/A',
        city: 'صنعاء',
        governorate: 'صنعاء'
      };
      
      if (editingCustomer) {
        await customerApi.update(editingCustomer.id, customerData);
      } else {
        await customerApi.create(customerData);
      }
      setShowModal(false);
      setEditingCustomer(null);
      setFormData({ name: '', email: '', phone: '', status: 'active' });
      fetchCustomers();
      alert('تم الحفظ بنجاح');
    } catch (err) {
      console.error('Save error:', err);
      alert('حدث خطأ في الحفظ');
    }
  };

  const handleDelete = async (id) => {
    if (!confirm('هل أنت متأكد من حذف هذا العميل؟')) return;
    try {
      await customerApi.delete(id);
      fetchCustomers();
      alert('تم الحذف بنجاح');
    } catch (err) {
      console.error('Delete error:', err);
      alert('لا يمكن حذف العميل');
    }
  };

  const defaultCustomers = [
    { id: 'CUS-001', name: 'أحمد محمد', email: 'ahmed@example.com', phone: '+967-770123456', status: 'active', balance: '450' },
    { id: 'CUS-002', name: 'سارة علي', email: 'sara@example.com', phone: '+967-771234567', status: 'active', balance: '1200' },
    { id: 'CUS-003', name: 'محمد حسن', email: 'mohammed@example.com', phone: '+967-772345678', status: 'pending', balance: '0' },
    { id: 'CUS-004', name: 'فاطمة اليمن', email: 'fatima@example.com', phone: '+967-773456789', status: 'active', balance: '850' },
    { id: 'CUS-005', name: 'علي سعيد', email: 'ali@example.com', phone: '+967-774567890', status: 'suspended', balance: '2300' }
  ];

  const displayCustomers = customers.length > 0 ? customers : defaultCustomers;
  const filteredCustomers = searchTerm 
    ? displayCustomers.filter(c => 
        c.name?.includes(searchTerm) || 
        c.email?.includes(searchTerm) || 
        c.phone?.includes(searchTerm) ||
        c.id?.includes(searchTerm)
      )
    : displayCustomers;

  return (
    <div className="dashboard-content">
      <div className="card">
        <div className="card-header">
          <h3>العملاء</h3>
          <button className="btn btn-primary" onClick={() => setShowModal(true)}>إضافة عميل جديد</button>
        </div>
        <div className="card-body">
          {error && <div className="alert alert-danger">{error}</div>}
          
          <div className="form-group" style={{ maxWidth: '400px' }}>
            <input 
              type="text" 
              placeholder="البحث عن عميل..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
            />
          </div>

          {loading ? (
            <div className="loading">جاري التحميل...</div>
          ) : (
            <table className="data-table">
              <thead>
                <tr>
                  <th>رقم العميل</th>
                  <th>الاسم</th>
                  <th>البريد الإلكتروني</th>
                  <th>رقم الهاتف</th>
                  <th>الرصيد</th>
                  <th>الحالة</th>
                  <th>إجراءات</th>
                </tr>
              </thead>
              <tbody>
                {filteredCustomers.map((customer) => (
                  <tr key={customer.id || customer.customerId}>
                    <td>{customer.externalId || customer.id}</td>
                    <td>{customer.firstName} {customer.lastName}</td>
                    <td>{customer.email || '-'}</td>
                    <td>{customer.primaryPhone || '-'}</td>
                    <td>{customer.outstandingBalance || '0'} ريال</td>
                    <td>
                      <span className={`status-badge ${(customer.status || '').toLowerCase()}`}>
                        {customer.status === 'ACTIVE' ? 'نشط' : 
                         customer.status === 'PENDING' ? 'معلق' : 
                         customer.status === 'SUSPENDED' ? 'موقف' : 'نشط'}
                      </span>
                    </td>
                    <td>
                      <button 
                        className="btn btn-secondary" 
                        style={{ padding: '6px 12px', marginLeft: '4px' }}
                        onClick={() => {
                          setEditingCustomer(customer);
                          setFormData({
                            name: `${customer.firstName || ''} ${customer.lastName || ''}`.trim(),
                            email: customer.email || '',
                            phone: customer.primaryPhone || '',
                            status: customer.status === 'ACTIVE' ? 'active' : customer.status === 'PENDING' ? 'pending' : 'suspended'
                          });
                          setShowModal(true);
                        }}
                      >
                        تعديل
                      </button>
                      <button 
                        className="btn btn-danger" 
                        style={{ padding: '6px 12px' }}
                        onClick={() => handleDelete(customer.id)}
                      >
                        حذف
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
            <h3>{editingCustomer ? 'تعديل عميل' : 'إضافة عميل جديد'}</h3>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>الاسم</label>
                <input 
                  type="text" 
                  value={formData.name}
                  onChange={(e) => setFormData({...formData, name: e.target.value})}
                  required
                />
              </div>
              <div className="form-group">
                <label>البريد الإلكتروني</label>
                <input 
                  type="email" 
                  value={formData.email}
                  onChange={(e) => setFormData({...formData, email: e.target.value})}
                />
              </div>
              <div className="form-group">
                <label>رقم الهاتف</label>
                <input 
                  type="text" 
                  value={formData.phone}
                  onChange={(e) => setFormData({...formData, phone: e.target.value})}
                />
              </div>
              <div className="form-group">
                <label>الحالة</label>
                <select 
                  value={formData.status}
                  onChange={(e) => setFormData({...formData, status: e.target.value})}
                >
                  <option value="active">نشط</option>
                  <option value="pending">معلق</option>
                  <option value="suspended">موقف</option>
                </select>
              </div>
              <div className="modal-actions">
                <button type="submit" className="btn btn-primary">حفظ</button>
                <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>إلغاء</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default Customers;