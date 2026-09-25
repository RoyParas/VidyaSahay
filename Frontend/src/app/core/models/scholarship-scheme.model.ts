export interface ScholarshipSchemeDetails {
  scholarshipSchemeId: string;
  scholarshipName: string;
  scholarshipType: string;
  academicYear: string;
  status: string;
  startDate: string;
  endDate: string;
  applierMinAge: number;
  applierMaxAge: number;
  maxFamilyAnnualIncome: number;
  minPercentageCriteria: number;
  requiredDocuments: string[];
  eligibleProfessions: string[];
  eligibleCategories: string[];
  scholarshipAmount: number;
  amountType: string;
  paymentFrequency: string;
  totalSchemeBudget: number;
}