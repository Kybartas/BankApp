import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';

import { accountService } from "../../../service/accountService";
import { statementService } from "../../../service/statementService";
import { Transaction } from "../../../types";

import { useLogs } from "../../../hooks/useLogs";
import '../mainDashboard.css';
import './modal.css';
import '../../../styles/base.css';
import LoadingDots from "../../common/loadingDots/LoadingDots";

const AccountDashboard = () => {

    const { accountNumber } = useParams<{ accountNumber: string }>();
    const [transactions, setTransactions] = useState<Transaction[]>([]);
    const [balance, setBalance] = useState<number>(0);

    const [fromDate, setFromDate] = useState<string>("");
    const [toDate, setToDate] = useState<string>("");
    const [showDateModal, setShowDateModal] = useState(false);
    const [showPaymentModal, setShowPaymentModal] = useState(false);
    const [loading, setLoading] = useState(false);
    const [recipientAccount, setRecipientAccount] = useState("");
    const [amount, setAmount] = useState("");

    const { addLog } = useLogs();

    useEffect(() => {
        const getTransactions = async () => {
            if (!accountNumber) return;

            const data = await accountService.getTransactions(accountNumber, { log: addLog });
            setTransactions(data);
        };

        const getBalance = async () => {
            if (!accountNumber) return;

            const data = await accountService.getBalance(accountNumber);
            setBalance(data);
        };

        setLoading(true);
        getTransactions();
        getBalance();
        setLoading(false);
    }, [accountNumber, addLog]);

    const exportCSV = async () => {
        if (!accountNumber) return;
        await statementService.exportCSV(accountNumber, { log: addLog });
    };

    const loadTransactionsByDate = async () => {
        if (!accountNumber || !fromDate || !toDate) {
            addLog(`warning`, `Provide a date range to load more transactions`, `loadTransactionsByDate`);
            return;
        }

        setShowDateModal(false);
        const from = new Date(fromDate);
        const to = new Date(toDate);

        const newTransactionList = await accountService.getTransactionsByDate(
            accountNumber, 
            from, 
            to, 
            { log: addLog }
        );
        setTransactions(newTransactionList);
    };

    const handlePayment = async () => {
        if (!accountNumber || !recipientAccount || !amount) {
            addLog(`warning`, `Fill in all fields`, `handlePayment`);
            return;
        }
        setShowPaymentModal(false);
        setLoading(true);
        await accountService.makePayment(accountNumber, recipientAccount, parseFloat(amount), { log: addLog });
        // Refresh transactions and balance
        await Promise.all([
            accountService.getTransactions(accountNumber, { log: addLog }),
            accountService.getBalance(accountNumber)
        ]).then(([transactions, balance]) => {
            setTransactions(transactions);
            setBalance(balance);
        });
        setLoading(false);
    };

    return (

        <div className="dashboard-container">

            <h1>{accountNumber} dashboard</h1>

            <div className="information-container">

                <h2>Balance: {balance}</h2>

                <button className="button" onClick={() => setShowPaymentModal(true)}>
                    Transfer funds
                </button>

                <button className="button" onClick={exportCSV}>
                    Download CSV statement
                </button>

                <button className="button" onClick={() => setShowDateModal(true)}>
                    View more transactions
                </button>

            </div>

            {showPaymentModal && (
                <div className="modal-overlay">

                    <h1>Transfer funds</h1>

                    <div className="modal-content">
                        
                            <label>
                                Recipient Account:
                                <input type="text" value={recipientAccount} onChange={(e) => setRecipientAccount(e.target.value)} placeholder="LT123"/>
                            </label>

                            <label>
                                Amount:
                                <input type="number" value={amount} onChange={(e) => setAmount(e.target.value)}/>
                            </label>

                        <div className="modal-buttons">
                            <button className="button" onClick={() => setShowPaymentModal(false)}>
                                Cancel
                            </button>

                            <button className="button" onClick={handlePayment}>
                                Transfer
                            </button>
                        </div>                        
                        
                    </div>
                </div>
            )}

            {showDateModal && (
                <div className="modal-overlay">

                    <h1>View more transactions</h1>

                    <div className="modal-content">

                        <label>
                            From:
                            <input type="date" value={fromDate} onChange={(e) => setFromDate(e.target.value)}/>
                        </label>

                        <label>
                            To:
                            <input type="date" value={toDate} onChange={(e) => setToDate(e.target.value)}/>
                        </label>

                        <div className="modal-buttons">
                            <button className="button" onClick={() => setShowDateModal(false)}>
                                Cancel
                            </button>
                            
                            <button className="button" onClick={loadTransactionsByDate}>
                                Confirm
                            </button>
                        </div>
                        
                    </div>
                </div>
            )}


            {loading ? (<LoadingDots/>) : transactions.length === 0 ? (<p>No transactions found</p>) :(

                <div className="data-container">

                    <table className="data-table">
                        <thead>
                            <tr>
                                <th>Date</th>
                                <th>Sender/Recipient</th>
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