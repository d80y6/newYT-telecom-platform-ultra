import { useQuery } from '@tanstack/react-query';
import { adminAPI } from '../services/api';

export default function Reports() {
  const { data: reports } = useQuery({ queryKey: ['reports'], queryFn: () => adminAPI.getReports('all') });

  const reportTypes = ['Revenue', 'Customer', 'Usage', 'Financial', 'Performance'];

  return (
    <div className="reports-page">
      <h2>Reports</h2>
      <div className="report-types">
        {reportTypes.map(type => (
          <button key={type} className="report-btn">{type} Report</button>
        ))}
      </div>
      <h3>Recent Reports</h3>
      <table>
        <thead><tr><th>Report</th><th>Generated</th><th>Status</th><th>Download</th></tr></thead>
        <tbody>
          {reports?.map((r: any) => (
            <tr key={r.id}><td>{r.name}</td><td>{r.generatedAt}</td><td>{r.status}</td><td><button>Download</button></td></tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
