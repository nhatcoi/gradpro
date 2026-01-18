import { useAuthStore } from '@/stores/authStore';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { ROLE_LABELS, ROLE_COLORS } from '@/types';
import {
  BookOpen,
  Users,
  ClipboardCheck,
  Calendar,
  FileText,
  Building2,
  TrendingUp,
  Clock,
} from 'lucide-react';

const DashboardPage = () => {
  const { user } = useAuthStore();

  const getWelcomeMessage = () => {
    const hour = new Date().getHours();
    if (hour < 12) return 'Chào buổi sáng';
    if (hour < 18) return 'Chào buổi chiều';
    return 'Chào buổi tối';
  };

  // Stats based on role
  const getStats = () => {
    switch (user?.role) {
      case 'ADMIN':
        return [
          { label: 'Người dùng', value: '150', icon: Users, color: 'text-blue-600 bg-blue-50' },
          { label: 'Đề tài', value: '45', icon: BookOpen, color: 'text-green-600 bg-green-50' },
          { label: 'Hội đồng', value: '8', icon: Building2, color: 'text-purple-600 bg-purple-50' },
          { label: 'Hoàn thành', value: '32', icon: ClipboardCheck, color: 'text-orange-600 bg-orange-50' },
        ];
      case 'TRAINING_DEPT':
        return [
          { label: 'Đợt đăng ký', value: '2', icon: Calendar, color: 'text-blue-600 bg-blue-50' },
          { label: 'Hội đồng', value: '8', icon: Building2, color: 'text-green-600 bg-green-50' },
          { label: 'Đang bảo vệ', value: '12', icon: Clock, color: 'text-purple-600 bg-purple-50' },
          { label: 'Hoàn thành', value: '32', icon: TrendingUp, color: 'text-orange-600 bg-orange-50' },
        ];
      case 'DEPT_HEAD':
        return [
          { label: 'Chờ duyệt', value: '5', icon: ClipboardCheck, color: 'text-yellow-600 bg-yellow-50' },
          { label: 'Đề tài', value: '28', icon: BookOpen, color: 'text-blue-600 bg-blue-50' },
          { label: 'Giảng viên', value: '15', icon: Users, color: 'text-green-600 bg-green-50' },
          { label: 'Hội đồng', value: '3', icon: Building2, color: 'text-purple-600 bg-purple-50' },
        ];
      case 'LECTURER':
        return [
          { label: 'Đề tài của tôi', value: '3', icon: BookOpen, color: 'text-blue-600 bg-blue-50' },
          { label: 'Chờ duyệt', value: '2', icon: ClipboardCheck, color: 'text-yellow-600 bg-yellow-50' },
          { label: 'Sinh viên HD', value: '5', icon: Users, color: 'text-green-600 bg-green-50' },
          { label: 'Hội đồng', value: '2', icon: Building2, color: 'text-purple-600 bg-purple-50' },
        ];
      case 'STUDENT':
        return [
          { label: 'Đề tài đăng ký', value: '1', icon: BookOpen, color: 'text-blue-600 bg-blue-50' },
          { label: 'Tiến độ', value: '60%', icon: TrendingUp, color: 'text-green-600 bg-green-50' },
          { label: 'Báo cáo', value: '2/5', icon: FileText, color: 'text-purple-600 bg-purple-50' },
          { label: 'Deadline', value: '15 ngày', icon: Clock, color: 'text-orange-600 bg-orange-50' },
        ];
      default:
        return [];
    }
  };

  const stats = getStats();

  return (
    <div className="p-6">
      {/* Header */}
      <div className="mb-8">
        <div className="flex items-center gap-3 mb-2">
          <h1 className="text-2xl font-bold text-slate-900">
            {getWelcomeMessage()}, {user?.fullName}
          </h1>
          <span className={`px-2.5 py-0.5 rounded-full text-xs font-medium ${user ? ROLE_COLORS[user.role] : ''}`}>
            {user ? ROLE_LABELS[user.role] : ''}
          </span>
        </div>
        <p className="text-slate-500">
          {user?.department && `${user.department}`}
          {user?.major && ` - ${user.major}`}
        </p>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        {stats.map((stat, index) => (
          <Card key={index} className="border-slate-200">
            <CardContent className="p-6">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-slate-500 mb-1">{stat.label}</p>
                  <p className="text-2xl font-bold text-slate-900">{stat.value}</p>
                </div>
                <div className={`p-3 rounded-lg ${stat.color}`}>
                  <stat.icon className="h-6 w-6" />
                </div>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>

      {/* Quick Actions */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card className="border-slate-200">
          <CardHeader>
            <CardTitle className="text-lg">Hoạt động gần đây</CardTitle>
            <CardDescription>Các hoạt động mới nhất trong hệ thống</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {[
                { action: 'Đề tài "Hệ thống quản lý thư viện" đã được phê duyệt', time: '2 giờ trước' },
                { action: 'Sinh viên Nguyễn Văn A đã nộp báo cáo giai đoạn 1', time: '5 giờ trước' },
                { action: 'Đợt đăng ký HK1-2025 đã kết thúc', time: '1 ngày trước' },
              ].map((item, index) => (
                <div key={index} className="flex items-start gap-3 pb-3 border-b border-slate-100 last:border-0 last:pb-0">
                  <div className="w-2 h-2 rounded-full bg-primary mt-2" />
                  <div className="flex-1">
                    <p className="text-sm text-slate-700">{item.action}</p>
                    <p className="text-xs text-slate-400 mt-1">{item.time}</p>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        <Card className="border-slate-200">
          <CardHeader>
            <CardTitle className="text-lg">Thông báo</CardTitle>
            <CardDescription>Các thông báo quan trọng</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {[
                { title: 'Đợt đăng ký mới', desc: 'Đợt đăng ký HK2-2025 sẽ mở vào ngày 01/02/2025', type: 'info' },
                { title: 'Deadline báo cáo', desc: 'Hạn nộp báo cáo giai đoạn 2 là ngày 15/01/2025', type: 'warning' },
                { title: 'Bảo vệ đồ án', desc: 'Lịch bảo vệ đồ án đã được cập nhật', type: 'success' },
              ].map((item, index) => (
                <div key={index} className="p-3 rounded-lg bg-slate-50 border border-slate-100">
                  <p className="text-sm font-medium text-slate-900">{item.title}</p>
                  <p className="text-xs text-slate-500 mt-1">{item.desc}</p>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default DashboardPage;
