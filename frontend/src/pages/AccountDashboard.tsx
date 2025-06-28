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

            const data = await accountService.getTransactions(accountNumber, { log: addNotification });
            setTransactions(data);
        };

        const getBalance = async () => {
            if (!accountNumber) return;

            const data = await accountService.getBalance(accountNumber);
            setBalance(data);
        };

        getTransactions();
        getBalance();

    }, [accountNumber, addNotification]);

    const exportCSV = async () => {
        if (!accountNumber) return;

        statementService.exportCSV(accountNumber, { log: addNotification });
    }

    const loadTransactionsByDate = async () => {

        if(!accountNumber || !fromDate || !toDate) {
            addNotification("Provide dates to load more transactions", "error");
            return;
        }

        const from = new Date(fromDate);
        const to = new Date(toDate);

        const newTransactionList
            = await accountService.getTransactionsByDate(accountNumber, from, to, { log: addNotification });
        setTransactions(newTransactionList);
    }

    return (

        <div className="dashboard-container">

            <div className="dashboard-header">
                <h1>Account {accountNumber} dashboard</h1>
            </div>

            <div className="information-container">

                <h2>Balance: {balance}</h2>

                <button className="button" onClick={exportCSV}>
                    Download CSV statement
                </button>

                <button className="button" onClick={() => setShowDateModal(true)}>
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
                                <th>Date</th>
                                <th>Beneficiary</th>
                                <th>Description</th>
                                <th>Amount</th>
                            </tr>
                        </thead>
                        <tbody>
                            {transactions.map((transaction) => (
                                <tr key={transaction.id}>
                                    <td>{transaction.date}</td>
                                    <td>{transaction.beneficiary}</td>
                                    <td>{transaction.description}</td>
                                    <td className={transaction.type === "K" ? "positive" : "negative"}>
                                        {transaction.type === "K" ? transaction.amount : "-" + transaction.amount}
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