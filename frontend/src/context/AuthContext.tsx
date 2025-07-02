import React, {useState, ReactNode, createContext, useEffect} from 'react';
import { User } from "../types"

interface AuthContextType {
    user: User | null;
    token: string | null;
    login: (userData: User, token: string) => void;
    logout: () => void;
    isAuthenticated: boolean;
    loading: boolean;
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined);
interface AuthProviderProps {
    children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {

    const [user, setUser] = useState<User | null>(null);
    const [token, setToken] = useState<string | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {

        const loadUser = () => {
            try {
                const storedUser = sessionStorage.getItem('user');
                const storedToken = sessionStorage.getItem('token');

                if (storedUser && storedToken) {
                    setUser(JSON.parse(storedUser));
                    setToken(storedToken);
                }
            } catch (error) {
                console.error("Error loading user from storage: ", error);
                sessionStorage.removeItem('user');
                sessionStorage.removeItem('token');
            } finally {
                setLoading(false);
            }
        };

        loadUser();
    }, []);

    const login = (userData: User, authToken: string) => {
        setUser(userData);
        setToken(authToken);

        sessionStorage.setItem('user', JSON.stringify(userData));
        sessionStorage.setItem('token', authToken);
    };

    const logout = () => {
        setUser(null);
        setToken(null);
        sessionStorage.removeItem('user');
        sessionStorage.removeItem('token');
    };

    const value: AuthContextType = {
        user,
        token,
        login,
        logout,
        isAuthenticated: !!user && !!token,
        loading
    };

    return (
        <AuthContext.Provider value = {value}>
            { children }
        </AuthContext.Provider>
    )
}