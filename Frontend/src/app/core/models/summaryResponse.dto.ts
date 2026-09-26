import { VerificationStatus } from "../enums/verification-status.enum";

export interface StudentSummary {
  [key: string]: unknown;
  studentId: string | null;
  userId: string;
  firstName: string;
  lastName: string;
  email: string;
  mobile: string;
  verificationStatus: VerificationStatus | null;
  courseName: string | null;
  instituteName: string | null;
  profileCompleted: boolean;
}
export interface InstituteAccountSummary {
  [key: string]: unknown;
  instituteId: string;
  instituteName: string;
  state: string;
  city: string;
  status: boolean;
}
export interface BankSummary {
  [key: string]: unknown;
  bankId: string;
  bankName: string;
  contactPersonFirstName: string;
  contactPersonLastName: string;
  email: string;
  mobile: string;
  active: boolean;
}
export interface LoanSchemeSummary {
  [key: string]: unknown;
  loanSchemeId: string;
  bankId: string;
  schemeName: string;
  interestType: string;
  minAmount: number;
  maxAmount: number;
  status: string;
}
export interface ScholarshipSchemeSummary {
  [key: string]: unknown;
  scholarshipSchemeId: string;
  scholarshipName: string;
  scholarshipType: string;
  academicYear: string;
  status: string;
}
export interface ApplicationSummary {
  [key: string]: unknown;
  applicationId: string;
  applicationType: 'LOAN' | 'SCHOLARSHIP' | string;
  schemeId: string | null;
  schemeName: string | null;
  studentId: string;
  studentFirstName: string;
  studentLastName: string;
  status: string;
  approvedAmount: number | null;
}
