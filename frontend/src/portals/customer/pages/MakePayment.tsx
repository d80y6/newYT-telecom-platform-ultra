import { useState } from 'react';
import { useMutation } from '@tanstack/react-query';
import { billingAPI } from '../services/api';

export default function MakePayment() {
  const [amount, setAmount] = useState('');
  const [method, setMethod] = useState('CARD');
  
  const mutation = useMutation({
    mutationFn: (data: any) => billingAPI.createPayment(data),
    onSuccess: () => alert('Payment initiated successfully!')
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    mutation.mutate({ amount: parseFloat(amount), paymentMethod: method });
  };

  return (
    <div className="payment-page">
      <h2>Make a Payment</h2>
      <form onSubmit={handleSubmit} className="payment-form">
        <div className="form-group">
          <label>Amount (YER)</label>
          <input 
            type="number" 
            value={amount} 
            onChange={(e) => setAmount(e.target.value)}
            min="100"
            required
          />
        </div>
        <div className="form-group">
          <label>Payment Method</label>
          <select value={method} onChange={(e) => setMethod(e.target.value)}>
            <option value="CARD">Credit/Debit Card</option>
            <option value="BANK">Bank Transfer</option>
            <option value="MOBILE">Mobile Money</option>
            <option value="CASH">Cash at Branch</option>
          </select>
        </div>
        <button type="submit" disabled={mutation.isPending}>
          {mutation.isPending ? 'Processing...' : 'Pay Now'}
        </button>
      </form>
    </div>
  );
}
