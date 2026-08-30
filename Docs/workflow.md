# VidyaSahay Workflow

## Overview
VidyaSahay is a single portal for students to apply for education loans and scholarships without moving across fragmented systems.

The portal supports these roles:
- Student
- Institute
- Bank
- Government
- Admin

Each role logs in with a username and password.

## Core Rules
- A student can self-register.
- Bank and Institute accounts are created only by Admin.
- A student must complete the profile before applying for any loan or scholarship.
- Institute verification is required once per student profile.
- Only after institute verification can the student apply for loan and scholarship schemes.
- A student can have only one active loan application at a time.
- A student can have only one active scholarship application at a time.
- Rejected applications cannot be re-submitted.
- Institute verification is permanent unless changed by an admin workflow later.
- Every disbursement must be logged.
- Disbursement tracking uses a shared model for both loan and scholarship disbursements.
- The portal supports in-app notifications (Future-Scope).

## Role Summary

### Student
Student can:
- Register
- Log in
- Complete and update profile
- Upload required documents
- View loan schemes
- View scholarship schemes
- Apply for a loan scheme
- Apply for a scholarship scheme
- View application status
- View notifications
- View disbursement history

### Institute
Institute can:
- Log in
- View assigned student profiles
- Verify or reject a student profile based on identification
- View loan and scholarship application status of verified students
- Monitor student requests related to institute verification

### Bank
Bank can:
- Log in
- Create, update, read, and delete loan schemes
- View loan applications
- Approve, reject, or revert applications with remarks
- Monitor disbursement records

### Government
Government can:
- Log in
- Create, update, read, and delete scholarship schemes
- View scholarship applications
- Approve, reject, or revert applications with remarks
- Monitor disbursement records

### Admin
Admin can:
- Log in
- Create bank profiles
- Create institute profiles
- View students
- View banks
- View institutes
- View loan schemes
- View scholarship schemes

## Student Flow

### 1. Registration and Login
- Student registers using the portal.
- Student logs in with username and password.

### 2. Profile Completion
- After login, student must complete the profile.
- Profile should include the student identity and the required document information.
- Until the profile is complete, the student cannot apply for any scheme.

### 3. Institute Verification
- Once the student profile is complete, it goes to the Institute for verification.
- Institute checks identification and approves or rejects the student profile.
- If approved, the student becomes eligible to apply for loans and scholarships.
- If rejected, the student is not allowed to apply.

### 4. Scheme Browsing
- Verified or not-verified student can browse available loan schemes and scholarship schemes based on eligibility but can only apply after verified from institute.


### 5. Application Submission
- Student can apply for one active loan application.
- Student can apply for one active scholarship application.
- Student uploads the required scheme documents during the application process.

### 6. Tracking and Updates
- Student can view application status at any time.
- Student receives in-app notifications for important events (future-scope).
- Student can view disbursement history once approved and paid.

## Institute Flow

### Verification Flow
- Institute receives the student profile for verification.
- Institute validates identity and supporting documents.
- Institute approves the profile if the student is genuine.
- Institute rejects the profile if the identity is not valid.

### After Verification
- The student profile remains verified permanently.
- Verified status is used as the eligibility gate for loan and scholarship applications.

## Bank Flow

### Loan Scheme Management
Bank can manage loan schemes:
- Create scheme
- Edit scheme
- View scheme
- Delete scheme (Soft-delete by changing the status)

Each loan scheme may contain:
- Scheme name
- Eligibility criteria
- Interest details
- Loan amount range
- Tenure
- Required documents
- Active or inactive status

### Loan Application Processing
- Bank views loan applications from verified students.
- Bank can approve the application.
- Bank can reject the application with a remark.
- Bank can revert the application with a remark if more information is needed.

### Disbursement Tracking
- After approval, disbursement records are created.
- Every disbursement is logged.
- Bank can monitor:
  - amount
  - disbursement date
  - disbursement status
  - reference number
  - remarks

## Government Flow

### Scholarship Scheme Management
Government can manage scholarship schemes:
- Create scheme
- Edit scheme
- View scheme
- Delete scheme (Soft-delete by changing the status)

Each scholarship scheme may contain:
- Scheme name
- Eligibility criteria
- Scholarship amount
- Application period
- Required documents
- Active or inactive status

### Scholarship Application Processing
- Government views scholarship applications from verified students.
- Government can approve the application.
- Government can reject the application with a remark.
- Government can revert the application with a remark if more information is needed.

### Disbursement Tracking
- After approval, disbursement records are created.
- Every disbursement is logged.
- Government can monitor:
  - amount
  - disbursement date
  - disbursement status
  - reference number
  - remarks

## Admin Flow
Admin is the master management role.

Admin can:
- Create institute accounts
- Create bank accounts
- View all students
- View all loan schemes
- View all scholarship schemes
- Support platform administration and account provisioning

## Notifications
The portal should support in-app notifications.

Typical notification events:
- Student profile submitted
- Institute verification approved
- Institute verification rejected
- Loan application submitted
- Scholarship application submitted
- Application approved
- Application rejected
- Application reverted
- Disbursement created
- Disbursement updated

Suggested notification fields:
- id
- user_id
- title
- message
- type
- reference_id
- is_read
- created_at

## Shared Disbursement Model
Use one common disbursement model for both loans and scholarships.

Suggested disbursement fields:
- id
- application_id
- application_type
- beneficiary_user_id
- amount
- disbursement_date
- reference_number
- status
- remark
- created_by
- created_at
- updated_at

## Suggested Application States

### Student Profile
- `draft`
- `submitted`
- `verified`
- `rejected`

### Loan Application
- `draft`
- `submitted`
- `under_review`
- `approved`
- `rejected`
- `reverted`
- `disbursed`

### Scholarship Application
- `draft`
- `submitted`
- `under_review`
- `approved`
- `rejected`
- `reverted`
- `disbursed`

### Disbursement
- `pending`
- `processed`
- `failed`
- `completed`

## High-Level Workflow
1. Student registers and logs in.
2. Student completes profile and uploads documents.
3. Institute verifies the student profile once.
4. Verified student becomes eligible to apply.
5. Student applies for one loan application and one scholarship application.
6. Bank processes loan applications.
7. Government processes scholarship applications.
8. Approved applications trigger disbursement records.
9. Every disbursement is logged and trackable.
10. In-app notifications keep users informed about status changes.

## Notes
- Documents can be kept flexible for now and finalized later.
- The workflow is designed so that profile verification is the main eligibility gate.
- Rejection ends the current application attempt.
- The system should preserve auditability for approvals, rejections, reverts, and disbursements.
