import React, { useState, useEffect } from 'react';
import {Link, useParams} from 'react-router-dom';
import { Transaction, testDataService } from '../api';

const AccountDashboard = () => {
    const { accountNumber } = useParams<{ accountNumber: string }>();
    const [transactions, setTransactions] = useState<Transaction[]>([]);
    const [balance, setBalance] = useState<number>(0);

    useEffect(() => {
        const getTransactions = async () => {
            if (!accountNumber) return;
            
            try {
                console.log("Sending getTransactions for " + accountNumber + " request...");
                const data = await testDataService.getTransactions(accountNumber);
                console.log("Transactions fetched!");
                setTransactions(data);
            } catch (err) {
                console.log('Error fetching transactions: ' + err);
            }
        };

        const getBalance = async () => {
            if (!accountNumber) return;
            
            try {
                console.log("Sending getBalance for " + accountNumber + " request...");
                const data = await testDataService.getBalance(accountNumber);
                console.log("Balance fetched!");
                setBalance(data);
            } catch (err) {
                console.log('Error fetching balance: ' + err);
            }
        };

        getBalance();
        getTransactions();
    }, []);

    return (
        <>
            <header className="header">

                <Link className="link" to={`/`}>
                    Back to main
                </Link>

                <h1>Account {accountNumber} Dashboard</h1>

            </header>

            <main className="App-content">

                <div className="transactions">

                    <h2>Balance: {balance}</h2>

                    <h2>Transactions: </h2>
                    
                    {transactions.length === 0 ? (<p>No statements found</p>) : (
                        <div className="data-container">
                            <table className="data-table">
                                <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Account Number</th>
                                    <th>Date</th>
                                    <th>Beneficiary</th>
                                    <th>Description</th>
                                    <th>Amount</th>
                                    <th>Type</th>
                                </tr>
                                </thead>
                                <tbody>
                                {transactions.map((transaction) => (
                                    <tr key={transaction.id}>
                                        <td>{transaction.id}</td>
                                        <td>{transaction.accountNumber}</td>
                                        <td>{transaction.date}</td>
                                        <td>{transaction.beneficiary}</td>
                                        <td>{transaction.description}</td>
                                        <td>{transaction.amount}</td>
                                        <td className={transaction.type === "K" ? "positive" : "negative"}>
                                            {transaction.type}
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

export default AccountDashboard;