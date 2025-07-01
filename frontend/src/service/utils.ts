export const API_BASE_URL = process.env.REACT_APP_API_URL;

// Helper function to get user headers
export const getAuthHeaders = (): Record<string, string> => {
    const token = sessionStorage.getItem('token');
    return token ? { 'Authorization': `Bearer ${token}` } : {};
};

// Helper function to get headers with content type
// const getHeadersWithAuth = (): Record<string, string> => {
//     const token = sessionStorage.getItem('token');
//     const headers: Record<string, string> = { 'Content-Type': 'application/json' };
//     if (token) {
//         headers['Authorization'] = `Bearer ${token}`;
//     }
//     return headers;
// };