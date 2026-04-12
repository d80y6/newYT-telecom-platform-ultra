import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { shoppingCartAPI } from '../services/api';

export default function ShoppingCart() {
  const queryClient = useQueryClient();

  const { data: carts, isLoading } = useQuery({ queryKey: ['carts'], queryFn: () => shoppingCartAPI.getAll() });

  const updateMutation = useMutation({ mutationFn: ({ id, status }: { id: string; status: string }) => shoppingCartAPI.updateStatus(id, status), onSuccess: () => queryClient.invalidateQueries({ queryKey: ['carts'] }) });

  if (isLoading) return <div className="loading">Loading...</div>;

  return (
    <div className="shopping-cart-page">
      <h2>My Shopping Cart</h2>
      <div className="cart-items">
        {carts?.length === 0 ? (
          <p className="empty">Your cart is empty</p>
        ) : (
          carts?.map((cart: any) => (
            <div key={cart.id} className="cart-card">
              <div className="cart-header">
                <span className="cart-id">{cart.cartId}</span>
                <span className={`status ${cart.status?.toLowerCase()}`}>{cart.status}</span>
              </div>
              <div className="cart-body">
                <p>Total: {cart.totalAmount} {cart.currency}</p>
                <p>Created: {cart.createdAt}</p>
              </div>
              <div className="cart-actions">
                {cart.status === 'ACTIVE' && (
                  <button className="primary" onClick={() => updateMutation.mutate({ id: cart.id, status: 'CHECKOUT' })}>Checkout</button>
                )}
              </div>
            </div>
          ))
        )}
      </div>
      <button className="primary">Add New Item</button>
    </div>
  );
}
