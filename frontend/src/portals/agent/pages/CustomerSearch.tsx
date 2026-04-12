import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { customerAPI } from '../services/api';

interface Customer {
  id: string;
  name: string;
  phone: string;
  email: string;
  status: string;
}

export default function CustomerSearch() {
  const [searchTerm, setSearchTerm] = useState('');
  const [searchBy, setSearchBy] = useState('name');

  const { data: results, isLoading } = useQuery({
    queryKey: ['customerSearch', searchTerm],
    queryFn: () => customerAPI.search(`${searchBy}=${searchTerm}`),
    enabled: searchTerm.length >= 2
  });

  return (
    <div className="search-page">
      <h2>Customer Search</h2>
      <div className="search-box">
        <select value={searchBy} onChange={(e) => setSearchBy(e.target.value)}>
          <option value="name">Name</option>
          <option value="phone">Phone</option>
          <option value="email">Email</option>
          <option value="id">Customer ID</option>
        </select>
        <input
          type="text"
          placeholder="Search..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
      </div>
      {isLoading && <div className="loading">Searching...</div>}
      <div className="search-results">
        {results?.map((customer: Customer) => (
          <Link to={`/customer/${customer.id}`} key={customer.id} className="customer-result">
            <div className="customer-info">
              <h3>{customer.name}</h3>
              <p>{customer.phone} | {customer.email}</p>
            </div>
            <span className={`status ${customer.status.toLowerCase()}`}>{customer.status}</span>
          </Link>
        ))}
      </div>
    </div>
  );
}
