import { useQuery } from '@tanstack/react-query';
import { networkAPI } from '../services/api';

export default function NetworkMap() {
  const { data: elements } = useQuery({ queryKey: ['networkElements'], queryFn: () => networkAPI.getElements() });

  const locations = ['Sana\'a', 'Aden', 'Taiz', 'Mukalla', 'Ibb', 'Seyoun', 'Hodeidah'];

  return (
    <div className="network-map">
      <h2>Network Topology</h2>
      <div className="map-container">
        <div className="network-node core">
          <h4>Core Network</h4>
          <span className="status healthy">Healthy</span>
        </div>
        {locations.map(loc => (
          <div key={loc} className={`network-node ${loc === 'Sana\'a' ? 'regional' : 'local'}`}>
            <h4>{loc}</h4>
            <span className="status healthy">Online</span>
          </div>
        ))}
      </div>
    </div>
  );
}
