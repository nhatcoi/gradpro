import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { useAuthStore } from '@/stores/authStore';
import MainLayout from '@/layouts/MainLayout';
import ProtectedRoute from '@/components/ProtectedRoute';
import LoginPage from '@/pages/LoginPage';
import DashboardPage from '@/pages/DashboardPage';
import TopicsPage from '@/pages/TopicsPage';
import UsersPage from '@/pages/UsersPage';
import SettingsPage from '@/pages/SettingsPage';

function App() {
  const { isAuthenticated } = useAuthStore();

  return (
    <BrowserRouter>
      <Routes>
        {/* Public routes */}
        <Route
          path="/login"
          element={isAuthenticated ? <Navigate to="/dashboard" replace /> : <LoginPage />}
        />

        {/* Protected routes */}
        <Route
          path="/"
          element={
            <ProtectedRoute>
              <MainLayout />
            </ProtectedRoute>
          }
        >
          <Route index element={<Navigate to="/dashboard" replace />} />
          <Route path="dashboard" element={<DashboardPage />} />
          
          {/* Topics */}
          <Route path="topics" element={<TopicsPage />} />
          <Route path="topics/available" element={<TopicsPage />} />
          <Route path="topics/pending" element={<TopicsPage />} />
          <Route path="my-topics" element={<TopicsPage />} />
          
          {/* Users - Admin only */}
          <Route
            path="users"
            element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <UsersPage />
              </ProtectedRoute>
            }
          />
          
          {/* Registration */}
          <Route path="registrations" element={<TopicsPage />} />
          <Route path="my-registration" element={<TopicsPage />} />
          <Route path="registration-periods" element={<TopicsPage />} />
          
          {/* Progress */}
          <Route path="progress" element={<TopicsPage />} />
          
          {/* Councils */}
          <Route path="councils" element={<TopicsPage />} />
          <Route path="my-councils" element={<TopicsPage />} />
          
          {/* Reports */}
          <Route path="reports" element={<TopicsPage />} />
          
          {/* Settings */}
          <Route path="settings" element={<SettingsPage />} />
        </Route>

        {/* Fallback */}
        <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
