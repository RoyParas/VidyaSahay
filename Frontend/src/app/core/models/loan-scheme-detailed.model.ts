export interface LoanSchemeDetails {
  loanSchemeId: string;
  bankId: string;
  schemeName: string;
  interestType: string;
  minAmount: number;
  maxAmount: number;
  status: string;
  effectiveFrom: string;
  effectiveTo: string;
  minInterestRate: number | null;
  maxInterestRate: number;
  disbursementType: string;
  applierMinAge: number;
  applierMaxAge: number;
  coBorrowerRequired: boolean;
  minCreditScore: number | null;
  requiredDocumentIds: string[];
  documentRequirements: SchemeDocumentRequirement[];
  eligibleProfessionIds: string[];
  minTenureForRepayment: number;
  maxTenureForRepayment: number;
  prepaymentAllowed: boolean;
  foreclosureCharges: number | null;
  coursePeriodIncluded: boolean;
  additionalMonths: number;
}

export interface SchemeDocumentRequirement {
  documentTypeId: string;
  name: string;
  description: string | null;
}
