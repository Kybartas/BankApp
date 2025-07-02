import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from "../../hooks/useAuth";
import './header.css';

const Header = () => {

    const { user, isAuthenticated, logout } = useAuth();

    const handleLogout = () => {
        logout();
    }

    return (
        <header className="header-container">

            <Link className="link" to="/"> BankApp demo </Link>

            <div className="header-right">
                {isAuthenticated && user ? (
                    <div className="user-info">
                        <span className="username">Welcome, {user.username}</span>
                        <button className="link logout-button" onClick={handleLogout}>
                            Logout
                        </button>
                    </div>
                ) : (
                    <Link className="link" to="/login">Login</Link>
                )}

                <a href="https://github.com/Kybartas/bankApp" className="link" target="_blank"
                   rel="noreferrer">
                    GitHub
                </a>
            </div>

        </header>
    )
}

export default Header;