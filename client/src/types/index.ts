export type RoleType = 'ADMIN' | 'TRAINING_DEPT' | 'DEPT_HEAD' | 'LECTURER' | 'STUDENT';

export interface User {
  id: number;
  code: string;
  email: string;
  fullName: string;
  phone?: string;
  role: RoleType;
  department?: string;
  major?: string;
  academicYear?: string;
  academicTitle?: string;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface LoginRequest {
  emailOrCode: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  user: User;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data?: T;
  timestamp: string;
}

export interface Topic {
  id: number;
  code: string;
  title: string;
  description?: string;
  technology?: string;
  requirements?: string;
  maxStudents: number;
  semester: string;
  status: string;
  lecturer?: LecturerInfo;
  supervisor?: LecturerInfo;
  student?: StudentInfo;
  createdAt: string;
}

export interface LecturerInfo {
  id: number;
  code: string;
  fullName: string;
  academicTitle?: string;
}

export interface StudentInfo {
  id: number;
  code: string;
  fullName: string;
}

export interface RegistrationPeriod {
  id: number;
  name: string;
  semester: string;
  startDate: string;
  endDate: string;
  active: boolean;
}

export interface Registration {
  id: number;
  student: StudentInfo;
  topic: Topic;
  status: string;
  registeredAt: string;
}

export const ROLE_LABELS: Record<RoleType, string> = {
  ADMIN: 'Quản trị viên',
  TRAINING_DEPT: 'Phòng Đào tạo',
  DEPT_HEAD: 'Trưởng Bộ môn',
  LECTURER: 'Giảng viên',
  STUDENT: 'Sinh viên',
};

export const ROLE_COLORS: Record<RoleType, string> = {
  ADMIN: 'bg-red-100 text-red-800',
  TRAINING_DEPT: 'bg-purple-100 text-purple-800',
  DEPT_HEAD: 'bg-blue-100 text-blue-800',
  LECTURER: 'bg-green-100 text-green-800',
  STUDENT: 'bg-orange-100 text-orange-800',
};
