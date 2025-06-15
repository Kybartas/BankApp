import React, {useState} from 'react';
import './App.css';
import {Account, Transaction, testDataService} from "./api";

interface LogEntry {
    timeStamp: string;
    text: string;
}

function App() {

    const [transactions, setTransactions] = useState<Transaction[]>([]);
    const [accounts, setAccounts] = useState<Account[]>([]);
    const [logEntries, setLogEntries] = useState<LogEntry[]>([]);

    const addLog = (text: string) => {
        const newLog: LogEntry = {
            timeStamp: new Date().toLocaleTimeString(),
            text: text
        };
        setLogEntries(oldLog => [newLog, ...oldLog]);
    };

    const handlePopulateDB = async () => {
        try {
            addLog("Sending populate db request...");
            const result = await testDataService.populateDB();
            addLog("API response: " + result);
            await getAccounts();
            await getTransactions();
        } catch (err) {
            addLog("Error populating database: " + err)
        }
    };

    const getAccounts = async () => {
        try {
            addLog("Sending getAllAccounts request...");
            const data = await testDataService.getAccounts();
            addLog("Accounts fetched!");
            setAccounts(data);
        } catch (err) {
            addLog('Error fetching accounts: ' + err);
        }
    };

    const getTransactions = async () => {
        try {
            addLog("Sending getAllTransactions request...");
            const data = await testDataService.getTransactions();
            addLog("Statements fetched!");
            setTransactions(data);
        } catch (err) {
            addLog('Error fetching transactions:' + err);
        }
    };

    const handleWipeDatabase = async () => {
        try {
            addLog("Sending wipeDatabase request...");
            const result = await testDataService.wipeDatabase();
            addLog("API response: " + result);
            await getAccounts();
            await getTransactions();
        } catch (err) {
            console.error('Error wiping database:', err);
        }
    };

    const clearLogs = () => {
        setLogEntries([])
    }

  return (
      <div className={"App"}>

          <header className={"App-header"}>

              <a href="https://github.com/Kybartas/juniorHomework" className="github-link" target="_blank"
              rel={"noreferrer"}>
                  GitHub
              </a>

              <h1>Bank frontend</h1>
          </header>

          <main className={"App-content"}>


              <div className={"accounts"}>

                  <h2>Accounts</h2>

                  {accounts.length === 0 ? (<p>No accounts found</p>) : (
                      <div className={"data-container"}>
                          <table className="data-table">
                              <thead>
                              <tr>
                                  <th>Account Number</th>
                                  <th>Balance</th>
                              </tr>
                              </thead>
                              <tbody>
                              {accounts.map((account) => (
                                  <tr key={account.id}>
                                      <td>{account.accountNumber}</td>
                                      <td className={(account.balance >= 0 ? "positive" : "negative")}>
                                          {account.balance}
                                      </td>
                                  </tr>
                              ))}
                              </tbody>
                          </table>
                      </div>
                  )}
              </div>


              <div className={"statements"}>

                  <h2>Statements</h2>

                  {transactions.length === 0 ? (<p>No statements found</p>) : (
                      <div className={"data-container"}>
                          <table className={"data-table"}>
                              <thead>
                              <tr>
                                  <th>Account Number</th>
                                  <th>Date</th>
                                  <th>Amount</th>
                                  <th>Type</th>
                              </tr>
                              </thead>
                              <tbody>
                              {transactions.map((transaction) => (
                                  <tr key={transaction.id}>
                                      <td>{transaction.accountNumber}</td>
                                      <td>{transaction.date}</td>
                                      <td>{transaction.amount}</td>
                                      <td className={(transaction.type === "K" ? "positive" : "negative")}>
                                          {transaction.type}
                                      </td>
                                  </tr>
                              ))}
                              </tbody>
                          </table>
                      </div>
                  )}
              </div>


              <div className={"logBox"}>

                  <h2>Log</h2>

                  <button className="button" onClick={handlePopulateDB}>
                      {'Populate database'}
                  </button>
                  <button className="button" onClick={handleWipeDatabase}>
                      {'Wipe database'}
                  </button>
                  <button className={"button"} onClick={clearLogs}>
                      {"Clear logs"}
                  </button>

                  {logEntries.length === 0 ? (<p>No logs</p>) : (
                      <div className={"data-container"}>
                          {logEntries.map((entry) => (
                              <div className={"log"}>
                                  <span className={"log-timestamp"}> {entry.timeStamp} </span>
                                  <span className={"log-text"}> {entry.text} </span>
                              </div>
                          ))}
                      </div>
                  )}

              </div>
          </main>
      </div>
  );
}

export default App;