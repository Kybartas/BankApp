import React, { ReactNode } from 'react';
import { useAuth } from '../hooks/useAuth';
import LoadingDots from './LoadingDots';

interface ProtectedRouteProps {
    children: ReactNode;
    fallback?: ReactNode;
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = (
    {children, fallback = <div>Please log in to access this page.</div>}) => {

    const { isAuthenticated, loading } = useAuth();

    if (loading) {
        return <LoadingDots />;
    }

    if (!isAuthenticated) {
        return <>{fallback}</>;
    }

    return <>{children}</>;
};

export default ProtectedRoute;