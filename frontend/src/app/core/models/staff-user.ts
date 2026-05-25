export interface StaffUser {
  id: number;
  fullName: string;
  email: string;
  role: 'ADMIN' | 'RECEPCIONISTA' | 'SUPERVISOR' | 'LIMPIEZA';
  status: 'ACTIVO' | 'INACTIVO';
  password?: string;
  phone?: string;
  department?: string;
  bio?: string;
  lastAccess?: string;
}
