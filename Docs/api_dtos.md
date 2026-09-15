# VidyaSahay API DTO Proposal

This document defines request and response DTOs for the scheme, application,
disbursement, and student-document endpoints. It follows the entities and
workflow in `schema_design.md` and `workflow.md`.

## Conventions

- IDs are `number` values in examples. Use `string` instead if the backend uses UUIDs.
- Dates are ISO-8601 strings: `YYYY-MM-DD` for dates and full ISO timestamps for audit fields.
- Amounts are decimal numbers. The backend should use decimal-safe storage.
- `created_by`, `updated_by`, and internal file paths must not be accepted from clients.
- Every protected endpoint derives the current user from the access token.
- List endpoints use the common paginated response unless stated otherwise.

## Common DTOs

### ApiResponse

```ts
interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
  traceId?: string;
}
```

### PageRequest

```ts
interface PageRequest {
  page?: number;       // default: 0
  size?: number;       // default: 20, maximum: 100
  sortBy?: string;
  sortDirection?: 'asc' | 'desc';
  search?: string;
}
```

### PageResponse

```ts
interface PageResponse<T> {
  items: T[];
  page: number;
  size: number;
  totalItems: number;
  totalPages: number;
  hasNext: boolean;
  hasPrevious: boolean;
}
```

### Lookup DTOs

```ts
interface LookupDto {
  id: number;
  name: string;
  code?: string;
}

interface RequiredDocumentDto {
  id: number;
  name: string;
  description?: string;
  mandatory: boolean;
}
```

## Scheme DTOs

The detailed scheme DTOs below are used for both list and detail responses.
List responses may use the smaller `SchemeSummaryDto` if response size needs
to be reduced.

### SchemeSummaryDto

```ts
interface SchemeSummaryDto {
  id: number;
  name: string;
  status: 'active' | 'inactive';
  effectiveFrom?: string;
  effectiveTo?: string;
  createdAt: string;
  updatedAt: string;
}
```

### LoanSchemeResponseDto

```ts
interface LoanSchemeResponseDto extends SchemeSummaryDto {
  minLoanAmount: number;
  maxLoanAmount: number;
  interestType: 'fixed' | 'floating';
  minimumRate: number;
  maximumRate: number;
  disbursementType: 'yearly' | 'one_time';
  eligibility: LoanEligibilityDto;
  eligibleProfessions: LookupDto[];
  requiredDocuments: RequiredDocumentDto[];
  moratorium?: LoanMoratoriumDto;
  repaymentRules?: LoanRepaymentRulesDto;
  createdBy?: UserSummaryDto;
  updatedBy?: UserSummaryDto;
}

interface LoanEligibilityDto {
  minAge?: number;
  maxAge?: number;
  coBorrowerRequired: boolean;
  minCreditScore?: number;
}

interface LoanMoratoriumDto {
  coursePeriodIncluded: boolean;
  additionalMonths: number;
}

interface LoanRepaymentRulesDto {
  minTenureYears: number;
  maxTenureYears: number;
  prepaymentAllowed: boolean;
  foreclosureCharges?: number;
}
```

### CreateLoanSchemeRequest

`POST /api/loan-scheme`

```ts
interface CreateLoanSchemeRequest {
  name: string;
  effectiveFrom?: string;
  effectiveTo?: string;
  minLoanAmount: number;
  maxLoanAmount: number;
  interestType: 'fixed' | 'floating';
  minimumRate: number;
  maximumRate: number;
  disbursementType: 'yearly' | 'one_time';
  eligibility: LoanEligibilityRequest;
  professionIds: number[];
  requiredDocumentTypeIds: number[];
  moratorium?: LoanMoratoriumRequest;
  repaymentRules?: LoanRepaymentRulesRequest;
}

interface LoanEligibilityRequest {
  minAge?: number;
  maxAge?: number;
  coBorrowerRequired: boolean;
  minCreditScore?: number;
}

interface LoanMoratoriumRequest {
  coursePeriodIncluded: boolean;
  additionalMonths: number;
}

interface LoanRepaymentRulesRequest {
  minTenureYears: number;
  maxTenureYears: number;
  prepaymentAllowed: boolean;
  foreclosureCharges?: number;
}
```

### UpdateLoanSchemeRequest

`PUT /api/loan-scheme/{id}`

Use the same shape as `CreateLoanSchemeRequest`. The server should validate
that the scheme exists and should not allow changes that invalidate submitted
applications without an explicit business rule.

### ScholarshipSchemeResponseDto

```ts
interface ScholarshipSchemeResponseDto extends SchemeSummaryDto {
  scholarshipType: 'merit_based' | 'general' | 'category_based';
  academicYear: string;
  startDate: string;
  endDate: string;
  eligibility: ScholarshipEligibilityDto;
  eligibleCategories: LookupDto[];
  eligibleProfessions: LookupDto[];
  requiredDocuments: RequiredDocumentDto[];
  benefit: ScholarshipBenefitDto;
  createdBy?: UserSummaryDto;
  updatedBy?: UserSummaryDto;
}

interface ScholarshipEligibilityDto {
  minimumAge?: number;
  maximumAge?: number;
  maximumAnnualFamilyIncome?: number;
  minimumPercentageCriteria?: number;
}

interface ScholarshipBenefitDto {
  scholarshipAmount: number;
  amountType: 'fixed_amount' | 'percentage_of_fees';
  paymentFrequency: 'one_time' | 'monthly' | 'quarterly' | 'yearly';
  totalSchemeBudget?: number;
}
```

### CreateScholarshipSchemeRequest

`POST /api/scholarship-scheme`

```ts
interface CreateScholarshipSchemeRequest {
  name: string;
  scholarshipType: 'merit_based' | 'general' | 'category_based';
  academicYear: string;
  startDate: string;
  endDate: string;
  eligibility: ScholarshipEligibilityRequest;
  categoryIds: number[];
  professionIds: number[];
  requiredDocumentTypeIds: number[];
  benefit: ScholarshipBenefitRequest;
}

interface ScholarshipEligibilityRequest {
  minimumAge?: number;
  maximumAge?: number;
  maximumAnnualFamilyIncome?: number;
  minimumPercentageCriteria?: number;
}

interface ScholarshipBenefitRequest {
  scholarshipAmount: number;
  amountType: 'fixed_amount' | 'percentage_of_fees';
  paymentFrequency: 'one_time' | 'monthly' | 'quarterly' | 'yearly';
  totalSchemeBudget?: number;
}
```

### UpdateScholarshipSchemeRequest

`PUT /api/scholarship-scheme/{id}`

Use the same shape as `CreateScholarshipSchemeRequest`.

### Scheme endpoints

| Method | Endpoint | Request DTO | Response DTO |
|---|---|---|---|
| GET | `/api/scholarship-scheme/all` | `PageRequest` query params | `ApiResponse<PageResponse<ScholarshipSchemeResponseDto>>` |
| GET | `/api/scholarship-scheme/{id}` | Path `id` | `ApiResponse<ScholarshipSchemeResponseDto>` |
| GET | `/api/scholarship-scheme/eligible` | `PageRequest` query params | `ApiResponse<PageResponse<ScholarshipSchemeResponseDto>>` |
| GET | `/api/scholarship-scheme/created-by-me` | `PageRequest` query params | `ApiResponse<PageResponse<ScholarshipSchemeResponseDto>>` |
| POST | `/api/scholarship-scheme` | `CreateScholarshipSchemeRequest` | `ApiResponse<ScholarshipSchemeResponseDto>` |
| PUT | `/api/scholarship-scheme/{id}` | `UpdateScholarshipSchemeRequest` | `ApiResponse<ScholarshipSchemeResponseDto>` |
| DELETE | `/api/scholarship-scheme/{id}` | Path `id` | `ApiResponse<DeleteResponseDto>` |
| GET | `/api/loan-scheme/all` | `PageRequest` query params | `ApiResponse<PageResponse<LoanSchemeResponseDto>>` |
| GET | `/api/loan-scheme/{id}` | Path `id` | `ApiResponse<LoanSchemeResponseDto>` |
| GET | `/api/loan-scheme/eligible` | `PageRequest` query params | `ApiResponse<PageResponse<LoanSchemeResponseDto>>` |
| GET | `/api/loan-scheme/created-by-me` | `PageRequest` query params | `ApiResponse<PageResponse<LoanSchemeResponseDto>>` |
| POST | `/api/loan-scheme` | `CreateLoanSchemeRequest` | `ApiResponse<LoanSchemeResponseDto>` |
| PUT | `/api/loan-scheme/{id}` | `UpdateLoanSchemeRequest` | `ApiResponse<LoanSchemeResponseDto>` |
| DELETE | `/api/loan-scheme/{id}` | Path `id` | `ApiResponse<DeleteResponseDto>` |

```ts
interface DeleteResponseDto {
  id: number;
  deleted: boolean;
  status: 'inactive';
}
```

`DELETE` should perform a soft delete by setting `status` to `inactive`, as
specified in the workflow.

## Application DTOs

### ApplicationSummaryDto

```ts
interface ApplicationSummaryDto {
  id: number;
  applicationType: 'loan' | 'scholarship';
  schemeId: number;
  schemeName: string;
  studentId: number;
  student?: StudentSummaryDto;
  status: 'draft' | 'submitted' | 'under_review' | 'reverted' | 'rejected' | 'approved' | 'disbursed';
  approvedAmount?: number;
  submittedAt?: string;
  createdAt: string;
  updatedAt: string;
}

interface StudentSummaryDto {
  id: number;
  userId: number;
  name: string;
  email?: string;
  mobile?: string;
  instituteId?: number;
  instituteName?: string;
  verificationStatus: 'pending' | 'verified' | 'rejected';
}

interface UserSummaryDto {
  id: number;
  name: string;
  role: 'student' | 'bank' | 'government' | 'institute' | 'admin';
}
```

### ApplicationDetailResponseDto

```ts
interface ApplicationDetailResponseDto extends ApplicationSummaryDto {
  student: StudentDetailDto;
  scheme: SchemeReferenceDto;
  documents: ApplicationDocumentDto[];
  history: ApplicationHistoryDto[];
  disbursements: DisbursementSummaryDto[];
}

interface StudentDetailDto extends StudentSummaryDto {
  dateOfBirth?: string;
  gender?: string;
  category?: LookupDto;
  profession?: LookupDto;
  course?: LookupDto;
  annualFamilyIncome?: number;
}

interface SchemeReferenceDto {
  id: number;
  name: string;
  type: 'loan' | 'scholarship';
}

interface ApplicationDocumentDto {
  id: number;
  studentDocumentId: number;
  documentType: LookupDto;
  fileName: string;
  verificationStatus: 'pending' | 'verified' | 'rejected';
}

interface ApplicationHistoryDto {
  id: number;
  status: ApplicationSummaryDto['status'];
  remark?: string;
  actionBy: UserSummaryDto;
  createdAt: string;
}
```

### ApplyApplicationRequest

`POST /api/application/apply`

```ts
interface ApplyApplicationRequest {
  applicationType: 'loan' | 'scholarship';
  schemeId: number;
  documentIds: number[];
  requestedAmount?: number; // required for loan; optional or ignored for fixed scholarships
}
```

The server must verify that the authenticated user is a student, the profile
is complete, institute verification is `verified`, the scheme is eligible and
active, all required documents are attached, and no active application of the
same type already exists.

### ApplicationActionRequest

Use this DTO for bank/government decisions. The endpoint list does not include
a separate action path, so this DTO is intended for the application status
endpoint.

```ts
interface ApplicationActionRequest {
  applicationId: number;
  action: 'approve' | 'reject' | 'revert' | 'under_review';
  remark?: string;
  approvedAmount?: number; // required when action is approve for a loan
}
```

Reject and revert actions should require a non-empty `remark`.

### Application endpoints

| Method | Endpoint | Request DTO | Response DTO |
|---|---|---|---|
| GET | `/api/application/{id}` | Path `id` | `ApiResponse<ApplicationDetailResponseDto>` |
| GET | `/api/application/my` | `ApplicationListQuery` | `ApiResponse<PageResponse<ApplicationSummaryDto>>` |
| POST | `/api/application/apply` | `ApplyApplicationRequest` | `ApiResponse<ApplicationDetailResponseDto>` |
| PATCH | `/api/application/status` | `ApplicationActionRequest` | `ApiResponse<ApplicationDetailResponseDto>` |

```ts
interface ApplicationListQuery extends PageRequest {
  applicationType?: 'loan' | 'scholarship';
  status?: ApplicationSummaryDto['status'];
  schemeId?: number;
  studentId?: number; // bank, government, institute, or admin only
}
```

`GET /api/application/my` means the authenticated user's applications for a
student. For bank, government, and institute users, it should return the
applications assigned or visible to that role. If a separate review endpoint
is later added, reuse `ApplicationListQuery` and the same response DTO.

`/api/application/status` should be restricted by role and transition rules:
students cannot approve, reject, revert, or change their own status.

## Disbursement DTOs

### DisbursementSummaryDto

```ts
interface DisbursementSummaryDto {
  id: number;
  applicationId: number;
  applicationType: 'loan' | 'scholarship';
  studentId: number;
  studentName?: string;
  amount: number;
  disbursementDate?: string;
  referenceNumber?: string;
  status: 'pending' | 'processed' | 'failed' | 'completed';
  remark?: string;
  createdBy?: UserSummaryDto;
  createdAt: string;
  updatedAt: string;
}
```

### CreateDisbursementRequest

`POST /api/disbursement`

```ts
interface CreateDisbursementRequest {
  applicationId: number;
  amount: number;
  disbursementDate?: string;
  referenceNumber?: string;
  status?: 'pending' | 'processed' | 'failed' | 'completed';
  remark?: string;
}
```

The server must derive `applicationType`, `studentId`, and `createdBy` from
the application and authenticated user. It must allow creation only for an
approved application and must write an immutable audit/history record for each
disbursement event.

### UpdateDisbursementRequest

`PATCH /api/disbursement/{id}`

```ts
interface UpdateDisbursementRequest {
  status?: 'pending' | 'processed' | 'failed' | 'completed';
  disbursementDate?: string;
  referenceNumber?: string;
  remark?: string;
}
```

### Disbursement endpoints

| Method | Endpoint | Request DTO | Response DTO |
|---|---|---|---|
| GET | `/api/disbursement/my` | `DisbursementListQuery` | `ApiResponse<PageResponse<DisbursementSummaryDto>>` |
| GET | `/api/disbursement/{id}` | Path `id` | `ApiResponse<DisbursementSummaryDto>` |
| POST | `/api/disbursement` | `CreateDisbursementRequest` | `ApiResponse<DisbursementSummaryDto>` |
| PATCH | `/api/disbursement/{id}` | `UpdateDisbursementRequest` | `ApiResponse<DisbursementSummaryDto>` |

```ts
interface DisbursementListQuery extends PageRequest {
  applicationType?: 'loan' | 'scholarship';
  status?: 'pending' | 'processed' | 'failed' | 'completed';
  applicationId?: number;
  studentId?: number;
  fromDate?: string;
  toDate?: string;
}
```

## Student Document DTOs

### UploadStudentDocumentRequest

`POST /api/student-document/upload`

This endpoint should use `multipart/form-data`, not JSON.

```ts
interface UploadStudentDocumentForm {
  file: File; // PDF only, size limit enforced by backend as well as UI
  documentTypeId: number;
}
```

### StudentDocumentResponseDto

```ts
interface StudentDocumentResponseDto {
  id: number;
  studentId: number;
  documentType: LookupDto;
  fileName: string;
  fileSize?: number;
  contentType: 'application/pdf';
  verificationStatus: 'pending' | 'verified' | 'rejected';
  verificationRemark?: string;
  verifiedBy?: UserSummaryDto;
  verifiedAt?: string;
  uploadedAt: string;
  viewUrl?: string;
}
```

### VerifyStudentDocumentRequest

`PATCH /api/student-document/verify`

```ts
interface VerifyStudentDocumentRequest {
  documentId: number;
  remark?: string;
}
```

### RejectStudentDocumentRequest

`PATCH /api/student-document/reject`

```ts
interface RejectStudentDocumentRequest {
  documentId: number;
  remark: string;
}
```

### Student document endpoints

| Method | Endpoint | Request DTO | Response DTO |
|---|---|---|---|
| POST | `/api/student-document/upload` | `UploadStudentDocumentForm` multipart | `ApiResponse<StudentDocumentResponseDto>` |
| GET | `/api/student-document/view` | `StudentDocumentListQuery` | `ApiResponse<PageResponse<StudentDocumentResponseDto>>` |
| PATCH | `/api/student-document/verify` | `VerifyStudentDocumentRequest` | `ApiResponse<StudentDocumentResponseDto>` |
| PATCH | `/api/student-document/reject` | `RejectStudentDocumentRequest` | `ApiResponse<StudentDocumentResponseDto>` |

```ts
interface StudentDocumentListQuery extends PageRequest {
  studentId?: number; // institute, bank, government, or admin only
  documentTypeId?: number;
  verificationStatus?: 'pending' | 'verified' | 'rejected';
}
```

`GET /api/student-document/view` should return metadata and a protected,
short-lived `viewUrl`; it should not expose the server's `file_path`.

## Validation and authorization summary

| Area | Required rule |
|---|---|
| Scheme creation | Bank creates loan schemes; government creates scholarship schemes |
| Scheme update/delete | Only the owning role or admin; delete is a soft delete |
| Eligible schemes | Filter by active dates, status, student profile, category, profession, age, and income where applicable |
| Application apply | Student only, complete profile, permanently verified institute status, eligible active scheme |
| Active applications | One active loan and one active scholarship application per student |
| Rejected applications | Cannot be re-submitted according to the workflow |
| Application decisions | Bank decides loans; government decides scholarships; reject/revert requires a remark |
| Disbursement creation | Only approved applications; derive student/type from the application |
| Document verification | Institute/admin or the configured reviewing role; rejection requires a remark |
| File security | Validate PDF type and size on the server; never return physical storage paths |

## Documentation decisions to confirm

1. The schema lists `disbursement_status` as `pending`/`disbursed`, while the workflow lists `pending`/`processed`/`failed`/`completed`. This proposal uses the more complete workflow enum.
2. The schema does not define an application `status` column directly, but the workflow and history require it. The `applications` table should contain the current status and `application_history` should retain every transition.
3. The endpoint list uses the same path for different operations. This proposal assumes `POST` for create, `PUT` for full scheme update, `PATCH` for status/update operations, and `DELETE` for soft delete.
4. The documentation does not define a separate application-review list endpoint for bank/government/institute users. Until one is added, role-based behavior can be implemented on `/api/application/my`; a dedicated `/api/application/review` endpoint would be clearer.
5. The documentation mentions `reference_number` and `remark` for disbursements but omits them from the schema table. They are included here because they are required for actual tracking.
