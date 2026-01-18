import { useState, useEffect } from 'react';
import { topicService } from '@/services/api';
import { useAuthStore } from '@/stores/authStore';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Search, Plus, BookOpen, User, Clock } from 'lucide-react';
import type { Topic } from '@/types';

const TopicsPage = () => {
  const { user } = useAuthStore();
  const [topics, setTopics] = useState<Topic[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchKeyword, setSearchKeyword] = useState('');

  useEffect(() => {
    loadTopics();
  }, []);

  const loadTopics = async () => {
    try {
      setLoading(true);
      const response = await topicService.searchTopics('', 0, 20);
      if (response.success && response.data) {
        setTopics(response.data.content || []);
      }
    } catch (error) {
      console.error('Error loading topics:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = async () => {
    try {
      setLoading(true);
      const response = await topicService.searchTopics(searchKeyword, 0, 20);
      if (response.success && response.data) {
        setTopics(response.data.content || []);
      }
    } catch (error) {
      console.error('Error searching topics:', error);
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'APPROVED':
        return 'bg-green-100 text-green-800';
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800';
      case 'REJECTED':
        return 'bg-red-100 text-red-800';
      case 'ASSIGNED':
        return 'bg-blue-100 text-blue-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getStatusLabel = (status: string) => {
    switch (status) {
      case 'APPROVED':
        return 'Đã duyệt';
      case 'PENDING':
        return 'Chờ duyệt';
      case 'REJECTED':
        return 'Từ chối';
      case 'ASSIGNED':
        return 'Đã gán';
      case 'COMPLETED':
        return 'Hoàn thành';
      default:
        return status;
    }
  };

  return (
    <div className="p-6">
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-slate-900">Đề tài</h1>
          <p className="text-slate-500">Danh sách đề tài đồ án tốt nghiệp</p>
        </div>
        {(user?.role === 'LECTURER' || user?.role === 'DEPT_HEAD') && (
          <Button>
            <Plus className="h-4 w-4 mr-2" />
            Tạo đề tài
          </Button>
        )}
      </div>

      {/* Search */}
      <div className="flex gap-4 mb-6">
        <div className="relative flex-1 max-w-md">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-400" />
          <Input
            placeholder="Tìm kiếm đề tài..."
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
            className="pl-10"
          />
        </div>
        <Button variant="secondary" onClick={handleSearch}>
          Tìm kiếm
        </Button>
      </div>

      {/* Topics List */}
      {loading ? (
        <div className="text-center py-12">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto" />
          <p className="text-slate-500 mt-4">Đang tải...</p>
        </div>
      ) : topics.length === 0 ? (
        <Card className="border-slate-200">
          <CardContent className="py-12 text-center">
            <BookOpen className="h-12 w-12 text-slate-300 mx-auto mb-4" />
            <p className="text-slate-500">Chưa có đề tài nào</p>
          </CardContent>
        </Card>
      ) : (
        <div className="grid gap-4">
          {topics.map((topic) => (
            <Card key={topic.id} className="border-slate-200 hover:border-slate-300 transition-colors">
              <CardHeader className="pb-3">
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    <div className="flex items-center gap-2 mb-1">
                      <span className="text-xs font-mono text-slate-400">{topic.code}</span>
                      <span className={`px-2 py-0.5 rounded-full text-xs font-medium ${getStatusColor(topic.status)}`}>
                        {getStatusLabel(topic.status)}
                      </span>
                    </div>
                    <CardTitle className="text-lg">{topic.title}</CardTitle>
                  </div>
                </div>
              </CardHeader>
              <CardContent>
                {topic.description && (
                  <p className="text-sm text-slate-600 mb-4 line-clamp-2">{topic.description}</p>
                )}
                <div className="flex flex-wrap gap-4 text-sm text-slate-500">
                  {topic.lecturer && (
                    <div className="flex items-center gap-1.5">
                      <User className="h-4 w-4" />
                      <span>GV: {topic.lecturer.fullName}</span>
                    </div>
                  )}
                  {topic.technology && (
                    <div className="flex items-center gap-1.5">
                      <BookOpen className="h-4 w-4" />
                      <span>{topic.technology}</span>
                    </div>
                  )}
                  <div className="flex items-center gap-1.5">
                    <Clock className="h-4 w-4" />
                    <span>{topic.semester}</span>
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
};

export default TopicsPage;
