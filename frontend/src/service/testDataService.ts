import { Notify, Account } from "../types/types"
import { getAuthHeaders, API_BASE_URL } from "./utils";

export const testDataService = {

    populateDB: async (notify?: Notify): Promise<string> => {

        notify?.log(`Sending populateDB request...`, `job`);

        const start = performance.now();
        const response = await fetch(`${API_BASE_URL}/bankApi/testData/populateDB`, {
            headers: getAuthHeaders()
        });
        const end = performance.now();

        if(!response.ok) {
            notify?.log(`Failed to populate db: ${ await response.text() }`, `error`);
            return "";
        }

        notify?.log(`Database populated! ${ Math.trunc(end - start) } ms`, `success`)
        return response.text();
    },

    getAccounts: async (notify?: Notify): Promise<Account[]> => {

        notify?.log(`Sending getAccounts request...`, `job`);

        const start = performance.now();
        const response = await fetch(`${API_BASE_URL}/bankApi/testData/getAllAccounts`, {
            headers: getAuthHeaders()
        });
        const end = performance.now();

        if(!response.ok) {
            notify?.log(`Failed to fetch accounts: ${ await response.text() }`, `error`);
            return [];
        }

        notify?.log(`Accounts fetched! ${ Math.trunc(end - start) } ms`, `success`)
        return response.json();
    },
}