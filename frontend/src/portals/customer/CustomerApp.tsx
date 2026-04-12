import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import CustomerLayout from './components/CustomerLayout';
import CustomerDashboard from './pages/CustomerDashboard';
import MyServices from './pages/MyServices';
import MyBills from './pages/MyBills';
import MakePayment from './pages/MakePayment';
import UsageHistory from './pages/UsageHistory';
import SupportTickets from './pages/SupportTickets';
import ShoppingCart from './pages/ShoppingCart';
import OrderTracking from './pages/OrderTracking';

export default function CustomerPortal() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<CustomerLayout />}>
          <Route index element={<CustomerDashboard />} />
          <Route path="services" element={<MyServices />} />
          <Route path="bills" element={<MyBills />} />
          <Route path="payment" element={<MakePayment />} />
          <Route path="usage" element={<UsageHistory />} />
          <Route path="support" element={<SupportTickets />} />
          <Route path="cart" element={<ShoppingCart />} />
          <Route path="orders" element={<OrderTracking />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
