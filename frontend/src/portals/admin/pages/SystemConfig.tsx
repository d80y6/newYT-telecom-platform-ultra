import { useQuery } from '@tanstack/react-query';
import { adminAPI } from '../services/api';

export default function SystemConfig() {
  const { data: config } = useQuery({ queryKey: ['config'], queryFn: () => adminAPI.getConfig() });

  return (
    <div className="config-page">
      <h2>System Configuration</h2>
      <div className="config-sections">
        <div className="config-card">
          <h3>General Settings</h3>
          <label>System Name <input defaultValue={config?.systemName || 'Yemen PTC BSS'} /></label>
          <label>Timezone <select><option>Asia/Riyadh</option></select></label>
        </div>
        <div className="config-card">
          <h3>API Settings</h3>
          <label>Rate Limit <input type="number" defaultValue={config?.rateLimit || 1000} /></label>
          <label>Timeout (ms) <input type="number" defaultValue={config?.timeout || 30000} /></label>
        </div>
        <div className="config-card">
          <h3>Notification</h3>
          <label><input type="checkbox" defaultChecked={config?.emailEnabled} /> Enable Email</label>
          <label><input type="checkbox" defaultChecked={config?.smsEnabled} /> Enable SMS</label>
        </div>
      </div>
      <button className="save-btn">Save Configuration</button>
    </div>
  );
}
