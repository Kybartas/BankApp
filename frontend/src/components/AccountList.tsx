import React, { useState } from 'react';
import { accountService, Account } from '../services/api';
import { statementService } from '../services/api';

const AccountList: React.FC = () => {

    const [accounts, setAccounts] = useState<Account[]>([]);
    const [message, setMessage] = useState<string>('');
    const [error, setError] = useState<string | null>(null);

    const fetchAccounts = async () => {
        try {
            const data = await accountService.getAccounts();
            setAccounts(data);
            setError(null);
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Failed to fetch accounts');
            console.error('Error fetching accounts:', err);
        }
    };

    const handlePopulateDB = async () => {
        try {
            const result = await statementService.populateDB();
            setMessage(result);
            await fetchAccounts();
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Failed to populate DB');
            console.error('Error populating DB:', err);
        }
    };

    const handleDeleteAccounts = async () => {
        try {
            const result = await accountService.deleteAccounts();
            setMessage(result)
            await fetchAccounts();
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Failed to delete accounts');
            console.error('Error deleting accounts:', err);
        }
    };

    if (error) {
        return (
            <div style={{ color: 'red' }}>
                <p>error: {error}</p>
            </div>
        );
    }

    return (
        <div>

            <h2>Bank Accounts</h2>

            <div>
                <button onClick={handlePopulateDB}>
                    {'Populate Demo Data'}
                </button>
                <button onClick={handleDeleteAccounts}>
                    {'Delete accounts'}
                </button>
            </div>

            {message && (
                <div style={{
                    marginTop: '20px',
                    padding: '10px',
                    backgroundColor: message.includes('Error') ? '#ffebee' : '#e8f5e8',
                    border: `1px solid ${message.includes('Error') ? '#f44336' : '#4caf50'}`,
                    borderRadius: '4px',
                    color: message.includes('Error') ? '#d32f2f' : '#2e7d32'
                }}>
                    {message}
                </div>
            )}

            {accounts.length === 0 ? (
                <p>No accounts found</p>
            ) : (
                <div>
                    {accounts.map((account) => (
                        <div style={{padding: '5px'}}>
                            <p>{account.accountNumber}</p>
                            <p>Balance {account.balance}</p>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

export default AccountList;