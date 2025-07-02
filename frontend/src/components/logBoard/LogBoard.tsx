import React from 'react';
import { useLogs } from '../../hooks/useLogs';
import './logBoard.css';

const LogBoard: React.FC = () => {
    const { logs } = useLogs();

    const getLogColor = (level: string) => {
        switch (level) {
            case 'info':
                return 'var(--color-info)';
            case 'success':
                return 'var(--color-success)';
            case 'warning':
                return 'var(--color-warning)';
            case 'error':
                return 'var(--color-error)';
            default:
                return 'var(--color-info)';
        }
    };

    return (
        <div className="log-container">

            <h1>Whats happening?</h1>


            <div className="log-entries">

                {logs.map((log) => (

                    <div key={log.id} className="log-entry" style={{ borderColor: getLogColor(log.type) }}>

                        <div className="log-meta">

                            <span className="timestamp">{log.timestamp}</span>
                            <span className="source">{log.source}</span>

                        </div>

                        <div className="log-message">{log.message}</div>

                    </div>

                ))}

            </div>

        </div>
    );
};

export default LogBoard;