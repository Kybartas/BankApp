import { Notify } from "../types/types"
import { getAuthHeaders, API_BASE_URL } from "./utils";

export const statementService = {

    exportCSV: async (accountNumber: string, notify?: Notify): Promise<void> => {

        notify?.log(`Sending exportCSV request...`, `job`);

        const start = performance.now();
        const response = await fetch(`${API_BASE_URL}/bankApi/statement/exportCSV?accountNumber=${accountNumber}`, {
            headers: getAuthHeaders()
        });
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

    importCSV: async (file: File, notify?: Notify): Promise<void> => {

        const formData = new FormData();
        formData.append("file", file);

        notify?.log(`Sending importCSV request...`, `job`);

        const start = performance.now();
        const response = await fetch(`${API_BASE_URL}/bankApi/statement/importCSV`, {
            method: "POST",
            headers: getAuthHeaders(),
            body: formData
        });
        const end = performance.now();

        if(!response.ok) {
            notify?.log(`Failed to import CSV: ${ await response.text() }`, `error`);
            return;
        }

        notify?.log(`CSV imported!: ${ Math.trunc(end - start) }ms`, `success`);
    }
}