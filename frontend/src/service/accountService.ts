import { Notify, Transaction } from "../types/types"
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

    getTransactions: async (accountNumber: string, notify?: Notify): Promise<Transaction[]> => {

        notify?.log(`Sending getTransactions request...`, `job`);

        const start = performance.now();
        const response = await fetch(`${API_BASE_URL}/bankApi/account/getTransactions?accountNumber=${accountNumber}`, {
            headers: getAuthHeaders()
        });
        const end = performance.now();

        if(!response.ok) {
            notify?.log(`Failed to getTransactions: ${ await response.text() }`, `error`);
            return [];
        }

        notify?.log(`Transactions fetched!: ${ Math.trunc(end - start) }ms`, `success`);

        return response.json();
    },

    getTransactionsByDate: async (accountNumber: string, from: Date, to: Date, notify?: Notify): Promise<Transaction[]> => {

        notify?.log(`Sending getTransactionsByDate request...`, `job`);

        const start = performance.now();
        const response = await fetch(`${API_BASE_URL}/bankApi/account/getTransactions?accountNumber=${accountNumber}&from=${from.toISOString().split('T')[0]}&to=${to.toISOString().split('T')[0]}`, {
            headers: getAuthHeaders()
        });
        const end = performance.now();

        if(!response.ok) {
            notify?.log(`Failed to getTransactionsByDate: ${ await response.text() }`, `error`);
            return [];
        }

        notify?.log(`Transactions fetched!: ${ Math.trunc(end - start) }ms`, `success`);

        return response.json();
    }
}