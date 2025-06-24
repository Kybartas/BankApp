import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { Transaction, accountService, statementService } from '../api';
import { useNotifications } from "../hooks/useNotifications";
import '../styles/dashboard.css';
import '../styles/modal.css';

const AccountDashboard = () => {

    const { accountNumber } = useParams<{ accountNumber: string }>();
    const [transactions, setTransactions] = useState<Transaction[]>([]);
    const [balance, setBalance] = useState<number>(0);

    const [fromDate, setFromDate] = useState<string>("");
    const [toDate, setToDate] = useState<string>("");
    const [showDateModal, setShowDateModal] = useState(false);

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

            addNotification(`Fetching data for ${accountNumber}...`, 'job');

            await Promise.all([
                getBalance(),
                getTransactions()
            ]);

            addNotification(`${accountNumber} data fetched!`, 'success');
        }

        loadData();

    }, [accountNumber, addNotification]);

    const exportCSV = async () => {
        if (!accountNumber) return;

        try {
            addNotification("Sending exportCSV request...", 'job')
            statementService.exportCSV(accountNumber)
            addNotification("Statement exported!", 'success');
        } catch (err) {
            addNotification("Error exporting CSV: " + err, 'error');
        }
    }

    const loadTransactionsByDate = async () => {
        if(!accountNumber || !fromDate || !toDate) {
            addNotification("Provide dates to load more transactions", "error");
            return;
        }

        try {
            const from = new Date(fromDate);
            const to = new Date(toDate);

            addNotification(`Fetching dated transactions list for ${accountNumber}...`, 'job');
            const newTransactionList = await accountService.getTransactionsByDate(accountNumber, from, to);
            setTransactions(newTransactionList);
            addNotification("Dated transactions fetched!", "success")
        } catch (err) {
            addNotification("Error fetching dated transactions: " + err, "error");
        }
    }

    return (

        <div className="dashboard-container">

            <div className="dashboard-header">

                <h1>Account {accountNumber} dashboard</h1>

                <h1>Balance: {balance}</h1>

                <button className="button" onClick={exportCSV}>
                    Download CSV statement
                </button>

                <button className="button" onClick={ () => setShowDateModal(true) }>
                    View more transactions
                </button>

            </div>


            {showDateModal && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>Select Date Range</h2>
                        <label>
                            From:
                            <input type="date" value={fromDate} onChange={(e) => setFromDate(e.target.value)} />
                        </label>
                        <label>
                            To:
                            <input type="date" value={toDate} onChange={(e) => setToDate(e.target.value)} />
                        </label>
                        <div className="modal-buttons">
                            <button className="button" onClick={loadTransactionsByDate}>Confirm</button>
                            <button className="button cancel" onClick={() => setShowDateModal(false)}>Cancel</button>
                        </div>
                    </div>
                </div>
            )}


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