import {LoginRequest, RegisterRequest, AuthResponse, Notify} from "../types/types";
import { API_BASE_URL } from "./utils";

export const authService = {

    login: async (credentials: LoginRequest, notify?: Notify): Promise<AuthResponse> => {

        notify?.log(`Sending login request...`, `job`);

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
            notify?.log(data.message || 'Login failed', `error`);
        }

        notify?.log(`Logged in!: ${ Math.trunc(end - start) }ms`, `success`);
        return data;
    },

    register: async (userData: RegisterRequest, notify?: Notify): Promise<AuthResponse> => {

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
            notify?.log(data.message || 'Register failed', `error`);
        }

        notify?.log(`Registered and logged in!: ${ Math.trunc(end - start) }ms`, `success`);
        return data;
    }
};