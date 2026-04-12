import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import NocLayout from './components/NocLayout';
import NocDashboard from './pages/NocDashboard';
import NetworkMap from './pages/NetworkMap';
import Alarms from './pages/Alarms';
import Performance from './pages/Performance';
import SlaMonitoring from './pages/SlaMonitoring';

export default function NocPortal() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<NocLayout />}>
          <Route index element={<NocDashboard />} />
          <Route path="network" element={<NetworkMap />} />
          <Route path="alarms" element={<Alarms />} />
          <Route path="performance" element={<Performance />} />
          <Route path="sla" element={<SlaMonitoring />} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
