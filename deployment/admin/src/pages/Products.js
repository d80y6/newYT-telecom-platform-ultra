import React, { useState, useEffect } from 'react';
import { productApi } from '../services/api';

function Products() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    try {
      setLoading(true);
      const data = await productApi.getOfferings();
      if (Array.isArray(data)) {
        setProducts(data);
      } else if (data.productOffering) {
        setProducts(data.productOffering);
      }
    } catch (err) {
      console.error('Fetch products error:', err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const defaultProducts = [
    { id: 'PRD-001', name: 'باقة الأساسية', price: '150', type: 'prepaid', status: 'active', customers: '4,500' },
    { id: 'PRD-002', name: 'باقة Premium', price: '300', type: 'prepaid', status: 'active', customers: '2,100' },
    { id: 'PRD-003', name: 'باقة العائلة', price: '450', type: 'postpaid', status: 'active', customers: '1,800' },
    { id: 'PRD-004', name: 'باقة الأعمال', price: '600', type: 'postpaid', status: 'active', customers: '650' },
    { id: 'PRD-005', name: 'باقة الشباب', price: '100', type: 'prepaid', status: 'inactive', customers: '0' }
  ];

  const displayProducts = products.length > 0 ? products : defaultProducts;

  return (
    <div className="dashboard-content">
      <div className="card">
        <div className="card-header">
          <h3>المنتجات والخدمات</h3>
          <button className="btn btn-primary">إضافة منتج جديد</button>
        </div>
        <div className="card-body">
          {loading && <div className="loading">جاري التحميل...</div>}
          {error && <div className="error">خطأ: {error}</div>}
          
          <table className="data-table">
            <thead>
              <tr>
                <th>رقم المنتج</th>
                <th>اسم المنتج</th>
                <th>الوصف</th>
                <th>النوع</th>
                <th>الحالة</th>
                <th>إجراءات</th>
              </tr>
            </thead>
            <tbody>
              {displayProducts.map((product) => (
                <tr key={product.id}>
                  <td>{product.id}</td>
                  <td>{product.name}</td>
                  <td>{product.description || '-'}</td>
                  <td>{product.serviceType === 'MOBILE' ? 'جوال' : product.serviceType}</td>
                  <td>
                    <span className={`status-badge ${(product.status || '').toLowerCase()}`}>
                      {product.status === 'ACTIVE' ? 'نشط' : product.status === 'INACTIVE' ? 'غير نشط' : product.status}
                    </span>
                  </td>
                  <td>
                    <button className="btn btn-secondary" style={{ padding: '6px 12px', marginLeft: '4px' }}>تعديل</button>
                    <button className="btn btn-primary" style={{ padding: '6px 12px' }}>تفاصيل</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

export default Products;