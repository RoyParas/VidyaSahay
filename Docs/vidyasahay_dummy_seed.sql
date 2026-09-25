-- VidyaSahay complete PostgreSQL dummy seed data
-- Assumptions: schema is dev; enum literals match your Java enums.
-- No pgcrypto is required because every UUID is supplied explicitly.
-- This script creates 5 rows in each business table. To satisfy unique user_id
-- constraints on 5 students, 5 institutes and 5 banks, it creates 17 users.

BEGIN;
SET search_path TO dev;

INSERT INTO roles (id, name, created_at, updated_at) VALUES
    ('277bb8c6-795d-5e9d-9033-b34045e64a0c', 'STUDENT', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('069ea755-cfa0-5088-8065-c48f0bc466f1', 'ADMIN', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('238c8155-f2b4-5ee6-a388-89b67bf17032', 'INSTITUTE', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('ce3dfb00-ad64-5a7f-8f36-37e331edf162', 'BANK', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('0e649790-23e5-5a8f-b325-21d0616bc60a', 'GOVERNMENT', '2026-09-17 10:00:00', '2026-09-17 10:00:00');

INSERT INTO users (id, role_id, first_name, last_name, email, mobile, hashed_password, must_change_password, is_active, created_at, updated_at) VALUES
    ('9387ed19-7a71-58fc-ad83-08224477581d', '277bb8c6-795d-5e9d-9033-b34045e64a0c', 'Student1', 'Demo', 'student1@vidyasahay.com', '9000000001', '$2a$10$wTP.bLmQZOmOco4sO9tnJeKH6cwExMXnxg87RUmz72vJaNR6WQBDW', False, True, '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('4f5aa9dd-ecc0-5833-be84-2385724e36df', '277bb8c6-795d-5e9d-9033-b34045e64a0c', 'Student2', 'Demo', 'student2@vidyasahay.com', '9000000002', '$2a$10$wTP.bLmQZOmOco4sO9tnJeKH6cwExMXnxg87RUmz72vJaNR6WQBDW', False, True, '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('21c9404f-498c-551a-b724-78ce8349a745', '277bb8c6-795d-5e9d-9033-b34045e64a0c', 'Student3', 'Demo', 'student3@vidyasahay.com', '9000000003', '$2a$10$wTP.bLmQZOmOco4sO9tnJeKH6cwExMXnxg87RUmz72vJaNR6WQBDW', False, True, '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('b491d348-7d74-5500-aa07-91a1f52c29cf', '277bb8c6-795d-5e9d-9033-b34045e64a0c', 'Student4', 'Demo', 'student4@vidyasahay.com', '9000000004', '$2a$10$wTP.bLmQZOmOco4sO9tnJeKH6cwExMXnxg87RUmz72vJaNR6WQBDW', False, True, '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('87cd519f-2efd-5241-9500-404611c2df6e', '277bb8c6-795d-5e9d-9033-b34045e64a0c', 'Student5', 'Demo', 'student5@vidyasahay.com', '9000000005', '$2a$10$wTP.bLmQZOmOco4sO9tnJeKH6cwExMXnxg87RUmz72vJaNR6WQBDW', False, True, '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('e0097d3c-23bd-5f48-9036-3032d6b6061e', '238c8155-f2b4-5ee6-a388-89b67bf17032', 'Institute1', 'Demo', 'institute1@vidyasahay.com', '9000000006', '$2a$10$wTP.bLmQZOmOco4sO9tnJeKH6cwExMXnxg87RUmz72vJaNR6WQBDW', False, True, '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('d813e2b7-0cbb-5f35-bc1c-6544008e5fde', '238c8155-f2b4-5ee6-a388-89b67bf17032', 'Institute2', 'Demo', 'institute2@vidyasahay.com', '9000000007', '$2a$10$wTP.bLmQZOmOco4sO9tnJeKH6cwExMXnxg87RUmz72vJaNR6WQBDW', False, True, '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('b7ec7b62-61b5-52b1-86cf-7ec4c462323f', '238c8155-f2b4-5ee6-a388-89b67bf17032', 'Institute3', 'Demo', 'institute3@vidyasahay.com', '9000000008', '$2a$10$wTP.bLmQZOmOco4sO9tnJeKH6cwExMXnxg87RUmz72vJaNR6WQBDW', False, True, '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('b3835a3c-da19-5cd1-a112-2136af2475a6', '238c8155-f2b4-5ee6-a388-89b67bf17032', 'Institute4', 'Demo', 'institute4@vidyasahay.com', '9000000009', '$2a$10$wTP.bLmQZOmOco4sO9tnJeKH6cwExMXnxg87RUmz72vJaNR6WQBDW', False, True, '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('478c4990-cb87-519a-a690-8622f8c71cf8', '238c8155-f2b4-5ee6-a388-89b67bf17032', 'Institute5', 'Demo', 'institute5@vidyasahay.com', '9000000010', '$2a$10$wTP.bLmQZOmOco4sO9tnJeKH6cwExMXnxg87RUmz72vJaNR6WQBDW', False, True, '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('47dcedee-f5d6-5a93-a9c6-1425aa435cf6', 'ce3dfb00-ad64-5a7f-8f36-37e331edf162', 'Bank1', 'Demo', 'bank1@vidyasahay.com', '9000000011', '$2a$10$wTP.bLmQZOmOco4sO9tnJeKH6cwExMXnxg87RUmz72vJaNR6WQBDW', False, True, '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('8e750dbb-b277-5e98-94ec-98965e3b85ca', 'ce3dfb00-ad64-5a7f-8f36-37e331edf162', 'Bank2', 'Demo', 'bank2@vidyasahay.com', '9000000012', '$2a$10$wTP.bLmQZOmOco4sO9tnJeKH6cwExMXnxg87RUmz72vJaNR6WQBDW', False, True, '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('6bc45c49-0ba3-5959-a46b-69ac19501d16', 'ce3dfb00-ad64-5a7f-8f36-37e331edf162', 'Bank3', 'Demo', 'bank3@vidyasahay.com', '9000000013', '$2a$10$wTP.bLmQZOmOco4sO9tnJeKH6cwExMXnxg87RUmz72vJaNR6WQBDW', False, True, '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('955ef5b5-1ad2-55e8-8288-6d69ace5e415', 'ce3dfb00-ad64-5a7f-8f36-37e331edf162', 'Bank4', 'Demo', 'bank4@vidyasahay.com', '9000000014', '$2a$10$wTP.bLmQZOmOco4sO9tnJeKH6cwExMXnxg87RUmz72vJaNR6WQBDW', False, True, '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('c5f70aac-0c9c-5f71-a1d0-e97d79af2ad8', 'ce3dfb00-ad64-5a7f-8f36-37e331edf162', 'Bank5', 'Demo', 'bank5@vidyasahay.com', '9000000015', '$2a$10$wTP.bLmQZOmOco4sO9tnJeKH6cwExMXnxg87RUmz72vJaNR6WQBDW', False, True, '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('8717897d-0892-5cc1-bf47-0aa88ef5e4f0', '069ea755-cfa0-5088-8065-c48f0bc466f1', 'Admin', 'Demo', 'admin1@vidyasahay.com', '9000000016', '$2a$10$wTP.bLmQZOmOco4sO9tnJeKH6cwExMXnxg87RUmz72vJaNR6WQBDW', False, True, '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('1bfd80a7-b582-55cc-9ec6-eddfd256f103', '0e649790-23e5-5a8f-b325-21d0616bc60a', 'Government', 'Demo', 'government1@vidyasahay.com', '9000000017', '$2a$10$wTP.bLmQZOmOco4sO9tnJeKH6cwExMXnxg87RUmz72vJaNR6WQBDW', False, True, '2026-09-17 10:00:00', '2026-09-17 10:00:00');

INSERT INTO addresses (id, country, state, district, city, created_at, updated_at) VALUES
    ('fc6212eb-8dde-5139-93a4-5e05feab190a', 'India', 'Maharashtra', 'Mumbai Suburban', 'Mumbai', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('f2474222-6915-5999-b905-5e3c1ed60ac2', 'India', 'Maharashtra', 'Pune', 'Pune', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('18ba8ff8-9dd0-5552-b27f-e203861a1679', 'India', 'Maharashtra', 'Nagpur', 'Nagpur', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('a2d0b4f8-b8e4-5c68-971a-e7650a79e278', 'India', 'Maharashtra', 'Nashik', 'Nashik', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('ee3bb9e1-924b-5555-8b1b-c20af7794cfc', 'India', 'Maharashtra', 'Thane', 'Thane', '2026-09-17 10:00:00', '2026-09-17 10:00:00');

INSERT INTO categories (id, code, created_at, updated_at) VALUES
    ('bc8c5235-2ac6-5588-8b30-2d70930249fa', 'OPEN', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('6c90e5b0-dc2b-5346-a6cc-4dee59268270', 'OBC', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('108c5c9d-e83e-52f6-9dc2-9ba8156419b6', 'SC', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('a33f9a88-61f0-5896-8904-c9d00639becc', 'ST', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('eb1e141b-ce09-5583-81b3-cc8370cf45a8', 'EWS', '2026-09-17 10:00:00', '2026-09-17 10:00:00');

INSERT INTO professions (id, name, created_at, updated_at) VALUES
    ('e9a0b5f3-3272-5d04-918a-b6a7c7cb40a2', 'Engineering', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('f9100406-fbd3-5f06-a2c9-ee4ee8781b8c', 'Medical', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('274c63af-ee2b-508e-b2fe-bfefcac2dfd0', 'Management', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('e3ce7ffc-1cc8-54d9-ac8f-5cbeb34ae3de', 'Law', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('4f0e39e5-2e8a-5115-bc4f-1b025ebe36c8', 'Arts', '2026-09-17 10:00:00', '2026-09-17 10:00:00');

INSERT INTO document_types (id, name, description, created_at, updated_at) VALUES
    ('4fb58687-055a-5058-ad58-83c9e8d86274', 'Aadhaar Card', 'Identity proof', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('5648a211-b8c7-561a-8fce-ea0b389f3f43', 'Income Certificate', 'Family income proof', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('5e3f0077-6a80-58b9-9b56-795109e1ced6', 'Marksheet', 'Academic marksheet', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('81ae888b-e753-507d-a907-fe18e0c905cf', 'Admission Letter', 'Institute admission proof', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('7aab9aee-20c4-5c77-b6ce-11cad563bf3c', 'Bank Passbook', 'Student bank details', '2026-09-17 10:00:00', '2026-09-17 10:00:00');

INSERT INTO institutes (id, user_id, name, address_id, location, pincode, bank_name, branch_name, ifsc_code, account_number, created_at, updated_at) VALUES
    ('aaa0c01a-4bb9-5310-8db4-74a4f0bdbacb', 'e0097d3c-23bd-5f48-9036-3032d6b6061e', 'Demo Institute 1', 'fc6212eb-8dde-5139-93a4-5e05feab190a', 'Campus 1', 400001, 'State Bank of India', 'Branch 1', 'SBIN0000001', '100000000001', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('1454bc20-7b4a-5518-a416-b2c71bea9043', 'd813e2b7-0cbb-5f35-bc1c-6544008e5fde', 'Demo Institute 2', 'f2474222-6915-5999-b905-5e3c1ed60ac2', 'Campus 2', 400002, 'State Bank of India', 'Branch 2', 'SBIN0000002', '100000000002', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('3ee3cd89-808e-50cb-aee5-8c347d9eac27', 'b7ec7b62-61b5-52b1-86cf-7ec4c462323f', 'Demo Institute 3', '18ba8ff8-9dd0-5552-b27f-e203861a1679', 'Campus 3', 400003, 'State Bank of India', 'Branch 3', 'SBIN0000003', '100000000003', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('18148b9d-4f9d-580b-83fa-559eb4543433', 'b3835a3c-da19-5cd1-a112-2136af2475a6', 'Demo Institute 4', 'a2d0b4f8-b8e4-5c68-971a-e7650a79e278', 'Campus 4', 400004, 'State Bank of India', 'Branch 4', 'SBIN0000004', '100000000004', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('83d05360-f372-5339-a053-6fee1ae76522', '478c4990-cb87-519a-a690-8622f8c71cf8', 'Demo Institute 5', 'ee3bb9e1-924b-5555-8b1b-c20af7794cfc', 'Campus 5', 400005, 'State Bank of India', 'Branch 5', 'SBIN0000005', '100000000005', '2026-09-17 10:00:00', '2026-09-17 10:00:00');

INSERT INTO banks (id, user_id, name, created_at, updated_at) VALUES
    ('eacd169b-f66a-54a9-98d6-ebff13531bb5', '47dcedee-f5d6-5a93-a9c6-1425aa435cf6', 'Demo Bank 1', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('55a648a4-b1f5-5820-af60-67b34f06d43f', '8e750dbb-b277-5e98-94ec-98965e3b85ca', 'Demo Bank 2', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('76c9192d-3859-53d3-9dd9-cfee10df3a91', '6bc45c49-0ba3-5959-a46b-69ac19501d16', 'Demo Bank 3', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('d896be7c-849a-5f10-ad96-76dbf502a717', '955ef5b5-1ad2-55e8-8288-6d69ace5e415', 'Demo Bank 4', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('c976a81b-da15-5301-9f53-7297005fff1d', 'c5f70aac-0c9c-5f71-a1d0-e97d79af2ad8', 'Demo Bank 5', '2026-09-17 10:00:00', '2026-09-17 10:00:00');

INSERT INTO courses (id, institute_id, profession_id, name, duration_years, fees, created_at, updated_at) VALUES
    ('92342026-150f-5c77-8c8d-898f451f5d68', 'aaa0c01a-4bb9-5310-8db4-74a4f0bdbacb', 'e9a0b5f3-3272-5d04-918a-b6a7c7cb40a2', 'B.Tech', 4, '400000.00', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('7c37152e-9d48-5cde-8bfc-68403db227f8', '1454bc20-7b4a-5518-a416-b2c71bea9043', 'f9100406-fbd3-5f06-a2c9-ee4ee8781b8c', 'MBBS', 5, '900000.00', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('35b6c84a-c439-5831-ad22-6748a822bfa5', '3ee3cd89-808e-50cb-aee5-8c347d9eac27', '274c63af-ee2b-508e-b2fe-bfefcac2dfd0', 'MBA', 2, '500000.00', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('4c90a025-2390-50f9-9dbc-1273607603f8', '18148b9d-4f9d-580b-83fa-559eb4543433', 'e3ce7ffc-1cc8-54d9-ac8f-5cbeb34ae3de', 'LLB', 3, '250000.00', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('e5816c31-c198-5f04-a6d5-a3b8c2f591f6', '83d05360-f372-5339-a053-6fee1ae76522', '4f0e39e5-2e8a-5115-bc4f-1b025ebe36c8', 'BA', 3, '150000.00', '2026-09-17 10:00:00', '2026-09-17 10:00:00');

INSERT INTO students (id, user_id, institute_id, address_id, location, pincode, aadhar_number, gender, date_of_birth, father_name, mother_name, fees_paid, fees_pending, annual_family_income, category_id, course_id, created_at, updated_at) VALUES
    ('a4a9989c-c9b9-50fc-a5e6-ba5d478df18a', '9387ed19-7a71-58fc-ad83-08224477581d', 'aaa0c01a-4bb9-5310-8db4-74a4f0bdbacb', 'fc6212eb-8dde-5139-93a4-5e05feab190a', 'Hostel 1', 400001, '111122220001', 'MALE', '2001-01-15', 'Father 1', 'Mother 1', '50000.00', '350000.00', '300000.00', 'bc8c5235-2ac6-5588-8b30-2d70930249fa', '92342026-150f-5c77-8c8d-898f451f5d68', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('7113fd68-5592-5321-9116-5875afacf59a', '4f5aa9dd-ecc0-5833-be84-2385724e36df', '1454bc20-7b4a-5518-a416-b2c71bea9043', 'f2474222-6915-5999-b905-5e3c1ed60ac2', 'Hostel 2', 400002, '111122220002', 'FEMALE', '2002-01-15', 'Father 2', 'Mother 2', '50000.00', '850000.00', '250000.00', '6c90e5b0-dc2b-5346-a6cc-4dee59268270', '7c37152e-9d48-5cde-8bfc-68403db227f8', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('30d6f63a-5a6f-59e1-bd68-e1fe76d11d9f', '21c9404f-498c-551a-b724-78ce8349a745', '3ee3cd89-808e-50cb-aee5-8c347d9eac27', '18ba8ff8-9dd0-5552-b27f-e203861a1679', 'Hostel 3', 400003, '111122220003', 'MALE', '2003-01-15', 'Father 3', 'Mother 3', '50000.00', '450000.00', '200000.00', '108c5c9d-e83e-52f6-9dc2-9ba8156419b6', '35b6c84a-c439-5831-ad22-6748a822bfa5', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('7d26bef4-0946-5e91-a010-13f6c1b0810d', 'b491d348-7d74-5500-aa07-91a1f52c29cf', '18148b9d-4f9d-580b-83fa-559eb4543433', 'a2d0b4f8-b8e4-5c68-971a-e7650a79e278', 'Hostel 4', 400004, '111122220004', 'FEMALE', '2004-01-15', 'Father 4', 'Mother 4', '50000.00', '200000.00', '150000.00', 'a33f9a88-61f0-5896-8904-c9d00639becc', '4c90a025-2390-50f9-9dbc-1273607603f8', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('f3b6091a-ea91-561f-b600-5e0c0e77bd9a', '87cd519f-2efd-5241-9500-404611c2df6e', '83d05360-f372-5339-a053-6fee1ae76522', 'ee3bb9e1-924b-5555-8b1b-c20af7794cfc', 'Hostel 5', 400005, '111122220005', 'MALE', '2005-01-15', 'Father 5', 'Mother 5', '50000.00', '100000.00', '100000.00', 'eb1e141b-ce09-5583-81b3-cc8370cf45a8', 'e5816c31-c198-5f04-a6d5-a3b8c2f591f6', '2026-09-17 10:00:00', '2026-09-17 10:00:00');

INSERT INTO student_verifications (id, student_id, institute_id, status, remark, verified_by, verified_at, created_at, updated_at) VALUES
    ('7518ce58-4fef-56d2-b2f3-1f8557e05767', 'a4a9989c-c9b9-50fc-a5e6-ba5d478df18a', 'aaa0c01a-4bb9-5310-8db4-74a4f0bdbacb', 'VERIFIED', 'Student verified', 'e0097d3c-23bd-5f48-9036-3032d6b6061e', '2026-09-17 10:00:00', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('84249652-6ed4-5f06-aa0e-0f77a5037068', '7113fd68-5592-5321-9116-5875afacf59a', '1454bc20-7b4a-5518-a416-b2c71bea9043', 'VERIFIED', 'Student verified', 'd813e2b7-0cbb-5f35-bc1c-6544008e5fde', '2026-09-17 10:00:00', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('c293ee5f-d9b9-548c-88aa-7bd475f636f3', '30d6f63a-5a6f-59e1-bd68-e1fe76d11d9f', '3ee3cd89-808e-50cb-aee5-8c347d9eac27', 'VERIFIED', 'Student verified', 'b7ec7b62-61b5-52b1-86cf-7ec4c462323f', '2026-09-17 10:00:00', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('5fa94242-a371-5b5c-a150-03156e31341b', '7d26bef4-0946-5e91-a010-13f6c1b0810d', '18148b9d-4f9d-580b-83fa-559eb4543433', 'VERIFIED', 'Student verified', 'b3835a3c-da19-5cd1-a112-2136af2475a6', '2026-09-17 10:00:00', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('4fa4856b-8672-5e24-a6a4-097680613665', 'f3b6091a-ea91-561f-b600-5e0c0e77bd9a', '83d05360-f372-5339-a053-6fee1ae76522', 'VERIFIED', 'Student verified', '478c4990-cb87-519a-a690-8622f8c71cf8', '2026-09-17 10:00:00', '2026-09-17 10:00:00', '2026-09-17 10:00:00');

INSERT INTO student_documents (id, student_id, document_type_id, file_name, file_path, verification_status, verified_by, verified_at, created_at, updated_at) VALUES
    ('884e8f56-5d24-54a7-a968-8d9389d13edc', 'a4a9989c-c9b9-50fc-a5e6-ba5d478df18a', '4fb58687-055a-5058-ad58-83c9e8d86274', 'document_1.pdf', '/uploads/students/a4a9989c-c9b9-50fc-a5e6-ba5d478df18a/document_1.pdf', 'VERIFIED', 'e0097d3c-23bd-5f48-9036-3032d6b6061e', '2026-09-17 10:00:00', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('4dfc47ea-72ab-5647-9b30-c0f46491ab9d', '7113fd68-5592-5321-9116-5875afacf59a', '5648a211-b8c7-561a-8fce-ea0b389f3f43', 'document_2.pdf', '/uploads/students/7113fd68-5592-5321-9116-5875afacf59a/document_2.pdf', 'VERIFIED', 'd813e2b7-0cbb-5f35-bc1c-6544008e5fde', '2026-09-17 10:00:00', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('970681cb-e5e4-56e5-b8a8-9a36441c2968', '30d6f63a-5a6f-59e1-bd68-e1fe76d11d9f', '5e3f0077-6a80-58b9-9b56-795109e1ced6', 'document_3.pdf', '/uploads/students/30d6f63a-5a6f-59e1-bd68-e1fe76d11d9f/document_3.pdf', 'VERIFIED', 'b7ec7b62-61b5-52b1-86cf-7ec4c462323f', '2026-09-17 10:00:00', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('66821017-4bf9-5382-a172-ab25f446acb5', '7d26bef4-0946-5e91-a010-13f6c1b0810d', '81ae888b-e753-507d-a907-fe18e0c905cf', 'document_4.pdf', '/uploads/students/7d26bef4-0946-5e91-a010-13f6c1b0810d/document_4.pdf', 'VERIFIED', 'b3835a3c-da19-5cd1-a112-2136af2475a6', '2026-09-17 10:00:00', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('63988ee6-0264-5490-a528-7ab9634e39f3', 'f3b6091a-ea91-561f-b600-5e0c0e77bd9a', '7aab9aee-20c4-5c77-b6ce-11cad563bf3c', 'document_5.pdf', '/uploads/students/f3b6091a-ea91-561f-b600-5e0c0e77bd9a/document_5.pdf', 'VERIFIED', '478c4990-cb87-519a-a690-8622f8c71cf8', '2026-09-17 10:00:00', '2026-09-17 10:00:00', '2026-09-17 10:00:00');

INSERT INTO loan_schemes (id, bank_id, name, status, effective_from, effective_to, min_loan_amount, max_loan_amount, interest_type, minimum_rate, maximum_rate, disbursement_type, created_by, updated_by, created_at, updated_at) VALUES
    ('309d7984-5ac0-5587-8eb5-86575a198f61', 'eacd169b-f66a-54a9-98d6-ebff13531bb5', 'Education Loan 1', 'ACTIVE', '2026-01-01', '2030-12-31', '50000.00', '600000.00', 'FLOATING', '7.5000', '11.5000', 'INSTALLMENT', '47dcedee-f5d6-5a93-a9c6-1425aa435cf6', '47dcedee-f5d6-5a93-a9c6-1425aa435cf6', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('9fe875e8-b10a-5647-bf68-726451a45bb2', '55a648a4-b1f5-5820-af60-67b34f06d43f', 'Education Loan 2', 'ACTIVE', '2026-01-01', '2030-12-31', '50000.00', '700000.00', 'FLOATING', '7.5000', '11.5000', 'INSTALLMENT', '8e750dbb-b277-5e98-94ec-98965e3b85ca', '8e750dbb-b277-5e98-94ec-98965e3b85ca', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('2b2020e7-0e57-5cc7-8c43-ecb93a8eee78', '76c9192d-3859-53d3-9dd9-cfee10df3a91', 'Education Loan 3', 'ACTIVE', '2026-01-01', '2030-12-31', '50000.00', '800000.00', 'FLOATING', '7.5000', '11.5000', 'INSTALLMENT', '6bc45c49-0ba3-5959-a46b-69ac19501d16', '6bc45c49-0ba3-5959-a46b-69ac19501d16', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('5b77f441-dd90-53ac-8f6f-abd39236138b', 'd896be7c-849a-5f10-ad96-76dbf502a717', 'Education Loan 4', 'ACTIVE', '2026-01-01', '2030-12-31', '50000.00', '900000.00', 'FLOATING', '7.5000', '11.5000', 'INSTALLMENT', '955ef5b5-1ad2-55e8-8288-6d69ace5e415', '955ef5b5-1ad2-55e8-8288-6d69ace5e415', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('5d48af3d-1da6-5593-9c54-ca0ab0a95009', 'c976a81b-da15-5301-9f53-7297005fff1d', 'Education Loan 5', 'ACTIVE', '2026-01-01', '2030-12-31', '50000.00', '1000000.00', 'FLOATING', '7.5000', '11.5000', 'INSTALLMENT', 'c5f70aac-0c9c-5f71-a1d0-e97d79af2ad8', 'c5f70aac-0c9c-5f71-a1d0-e97d79af2ad8', '2026-09-17 10:00:00', '2026-09-17 10:00:00');

INSERT INTO loan_scheme_eligibility (id, loan_scheme_id, min_age, max_age, co_borrower_required, min_credit_score, created_at, updated_at) VALUES
    ('b48bdf3a-74cd-5d35-b221-6347408d9a4f', '309d7984-5ac0-5587-8eb5-86575a198f61', 18, 35, True, 650, '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('17b2b918-5019-50ac-b415-b457c982e9cf', '9fe875e8-b10a-5647-bf68-726451a45bb2', 18, 35, True, 650, '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('111a18b5-8e59-5eac-8fb6-ebc942cfaab6', '2b2020e7-0e57-5cc7-8c43-ecb93a8eee78', 18, 35, True, 650, '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('d18d8fc3-172f-5c2c-aedc-70983128e531', '5b77f441-dd90-53ac-8f6f-abd39236138b', 18, 35, True, 650, '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('29512684-664f-58d8-b007-371792d5534b', '5d48af3d-1da6-5593-9c54-ca0ab0a95009', 18, 35, True, 650, '2026-09-17 10:00:00', '2026-09-17 10:00:00');

INSERT INTO loan_scheme_moratoriums (id, loan_scheme_id, course_period_included, additional_months, created_at, updated_at) VALUES
    ('f8013faf-257e-59e8-9035-2daf83009b2f', '309d7984-5ac0-5587-8eb5-86575a198f61', True, 6, '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('6dad7aab-22eb-5f1f-99ed-19af0ecf5e62', '9fe875e8-b10a-5647-bf68-726451a45bb2', True, 6, '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('0205f9c4-59b7-5805-8d9e-4353072f8cff', '2b2020e7-0e57-5cc7-8c43-ecb93a8eee78', True, 6, '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('db715835-25f1-522a-ad14-41c9c086260c', '5b77f441-dd90-53ac-8f6f-abd39236138b', True, 6, '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('6bc01bf0-c7a7-5464-85a5-ea6e5928d70d', '5d48af3d-1da6-5593-9c54-ca0ab0a95009', True, 6, '2026-09-17 10:00:00', '2026-09-17 10:00:00');

INSERT INTO loan_scheme_professions (id, loan_scheme_id, profession_id, created_at, updated_at) VALUES
    ('c9f46a69-c0b5-549a-ae26-44a9193a1404', '309d7984-5ac0-5587-8eb5-86575a198f61', 'e9a0b5f3-3272-5d04-918a-b6a7c7cb40a2', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('44501de4-a826-5ccc-a657-cc4baf5a1c67', '9fe875e8-b10a-5647-bf68-726451a45bb2', 'f9100406-fbd3-5f06-a2c9-ee4ee8781b8c', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('eec912f6-e991-5f7c-9e8b-3f49cefc83e6', '2b2020e7-0e57-5cc7-8c43-ecb93a8eee78', '274c63af-ee2b-508e-b2fe-bfefcac2dfd0', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('959015f9-a3e3-55c9-91b1-80634293d6da', '5b77f441-dd90-53ac-8f6f-abd39236138b', 'e3ce7ffc-1cc8-54d9-ac8f-5cbeb34ae3de', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('99cab3dd-a137-5daf-a1c2-e0957bdec1db', '5d48af3d-1da6-5593-9c54-ca0ab0a95009', '4f0e39e5-2e8a-5115-bc4f-1b025ebe36c8', '2026-09-17 10:00:00', '2026-09-17 10:00:00');

INSERT INTO loan_scheme_repayment_rules (id, loan_scheme_id, min_tenure_years, max_tenure_years, prepayment_allowed, foreclosure_charges, created_at, updated_at) VALUES
    ('86c87383-2faf-52c7-b956-1744e2700d16', '309d7984-5ac0-5587-8eb5-86575a198f61', 3, 10, True, '0.00', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('08395ac7-bc59-5632-af6e-449e3b92ee68', '9fe875e8-b10a-5647-bf68-726451a45bb2', 3, 10, True, '0.00', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('68d284a1-1922-554f-9b48-3e178d24ee7d', '2b2020e7-0e57-5cc7-8c43-ecb93a8eee78', 3, 10, True, '0.00', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('5de1c977-5e2f-5dc1-9d99-d435fa0da322', '5b77f441-dd90-53ac-8f6f-abd39236138b', 3, 10, True, '0.00', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('557df8d9-56b8-58ee-a066-663cb62644fa', '5d48af3d-1da6-5593-9c54-ca0ab0a95009', 3, 10, True, '0.00', '2026-09-17 10:00:00', '2026-09-17 10:00:00');

INSERT INTO loan_scheme_required_documents (id, loan_scheme_id, document_type_id, created_at, updated_at) VALUES
    ('fdf5f41a-b855-51bf-9b00-052d3073b194', '309d7984-5ac0-5587-8eb5-86575a198f61', '4fb58687-055a-5058-ad58-83c9e8d86274', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('6a42e403-9237-5428-9726-c3ceddf0e421', '9fe875e8-b10a-5647-bf68-726451a45bb2', '5648a211-b8c7-561a-8fce-ea0b389f3f43', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('3714c885-2fee-56ed-ba10-a54bb1251c15', '2b2020e7-0e57-5cc7-8c43-ecb93a8eee78', '5e3f0077-6a80-58b9-9b56-795109e1ced6', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('cd014eb9-5f4c-591c-adc9-b5a9893edbc0', '5b77f441-dd90-53ac-8f6f-abd39236138b', '81ae888b-e753-507d-a907-fe18e0c905cf', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('7762dfd7-4269-5e8c-9b35-aae543728c26', '5d48af3d-1da6-5593-9c54-ca0ab0a95009', '7aab9aee-20c4-5c77-b6ce-11cad563bf3c', '2026-09-17 10:00:00', '2026-09-17 10:00:00');

INSERT INTO scholarship_schemes (id, name, scholarship_type, academic_year, start_date, end_date, status, created_by, updated_by, created_at, updated_at) VALUES
    ('b47bb1ec-d0d1-5e99-933a-37dd6bcf0a7e', 'Government Scholarship 1', 'MERIT_BASED', '2026-27', '2026-06-01', '2027-03-31', 'ACTIVE', '1bfd80a7-b582-55cc-9ec6-eddfd256f103', '1bfd80a7-b582-55cc-9ec6-eddfd256f103', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('ea7f320c-da1e-5735-8758-c0b61f2739d1', 'Government Scholarship 2', 'NEED_BASED', '2026-27', '2026-06-01', '2027-03-31', 'ACTIVE', '1bfd80a7-b582-55cc-9ec6-eddfd256f103', '1bfd80a7-b582-55cc-9ec6-eddfd256f103', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('1c75aa66-3401-57b4-97a8-584869331315', 'Government Scholarship 3', 'MERIT_BASED', '2026-27', '2026-06-01', '2027-03-31', 'ACTIVE', '1bfd80a7-b582-55cc-9ec6-eddfd256f103', '1bfd80a7-b582-55cc-9ec6-eddfd256f103', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('85038174-bdf5-5450-a820-e318a4ad7ef0', 'Government Scholarship 4', 'NEED_BASED', '2026-27', '2026-06-01', '2027-03-31', 'ACTIVE', '1bfd80a7-b582-55cc-9ec6-eddfd256f103', '1bfd80a7-b582-55cc-9ec6-eddfd256f103', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('20bec9a0-5218-557f-bef7-57b8ce563125', 'Government Scholarship 5', 'MERIT_BASED', '2026-27', '2026-06-01', '2027-03-31', 'ACTIVE', '1bfd80a7-b582-55cc-9ec6-eddfd256f103', '1bfd80a7-b582-55cc-9ec6-eddfd256f103', '2026-09-17 10:00:00', '2026-09-17 10:00:00');

INSERT INTO scholarship_benefit_details (id, scholarship_scheme_id, scholarship_amount, amount_type, payment_frequency, total_scheme_budget, created_at, updated_at) VALUES
    ('2dd90cbe-975f-514d-9c08-c2e950a1f695', 'b47bb1ec-d0d1-5e99-933a-37dd6bcf0a7e', '10000.00', 'FIXED', 'ANNUAL', '1000000.00', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('b96b211f-2db7-5ef4-9229-9e95dae6f64d', 'ea7f320c-da1e-5735-8758-c0b61f2739d1', '20000.00', 'FIXED', 'ANNUAL', '2000000.00', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('89a132d0-c3e1-5a2b-8d3c-49c16766e3ec', '1c75aa66-3401-57b4-97a8-584869331315', '30000.00', 'FIXED', 'ANNUAL', '3000000.00', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('08c83bc5-168c-5e46-8f16-3ae286e1b16c', '85038174-bdf5-5450-a820-e318a4ad7ef0', '40000.00', 'FIXED', 'ANNUAL', '4000000.00', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('bf22974d-5ddf-5b0d-bdf5-7b73ddd5f934', '20bec9a0-5218-557f-bef7-57b8ce563125', '50000.00', 'FIXED', 'ANNUAL', '5000000.00', '2026-09-17 10:00:00', '2026-09-17 10:00:00');

INSERT INTO scholarship_scheme_categories (id, scholarship_scheme_id, category_id, created_at, updated_at) VALUES
    ('31ef03a7-4b0b-59bf-a08f-25785b4efc56', 'b47bb1ec-d0d1-5e99-933a-37dd6bcf0a7e', 'bc8c5235-2ac6-5588-8b30-2d70930249fa', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('83263c40-25f7-5508-acc6-c4b2f2d05c08', 'ea7f320c-da1e-5735-8758-c0b61f2739d1', '6c90e5b0-dc2b-5346-a6cc-4dee59268270', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('3bc6fef3-5b55-57c0-869a-dea8b13a7db6', '1c75aa66-3401-57b4-97a8-584869331315', '108c5c9d-e83e-52f6-9dc2-9ba8156419b6', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('29ed9854-a488-5f66-8400-f96d9e5b09f8', '85038174-bdf5-5450-a820-e318a4ad7ef0', 'a33f9a88-61f0-5896-8904-c9d00639becc', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('ce43b14a-bffe-5a84-9696-ac3a34a754b4', '20bec9a0-5218-557f-bef7-57b8ce563125', 'eb1e141b-ce09-5583-81b3-cc8370cf45a8', '2026-09-17 10:00:00', '2026-09-17 10:00:00');

INSERT INTO scholarship_scheme_eligibility (id, scholarship_scheme_id, minimum_age, maximum_age, maximum_annual_family_income, minimum_percentage_criteria, created_at, updated_at) VALUES
    ('1221d131-c2f7-586f-9e53-111059721e3e', 'b47bb1ec-d0d1-5e99-933a-37dd6bcf0a7e', 17, 30, '800000.00', '60.00', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('c9416843-2221-563a-918c-e8deacd80902', 'ea7f320c-da1e-5735-8758-c0b61f2739d1', 17, 30, '800000.00', '60.00', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('65004177-abdd-5b7c-8408-faa098b53574', '1c75aa66-3401-57b4-97a8-584869331315', 17, 30, '800000.00', '60.00', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('f5a39a1a-5383-504b-8632-54f3e3eb2584', '85038174-bdf5-5450-a820-e318a4ad7ef0', 17, 30, '800000.00', '60.00', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('29f4f293-d7b8-5d2b-85dd-a209f083f2c6', '20bec9a0-5218-557f-bef7-57b8ce563125', 17, 30, '800000.00', '60.00', '2026-09-17 10:00:00', '2026-09-17 10:00:00');

INSERT INTO scholarship_scheme_professions (id, scholarship_scheme_id, profession_id, created_at, updated_at) VALUES
    ('2e625a39-085c-50ec-94ea-10ceaa51d93d', 'b47bb1ec-d0d1-5e99-933a-37dd6bcf0a7e', 'e9a0b5f3-3272-5d04-918a-b6a7c7cb40a2', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('7b081ed2-07d2-55b4-8806-a187a0616dfa', 'ea7f320c-da1e-5735-8758-c0b61f2739d1', 'f9100406-fbd3-5f06-a2c9-ee4ee8781b8c', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('7e3df893-6a07-50c0-91d3-0d63fbae8b5e', '1c75aa66-3401-57b4-97a8-584869331315', '274c63af-ee2b-508e-b2fe-bfefcac2dfd0', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('b665b6f7-5791-5066-836a-9da1a7f46ff8', '85038174-bdf5-5450-a820-e318a4ad7ef0', 'e3ce7ffc-1cc8-54d9-ac8f-5cbeb34ae3de', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('031d0846-5105-5d15-ac7b-e528f24f2bac', '20bec9a0-5218-557f-bef7-57b8ce563125', '4f0e39e5-2e8a-5115-bc4f-1b025ebe36c8', '2026-09-17 10:00:00', '2026-09-17 10:00:00');

INSERT INTO scholarship_scheme_required_documents (id, scholarship_scheme_id, document_type_id, created_at, updated_at) VALUES
    ('de529157-557d-5999-8d51-54941241959f', 'b47bb1ec-d0d1-5e99-933a-37dd6bcf0a7e', '4fb58687-055a-5058-ad58-83c9e8d86274', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('7005dba3-03d3-5084-a97c-d52677ff5aba', 'ea7f320c-da1e-5735-8758-c0b61f2739d1', '5648a211-b8c7-561a-8fce-ea0b389f3f43', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('5d672013-3110-53bd-9c99-a6e785b7fd48', '1c75aa66-3401-57b4-97a8-584869331315', '5e3f0077-6a80-58b9-9b56-795109e1ced6', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('94e3457d-c48b-55d6-8ab4-3c8dd47b947e', '85038174-bdf5-5450-a820-e318a4ad7ef0', '81ae888b-e753-507d-a907-fe18e0c905cf', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('d9af5fc8-0353-52a8-94e3-aa9f178b700f', '20bec9a0-5218-557f-bef7-57b8ce563125', '7aab9aee-20c4-5c77-b6ce-11cad563bf3c', '2026-09-17 10:00:00', '2026-09-17 10:00:00');

INSERT INTO applications (id, student_id, application_type, loan_scheme_id, scholarship_scheme_id, approved_amount, status, submitted_at, created_at, updated_at) VALUES
    ('f0555d89-83ce-5823-b743-d66cca250bcd', 'a4a9989c-c9b9-50fc-a5e6-ba5d478df18a', 'LOAN', '309d7984-5ac0-5587-8eb5-86575a198f61', NULL, '100000.00', 'APPROVED', '2026-09-17 10:00:00', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('844fd783-d606-5167-b175-7998a8c01e27', '7113fd68-5592-5321-9116-5875afacf59a', 'SCHOLARSHIP', NULL, 'ea7f320c-da1e-5735-8758-c0b61f2739d1', '200000.00', 'APPROVED', '2026-09-17 10:00:00', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('c0c5ce62-ef69-513a-aaf2-9e592958212e', '30d6f63a-5a6f-59e1-bd68-e1fe76d11d9f', 'LOAN', '2b2020e7-0e57-5cc7-8c43-ecb93a8eee78', NULL, '300000.00', 'APPROVED', '2026-09-17 10:00:00', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('a77c5311-9eaf-58f4-967d-59ddaa9c3cde', '7d26bef4-0946-5e91-a010-13f6c1b0810d', 'SCHOLARSHIP', NULL, '85038174-bdf5-5450-a820-e318a4ad7ef0', '400000.00', 'APPROVED', '2026-09-17 10:00:00', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('02b9a66f-e0c1-5c34-9b6b-0edb071e2959', 'f3b6091a-ea91-561f-b600-5e0c0e77bd9a', 'LOAN', '5d48af3d-1da6-5593-9c54-ca0ab0a95009', NULL, '500000.00', 'APPROVED', '2026-09-17 10:00:00', '2026-09-17 10:00:00', '2026-09-17 10:00:00');

INSERT INTO application_documents (id, application_id, student_document_id, created_at, updated_at) VALUES
    ('19800f8e-e1a9-5510-acf7-14c4bc0dc7ab', 'f0555d89-83ce-5823-b743-d66cca250bcd', '884e8f56-5d24-54a7-a968-8d9389d13edc', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('4e0887e8-91b0-5d77-9608-3dfe37e3bfe9', '844fd783-d606-5167-b175-7998a8c01e27', '4dfc47ea-72ab-5647-9b30-c0f46491ab9d', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('bb12aa18-3f0c-5754-a5ad-96d92c66030c', 'c0c5ce62-ef69-513a-aaf2-9e592958212e', '970681cb-e5e4-56e5-b8a8-9a36441c2968', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('b60b013e-3a8a-5577-b60a-555addf6cf7f', 'a77c5311-9eaf-58f4-967d-59ddaa9c3cde', '66821017-4bf9-5382-a172-ab25f446acb5', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('1ebcbda7-b315-52ff-b440-da93c312ceae', '02b9a66f-e0c1-5c34-9b6b-0edb071e2959', '63988ee6-0264-5490-a528-7ab9634e39f3', '2026-09-17 10:00:00', '2026-09-17 10:00:00');

INSERT INTO application_history (id, application_id, status, remark, action_by_user_id, assigned_to_user_id, created_at) VALUES
    ('47b8d073-6b97-5590-a4be-b393515970dc', 'f0555d89-83ce-5823-b743-d66cca250bcd', 'APPROVED', 'Dummy application approved', '47dcedee-f5d6-5a93-a9c6-1425aa435cf6', NULL, '2026-09-17 10:00:00'),
    ('32368ab8-3192-59ec-9a92-0c99dd9bfc6f', '844fd783-d606-5167-b175-7998a8c01e27', 'APPROVED', 'Dummy application approved', '1bfd80a7-b582-55cc-9ec6-eddfd256f103', NULL, '2026-09-17 10:00:00'),
    ('2a2867cf-e3d1-56b8-853d-538f5e1081d6', 'c0c5ce62-ef69-513a-aaf2-9e592958212e', 'APPROVED', 'Dummy application approved', '6bc45c49-0ba3-5959-a46b-69ac19501d16', NULL, '2026-09-17 10:00:00'),
    ('743e66b3-1178-5c41-a4af-ec8d36f62628', 'a77c5311-9eaf-58f4-967d-59ddaa9c3cde', 'APPROVED', 'Dummy application approved', '1bfd80a7-b582-55cc-9ec6-eddfd256f103', NULL, '2026-09-17 10:00:00'),
    ('d2531cb4-aa89-5727-8886-813bf3526944', '02b9a66f-e0c1-5c34-9b6b-0edb071e2959', 'APPROVED', 'Dummy application approved', 'c5f70aac-0c9c-5f71-a1d0-e97d79af2ad8', NULL, '2026-09-17 10:00:00');

INSERT INTO disbursements (id, application_id, application_type, student_id, disbursed_by_user_id, amount, disbursement_date, status, remark, created_at, updated_at) VALUES
    ('84f46c5f-5b3e-57a3-95ba-7a302b4c3026', 'f0555d89-83ce-5823-b743-d66cca250bcd', 'LOAN', 'a4a9989c-c9b9-50fc-a5e6-ba5d478df18a', '47dcedee-f5d6-5a93-a9c6-1425aa435cf6', '100000.00', '2026-09-17', 'COMPLETED', 'Dummy disbursement', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('6595a11d-09a0-5d61-9ccf-a0c13b024930', '844fd783-d606-5167-b175-7998a8c01e27', 'SCHOLARSHIP', '7113fd68-5592-5321-9116-5875afacf59a', '1bfd80a7-b582-55cc-9ec6-eddfd256f103', '200000.00', '2026-09-17', 'COMPLETED', 'Dummy disbursement', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('4eff0caf-1949-5842-8beb-859bc799be92', 'c0c5ce62-ef69-513a-aaf2-9e592958212e', 'LOAN', '30d6f63a-5a6f-59e1-bd68-e1fe76d11d9f', '6bc45c49-0ba3-5959-a46b-69ac19501d16', '300000.00', '2026-09-17', 'COMPLETED', 'Dummy disbursement', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('cf1af353-77e6-54f8-ae52-e60413368e40', 'a77c5311-9eaf-58f4-967d-59ddaa9c3cde', 'SCHOLARSHIP', '7d26bef4-0946-5e91-a010-13f6c1b0810d', '1bfd80a7-b582-55cc-9ec6-eddfd256f103', '400000.00', '2026-09-17', 'COMPLETED', 'Dummy disbursement', '2026-09-17 10:00:00', '2026-09-17 10:00:00'),
    ('59ff55cf-1187-5aac-942b-1d52e931e006', '02b9a66f-e0c1-5c34-9b6b-0edb071e2959', 'LOAN', 'f3b6091a-ea91-561f-b600-5e0c0e77bd9a', 'c5f70aac-0c9c-5f71-a1d0-e97d79af2ad8', '500000.00', '2026-09-17', 'COMPLETED', 'Dummy disbursement', '2026-09-17 10:00:00', '2026-09-17 10:00:00');

COMMIT;
