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

interface Notify {
    log: (msg: string, type: 'job' | 'success' | 'error') => void;
}

export const testDataService = {

    populateDB : async (notify?: Notify): Promise<string> => {

        notify?.log(`Sending populateDB request...`, `job`);

        const start = performance.now();
        const response = await fetch(`${API_BASE_URL}/bankApi/testData/populateDB`);
        const end = performance.now();

        if(!response.ok) {
            notify?.log(`Failed to populate db: ${ await response.text() }`, `error`);
            return "";
        }

        notify?.log(`Database populated! ${ Math.trunc(end - start) } ms`, `success`)
        return response.text();
    },

    getAccounts : async (notify?: Notify): Promise<Account[]> => {

        notify?.log(`Sending getAccounts request...`, `job`);

        const start = performance.now();
        const response = await fetch(`${API_BASE_URL}/bankApi/testData/getAllAccounts`);
        const end = performance.now();

        if(!response.ok) {
            notify?.log(`Failed to fetch accounts: ${ await response.text() }`, `error`);
            return [];
        }

        notify?.log(`Accounts fetched! ${ Math.trunc(end - start) } ms`, `success`)
        return response.json();
    }
}

export const accountService = {

    getBalance : async (accountNumber: string): Promise<number> => {

        const response = await fetch(`${API_BASE_URL}/bankApi/account/getBalance?accountNumber=${accountNumber}`);

        if(!response.ok) {
            return 0;
        }
        return response.json();
    },

    getTransactions : async (accountNumber: string, notify?: Notify): Promise<Transaction[]> => {

        notify?.log(`Sending getTransactions request...`, `job`);

        const start = performance.now();
        const response = await fetch(`${API_BASE_URL}/bankApi/account/getTransactions?accountNumber=${accountNumber}`);
        const end = performance.now();

        if(!response.ok) {
            notify?.log(`Failed to getTransactions: ${ await response.text() }`, `error`);
            return [];
        }

        notify?.log(`Transactions fetched!: ${ Math.trunc(end - start) }ms`, `success`);

        return response.json();
    },

    getTransactionsByDate : async (accountNumber: string, from: Date, to: Date, notify?: Notify): Promise<Transaction[]> => {

        notify?.log(`Sending getTransactionsByDate request...`, `job`);

        const start = performance.now();
        const response = await fetch(`${API_BASE_URL}/bankApi/account/getTransactions?accountNumber=${accountNumber}&from=${from.toISOString().split('T')[0]}&to=${to.toISOString().split('T')[0]}`);
        const end = performance.now();

        if(!response.ok) {
            notify?.log(`Failed to getTransactionsByDate: ${ await response.text() }`, `error`);
            return [];
        }

        notify?.log(`Transactions fetched!: ${ Math.trunc(end - start) }ms`, `success`);

        return response.json();
    }
}

export const statementService = {

    exportCSV : async (accountNumber: string, notify?: Notify): Promise<void> => {

        notify?.log(`Sending exportCSV request...`, `job`);

        const start = performance.now();
        const response = await fetch(`${API_BASE_URL}/bankApi/statement/exportCSV?accountNumber=${accountNumber}`);
        const end = performance.now();

        if(!response.ok) {
            notify?.log(`Failed to export CSV: ${ await response.text() }`, `error`);
            return;
        }

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = accountNumber + "_statement.csv";
        a.click();

        notify?.log(`CSV exported!: ${ Math.trunc(end - start) }ms`, `success`);
    },

    importCSV : async (file: File, notify?: Notify): Promise<void> => {

        const formData = new FormData();
        formData.append("file", file);

        notify?.log(`Sending importCSV request...`, `job`);

        const start = performance.now();
        const response = await fetch(`${API_BASE_URL}/bankApi/statement/importCSV`, {method: "POST", body: formData});
        const end = performance.now();

        if(!response.ok) {
            notify?.log(`Failed to import CSV: ${ await response.text() }`, `error`);
            return;
        }

        notify?.log(`CSV imported!: ${ Math.trunc(end - start) }ms`, `success`);
    }
}