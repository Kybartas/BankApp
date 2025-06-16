import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import MainDashboard from './components/MainDashboard';
import AccountDashboard from './components/AccountDashboard';
import './App.css';

function App() {
    return (
        <div className="App">
            <Router>
                <Routes>
                    <Route path="/" element={<MainDashboard />} />
                    <Route path="/accountDashboard/:accountNumber" element={<AccountDashboard />} />
                </Routes>
            </Router>
        </div>
    );
}

export default App;