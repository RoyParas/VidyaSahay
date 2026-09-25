export interface CreateInstituteRequest {
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
}