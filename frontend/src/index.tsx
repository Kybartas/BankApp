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
import Login from "./pages/Login";
import {AuthProvider} from "./context/AuthContext";
import ProtectedRoute from "./components/ProtectedRoute";

const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);

root.render(
    <NotificationProvider>
        <AuthProvider>
            <BrowserRouter>

                <Header/>
                <div className = "App">
                    <AdminPane/>
                    <Routes>

                        <Route path = "/" element = {
                            <ProtectedRoute>
                                <HomeDashboard/>
                            </ProtectedRoute>
                        }/>
                        <Route path = "/login" element = { <Login/> }/>
                        <Route path = "/:accountNumber" element = {
                            <ProtectedRoute>
                                <AccountDashboard/>
                            </ProtectedRoute>
                        }/>

                        {/*<Route path = "/" element = { <HomeDashboard/> }/>*/}
                        {/*<Route path = "/login" element = { <Login/> }/>*/}
                        {/*<Route path = "/:accountNumber" element = { <AccountDashboard/> }/>*/}
                    </Routes>
                </div>

            </BrowserRouter>
        </AuthProvider>
        <NotificationContainer/>
    </NotificationProvider>
);