import {MakeLog} from "../types"
import { getAuthHeaders, API_BASE_URL } from "./utils";

export const statementService = {

    exportCSV: async (accountNumber: string, makeLog?: MakeLog): Promise<void> => {

        makeLog?.log(`info`, `Sending exportCSV request...`, `statementService`);

        const start = performance.now();
        const response = await fetch(`${API_BASE_URL}/bankApi/statement/exportCSV?accountNumber=${accountNumber}`, {
            headers: getAuthHeaders()
        });
        const end = performance.now();

        if(!response.ok) {
            makeLog?.log(`error`, `Failed to exportCSV: ${ await response.text() }`, `statementService`);
            return;
        }

        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = accountNumber + "_statement.csv";
        a.click();

        makeLog?.log(`success`, `CSV exported! ${ Math.trunc(end - start) }ms`, `statementService`);
    }
}