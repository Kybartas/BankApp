import React from 'react';
import ReactDOM from 'react-dom/client';
import {LogProvider} from './context/LogContext';
import Header from "./components/header/Header";
import LogBoard from "./components/logBoard/LogBoard";
import './styles/index.css';
import {AuthProvider} from "./context/AuthContext";
import MainBoard from "./components/mainBoard/MainBoard";
import {BrowserRouter} from "react-router-dom";

const root = ReactDOM.createRoot(
  document.getElementById('root') as HTMLElement
);

root.render(
    <BrowserRouter>
        <AuthProvider>
            <LogProvider>

                <Header/>
                <div className = "App">
                    <LogBoard/>
                    <MainBoard />
                </div>

            </LogProvider>
        </AuthProvider>
    </BrowserRouter>
);