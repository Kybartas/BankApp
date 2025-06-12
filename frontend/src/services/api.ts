const API_BASE_URL = process.env.REACT_APP_API_URL;

export interface Account {
    accountNumber: string;
    balance: number;
}

export interface Statement {
    Id: string;
    accountNumber: string;
    date: string;
    beneficiary: string;
    description: string;
    amount: number;
    currency: string;
    type: string;
}

export const accountService = {

    getAccounts : async (): Promise<Account[]> => {
        const response = await fetch(`${API_BASE_URL}/api/accounts/getAccounts`);
        if(!response.ok) {
            throw new Error('failed to fetch accounts');
        }
        return response.json();
    },

    deleteAccounts : async (): Promise<string> => {
        const response = await fetch(`${API_BASE_URL}/api/accounts/deleteAccounts`, { method: 'DELETE' });
        if(!response.ok) {
            throw new Error('failed to delete accounts');
        }
        return response.text();
    }
}

export const statementService = {

    getStatements :async (): Promise<Statement[]> => {
        const response = await fetch(`${API_BASE_URL}/api/statements/getStatements`);
        if(!response.ok) {
            throw new Error('failed to fetch statements');
        }
        return response.json();
    },

    populateDB : async (): Promise<string> => {
        const response = await fetch(`${API_BASE_URL}/api/statements/populateDB`);
        if(!response.ok) {
            throw new Error('failed to populate database');
        }
        return response.text();
    },

    deleteStatements : async (): Promise<string> => {
        const response = await fetch(`${API_BASE_URL}/api/statements/deleteStatements`, { method: 'DELETE' });
        if(!response.ok) {
            throw new Error('failed to delete statements');
        }
        return response.text();
    }
}