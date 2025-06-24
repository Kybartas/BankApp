import { useNotifications } from "../hooks/useNotifications";
import "../styles/notifications.css";

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