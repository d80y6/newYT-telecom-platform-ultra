import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import AdminLayout from './components/AdminLayout';
import AdminDashboard from './pages/AdminDashboard';
import UserManagement from './pages/UserManagement';
import ProductCatalog from './pages/ProductCatalog';
import SystemConfig from './pages/SystemConfig';
import Reports from './pages/Reports';
import AuditLogs from './pages/AuditLogs';
import ProductPricing from './pages/ProductPricing';
import PricePlans from './pages/PricePlans';
import SalesLeads from './pages/SalesLeads';
import Quotes from './pages/Quotes';
import Analytics from './pages/Analytics';
import AgreementManagement from './pages/AgreementManagement';
import NotificationManagement from './pages/NotificationManagement';
import CampaignManagement from './pages/CampaignManagement';
import SlaManagement from './pages/SlaManagement';
import ResourceInventory from './pages/ResourceInventory';
import UsageManagement from './pages/UsageManagement';
import FraudDetection from './pages/FraudDetection';
import ConvergentBilling from './pages/ConvergentBilling';
import PartyManagement from './pages/PartyManagement';
import ProductConfiguration from './pages/ProductConfiguration';

export default function AdminPortal() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<AdminLayout />}>
          <Route index element={<AdminDashboard />} />
          <Route path="users" element={<UserManagement />} />
          <Route path="catalog" element={<ProductCatalog />} />
          <Route path="pricing" element={<ProductPricing />} />
          <Route path="price-plans" element={<PricePlans />} />
          <Route path="sales-leads" element={<SalesLeads />} />
          <Route path="quotes" element={<Quotes />} />
          <Route path="analytics" element={<Analytics />} />
          <Route path="agreements" element={<AgreementManagement />} />
          <Route path="notifications" element={<NotificationManagement />} />
          <Route path="campaigns" element={<CampaignManagement />} />
          <Route path="sla" element={<SlaManagement />} />
          <Route path="resources" element={<ResourceInventory />} />
          <Route path="usage" element={<UsageManagement />} />
          <Route path="fraud" element={<FraudDetection />} />
          <Route path="convergent-billing" element={<ConvergentBilling />} />
          <Route path="party" element={<PartyManagement />} />
          <Route path="product-config" element={<ProductConfiguration />} />
          <Route path="config" element={<SystemConfig />} />
          <Route path="reports" element={<Reports />} />
          <Route path="audit" element={<AuditLogs />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
