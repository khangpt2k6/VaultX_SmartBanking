-- Data Integrity Check Script
-- Run this to identify potential constraint violations causing transaction aborts

-- Check for orphaned accounts (accounts without customers)
SELECT 'Orphaned Accounts' as issue_type, COUNT(*) as count
FROM accounts a
LEFT JOIN customers c ON a.customer_id = c.customer_id
WHERE c.customer_id IS NULL;

-- Check for orphaned transactions (transactions without accounts)
SELECT 'Orphaned Transactions' as issue_type, COUNT(*) as count
FROM transactions t
LEFT JOIN accounts a ON t.account_id = a.account_id
WHERE a.account_id IS NULL;

-- Check for transactions referencing non-existent destination accounts
SELECT 'Invalid Destination Accounts' as issue_type, COUNT(*) as count
FROM transactions t
LEFT JOIN accounts a ON t.destination_account_id = a.account_id
WHERE t.destination_account_id IS NOT NULL AND a.account_id IS NULL;

-- Check for accounts with invalid status
SELECT 'Invalid Account Status' as issue_type, COUNT(*) as count
FROM accounts
WHERE status NOT IN ('ACTIVE', 'INACTIVE', 'CLOSED', 'SUSPENDED');

-- Check for transactions with invalid status
SELECT 'Invalid Transaction Status' as issue_type, COUNT(*) as count
FROM transactions
WHERE status NOT IN ('PENDING', 'COMPLETED', 'FAILED');

-- Check for transactions with invalid types
SELECT 'Invalid Transaction Types' as issue_type, COUNT(*) as count
FROM transactions
WHERE transaction_type NOT IN ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER', 'INTEREST_CREDIT');

-- Check for accounts with invalid types
SELECT 'Invalid Account Types' as issue_type, COUNT(*) as count
FROM accounts
WHERE account_type NOT IN ('SAVINGS', 'CHECKING', 'FIXED_DEPOSIT');

-- Summary of data counts
SELECT 'Customers' as table_name, COUNT(*) as count FROM customers
UNION ALL
SELECT 'Accounts' as table_name, COUNT(*) as count FROM accounts
UNION ALL
SELECT 'Transactions' as table_name, COUNT(*) as count FROM transactions
UNION ALL
SELECT 'Users' as table_name, COUNT(*) as count FROM users
UNION ALL
SELECT 'Roles' as table_name, COUNT(*) as count FROM roles;