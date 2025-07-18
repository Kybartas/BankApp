import React, { useState } from 'react'
import { useAuth } from '../../../hooks/useAuth';
import { authService } from '../../../service/authService';
import { useLogs } from '../../../hooks/useLogs';
import './login.css';

interface LoginFormProps {
    onSuccess?: () => void;
    onSwitchToRegister?: () => void;
}

const LoginForm: React.FC<LoginFormProps> = ({ onSuccess, onSwitchToRegister }) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [isLoading, setIsLoading] = useState(false);

    const { login } = useAuth();
    const { addLog } = useLogs();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!username.trim() || !password.trim()) {
            addLog(`warning`, `Please fill in all fields`, `login`);
            return;
        }

        setIsLoading(true);

        try {
            const response = await authService.login({ username, password }, { log: addLog });

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
            <h1>Login</h1>

            <form onSubmit={handleSubmit} className="auth-form">
                <div className="form-group">
                    <label htmlFor="username">Username</label>
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

                <button
                    type="submit"
                    className="button primary"
                    disabled={isLoading}
                >
                    {isLoading ? 'Logging in...' : 'Login'}
                </button>
            </form>

            {onSwitchToRegister && (
                <div className="auth-switch">
                    <p>
                        Don't have an account?{' '}
                        <button
                            type="button"
                            className="link-button"
                            onClick={onSwitchToRegister}
                            disabled={isLoading}
                        >
                            Register here
                        </button>
                    </p>
                </div>
            )}
        </div>
    );
};

export default LoginForm;