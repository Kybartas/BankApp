import React, { createContext, useContext, useState, ReactNode } from 'react';
import './notifications.css'

type Notification = {
    id: number;
    message: string;
    type: 'success' | 'error' | 'job';
}

type NotificationContextType = {
    notifications: Notification[];
    addNotification: (message: string, type: Notification['type']) => void;
}

const NotificationContext = createContext<NotificationContextType | undefined>(undefined);

export const useNotifications = () => {
    const context = useContext(NotificationContext);
    if (!context) {
        throw new Error('useNotifications must be used within a NotificationProvider');
    }
    return context;
};

export const NotificationProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
    const [notifications, setNotifications] = useState<Notification[]>([]);

    const addNotification = (message: string, type: Notification['type']) => {
        const id = Date.now();
        const newNotification = { id, message, type };
        setNotifications(prev => [...prev, newNotification]);

        setTimeout(() => {
            removeNotification(id);
        }, 4000);
    };

    const removeNotification = (id: number) => {
        setNotifications(prev => prev.filter(n => n.id !== id));
    };

    return (
        <NotificationContext.Provider value={{ notifications, addNotification }}>
            {children}
        </NotificationContext.Provider>
    );
};

export const NotificationContainer: React.FC = () => {
    const { notifications } = useNotifications();


    return (
        <div className="notification-container">

            {notifications.map(notification => (
                <div
                    key={notification.id}
                    className={`notification notification-${notification.type}`}
                >
                    <span className="notification-message">
                        {notification.message}
                    </span>
                </div>
            ))}

        </div>
    );
};