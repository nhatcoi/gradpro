import axios, { AxiosError } from 'axios';
import type { ApiResponse, LoginRequest, LoginResponse } from '@/types';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor to handle errors
api.interceptors.response.use(
  (response) => response,
  (error: AxiosError<ApiResponse<unknown>>) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('user');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const authService = {
  login: async (data: LoginRequest): Promise<ApiResponse<LoginResponse>> => {
    const response = await api.post<ApiResponse<LoginResponse>>('/auth/login', data);
    return response.data;
  },

  changePassword: async (currentPassword: string, newPassword: string): Promise<ApiResponse<void>> => {
    const response = await api.post<ApiResponse<void>>('/auth/change-password', {
      currentPassword,
      newPassword,
    });
    return response.data;
  },
};

export const userService = {
  getProfile: async () => {
    const response = await api.get('/users/profile');
    return response.data;
  },

  updateProfile: async (data: Partial<{ fullName: string; phone: string }>) => {
    const response = await api.put('/users/profile', data);
    return response.data;
  },

  getAllUsers: async (page = 0, size = 20) => {
    const response = await api.get('/users/manage', { params: { page, size } });
    return response.data;
  },

  createUser: async (data: unknown) => {
    const response = await api.post('/users/manage', data);
    return response.data;
  },

  getLecturers: async () => {
    const response = await api.get('/users/lecturers');
    return response.data;
  },

  getStudents: async () => {
    const response = await api.get('/users/students');
    return response.data;
  },
};

export const topicService = {
  getAvailableTopics: async (semester: string, page = 0, size = 20) => {
    const response = await api.get('/topics/available', { params: { semester, page, size } });
    return response.data;
  },

  searchTopics: async (keyword: string, page = 0, size = 20) => {
    const response = await api.get('/topics/search', { params: { keyword, page, size } });
    return response.data;
  },

  getTopicById: async (id: number) => {
    const response = await api.get(`/topics/${id}`);
    return response.data;
  },

  createTopic: async (data: unknown) => {
    const response = await api.post('/topics/propose', data);
    return response.data;
  },

  approveTopic: async (id: number, approved: boolean, comment?: string) => {
    const response = await api.post(`/topics/approve/${id}`, { approved, comment });
    return response.data;
  },

  getTopicsByLecturer: async (page = 0, size = 20) => {
    const response = await api.get('/topics/my-topics', { params: { page, size } });
    return response.data;
  },

  getPendingTopics: async (page = 0, size = 20) => {
    const response = await api.get('/topics/pending', { params: { page, size } });
    return response.data;
  },
};

export const registrationService = {
  getCurrentPeriod: async () => {
    const response = await api.get('/registration-periods/current');
    return response.data;
  },

  registerTopic: async (topicId: number, note?: string) => {
    const response = await api.post('/registrations/register', { topicId, note });
    return response.data;
  },

  getMyRegistrations: async () => {
    const response = await api.get('/registrations/my-registrations');
    return response.data;
  },

  getRegistrationsByLecturer: async (status?: string, page = 0, size = 20) => {
    const response = await api.get('/registrations/lecturer', { params: { status, page, size } });
    return response.data;
  },

  approveRegistration: async (id: number, approved: boolean, comment?: string) => {
    const response = await api.post(`/registrations/approve/${id}`, { approved, comment });
    return response.data;
  },
};

export const progressService = {
  getMilestonesByTopic: async (topicId: number) => {
    const response = await api.get(`/milestones/topic/${topicId}`);
    return response.data;
  },

  createMilestone: async (data: unknown) => {
    const response = await api.post('/milestones', data);
    return response.data;
  },

  getProgressByTopic: async (topicId: number) => {
    const response = await api.get(`/progress/topic/${topicId}`);
    return response.data;
  },

  submitProgress: async (milestoneId: number, content: string, file?: File) => {
    const formData = new FormData();
    formData.append('data', new Blob([JSON.stringify({ milestoneId, content })], { type: 'application/json' }));
    if (file) formData.append('file', file);
    const response = await api.post('/progress/submit', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return response.data;
  },
};

export const councilService = {
  getCouncilsBySemester: async (semester: string) => {
    const response = await api.get(`/councils/semester/${semester}`);
    return response.data;
  },

  getCouncilById: async (id: number) => {
    const response = await api.get(`/councils/${id}`);
    return response.data;
  },

  createCouncil: async (data: unknown) => {
    const response = await api.post('/councils/create', data);
    return response.data;
  },
};

export const reportService = {
  getStatistics: async (semester: string) => {
    const response = await api.get('/reports/statistics', { params: { semester } });
    return response.data;
  },

  exportTopics: async (semester: string) => {
    const response = await api.get('/reports/export/topics', {
      params: { semester },
      responseType: 'blob',
    });
    return response.data;
  },
};

export default api;
