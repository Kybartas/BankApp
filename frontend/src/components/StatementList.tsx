import React, { useState } from 'react';
import { statementService, Statement } from '../services/api';

const StatementList: React.FC = () => {

    const [statements, setStatements] = useState<Statement[]>([]);
    const [message, setMessage] = useState<string>('');
    const [error, setError] = useState<string | null>(null);

    const fetchStatements = async () => {
        try {
            const data = await statementService.getStatements();
            setStatements(data);
            setError(null);
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Failed to fetch statements');
            console.error('Error fetching statements:', err);
        }
    };

    const handleDeleteStatements = async () => {
        try {
            const result = await statementService.deleteStatements();
            setMessage(result);
            await fetchStatements()
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Failed to delete statements');
            console.error('Error deleting statements:', err);
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

            <h2>Bank Statements</h2>

            <div>
                <button onClick={fetchStatements}>
                    {'Fetch statements'}
                </button>
                <button onClick={handleDeleteStatements}>
                    {'Delete statements'}
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

            {statements.length === 0 ? (
                <p>No statements found</p>
            ) : (
                <div>
                    {statements.map((statement) => (
                        <div style={{padding: '1px'}}>
                            <p>
                                {statement.accountNumber + " " +
                                statement.date + " " +
                                statement.amount + " " +
                                statement.type}
                            </p>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

export default StatementList;