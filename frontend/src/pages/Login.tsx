import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import LoginForm from '../components/LoginForm';
import RegisterForm from '../components/RegisterForm';

const Login = () => {
    const [isLogin, setIsLogin] = useState(true);
    const { isAuthenticated } = useAuth();
    const navigate = useNavigate();

    // Redirect if already authenticated
    useEffect(() => {
        if (isAuthenticated) {
            navigate('/');
        }
    }, [isAuthenticated, navigate]);

    // Don't render anything if already authenticated
    if (isAuthenticated) {
        return null;
    }

    const handleAuthSuccess = () => {
        navigate('/');
    };

    const switchToRegister = () => {
        setIsLogin(false);
    };

    const switchToLogin = () => {
        setIsLogin(true);
    };

    return (
        <div className="dashboard-container">
            {isLogin ? (
                <LoginForm 
                    onSuccess={handleAuthSuccess}
                    onSwitchToRegister={switchToRegister}
                />
            ) : (
                <RegisterForm 
                    onSuccess={handleAuthSuccess}
                    onSwitchToLogin={switchToLogin}
                />
            )}
        </div>
    );
};

export default Login;