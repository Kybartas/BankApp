import {LoginRequest, RegisterRequest, AuthResponse, MakeLog} from "../types";
import { API_BASE_URL } from "./utils";

export const authService = {

    login: async (credentials: LoginRequest, makeLog?: MakeLog): Promise<AuthResponse> => {

        makeLog?.log(`info`, `Sending login request...`, `authService`);

        const start = performance.now();

        const response = await fetch(`${API_BASE_URL}/bankApi/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(credentials),
        });
        const data = await response.json();

        const end = performance.now();

        if (!response.ok) {
            makeLog?.log(`error`, `Failed to login: ${ await response.text() }`, `authService`);
        }

        makeLog?.log(`success`, `Logged in! ${ Math.trunc(end - start) }ms`, `authService`);
        return data;
    },

    register: async (userData: RegisterRequest, makeLog?: MakeLog): Promise<AuthResponse> => {

        makeLog?.log(`info`, `Sending register request...`, `authService`);

        const start = performance.now();

        const response = await fetch(`${API_BASE_URL}/bankApi/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(userData),
        });
        const data = await response.json();

        const end = performance.now();

        if (!response.ok) {
            makeLog?.log(`error`, `Failed to register: ${ await response.text() }`, `authService`);
        }

        makeLog?.log(`success`, `Registered! ${ Math.trunc(end - start) }ms`, `authService`);
        return data;
    }
};