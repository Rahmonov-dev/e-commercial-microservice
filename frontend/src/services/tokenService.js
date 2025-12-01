const ACCESS_TOKEN_KEY = 'accessToken';
const REFRESH_TOKEN_KEY = 'refreshToken';
const USER_KEY = 'user';

export const tokenService = {
  // Save tokens
  setTokens: (accessToken, refreshToken) => {
    if (accessToken) {
      localStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
    }
    if (refreshToken) {
      localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
    }
  },

  // Get access token
  getAccessToken: () => {
    return localStorage.getItem(ACCESS_TOKEN_KEY);
  },

  // Get refresh token
  getRefreshToken: () => {
    return localStorage.getItem(REFRESH_TOKEN_KEY);
  },

  // Save user data
  setUser: (userData) => {
    if (userData) {
      localStorage.setItem(USER_KEY, JSON.stringify(userData));
    }
  },

  // Get user data
  getUser: () => {
    const userData = localStorage.getItem(USER_KEY);
    return userData ? JSON.parse(userData) : null;
  },

  // Clear all tokens and user data
  clearTokens: () => {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
  },

  // Check if token is expired
  isTokenExpired: (token) => {
    if (!token) return true;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const expirationTime = payload.exp * 1000; // Convert to milliseconds
      return Date.now() >= expirationTime;
    } catch (error) {
      console.error('Error checking token expiration:', error);
      return true;
    }
  },

  // Get token expiration time
  getTokenExpiration: (token) => {
    if (!token) return null;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.exp * 1000; // Convert to milliseconds
    } catch (error) {
      console.error('Error getting token expiration:', error);
      return null;
    }
  },

  // Get user role from token
  getUserRole: () => {
    const token = tokenService.getAccessToken();
    if (!token) return null;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      // Try different possible role field names
      return payload.role || payload.authorities?.[0] || payload.authority || null;
    } catch (error) {
      console.error('Error getting user role:', error);
      return null;
    }
  },

  // Get all token payload data
  getTokenPayload: () => {
    const token = tokenService.getAccessToken();
    if (!token) return null;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload;
    } catch (error) {
      console.error('Error getting token payload:', error);
      return null;
    }
  },
};




