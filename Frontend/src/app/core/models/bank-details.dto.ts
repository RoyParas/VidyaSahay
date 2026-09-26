export interface BankDetails {
  userId: string;
  bankId: string;
  bankName: string;
  contactPersonFirstName: string;
  contactPersonLastName: string;
  email: string;
  mobile: string;
  status: boolean;
}

export interface UpdateBankRequest {
  firstName: string;
  lastName: string;
  email: string;
  mobile: string;
  status: boolean;
}
