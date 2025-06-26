import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Account, testDataService, statementService } from '../api';
import ImportCSVButton from '../components/ImportCSVButton';
import '../styles/dashboard.css';
import {useNotifications} from "../hooks/useNotifications";

const HomeDashboard = () => {

    const [accounts, setAccounts] = useState<Account[]>([]);
    const [loading, setLoading] = useState(false);

    const { addNotification } = useNotifications();

    useEffect(() => {

        const getAccounts = async () => {

            setLoading(true);
            const data = await testDataService.getAccounts({ log: addNotification });
            setAccounts(data);
            setLoading(false);
        };

        getAccounts();

    }, [addNotification]);

    const handleImportCSV = async (event: React.ChangeEvent<HTMLInputElement>) => {
        const file = event.target.files?.[0];
        if(!file) return;

        setLoading(true);
        await statementService.importCSV(file, { log: addNotification });
        setLoading(false);
    };

    return (
        <div className="dashboard-container">

            <div className="dashboard-header">

                <h1>Home dashboard</h1>

                <ImportCSVButton onImport={handleImportCSV}/>

            </div>


            {loading ? (<p>Loading accounts...</p>) : accounts.length === 0 ? (<p>No accounts found</p>) : (

                <div className="data-container">

                    <table className="data-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Account Number</th>
                                <th>Balance</th>
                            </tr>
                        </thead>
                        <tbody>
                            {accounts.map((account) => (
                                <tr key={account.id}>
                                    <td>{account.id}</td>
                                    <td>
                                        <Link className="account-dashboard-link" to={`/${account.accountNumber}`}>
                                            {account.accountNumber}
                                        </Link>
                                    </td>
                                    <td className={account.balance >= 0 ? "positive" : "negative"}>
                                        {account.balance}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>

                </div>

            )}

        </div>
    );
};

export default HomeDashboard;