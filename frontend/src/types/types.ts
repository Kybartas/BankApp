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

export interface Notify {
    log: (msg: string, type: 'job' | 'success' | 'error') => void;
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