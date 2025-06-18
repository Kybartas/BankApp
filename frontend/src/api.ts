const API_BASE_URL = process.env.REACT_APP_API_URL;

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

export const testDataService = {

    populateDB : async (): Promise<string> => {
        const response = await fetch(`${API_BASE_URL}/bankApi/testData/populateDB`);
        if(!response.ok) {
            throw new Error('Failed to populate database');
        }
        return response.text();
    },

    getAccounts : async (): Promise<Account[]> => {
        const response = await fetch(`${API_BASE_URL}/bankApi/testData/getAllAccounts`);
        if(!response.ok) {
            throw new Error('Failed to fetch accounts');
        }
        return response.json();
    },

    getBalance : async (accountNumber: string): Promise<number> => {
        const response = await fetch(`${API_BASE_URL}/bankApi/account/getBalance?accountNumber=${accountNumber}`);
        if(!response.ok) {
            throw new Error('Failed to fetch balance');
        }
        return response.json();
    },

    getTransactions : async (accountNumber: string): Promise<Transaction[]> => {
        const response = await fetch(`${API_BASE_URL}/bankApi/account/getTransactions?accountNumber=${accountNumber}`);
        if(!response.ok) {
            throw new Error('Failed to fetch transactions');
        }
        return response.json();
    },
}

export const statementService = {

    exportCSV : async (accountNumber: string): Promise<void> => {
        const response = await fetch(`${API_BASE_URL}/bankApi/statement/exportCSV?accountNumber=${accountNumber}`);
        if(!response.ok) {
            throw new Error("Failed to export CSV: " + await response.text());
        }

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = accountNumber + "_statement.csv";
        a.click();
    }
}