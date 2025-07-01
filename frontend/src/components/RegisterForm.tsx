import React, { useState } from 'react';
import { useAuth } from '../hooks/useAuth';
import { authService } from '../service/authService';
import { useNotifications } from '../hooks/useNotifications';
import '../styles/loginForm.css';

interface RegisterFormProps {
    onSuccess?: () => void;
    onSwitchToLogin?: () => void;
}

const RegisterForm: React.FC<RegisterFormProps> = ({ onSuccess, onSwitchToLogin }) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    const { login } = useAuth();
    const { addNotification } = useNotifications();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!username.trim() || !password.trim() || !confirmPassword.trim()) {
            addNotification('Please fill in all fields', 'error');
            return;
        }

        if (password !== confirmPassword) {
            addNotification('Passwords do not match', 'error');
            return;
        }

        if (password.length < 6) {
            addNotification('Password must be at least 6 characters long', 'error');
            return;
        }

        setIsLoading(true);

        try {

            const response = await authService.register({ username, password }, { log: addNotification });

            // Login through context
            login({ username: username }, response.token);
            onSuccess?.();
        } catch (error) {

        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="auth-card">
            <h2>Register</h2>
            <form onSubmit={handleSubmit} className="auth-form">
                <div className="form-group">
                    <label htmlFor="username">name</label>
                    <input
                        type="text"
                        id="username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        placeholder="Enter your username"
                        required
                        disabled={isLoading}
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="password">Password</label>
                    <input
                        type="password"
                        id="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Enter your password"
                        required
                        disabled={isLoading}
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="confirmPassword">Confirm Password</label>
                    <input
                        type="password"
                        id="confirmPassword"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        placeholder="Confirm your password"
                        required
                        disabled={isLoading}
                    />
                </div>
                <button
                    type="submit"
                    className="button primary"
                    disabled={isLoading}
                >
                    {isLoading ? 'Creating account...' : 'Register'}
                </button>
            </form>

            {onSwitchToLogin && (
                <div className="auth-switch">
                    <p>
                        Already have an account?{' '}
                        <button
                            type="button"
                            className="link-button"
                            onClick={onSwitchToLogin}
                            disabled={isLoading}
                        >
                            Login here
                        </button>
                    </p>
                </div>
            )}
        </div>
    );
};

export default RegisterForm;