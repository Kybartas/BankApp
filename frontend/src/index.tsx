import React from 'react';
import ReactDOM from 'react-dom/client';
import {BrowserRouter, Route, Routes} from 'react-router-dom'
import { NotificationProvider } from './context/NotificationContext';
import { NotificationContainer } from './components/NotificationContainer';
import Header from "./components/Header";
import HomeDashboard from "./pages/HomeDashboard";
import AccountDashboard from "./pages/AccountDashboard";
import AdminPane from "./components/AdminPane";
import './styles/index.css';

const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);

root.render(
    <NotificationProvider>
        <BrowserRouter>

            <AdminPane/>
            <div className="App">
                <Header/>
                <Routes>
                    <Route path="/" element={<HomeDashboard/>}/>
                    <Route path="/:accountNumber" element={<AccountDashboard/>}/>
                </Routes>
            </div>

        </BrowserRouter>
        <NotificationContainer/>
    </NotificationProvider>
);