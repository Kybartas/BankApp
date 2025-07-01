import React, { useState, useCallback, ReactNode, createContext } from 'react';

type Notification = {
    id: number;
    message: string;
    type: 'success' | 'error' | 'job';
}

type NotificationContextType = {
    notifications: Notification[];
    addNotification: (message: string, type: Notification['type']) => void;
}

export const NotificationContext = createContext<NotificationContextType | undefined>(undefined);

export const NotificationProvider: React.FC<{ children: ReactNode }> = ({ children }) => {

    const [notifications, setNotifications] = useState<Notification[]>([]);

    const removeNotification = useCallback((id: number) => {
        setNotifications(prev => prev.filter(n => n.id !== id));
    }, []);

    const addNotification = useCallback((message: string, type: Notification['type']) => {
        const id = Date.now();
        const newNotification = { id, message, type };
        setNotifications(prev => [...prev, newNotification]);

        setTimeout(() => {
            removeNotification(id);
        }, 4000);
    }, [removeNotification]);

    return (
        <NotificationContext.Provider value={{ notifications, addNotification }}>
            {children}
        </NotificationContext.Provider>
    );
};