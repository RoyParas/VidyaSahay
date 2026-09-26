import { AddressResponse } from './student-verification.dto/student-verification.dto';

export interface InstituteDetails {
  instituteId: string;
  firstName: string;
  lastName: string;
  email: string;
  mobile: string;
  instituteName: string;
  address: AddressResponse;
  location: string | null;
  pincode: number;
  bankName: string | null;
  branchName: string | null;
  ifscCode: string | null;
  accountNumber: string | null;
  status?: boolean;
}

export interface UpdateInstituteRequest {
  firstName: string;
  lastName: string;
  email: string;
  mobile: string;
  instituteName: string;
  addressId: string;
  location: string | null;
  pincode: number;
  bankName: string | null;
  branchName: string | null;
  ifscCode: string | null;
  accountNumber: string | null;
  status: boolean;
}
