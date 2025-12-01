# E-Commerce Frontend

React-based frontend application for E-Commerce platform with JWT authentication.

## Features

- ✅ **Sign Up** - User registration
- ✅ **Login** - User authentication
- ✅ **JWT Token Management** - Access token & Refresh token in localStorage
- ✅ **Auto Token Refresh** - Automatically refreshes access token before expiration
- ✅ **Protected Routes** - Route protection based on authentication
- ✅ **Logout** - Clear tokens from localStorage

## Tech Stack

- **React 18** - UI library
- **React Router DOM 6** - Routing
- **Axios** - HTTP client
- **Vite** - Build tool
- **JWT Decode** - Token decoding

## Installation

```bash
cd frontend
npm install
```

## Development

```bash
npm run dev
```

Application will run on `http://localhost:3000`

## Build

```bash
npm run build
```

## Project Structure

```
frontend/
├── public/
│   └── index.html
├── src/
│   ├── components/        # Reusable components
│   ├── pages/             # Page components
│   │   ├── Login.jsx
│   │   ├── SignUp.jsx
│   │   ├── Home.jsx
│   │   ├── Auth.css
│   │   └── Home.css
│   ├── services/          # API services
│   │   ├── api.js         # Axios instance with interceptors
│   │   └── tokenService.js # localStorage token management
│   ├── context/           # React Context
│   │   └── AuthContext.jsx # Authentication context
│   ├── App.jsx            # Main app component
│   ├── main.jsx           # Entry point
│   └── index.css          # Global styles
├── package.json
└── vite.config.js
```

## API Configuration

Default API base URL: `http://localhost:8081`

**Backend Endpoints:**
- `POST /auth/register` - User registration
- `POST /auth/login` - User login
- `POST /auth/refresh-token` - Refresh access token
- `POST /auth/logout` - User logout

To change API URL, update `src/services/api.js`:

```javascript
const api = axios.create({
  baseURL: 'YOUR_API_URL', // e.g., 'http://localhost:8081'
  // ...
});
```

## Authentication Flow

1. **Login/SignUp** → Get access token + refresh token
2. **Save tokens** → Store in localStorage
3. **Auto refresh** → Refresh token before expiration (5 minutes before)
4. **401 handling** → Auto refresh on 401 error
5. **Logout** → Clear all tokens from localStorage

## Token Management

- **Access Token**: Stored in localStorage, expires in 24 hours (default)
- **Refresh Token**: Stored in localStorage, expires in 7 days (default)
- **Auto Refresh**: Checks every minute, refreshes if expires in < 5 minutes

## Usage

### Login

```javascript
const { login } = useAuth();

const result = await login({
  phoneNumber: '+998901234567',
  password: 'password123'
});
```

### Register

```javascript
const { register } = useAuth();

const result = await register({
  firstName: 'John',
  lastName: 'Doe',
  phoneNumber: '+998901234567',
  email: 'john@example.com',
  password: 'password123'
});
```

### Logout

```javascript
const { logout } = useAuth();

await logout();
```

### Protected Route

```javascript
<ProtectedRoute>
  <YourComponent />
</ProtectedRoute>
```

## Environment Variables

Create `.env` file:

```
VITE_API_BASE_URL=http://localhost:8081/api
```


React-based frontend application for E-Commerce platform with JWT authentication.

## Features

- ✅ **Sign Up** - User registration
- ✅ **Login** - User authentication
- ✅ **JWT Token Management** - Access token & Refresh token in localStorage
- ✅ **Auto Token Refresh** - Automatically refreshes access token before expiration
- ✅ **Protected Routes** - Route protection based on authentication
- ✅ **Logout** - Clear tokens from localStorage

## Tech Stack

- **React 18** - UI library
- **React Router DOM 6** - Routing
- **Axios** - HTTP client
- **Vite** - Build tool
- **JWT Decode** - Token decoding

## Installation

```bash
cd frontend
npm install
```

## Development

```bash
npm run dev
```

Application will run on `http://localhost:3000`

## Build

```bash
npm run build
```

## Project Structure

```
frontend/
├── public/
│   └── index.html
├── src/
│   ├── components/        # Reusable components
│   ├── pages/             # Page components
│   │   ├── Login.jsx
│   │   ├── SignUp.jsx
│   │   ├── Home.jsx
│   │   ├── Auth.css
│   │   └── Home.css
│   ├── services/          # API services
│   │   ├── api.js         # Axios instance with interceptors
│   │   └── tokenService.js # localStorage token management
│   ├── context/           # React Context
│   │   └── AuthContext.jsx # Authentication context
│   ├── App.jsx            # Main app component
│   ├── main.jsx           # Entry point
│   └── index.css          # Global styles
├── package.json
└── vite.config.js
```

## API Configuration

Default API base URL: `http://localhost:8081`

**Backend Endpoints:**
- `POST /auth/register` - User registration
- `POST /auth/login` - User login
- `POST /auth/refresh-token` - Refresh access token
- `POST /auth/logout` - User logout

To change API URL, update `src/services/api.js`:

```javascript
const api = axios.create({
  baseURL: 'YOUR_API_URL', // e.g., 'http://localhost:8081'
  // ...
});
```

## Authentication Flow

1. **Login/SignUp** → Get access token + refresh token
2. **Save tokens** → Store in localStorage
3. **Auto refresh** → Refresh token before expiration (5 minutes before)
4. **401 handling** → Auto refresh on 401 error
5. **Logout** → Clear all tokens from localStorage

## Token Management

- **Access Token**: Stored in localStorage, expires in 24 hours (default)
- **Refresh Token**: Stored in localStorage, expires in 7 days (default)
- **Auto Refresh**: Checks every minute, refreshes if expires in < 5 minutes

## Usage

### Login

```javascript
const { login } = useAuth();

const result = await login({
  phoneNumber: '+998901234567',
  password: 'password123'
});
```

### Register

```javascript
const { register } = useAuth();

const result = await register({
  firstName: 'John',
  lastName: 'Doe',
  phoneNumber: '+998901234567',
  email: 'john@example.com',
  password: 'password123'
});
```

### Logout

```javascript
const { logout } = useAuth();

await logout();
```

### Protected Route

```javascript
<ProtectedRoute>
  <YourComponent />
</ProtectedRoute>
```

## Environment Variables

Create `.env` file:

```
VITE_API_BASE_URL=http://localhost:8081/api
```


React-based frontend application for E-Commerce platform with JWT authentication.

## Features

- ✅ **Sign Up** - User registration
- ✅ **Login** - User authentication
- ✅ **JWT Token Management** - Access token & Refresh token in localStorage
- ✅ **Auto Token Refresh** - Automatically refreshes access token before expiration
- ✅ **Protected Routes** - Route protection based on authentication
- ✅ **Logout** - Clear tokens from localStorage

## Tech Stack

- **React 18** - UI library
- **React Router DOM 6** - Routing
- **Axios** - HTTP client
- **Vite** - Build tool
- **JWT Decode** - Token decoding

## Installation

```bash
cd frontend
npm install
```

## Development

```bash
npm run dev
```

Application will run on `http://localhost:3000`

## Build

```bash
npm run build
```

## Project Structure

```
frontend/
├── public/
│   └── index.html
├── src/
│   ├── components/        # Reusable components
│   ├── pages/             # Page components
│   │   ├── Login.jsx
│   │   ├── SignUp.jsx
│   │   ├── Home.jsx
│   │   ├── Auth.css
│   │   └── Home.css
│   ├── services/          # API services
│   │   ├── api.js         # Axios instance with interceptors
│   │   └── tokenService.js # localStorage token management
│   ├── context/           # React Context
│   │   └── AuthContext.jsx # Authentication context
│   ├── App.jsx            # Main app component
│   ├── main.jsx           # Entry point
│   └── index.css          # Global styles
├── package.json
└── vite.config.js
```

## API Configuration

Default API base URL: `http://localhost:8081`

**Backend Endpoints:**
- `POST /auth/register` - User registration
- `POST /auth/login` - User login
- `POST /auth/refresh-token` - Refresh access token
- `POST /auth/logout` - User logout

To change API URL, update `src/services/api.js`:

```javascript
const api = axios.create({
  baseURL: 'YOUR_API_URL', // e.g., 'http://localhost:8081'
  // ...
});
```

## Authentication Flow

1. **Login/SignUp** → Get access token + refresh token
2. **Save tokens** → Store in localStorage
3. **Auto refresh** → Refresh token before expiration (5 minutes before)
4. **401 handling** → Auto refresh on 401 error
5. **Logout** → Clear all tokens from localStorage

## Token Management

- **Access Token**: Stored in localStorage, expires in 24 hours (default)
- **Refresh Token**: Stored in localStorage, expires in 7 days (default)
- **Auto Refresh**: Checks every minute, refreshes if expires in < 5 minutes

## Usage

### Login

```javascript
const { login } = useAuth();

const result = await login({
  phoneNumber: '+998901234567',
  password: 'password123'
});
```

### Register

```javascript
const { register } = useAuth();

const result = await register({
  firstName: 'John',
  lastName: 'Doe',
  phoneNumber: '+998901234567',
  email: 'john@example.com',
  password: 'password123'
});
```

### Logout

```javascript
const { logout } = useAuth();

await logout();
```

### Protected Route

```javascript
<ProtectedRoute>
  <YourComponent />
</ProtectedRoute>
```

## Environment Variables

Create `.env` file:

```
VITE_API_BASE_URL=http://localhost:8081/api
```












