import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Account, testDataService, statementService } from '../api';
import ImportCSVButton from './ImportCSVButton';

const MainDashboard = () => {

    const [accounts, setAccounts] = useState<Account[]>([]);
    const [dbVersion, setDbVersion] = useState(0);
    const [loading, setLoading] = useState(false);

    type Notification = {
        id: number;
        message: string;
        type: 'success' | 'error' | 'job';
    }

    const [notifications, setNotifications] = useState<Notification[]>([]);

    const addNotification = (message: string, type: Notification['type']) => {
        const id = Date.now();
        const newNotification = {id, message, type};
        setNotifications(prev => [...prev, newNotification]);

        setTimeout(() => {
            setNotifications(prev => prev.filter(n => n.id !== id));
        }, 4000);
    }

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
        <>
            <header className="header">

                <h1>BankApp demo</h1>

                <button className="button" onClick={handlePopulateDB}>
                    {'Populate database'}
                </button>
                <ImportCSVButton onImport={handleImportCSV}/>

                <a href="https://github.com/Kybartas/juniorHomework" className="github-link" target="_blank"
                   rel="noreferrer">
                    GitHub
                </a>

            </header>

            <main className="App-content">

                <div className="accounts">

                    <h2>Accounts</h2>

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
                                            <Link className="link" to={`/accountDashboard/${account.accountNumber}`}>
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
            </main>

            <div className={"notification-container"}>
                {notifications.map(n => (
                    <div key={n.id} className={`notification ${n.type}`}>
                        {n.message}
                    </div>
                ))}
            </div>

        </>
    );
};

export default MainDashboard;