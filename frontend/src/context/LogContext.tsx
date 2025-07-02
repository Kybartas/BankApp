import React, { useState, useCallback, ReactNode, createContext } from 'react';
import { Log } from "../types"

interface LogContextType {
    logs: Log[];
    addLog: (type: Log['type'], message: string, source: string) => void;
    clearLogs: () => void;
    setMaxEntries: (max: number) => void;
}

export const LogContext = createContext<LogContextType | undefined>(undefined);

export const LogProvider: React.FC<{ children: ReactNode }> = ({ children }) => {

    const [logs, setLogs] = useState<Log[]>([]);
    const [maxEntries, setMaxEntries] = useState<number>(100);

    const addLog = useCallback((type: Log['type'], message: string, source: string) => {

        const newLog: Log = {
            id: Date.now(),
            timestamp: new Date().toLocaleTimeString(),
            type,
            message,
            source
        }

        setLogs(prev => [newLog, ...prev].slice(-maxEntries));
    }, []);


    const clearLogs = () => {
        setLogs([]);
    }

    return (
        <LogContext.Provider value={{ logs, addLog, clearLogs, setMaxEntries }}>
            { children }
        </LogContext.Provider>
    );
};