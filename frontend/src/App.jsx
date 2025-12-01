import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import Login from './pages/Login';
import SignUp from './pages/SignUp';
import Home from './pages/Home';
import ProductDetail from './pages/ProductDetail';
import About from './pages/About';
import Contact from './pages/Contact';
import AdminPage from './pages/AdminPage';
import SellerPage from './pages/SellerPage';
import SuperAdminPage from './pages/SuperAdminPage';
import MyAccount from './pages/MyAccount';
import Cart from './pages/Cart';
import './App.css';

function App() {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return (
      <div style={{ 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center', 
        height: '100vh',
        fontSize: '18px'
      }}>
        <div>Loading...</div>
      </div>
    );
  }

  return (
    <Routes>
      <Route 
        path="/login" 
        element={isAuthenticated ? <Navigate to="/" replace /> : <Login />} 
      />
      <Route 
        path="/signup" 
        element={isAuthenticated ? <Navigate to="/" replace /> : <SignUp />} 
      />
      <Route 
        path="/" 
        element={isAuthenticated ? <Home /> : <Navigate to="/login" replace />} 
      />
      <Route path="/product/:id" element={<ProductDetail />} />
      <Route path="/about" element={<About />} />
      <Route path="/contact" element={<Contact />} />
      <Route path="/super-admin" element={<SuperAdminPage />} />
      <Route path="/admin" element={<AdminPage />} />
      <Route path="/seller" element={<SellerPage />} />
      <Route 
        path="/account" 
        element={isAuthenticated ? <MyAccount /> : <Navigate to="/login" replace />} 
      />
      <Route 
        path="/cart" 
        element={<Cart />} 
      />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default App;










