export type ApplicationType = 'LOAN' | 'SCHOLARSHIP';

export type ApplicationStatus =
  | 'DRAFT'
  | 'SUBMITTED'
  | 'UNDER_REVIEW'
  | 'REVERTED'
  | 'PARTIALLY_DISBURSED'
  | 'DISBURSED'
  | 'APPROVED'
  | 'REJECTED';

export type DecisionStatus =
  | 'APPROVED'
  | 'REJECTED'
  | 'REVERTED';

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
  submissionDetails: ApplicationSubmissionDetails;
  student: StudentDetails;
  instituteName: string;
  documents: ApplicationDocument[];
  history: ApplicationHistory[];
}

export interface ApplicationHistory {
  historyId: string;
  status: ApplicationStatus | string;
  remark: string | null;
  actionByUserId: string;
  actionByName: string;
  assignedToUserId: string | null;
  assignedToName: string | null;
  createdAt: string;
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

export interface ApplicationSubmissionDetails {
  requestedLoanAmount: number | null;
  academicPercentage: number | null;
  loanPurpose: string | null;
  repaymentTenureYears: number | null;
  coBorrowerName: string | null;
  coBorrowerIncome: number | null;
}
