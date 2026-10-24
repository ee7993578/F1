import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import { AuthProvider } from './auth/AuthContext'
import GlobalLoader from './components/layout/GlobalLoader'
import './index.css'
ReactDOM.createRoot(document.getElementById('root')).render(<React.StrictMode><AuthProvider><App /><GlobalLoader /></AuthProvider></React.StrictMode>)
