import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Account, testDataService } from '../api';

const MainDashboard = () => {
    const [accounts, setAccounts] = useState<Account[]>([]);
    const [DBVersion, setDBVersion] = useState(0);

    useEffect(() => {
        const getAccounts = async () => {
            try {
                console.log("Sending getAllAccounts request...");
                const data = await testDataService.getAccounts();
                console.log("Accounts fetched!");
                setAccounts(data);
            } catch (err) {
                console.error('Error fetching accounts: ' + err);
            }
        };

        getAccounts();
    }, [DBVersion]);

    const handlePopulateDB = async () => {
        try {
            console.log("Sending populate db request...");
            const result = await testDataService.populateDB();
            console.log("API response: " + result);
            setDBVersion(prev => prev + 1);
        } catch (err) {
            console.error("Error populating database: " + err);
        }
    };

    return (
        <>
            <header className="header">

                <a href="https://github.com/Kybartas/juniorHomework" className="github-link" target="_blank"
                   rel="noreferrer">
                    GitHub
                </a>

                <h1>BankApp demo</h1>

                <button className="button" onClick={handlePopulateDB}>
                    {'Populate database'}
                </button>

            </header>

            <main className="App-content">

                <div className="accounts">

                    <h2>Accounts</h2>

                    {accounts.length === 0 ? (<p>No accounts found</p>) : (
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
        </>
    );
};

export default MainDashboard;