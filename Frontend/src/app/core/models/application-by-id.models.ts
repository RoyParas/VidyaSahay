export type ApplicationType = 'LOAN' | 'SCHOLARSHIP';

export type ApplicationStatus =
  | 'PENDING'
  | 'UNDER_REVIEW'
  | 'APPROVED'
  | 'REJECTED';

export type DecisionStatus =
  | 'APPROVED'
  | 'REJECTED'
  | 'PENDING';

export type DocumentVerificationStatus =
  | 'PENDING'
  | 'VERIFIED'
  | 'REJECTED';

export interface ApplicationSummary {
  id: string;
  applicationType: ApplicationType;
  schemeId: string;
  schemeName: string;
  studentId: string;
  studentFirstName: string;
  studentLastName: string;
  status: ApplicationStatus | string;
  approvedAmount: number | null;
}

export interface StudentAddress {
  id: string;
  country: string;
  state: string;
  district: string;
  city: string;
}

export interface StudentDetails {
  studentId: string;
  userId: string;
  firstName: string;
  lastName: string;
  email: string;
  mobile: string;
  institute: string;
  course: string;
  category: string;
  address: StudentAddress;
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
  verificationStatus: string;
  profileCompleted: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface ApplicationDocument {
  id: string;
  studentId: string;
  documentTypeId: string;
  documentTypeName: string;
  documentTypeDescription: string;
  fileName: string;
  fileUrl: string;
  verificationStatus: DocumentVerificationStatus | string;
  verifiedByUserId: string | null;
  verifiedAt: string | null;
}

export interface ApplicationDetailsResponse {
  applicationSummary: ApplicationSummary;
  student: StudentDetails;
  instituteName: string;
  documents: ApplicationDocument[];
}

export interface ApplicationStatusRequest {
  applicationId: string;
  status: DecisionStatus;
  remark: string;
  approvedAmount?: number;
}

export interface DocumentStatusRequest {
  verificationStatus: DocumentVerificationStatus;
}