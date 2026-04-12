import { useQuery } from '@tanstack/react-query';
import { adminAPI } from '../services/api';

export default function ProductCatalog() {
  const { data: products, isLoading } = useQuery({
    queryKey: ['products'],
    queryFn: () => adminAPI.getProducts()
  });

  if (isLoading) return <div className="loading">Loading...</div>;

  return (
    <div className="catalog-page">
      <div className="page-header">
        <h2>Product Catalog</h2>
        <button>+ Add Product</button>
      </div>
      <div className="products-grid">
        {products?.map((p: any) => (
          <div key={p.id} className="product-card">
            <h3>{p.name}</h3>
            <p>{p.description}</p>
            <p className="price">YER {p.price}</p>
            <span className={`status ${p.status?.toLowerCase()}`}>{p.status}</span>
          </div>
        ))}
      </div>
    </div>
  );
}
