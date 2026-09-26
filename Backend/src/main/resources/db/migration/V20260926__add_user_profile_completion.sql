-- Run against the configured database schema before deploying the matching application build.
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS profile_completed BOOLEAN NOT NULL DEFAULT TRUE;

-- Student accounts created before this field was introduced may not yet have a Student row.
UPDATE users u
SET profile_completed = FALSE
FROM roles r
WHERE u.role_id = r.id
  AND r.name = 'STUDENT'
  AND NOT EXISTS (
      SELECT 1
      FROM students s
      WHERE s.user_id = u.id
  );
