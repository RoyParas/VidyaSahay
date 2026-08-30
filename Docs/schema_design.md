# VidyaSahay Schema Design Proposal

This document proposes a normalized schema for VidyaSahay based on the current workflow:

- Students register and complete profiles
- Institutes verify student profiles once
- Verified students apply for one active loan and one active scholarship at a time
- Banks manage loan schemes and loan applications
- Government manages scholarship schemes and scholarship applications
- Disbursements are tracked in a shared model
- In-app notifications are supported

## Design Goals

- Keep the schema normalized and easy to query
- Separate master data, transactional data, and audit data
- Support role-based access cleanly
- Preserve a full history of status changes and remarks
- Make disbursement tracking explicit and auditable
- Allow reporting without relying on arrays of foreign keys

## Naming Convention

Recommended naming standard:

- Use `snake_case` for all tables and columns
- Use singular names for tables where possible
- Use `_id` for foreign keys
- Use `created_at`, `updated_at`, and `created_by` consistently
- Use `status` fields with controlled enum values

## Core Reference Tables

### roles
Stores the supported portal roles.

- `id` PK
- `name` unique
- `created_at`
- `updated_at`

Suggested role values:
- `student`
- `bank`
- `government`
- `institute`
- `admin`

### users
Stores login and identity details for every portal user.

- `id` PK
- `role_id` FK -> `roles.id`
- `first_name`
- `last_name`
- `email` unique
- `mobile` unique
- `hashed_password`
- `must_change_password` boolean
- `is_active` boolean
- `created_at`
- `updated_at`

Notes:
- Every portal account should come from this table.
- Students self-register here.
- Bank and institute users are created by admin.

### addresses
Stores common address details.

- `id` PK
- `country`
- `state`
- `district`
- `city`
- `created_at`
- `updated_at`

## Student Domain

### students
Stores student profile data.

- `id` PK
- `user_id` FK -> `users.id`
- `institute_id` FK -> `institutes.id` nullable until linked
- `address_id` FK -> `addresses.id`
- `location`
- `pincode`
- `aadhar_number` unique
- `gender`
- `date_of_birth`
- `father_name`
- `mother_name`
- `fees_paid`
- `fees_pending`
- `annual_family_income`
- `category_id` FK -> `categories.id`
- `course_id` FK -> `courses.id`
- `institute_verification_status` enum: `pending`, `verified`, `rejected`
- `created_at`
- `updated_at`

Notes:
- Institute verification is a one-time gate for eligibility.

### student_documents
Stores uploaded documents for students.

- `id` PK
- `student_id` FK -> `students.id`
- `file_name`
- `file_path`
- `verification_status` enum: `pending`, `verified`, `rejected`
- `verified_by` FK -> `users.id` nullable
- `verified_at` nullable
- `created_at`
- `updated_at`

Notes:
- Provide constraint to upload only pdf file within the size limit on UI itself
- The actual file should usually be stored in the filesystem or object storage.

### categories
Stores caste/category information used across the portal.

- `id` PK
- `code` unique nullable
- `created_at`
- `updated_at`

Suggested values:
- `general`
- `ebc`
- `obc`
- `sc`
- `st`
- `ews`
- `nt`
- `sebc`
- `minority`

## Institute Domain

### institutes
Stores institute profile data.

- `id` PK
- `user_id` FK -> `users.id`
- `name`
- `address_id` FK -> `addresses.id`
- `location`
- `pincode`
- `bank_name`
- `branch_name`
- `ifsc_code`
- `account_number`
- `created_at`
- `updated_at`

## Bank Domain

### banks
Stores bank partner profile data.

- `id` PK
- `user_id` FK -> `users.id`
- `name`
- `created_at`
- `updated_at`


## Academic Domain

### professions
Master list for academic or professional tracks.

- `id` PK
- `name` unique
- `created_at`
- `updated_at`

Example values:
- Engineering
- MBA
- Medical
- Law
- CA
- PhD

### courses
Stores courses linked to institutes and professions.

- `id` PK
- `institute_id` FK -> `institutes.id`
- `profession_id` FK -> `professions.id`
- `name`
- `duration_years`
- `fees`
- `created_at`
- `updated_at`

## Document Master

### document_types
Master list of allowed document types.

- `id` PK
- `name` unique
- `description` nullable
- `created_at`
- `updated_at`

Examples:
- Aadhaar Card
- Income Certificate
- Caste Certificate
- Domicile Certificate
- Marksheet
- Admission Proof
- Bonafide Certificate
- Bank Passbook
- Disability Certificate
- Photograph

## Loan Scheme Domain

### loan_schemes
Stores loan scheme master data.

- `id` PK
- `name`
- `status` enum: `active`, `inactive`
- `effective_from`
- `effective_to`
- `min_loan_amount`
- `max_loan_amount`
- `interest_type` enum: `fixed`, `floating`
- `minimum_rate`
- `maximum_rate`
- `disbursement_type` enum: `yearly`, `one_time`
- `created_by` FK -> `users.id`
- `updated_by` FK -> `users.id`
- `created_at`
- `updated_at`

### loan_scheme_eligibility
Stores generic eligibility criteria for a loan scheme.

- `id` PK
- `loan_scheme_id` FK -> `loan_schemes.id`
- `min_age`
- `max_age`
- `co_borrower_required` boolean
- `min_credit_score` nullable
- `created_at`
- `updated_at`

### loan_scheme_professions
Mapping table for eligible professions.

- `id` PK
- `loan_scheme_id` FK -> `loan_schemes.id`
- `profession_id` FK -> `professions.id`

### loan_scheme_required_documents
Mapping table for required documents.

- `id` PK
- `loan_scheme_id` FK -> `loan_schemes.id`
- `document_type_id` FK -> `document_types.id`

### loan_scheme_moratoriums
Stores moratorium rules for loan schemes.

- `id` PK
- `loan_scheme_id` FK -> `loan_schemes.id`
- `course_period_included` boolean
- `additional_months`
- `created_at`
- `updated_at`

### loan_scheme_repayment_rules
Stores repayment rules for loan schemes.

- `id` PK
- `loan_scheme_id` FK -> `loan_schemes.id`
- `min_tenure_years`
- `max_tenure_years`
- `prepayment_allowed` boolean
- `foreclosure_charges`
- `created_at`
- `updated_at`

## Scholarship Scheme Domain

### scholarship_schemes
Stores scholarship scheme master data.

- `id` PK
- `name`
- `scholarship_type` enum: `merit_based`, `general`, `category_based`
- `academic_year`
- `start_date`
- `end_date`
- `status` enum: `active`, `inactive`
- `created_by` FK -> `users.id`
- `updated_by` FK -> `users.id`
- `created_at`
- `updated_at`

### scholarship_scheme_eligibility
Stores generic eligibility criteria for a scholarship scheme.

- `id` PK
- `scholarship_scheme_id` FK -> `scholarship_schemes.id`
- `minimum_age` nullable
- `maximum_age` nullable
- `maximum_annual_family_income` nullable
- `minimum_percentage_criteria` nullable
- `created_at`
- `updated_at`

### scholarship_scheme_categories
Mapping table for eligible categories.

- `id` PK
- `scholarship_scheme_id` FK -> `scholarship_schemes.id`
- `category_id` FK -> `categories.id`

### scholarship_scheme_professions
Mapping table for eligible professions.

- `id` PK
- `scholarship_scheme_id` FK -> `scholarship_schemes.id`
- `profession_id` FK -> `professions.id`

### scholarship_scheme_required_documents
Mapping table for required documents.

- `id` PK
- `scholarship_scheme_id` FK -> `scholarship_schemes.id`
- `document_type_id` FK -> `document_types.id`

### scholarship_benefit_details
Stores benefit and payment structure for scholarship schemes.

- `id` PK
- `scholarship_scheme_id` FK -> `scholarship_schemes.id`
- `scholarship_amount`
- `amount_type` enum: `fixed_amount`, `percentage_of_fees`
- `payment_frequency` enum: `one_time`, `monthly`, `quarterly`, `yearly`
- `total_scheme_budget`
- `created_at`
- `updated_at`

## Application Domain

### applications
Stores all application headers in one table.

- `id` PK
- `student_id` FK -> `students.id`
- `application_type` enum: `loan`, `scholarship`
- `scheme_id`
- `scheme_type` enum: `loan_scheme`, `scholarship_scheme`
- `submitted_at` nullable
- `created_at`
- `updated_at`

Notes:
- This approach keeps one application table while preserving clear scheme linkage.
- Business rules should enforce only one active loan application and one active scholarship application per student.

### application_documents
Mapping table between applications and uploaded documents.

- `id` PK
- `application_id` FK -> `applications.id`
- `student_document_id` FK -> `student_documents.id`
- `created_at`

### application_history
Stores every status transition and remark.

- `id` PK
- `application_id` FK -> `applications.id`
- `status` enum: `draft`, `submitted`, `under_review`, `reverted`, `rejected`, `approved`
- `remark`
- `action_by_user_id` FK -> `users.id`
- `assigned_to_user_id` FK -> `users.id` nullable
- `created_at`

Notes:
- This table is important for auditability and reports.
- A single application can have many history rows.

## Verification Domain

### student_verifications
Stores the one-time institute verification record for a student.

- `id` PK
- `student_id` FK -> `students.id` unique
- `institute_id` FK -> `institutes.id`
- `status` enum: `pending`, `verified`, `rejected`
- `remark`
- `verified_by` FK -> `users.id` nullable
- `verified_at` nullable
- `created_at`
- `updated_at`

Notes:
- This is cleaner than mixing institute verification into the generic applications table.
- Because verification is permanent, one student should generally have one verification record.

## Disbursement Domain

### disbursements
Stores shared disbursement records for both loans and scholarships.

- `id` PK
- `application_id` FK -> `applications.id`
- `application_type` enum: `loan`, `scholarship`
- `student_id` FK -> `students.id`
- `disbursed_by_user_id` FK -> `users.id`
- `amount`
- `disbursement_date`
- `created_at`
- `updated_at`

## Notification Domain (Future-Scope)

### notifications
Stores in-app notifications for all roles.

- `id` PK
- `user_id` FK -> `users.id`
- `title`
- `message`
- `notification_type` enum: `verification`, `application_status`, `disbursement`, `scheme_update`, `system`
- `reference_type` nullable
- `reference_id` nullable
- `is_read` boolean
- `read_at` nullable
- `created_at`

## Reporting Support

For reporting, the following tables will be the main sources:

- `students`
- `student_verifications`
- `applications`
- `application_history`
- `loan_schemes`
- `scholarship_schemes`
- `disbursements`
- `notifications`

Recommended report dimensions:

- Date range
- Role
- Status
- Scheme
- Institute
- Category
- Profession
- Disbursement status

## Suggested Indexes

Add indexes for these columns:

- `users.email`
- `users.username`
- `users.role_id`
- `students.user_id`
- `students.institute_id`
- `students.aadhar_number`
- `applications.student_id`
- `applications.application_type`
- `applications.scheme_id`
- `applications.status`
- `application_history.application_id`
- `disbursements.application_id`
- `disbursements.student_id`
- `notifications.user_id`
- `notifications.is_read`

## Suggested Constraints

- One `user` per account
- One `student_verifications` row per student
- One active loan application per student
- One active scholarship application per student
- Unique `aadhar_number` for students
- Unique `email` and `username` for users
- Unique `ifsc_code` for bank branches
- Enforce foreign keys for all master and mapping tables

## Recommended Status Values

### student_verifications.status
- `pending`
- `verified`
- `rejected`

### applications.status
- `draft`
- `submitted`
- `under_review`
- `reverted`
- `rejected`
- `approved`
- `disbursed`

### disbursements.status
- `pending`
- `processed`
- `failed`
- `completed`

### notifications.notification_type
- `verification`
- `application_status`
- `disbursement`
- `scheme_update`
- `system`

## Implementation Notes

- Keep all workflow changes in `application_history` and `disbursement_history`
- Avoid storing arrays of foreign keys in the database
- Keep document metadata separate from file storage
- Use admin-created records only for bank and institute user accounts
- Keep institute verification separate from scheme application logic
- Use consistent auditing fields in all major transactional tables

## Suggested Next Step

If you want to move from design to implementation, the next useful step is to convert this proposal into:

1. SQL DDL scripts
2. Entity classes/models
3. API endpoint design
4. A migration plan for your current schema