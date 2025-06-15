import React, {useState} from 'react';
import './App.css';
import {Account, accountService, Statement, statementService} from "./api";

interface LogEntry {
    timeStamp: string;
    text: string;
}

function App() {

    const [statements, setStatements] = useState<Statement[]>([]);
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
            const result = await statementService.populateDB();
            addLog("API response: " + result);
            await getAccounts();
            await getStatements();
        } catch (err) {
            addLog("Error populating database!:" + err)
        }
    };

    const getAccounts = async () => {
        try {
            addLog("Sending getAccounts request...");
            const data = await accountService.getAccounts();
            addLog("Accounts fetched!");
            setAccounts(data);
        } catch (err) {
            addLog('Error fetching accounts:' + err);
        }
    };

    const getStatements = async () => {
        try {
            addLog("Sending getStatements request...");
            const data = await statementService.getStatements();
            addLog("Statements fetched!");
            setStatements(data);
        } catch (err) {
            addLog('Error fetching statements:' + err);
        }
    };

    const handleDeleteAccounts = async () => {
        try {
            addLog("Sending deleteAccounts request...");
            const result = await accountService.deleteAccounts();
            addLog("API response: " + result);
            await getAccounts();
        } catch (err) {
            console.error('Error deleting accounts:', err);
        }
    };

    const handleDeleteStatements = async () => {
        try {
            addLog("Sending deleteStatements request...");
            const result = await statementService.deleteStatements();
            addLog("API response: " + result);
            await getStatements()
        } catch (err) {
            addLog('Error deleting statements:' + err);
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

                  <button className="button" onClick={handleDeleteAccounts}>
                          {'Delete accounts'}
                  </button>

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
                                  <tr key={account.accountNumber}>
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

                  <button className="button" onClick={handleDeleteStatements}>
                      {'Delete statements'}
                  </button>

                  {statements.length === 0 ? (<p>No statements found</p>) : (
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
                              {statements.map((statement) => (
                                  <tr key={statement.accountNumber}>
                                      <td>{statement.accountNumber}</td>
                                      <td>{statement.date}</td>
                                      <td>{statement.amount}</td>
                                      <td className={(statement.type === "K" ? "positive" : "negative")}>
                                          {statement.type}
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
                      {'Populate Demo Data'}
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