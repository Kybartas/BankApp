import React from 'react';
import './App.css';
import AccountList from './components/AccountList';

function App() {
  return (
    <div className="App">
      <header className="App-header">

        <h1>Bank frontend</h1>

        <main>
            <AccountList/>
        </main>

      </header>
    </div>
  );
}

export default App;