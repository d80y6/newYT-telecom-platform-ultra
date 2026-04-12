import { useQuery } from '@tanstack/react-query';
import { subscriptionAPI } from '../services/api';

interface Service {
  id: string;
  name: string;
  status: string;
  type: string;
  startDate: string;
}

export default function MyServices() {
  const { data: services, isLoading } = useQuery({
    queryKey: ['myServices'],
    queryFn: () => subscriptionAPI.getMyServices()
  });

  if (isLoading) return <div className="loading">Loading...</div>;

  return (
    <div className="services-page">
      <h2>My Services</h2>
      <div className="services-grid">
        {services?.map((service: Service) => (
          <div key={service.id} className="service-card">
            <div className="service-icon">{service.type === 'MOBILE' ? '📱' : '📺'}</div>
            <div className="service-info">
              <h3>{service.name}</h3>
              <p>Started: {service.startDate}</p>
              <span className={`status ${service.status.toLowerCase()}`}>{service.status}</span>
            </div>
            <button className="manage-btn">Manage</button>
          </div>
        ))}
      </div>
    </div>
  );
}
