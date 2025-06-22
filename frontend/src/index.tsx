import React from 'react';
import ReactDOM from 'react-dom/client';
import {BrowserRouter, Route, Routes} from 'react-router-dom'
import { NotificationProvider, NotificationContainer } from './notification/Notifications';
import Header from "./header/Header";
import HomeDashboard from "./dashboard/HomeDashboard";
import AccountDashboard from "./dashboard/AccountDashboard";
import './index.css';

const root = ReactDOM.createRoot(
    document.getElementById('root') as HTMLElement
);

root.render(
    <NotificationProvider>
        <BrowserRouter>
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