-- Add assigned_employee_id column to appointments table
ALTER TABLE appointments 
ADD COLUMN IF NOT EXISTS assigned_employee_id BIGINT;

-- Optional: Add a comment to the column
COMMENT ON COLUMN appointments.assigned_employee_id IS 'ID of the employee assigned to handle this appointment';
