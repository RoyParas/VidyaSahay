import { UserRole } from "../../enums/user-role.enum";

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string,
  expiresIn: number,
  user: AuthenticatedUserResponse
}

export interface AuthenticatedUserResponse {
  userId: string,
  firstName: string,
  lastName: string,
  email: string,
  role: UserRole,
  mustChangePassword: boolean,
  profileCompleted: boolean
}
