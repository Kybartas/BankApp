import React, { useState } from 'react';
import { useAuth } from '../../../hooks/useAuth';
import { authService } from '../../../service/authService';
import { useLogs } from '../../../hooks/useLogs';
import './login.css';

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
    const { addLog } = useLogs();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!username.trim() || !password.trim() || !confirmPassword.trim()) {
            addLog(`warning`, `Please fill in all fields`, `register`);
            return;
        }

        if (password !== confirmPassword) {
            addLog(`warning`, `Passwords do not match`, `register`);
            return;
        }

        if (password.length < 6) {
            addLog(`warning`, `Password must be at least 6 characters long`, `register`);
            return;
        }

        setIsLoading(true);

        try {

            const response = await authService.register({ username, password }, { log: addLog });

            // Login through context
            login({ username: username }, response.token);
            onSuccess?.();
        } catch (error) {

        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div>
            <h1>Register</h1>

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