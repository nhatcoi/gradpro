import { useState } from 'react';
import { useAuthStore } from '@/stores/authStore';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { ROLE_LABELS } from '@/types';
import { User, Lock, Bell } from 'lucide-react';

const SettingsPage = () => {
  const { user } = useAuthStore();
  const [isLoading, setIsLoading] = useState(false);

  const handleSaveProfile = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    // TODO: Implement save profile
    setTimeout(() => setIsLoading(false), 1000);
  };

  return (
    <div className="p-6 max-w-4xl">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-slate-900">Cài đặt</h1>
        <p className="text-slate-500">Quản lý thông tin tài khoản của bạn</p>
      </div>

      <div className="space-y-6">
        {/* Profile Section */}
        <Card className="border-slate-200">
          <CardHeader>
            <div className="flex items-center gap-3">
              <div className="p-2 rounded-lg bg-primary/10">
                <User className="h-5 w-5 text-primary" />
              </div>
              <div>
                <CardTitle className="text-lg">Thông tin cá nhân</CardTitle>
                <CardDescription>Cập nhật thông tin cá nhân của bạn</CardDescription>
              </div>
            </div>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSaveProfile} className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="code">Mã số</Label>
                  <Input id="code" value={user?.code || ''} disabled />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="role">Vai trò</Label>
                  <Input id="role" value={user ? ROLE_LABELS[user.role] : ''} disabled />
                </div>
              </div>
              <div className="space-y-2">
                <Label htmlFor="fullName">Họ và tên</Label>
                <Input id="fullName" defaultValue={user?.fullName || ''} />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="email">Email</Label>
                  <Input id="email" type="email" value={user?.email || ''} disabled />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="phone">Số điện thoại</Label>
                  <Input id="phone" defaultValue={user?.phone || ''} />
                </div>
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="department">Khoa</Label>
                  <Input id="department" value={user?.department || ''} disabled />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="major">Chuyên ngành</Label>
                  <Input id="major" value={user?.major || ''} disabled />
                </div>
              </div>
              <Button type="submit" disabled={isLoading}>
                {isLoading ? 'Đang lưu...' : 'Lưu thay đổi'}
              </Button>
            </form>
          </CardContent>
        </Card>

        {/* Security Section */}
        <Card className="border-slate-200">
          <CardHeader>
            <div className="flex items-center gap-3">
              <div className="p-2 rounded-lg bg-orange-50">
                <Lock className="h-5 w-5 text-orange-600" />
              </div>
              <div>
                <CardTitle className="text-lg">Bảo mật</CardTitle>
                <CardDescription>Quản lý mật khẩu và bảo mật tài khoản</CardDescription>
              </div>
            </div>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="currentPassword">Mật khẩu hiện tại</Label>
                <Input id="currentPassword" type="password" />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label htmlFor="newPassword">Mật khẩu mới</Label>
                  <Input id="newPassword" type="password" />
                </div>
                <div className="space-y-2">
                  <Label htmlFor="confirmPassword">Xác nhận mật khẩu</Label>
                  <Input id="confirmPassword" type="password" />
                </div>
              </div>
              <Button variant="secondary">Đổi mật khẩu</Button>
            </div>
          </CardContent>
        </Card>

        {/* Notifications Section */}
        <Card className="border-slate-200">
          <CardHeader>
            <div className="flex items-center gap-3">
              <div className="p-2 rounded-lg bg-blue-50">
                <Bell className="h-5 w-5 text-blue-600" />
              </div>
              <div>
                <CardTitle className="text-lg">Thông báo</CardTitle>
                <CardDescription>Cấu hình nhận thông báo</CardDescription>
              </div>
            </div>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {[
                { label: 'Thông báo qua email', desc: 'Nhận thông báo qua email khi có cập nhật mới' },
                { label: 'Thông báo đăng ký', desc: 'Nhận thông báo khi có sinh viên đăng ký đề tài' },
                { label: 'Thông báo deadline', desc: 'Nhận nhắc nhở trước khi đến hạn nộp báo cáo' },
              ].map((item, index) => (
                <div key={index} className="flex items-center justify-between py-2">
                  <div>
                    <p className="text-sm font-medium text-slate-900">{item.label}</p>
                    <p className="text-xs text-slate-500">{item.desc}</p>
                  </div>
                  <Button variant="outline" size="sm">Bật</Button>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default SettingsPage;
