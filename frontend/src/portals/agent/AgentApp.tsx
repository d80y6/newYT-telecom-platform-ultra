import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import AgentLayout from './components/AgentLayout';
import AgentDashboard from './pages/AgentDashboard';
import CustomerSearch from './pages/CustomerSearch';
import CustomerDetail from './pages/CustomerDetail';
import OrderManagement from './pages/OrderManagement';
import TicketQueue from './pages/TicketQueue';
import BillingAgent from './pages/BillingAgent';
import Provisioning from './pages/Provisioning';
import IdentityVerification from './pages/IdentityVerification';
import AppointmentScheduling from './pages/AppointmentScheduling';
import RatingCharging from './pages/RatingCharging';
import Customer360 from './pages/Customer360';

export default function AgentPortal() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<AgentLayout />}>
          <Route index element={<AgentDashboard />} />
          <Route path="search" element={<CustomerSearch />} />
          <Route path="customer/:id" element={<CustomerDetail />} />
          <Route path="orders" element={<OrderManagement />} />
          <Route path="tickets" element={<TicketQueue />} />
          <Route path="billing" element={<BillingAgent />} />
          <Route path="provisioning" element={<Provisioning />} />
          <Route path="identity" element={<IdentityVerification />} />
          <Route path="appointments" element={<AppointmentScheduling />} />
          <Route path="rating" element={<RatingCharging />} />
          <Route path="customer360" element={<Customer360 />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
