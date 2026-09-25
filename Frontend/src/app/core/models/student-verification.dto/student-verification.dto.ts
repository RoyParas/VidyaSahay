import { VerificationStatus } from '../../enums/verification-status.enum';

export interface AddressResponse {
  id: string;
  country: string;
  state: string;
  district: string;
  city: string;
}

export interface StudentDetailedResponse {
  studentId: string;
  userId: string;
  firstName: string;
  lastName: string;
  email: string;
  mobile: string;
  institute: string;
  course: string;
  category: string;
  address: AddressResponse | null;
  location: string;
  pincode: number;
  maskedAadharNumber: string;
  gender: string;
  dateOfBirth: string;
  fatherName: string;
  motherName: string;
  feesPaid: number;
  feesPending: number;
  annualFamilyIncome: number;
  verificationStatus: VerificationStatus;
  profileCompleted: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface UpdateStudentVerificationRequest {
  status: VerificationStatus;
  remark: string;
}