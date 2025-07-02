import { MakeLog, Transaction } from "../types"
import { getAuthHeaders, API_BASE_URL } from "./utils";

export const accountService = {

    getBalance: async (accountNumber: string): Promise<number> => {

        const response = await fetch(`${API_BASE_URL}/bankApi/account/getBalance?accountNumber=${accountNumber}`, {
            headers: getAuthHeaders()
        });

        if(!response.ok) {
            return 0;
        }
        return response.json();
    },

    getTransactions: async (accountNumber: string, makeLog?: MakeLog): Promise<Transaction[]> => {

        makeLog?.log(`info`, `Sending getTransactions request...`, `accountService`);

        const start = performance.now();
        const response = await fetch(`${API_BASE_URL}/bankApi/account/getTransactions?accountNumber=${accountNumber}`, {
            headers: getAuthHeaders()
        });
        const end = performance.now();

        if(!response.ok) {
            makeLog?.log(`error`, `Failed to getTransactions: ${ await response.text() }`, `accountService`);
            return [];
        }

        makeLog?.log(`success`, `Transactions fetched! ${ Math.trunc(end - start) }ms`, `accountService`);

        return response.json();
    },

    getTransactionsByDate: async (accountNumber: string, from: Date, to: Date, makeLog?: MakeLog): Promise<Transaction[]> => {

        makeLog?.log(`info`, `Sending getTransactions with dates request...`, `accountService`);

        const start = performance.now();
        const response = await fetch(`${API_BASE_URL}/bankApi/account/getTransactions?accountNumber=${accountNumber}&from=${from.toISOString().split('T')[0]}&to=${to.toISOString().split('T')[0]}`, {
            headers: getAuthHeaders()
        });
        const end = performance.now();

        if(!response.ok) {
            makeLog?.log(`error`, `Failed to getTransactions with dates: ${ await response.text() }`, `accountService`);
            return [];
        }

        makeLog?.log(`success`, `Transactions fetched! ${ Math.trunc(end - start) }ms`, `accountService`);

        return response.json();
    },

    makePayment: async (sender: string, recipient: string, amount: number, makeLog?: MakeLog): Promise<void> => {

        makeLog?.log(`info`, `Sending pay request...`, `accountService`);

        const start = performance.now();
        const response = await fetch(`${API_BASE_URL}/bankApi/account/pay?sender=${sender}&recipient=${recipient}&amount=${amount}`, {
            headers: getAuthHeaders()
        });
        const end = performance.now();

        if(!response.ok) {
            makeLog?.log(`error`, `Failed to transfer funds: ${ await response.text() }`, `accountService`);
        }

        makeLog?.log(`success`, `Funds transferred! ${ Math.trunc(end - start) }ms`, `accountService`);

    }
}