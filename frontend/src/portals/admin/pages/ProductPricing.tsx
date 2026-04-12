import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { productPriceAPI } from '../../../services/api';

export default function ProductPricing() {
  const queryClient = useQueryClient();

  const { data: prices, isLoading } = useQuery({ queryKey: ['productPrices'], queryFn: () => productPriceAPI.getAll() });

  const createMutation = useMutation({ mutationFn: (data: any) => productPriceAPI.create(data), onSuccess: () => queryClient.invalidateQueries({ queryKey: ['productPrices'] }) });

  if (isLoading) return <div className="loading">Loading...</div>;

  return (
    <div className="product-pricing-page">
      <h2>Product Pricing</h2>
      <button className="primary" onClick={() => createMutation.mutate({ name: 'New Price', priceType: 'ONE_TIME', price: 0 })}>
        Add Price
      </button>
      <table className="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Type</th>
            <th>Price</th>
            <th>Currency</th>
          </tr>
        </thead>
        <tbody>
          {prices?.map((price: any) => (
            <tr key={price.id}>
              <td>{price.id}</td>
              <td>{price.name}</td>
              <td>{price.priceType}</td>
              <td>{price.price}</td>
              <td>{price.currency}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
