import React, { useState, useEffect } from 'react';
import {useParams} from 'react-router-dom';
import { Transaction, accountService, statementService } from '../api';
import {useNotifications} from "../notification/Notifications";
import './dashboard.css';

const AccountDashboard = () => {

    const { accountNumber } = useParams<{ accountNumber: string }>();
    const [transactions, setTransactions] = useState<Transaction[]>([]);
    const [balance, setBalance] = useState<number>(0);

    const { addNotification } = useNotifications();

    useEffect(() => {

        const getTransactions = async () => {
            if (!accountNumber) return;
            
            try {
                const data = await accountService.getTransactions(accountNumber);
                setTransactions(data);
            } catch (err) {
                addNotification('Error fetching transactions: ' + err, 'error');
            }
        };

        const getBalance = async () => {
            if (!accountNumber) return;
            
            try {
                const data = await accountService.getBalance(accountNumber);
                setBalance(data);
            } catch (err) {
                addNotification('Error fetching balance: ' + err, 'error');
            }
        };

        const loadData = async () => {
            if (!accountNumber) return;

            addNotification(`Getting data for ${accountNumber}...`, 'job');

            await Promise.all([
                getBalance(),
                getTransactions()
            ]);

            addNotification(`${accountNumber} data fetched!`, 'success');
        }

        loadData();

    }, [accountNumber]);

    const exportCSV = () => {
        if (!accountNumber) return;

        try {
            addNotification("Sending exportCSV request...", 'job')
            statementService.exportCSV(accountNumber)
            addNotification("Statement exported!", 'success');
        } catch (err) {
            addNotification("Error exporting CSV: " + err, 'error');
        }
    }

    return (

        <div className="dashboard-container">

            <div className="dashboard-header">

                <h1>Account {accountNumber} dashboard</h1>

                <h1>Balance: {balance}</h1>

                <button className="button" onClick={exportCSV}>
                    {'Download CSV statement'}
                </button>

            </div>


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
                                <th>Currency</th>
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
                                    <td>{transaction.currency}</td>
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
    );
};

export default AccountDashboard;