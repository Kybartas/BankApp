// entity

export interface User {
    username: string;
}

export interface Account {
    id: string;
    accountNumber: string;
    balance: number;
}

export interface Transaction {
    id: string;
    accountNumber: string;
    date: string;
    beneficiary: string;
    description: string;
    amount: number;
    currency: string;
    type: string;
}

// dto

export interface LoginRequest {
    username: string;
    password: string;
}

export interface RegisterRequest {
    username: string;
    password: string;
}

export interface AuthResponse {
    token: string;
    username: string;
    message?: string;
}

// asd

export interface Log {
    id: number;
    timestamp: string;
    type: 'info' | 'success' | 'warning' | 'error';
    message: string;
    source: string;
    details?: string;
}

export interface MakeLog {
    log: (type: 'info' | 'success' | 'warning' | 'error', msg: string, source: string) => void
}