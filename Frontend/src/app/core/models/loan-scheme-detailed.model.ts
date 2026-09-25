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
  minInterestRate: number;
  maxInterestRate: number;
  disbursementType: string;
  applierMinAge: number;
  applierMaxAge: number;
  coBorrowerRequired: boolean;
  minCreditScore: number;
  requiredDocumentIds: string[];
  eligibleProfessionIds: string[];
  minTenureForRepayment: number;
  maxTenureForRepayment: number;
  prepaymentAllowed: boolean;
  foreclosureCharges: number;
  coursePeriodIncluded: boolean;
  additionalMonths: number;
}