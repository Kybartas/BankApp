import React from 'react';
import './App.css';
import AccountList from './components/AccountList';
import StatementList from "./components/StatementList";

function App() {
  return (
      <div className="App">
          <header className="App-header">
              <h1>Bank frontend</h1>
          </header>

          <main className={"App-content"}>
              <div className={"accounts"}>
                  <AccountList/>
              </div>
              <div className={"statements"}>
                  <StatementList/>
              </div>
          </main>
      </div>
  );
}

export default App;