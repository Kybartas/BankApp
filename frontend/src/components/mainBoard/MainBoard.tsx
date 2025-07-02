import React from "react";
import { Routes, Route } from "react-router-dom";
import UserDashboard from "./userDashboard/UserDashboard";
import AccountDashboard from "./accountDashboard/AccountDashboard";
import Login from "./login/Login";
import ProtectedRoute from "./ProtectedRoute";

const MainBoard = () => (

    <Routes>
        <Route path = "/login" element = { <Login/> }/>
        <Route path="/*" element={
            <ProtectedRoute>
                <Routes>
                    <Route path="/" element={<UserDashboard />} />
                    <Route path="/:accountNumber" element={<AccountDashboard />} />
                </Routes>
            </ProtectedRoute>
        }/>
    </Routes>
);

export default MainBoard;