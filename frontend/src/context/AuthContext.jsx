import React, { createContext, useContext, useState, useEffect, useCallback } from 'react';
import { tokenService } from '../services/tokenService';
import { authAPI } from '../services/api';

const AuthContext = createContext(null);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [userRole, setUserRole] = useState(null);

  // Initialize auth state on mount
  useEffect(() => {
    const initAuth = async () => {
      try {
        const storedUser = tokenService.getUser();
        const accessToken = tokenService.getAccessToken();
        const refreshToken = tokenService.getRefreshToken();

        if (accessToken && refreshToken) {
          // Get user role from token
          const role = tokenService.getUserRole();
          setUserRole(role);

          // Check if access token is expired
          if (tokenService.isTokenExpired(accessToken)) {
            // Try to refresh token
            try {
              const response = await authAPI.refreshToken(refreshToken);
              // Backend returns { token, refreshToken, message, phoneNumber, firstName, lastName }
              if (response && response.token) {
                tokenService.setTokens(response.token, response.refreshToken);
                const newRole = tokenService.getUserRole();
                setUserRole(newRole);
                setUser(storedUser || {
                  phoneNumber: response.phoneNumber,
                  firstName: response.firstName,
                  lastName: response.lastName,
                });
                setIsAuthenticated(true);
              } else {
                throw new Error('Invalid refresh token response');
              }
            } catch (error) {
              console.error('Token refresh failed:', error);
              // Refresh failed, clear tokens
              tokenService.clearTokens();
              setUser(null);
              setUserRole(null);
              setIsAuthenticated(false);
            }
          } else {
            // Token is valid
            if (storedUser) {
              setUser(storedUser);
              setIsAuthenticated(true);
            } else {
              setUser(null);
              setIsAuthenticated(false);
            }
          }
        } else {
          setUser(null);
          setUserRole(null);
          setIsAuthenticated(false);
        }
      } catch (error) {
        console.error('Auth initialization error:', error);
        tokenService.clearTokens();
        setUser(null);
        setIsAuthenticated(false);
      } finally {
        setLoading(false);
      }
    };

    initAuth();
  }, []);

  // Logout function
  const logout = useCallback(async () => {
    try {
      const refreshToken = tokenService.getRefreshToken();
      if (refreshToken) {
        await authAPI.logout(refreshToken);
      }
    } catch (error) {
      console.error('Logout error:', error);
    } finally {
      // Clear tokens and user data
      tokenService.clearTokens();
      setUser(null);
      setUserRole(null);
      setIsAuthenticated(false);
    }
  }, []);

  // Auto-refresh token before expiration
  useEffect(() => {
    if (!isAuthenticated) return;

    const checkTokenExpiration = () => {
      const accessToken = tokenService.getAccessToken();
      const refreshToken = tokenService.getRefreshToken();

      if (!accessToken || !refreshToken) return;

      // Check if token expires in next 5 minutes
      const expirationTime = tokenService.getTokenExpiration(accessToken);
      if (expirationTime) {
        const timeUntilExpiry = expirationTime - Date.now();
        const fiveMinutes = 5 * 60 * 1000;

        if (timeUntilExpiry < fiveMinutes && timeUntilExpiry > 0) {
          // Refresh token before it expires
          authAPI.refreshToken(refreshToken)
            .then((response) => {
              // Backend returns { token, refreshToken, message, phoneNumber, firstName, lastName }
              tokenService.setTokens(response.token, response.refreshToken);
            })
            .catch((error) => {
              console.error('Auto-refresh failed:', error);
              logout();
            });
        }
      }
    };

    // Check every minute
    const interval = setInterval(checkTokenExpiration, 60000);
    checkTokenExpiration(); // Check immediately

    return () => clearInterval(interval);
  }, [isAuthenticated, logout]);

  // Login function
  const login = async (credentials) => {
    try {
      const response = await authAPI.login(credentials);
      
      // Save tokens (backend returns 'token' not 'accessToken')
      tokenService.setTokens(response.token, response.refreshToken);
      
      // Get user role from token
      const role = tokenService.getUserRole();
      setUserRole(role);
      
      // Save user data
      const userData = {
        phoneNumber: response.phoneNumber,
        firstName: response.firstName,
        lastName: response.lastName,
      };
      tokenService.setUser(userData);
      
      setUser(userData);
      setIsAuthenticated(true);
      
      return { success: true, data: response };
    } catch (error) {
      return {
        success: false,
        error: error.response?.data?.message || error.message || 'Login failed',
      };
    }
  };

  // Register function
  const register = async (userData) => {
    try {
      // Register user
      await authAPI.register(userData);
      
      // After registration, automatically login
      const loginResult = await login({
        phoneNumber: userData.phoneNumber,
        password: userData.password,
      });
      
      if (loginResult.success) {
        return { success: true, data: loginResult.data };
      } else {
        return {
          success: false,
          error: 'Registration successful, but auto-login failed. Please login manually.',
        };
      }
    } catch (error) {
      return {
        success: false,
        error: error.response?.data?.message || error.message || 'Registration failed',
      };
    }
  };

  const value = {
    user,
    userRole,
    isAuthenticated,
    loading,
    login,
    register,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};



