import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { pricePlanAPI } from '../../../services/api';

export default function PricePlans() {
  const queryClient = useQueryClient();

  const { data: plans, isLoading } = useQuery({ queryKey: ['pricePlans'], queryFn: () => pricePlanAPI.getAll() });

  const createMutation = useMutation({ mutationFn: (data: any) => pricePlanAPI.create(data), onSuccess: () => queryClient.invalidateQueries({ queryKey: ['pricePlans'] }) });

  if (isLoading) return <div className="loading">Loading...</div>;

  return (
    <div className="price-plans-page">
      <h2>Price Plans (Recurring)</h2>
      <button className="primary" onClick={() => createMutation.mutate({ name: 'New Plan', billingPeriod: 'MONTHLY' })}>
        Create Plan
      </button>
      <table className="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Period</th>
            <th>Price</th>
          </tr>
        </thead>
        <tbody>
          {plans?.map((plan: any) => (
            <tr key={plan.id}>
              <td>{plan.id}</td>
              <td>{plan.name}</td>
              <td>{plan.billingPeriod}</td>
              <td>{plan.price}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
