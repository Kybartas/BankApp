import React, { useState, useEffect } from 'react';
import { accountService, Account } from '../services/api';
import { statementService } from '../services/api';

const AccountList: React.FC = () => {
    const [accounts, setAccounts] = useState<Account[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        fetchAccounts();
    }, []);

    const fetchAccounts = async () => {
        try {
            setLoading(true);
            const data = await accountService.getAccounts();
            setAccounts(data);
            setError(null);
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Failed to fetch accounts');
            console.error('Error fetching accounts:', err);
        } finally {
            setLoading(false);
        }
    };

    const handlePopulateDB = async () => {
        try {
            setLoading(true);
            await statementService.populateDB();
            await fetchAccounts();
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Failed to populate DB');
            console.error('Error populating DB:', err);
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return <div>Loading accounts...</div>;
    }

    if (error) {
        return (
            <div style={{ color: 'red' }}>
                <p>Error: {error}</p>
            </div>
        );
    }

    return (
        <div>
            <button onClick={handlePopulateDB} disabled={loading}>
                {loading ? 'Working...' : 'Populate Demo Data'}
            </button>
            <h2>Bank Accounts</h2>
            {accounts.length === 0 ? (
                <p>No accounts found</p>
            ) : (
                <div>
                    {accounts.map((account) => (
                        <div key={account.accountNumber} style={{
                            padding: '5px'
                        }}>
                            <p>Account {account.accountNumber}</p>
                            <p>Balance {account.balance}</p>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

export default AccountList;