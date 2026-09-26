ALTER TABLE applications
    ADD COLUMN IF NOT EXISTS requested_loan_amount NUMERIC(15, 2),
    ADD COLUMN IF NOT EXISTS academic_percentage NUMERIC(5, 2),
    ADD COLUMN IF NOT EXISTS loan_purpose VARCHAR(500),
    ADD COLUMN IF NOT EXISTS repayment_tenure_years INTEGER,
    ADD COLUMN IF NOT EXISTS co_borrower_name VARCHAR(150),
    ADD COLUMN IF NOT EXISTS co_borrower_income NUMERIC(15, 2);
