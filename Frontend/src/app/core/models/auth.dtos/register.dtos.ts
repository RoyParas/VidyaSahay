import { UserRole } from "../../enums/user-role.enum"

export interface RegisterRequest {
  firstName: string,
  lastName: string,
  email: string,
  mobile: string,
  password: string
}

export interface RegisterResponse {
  userId: string,
  firstName: string,
  lastName: string,
  email: string,
  mobile: string,
  role: UserRole,
  mustCompleteProfile: boolean,
  message: string
}
