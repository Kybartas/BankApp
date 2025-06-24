import React from 'react';
import { Link } from 'react-router-dom';
import '../styles/header.css';

const Header = () => {

    return (
        <header className="header-container">

            <Link className="link" to="/"> BankApp demo </Link>

            <a href="https://github.com/Kybartas/bankApp" className="link" target="_blank"
               rel="noreferrer">
                GitHub
            </a>

        </header>
    )
}

export default Header;