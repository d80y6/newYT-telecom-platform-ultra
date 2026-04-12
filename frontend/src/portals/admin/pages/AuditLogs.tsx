import { useQuery } from '@tanstack/react-query';
import { adminAPI } from '../services/api';

export default function AuditLogs() {
  const { data: logs } = useQuery({ queryKey: ['auditLogs'], queryFn: () => adminAPI.getAuditLogs() });

  return (
    <div className="audit-page">
      <h2>Audit Logs</h2>
      <div className="filter-bar">
        <input type="text" placeholder="Search logs..." />
        <select><option>All Actions</option><option>CREATE</option><option>UPDATE</option><option>DELETE</option></select>
      </div>
      <table>
        <thead>
          <tr><th>Timestamp</th><th>User</th><th>Action</th><th>Resource</th><th>Details</th></tr>
        </thead>
        <tbody>
          {logs?.map((log: any) => (
            <tr key={log.id}>
              <td>{log.timestamp}</td>
              <td>{log.user}</td>
              <td><span className={`action ${log.action.toLowerCase()}`}>{log.action}</span></td>
              <td>{log.resource}</td>
              <td>{log.details}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
