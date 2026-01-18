import { Outlet, Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuthStore } from '@/stores/authStore';
import { Button } from '@/components/ui/button';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import { Separator } from '@/components/ui/separator';
import { ROLE_LABELS } from '@/types';
import {
  LayoutDashboard,
  BookOpen,
  Users,
  FileText,
  Calendar,
  Settings,
  LogOut,
  GraduationCap,
  ClipboardList,
  BarChart3,
  Building2,
} from 'lucide-react';

const MainLayout = () => {
  const { user, logout } = useAuthStore();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const getInitials = (name: string) => {
    return name
      .split(' ')
      .map((n) => n[0])
      .join('')
      .toUpperCase()
      .slice(0, 2);
  };

  // Menu items based on role
  const getMenuItems = () => {
    const baseItems = [
      { path: '/dashboard', label: 'Tổng quan', icon: LayoutDashboard },
    ];

    switch (user?.role) {
      case 'ADMIN':
        return [
          ...baseItems,
          { path: '/users', label: 'Quản lý người dùng', icon: Users },
          { path: '/topics', label: 'Đề tài', icon: BookOpen },
          { path: '/councils', label: 'Hội đồng', icon: Building2 },
          { path: '/reports', label: 'Thống kê', icon: BarChart3 },
        ];
      case 'TRAINING_DEPT':
        return [
          ...baseItems,
          { path: '/registration-periods', label: 'Đợt đăng ký', icon: Calendar },
          { path: '/councils', label: 'Hội đồng', icon: Building2 },
          { path: '/reports', label: 'Thống kê', icon: BarChart3 },
        ];
      case 'DEPT_HEAD':
        return [
          ...baseItems,
          { path: '/topics/pending', label: 'Duyệt đề tài', icon: ClipboardList },
          { path: '/topics', label: 'Đề tài', icon: BookOpen },
          { path: '/councils', label: 'Hội đồng', icon: Building2 },
        ];
      case 'LECTURER':
        return [
          ...baseItems,
          { path: '/my-topics', label: 'Đề tài của tôi', icon: BookOpen },
          { path: '/registrations', label: 'Đăng ký', icon: ClipboardList },
          { path: '/my-councils', label: 'Hội đồng', icon: Building2 },
        ];
      case 'STUDENT':
        return [
          ...baseItems,
          { path: '/topics/available', label: 'Đề tài mở', icon: BookOpen },
          { path: '/my-registration', label: 'Đăng ký của tôi', icon: ClipboardList },
          { path: '/progress', label: 'Tiến độ', icon: FileText },
        ];
      default:
        return baseItems;
    }
  };

  const menuItems = getMenuItems();

  return (
    <div className="min-h-screen bg-slate-50">
      {/* Sidebar */}
      <aside className="fixed inset-y-0 left-0 z-50 w-64 bg-white border-r border-slate-200">
        {/* Logo */}
        <div className="flex items-center gap-3 h-16 px-6 border-b border-slate-200">
          <GraduationCap className="h-8 w-8 text-primary" />
          <div>
            <h1 className="font-bold text-lg text-slate-900">GradPro</h1>
            <p className="text-xs text-slate-500">Quản lý Đồ án</p>
          </div>
        </div>

        {/* Navigation */}
        <nav className="flex-1 p-4 space-y-1">
          {menuItems.map((item) => {
            const isActive = location.pathname === item.path;
            return (
              <Link
                key={item.path}
                to={item.path}
                className={`flex items-center gap-3 px-3 py-2.5 rounded-md text-sm font-medium transition-colors ${
                  isActive
                    ? 'bg-primary/10 text-primary'
                    : 'text-slate-600 hover:bg-slate-100 hover:text-slate-900'
                }`}
              >
                <item.icon className="h-5 w-5" />
                {item.label}
              </Link>
            );
          })}
        </nav>

        {/* User section */}
        <div className="absolute bottom-0 left-0 right-0 p-4 border-t border-slate-200 bg-white">
          <div className="flex items-center gap-3 mb-3">
            <Avatar className="h-10 w-10">
              <AvatarFallback className="bg-primary/10 text-primary font-medium">
                {user ? getInitials(user.fullName) : 'U'}
              </AvatarFallback>
            </Avatar>
            <div className="flex-1 min-w-0">
              <p className="text-sm font-medium text-slate-900 truncate">
                {user?.fullName}
              </p>
              <p className="text-xs text-slate-500">
                {user ? ROLE_LABELS[user.role] : ''}
              </p>
            </div>
          </div>
          <Separator className="mb-3" />
          <div className="flex gap-2">
            <Button
              variant="ghost"
              size="sm"
              className="flex-1 justify-start text-slate-600"
              onClick={() => navigate('/settings')}
            >
              <Settings className="h-4 w-4 mr-2" />
              Cài đặt
            </Button>
            <Button
              variant="ghost"
              size="sm"
              className="text-red-600 hover:text-red-700 hover:bg-red-50"
              onClick={handleLogout}
            >
              <LogOut className="h-4 w-4" />
            </Button>
          </div>
        </div>
      </aside>

      {/* Main content */}
      <main className="pl-64">
        <div className="min-h-screen">
          <Outlet />
        </div>
      </main>
    </div>
  );
};

export default MainLayout;
