# Runbook: Billing Failures

## Symptoms
- Invoice generation failures
- Payment processing errors
- Dunning job not running
- Outstanding balance discrepancies

## Diagnosis

1. Check invoice status distribution:
```sql
SELECT status, COUNT(*) FROM invoices GROUP BY status;
```

2. Check payment failures:
```sql
SELECT COUNT(*) FROM payments WHERE status = 'FAILED' AND created_at > NOW() - INTERVAL '24 hours';
```

3. Check dunning job execution:
```sql
SELECT characteristics->>'dunningLastProcessed' FROM invoices WHERE status = 'OVERDUE' LIMIT 5;
```

## Resolution

### Invoice stuck in DRAFT
1. Check if line items were added:
```sql
SELECT id, invoice_number, subtotal_amount FROM invoices WHERE status = 'DRAFT' AND created_at < NOW() - INTERVAL '1 hour';
```
2. Manually finalize via API:
```bash
curl -X POST http://bss-core:8080/tmf-api/customerBillManagement/v5/bill/{id}/finalize
```

### Payment failures
1. Check account balance:
```sql
SELECT id, account_number, balance, credit_limit FROM accounts WHERE id = '{account_id}';
```
2. If negative balance, check for missing credits:
```sql
SELECT SUM(amount) FROM payments WHERE account_id = '{account_id}' AND status = 'COMPLETED';
```

### Dunning not running
1. Check scheduled task status in application logs
2. Manually trigger dunning:
```bash
curl -X POST http://bss-core:8080/actuator/scheduledtasks
```

## Prevention
- Monitor invoice DRAFT count alert > 100
- Monitor payment failure rate > 1%
- Verify dunning job runs daily at 2 AM
