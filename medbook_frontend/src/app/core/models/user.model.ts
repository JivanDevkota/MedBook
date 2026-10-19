export interface User {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  role: Role;
  active: boolean;
  createdAt: string;
}

export enum Role {
  ADMIN = 'ADMIN',
  DOCTOR = 'DOCTOR',
  PATIENT = 'PATIENT',
  STAFF = 'STAFF',
}
