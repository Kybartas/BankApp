import { Account, MakeLog } from "../types";
import { getAuthHeaders, API_BASE_URL } from "./utils";


export const userService = {

    openNewAccount: async (name: string, makeLog?: MakeLog): Promise<void> => {

        makeLog?.log(`info`, `Sending openAccount request...`, `userService`);

        const start = performance.now();
        const response = await fetch(`${API_BASE_URL}/bankApi/user/openAccount?name=${name}`, {
            headers: getAuthHeaders()
        });
        const end = performance.now();

        if(!response.ok) {
            makeLog?.log(`error`, `Failed to openAccount: ${ await response.text() }`, `userService`);
            return;
        }

        makeLog?.log(`success`, `Account opened! ${ Math.trunc(end - start) }ms`, `accountService`);
    },

    getAccounts: async (name: string, makeLog?: MakeLog): Promise<Account[]> => {

        makeLog?.log(`info`, `Sending getAccounts request...`, `userService`);

        const start = performance.now();
        const response = await fetch(`${API_BASE_URL}/bankApi/user/getAccounts?name=${name}`, {
            headers: getAuthHeaders()
        });
        const end = performance.now();

        if(!response.ok) {
            makeLog?.log(`error`, `Failed to getAccounts: ${ await response.text() }`, `userService`);
            return [];
        }

        makeLog?.log(`success`, `Accounts fetched! ${ Math.trunc(end - start) }ms`, `accountService`);
        return response.json();
    }
}