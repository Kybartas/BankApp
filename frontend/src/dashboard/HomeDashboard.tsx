import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Account, testDataService, statementService } from '../api';
import ImportCSVButton from './ImportCSVButton';
import {useNotifications} from "../notification/Notifications";
import './dashboard.css';

const HomeDashboard = () => {

    const [accounts, setAccounts] = useState<Account[]>([]);
    const [dbVersion, setDbVersion] = useState(0);
    const [loading, setLoading] = useState(false);

    const { addNotification } = useNotifications();

    useEffect(() => {

        const getAccounts = async () => {
            setLoading(true);
            try {
                addNotification("Sending getAllAccounts request...", 'job');
                const data = await testDataService.getAccounts();
                addNotification("Accounts fetched!", "success");
                setAccounts(data);
            } catch (err) {
                addNotification('Error fetching accounts: ' + err, 'error');
            } finally {
                setLoading(false);
            }
        };

        getAccounts();

    }, [dbVersion]);

    const handlePopulateDB = async () => {
        setLoading(true);
        try {
            addNotification("Sending populate db request...", 'job');
            const result = await testDataService.populateDB();
            addNotification('API response: ' + result, 'success');
            setDbVersion(prev => prev + 1);
        } catch (err) {
            addNotification("Error populating database: " + err, 'error');
        } finally {
            setLoading(false);
        }
    };

    const handleImportCSV = async (event: React.ChangeEvent<HTMLInputElement>) => {
        const file = event.target.files?.[0];
        if(!file) return;
        setLoading(true);
        try {
            addNotification("Sending import CSV request...", 'job');
            await statementService.importCSV(file);
            setDbVersion(prev => prev + 1);
            addNotification('Statement imported successfully!', 'success');
        } catch (err) {
            addNotification('Error importing CSV: ' + err, 'error');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="dashboard-container">

            <div className="dashboard-header">

                <h1>Home dashboard</h1>

                <button className="button" onClick={handlePopulateDB}>
                    {'Populate database'}
                </button>
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