-- Update appointment status check constraint to include new status values
-- Drop the old constraint
ALTER TABLE appointments DROP CONSTRAINT IF EXISTS appointments_status_check;

-- Add the new constraint with all status values
ALTER TABLE appointments ADD CONSTRAINT appointments_status_check 
    CHECK (status IN ('PENDING', 'CONFIRMED', 'IN_SERVICE', 'READY', 'COMPLETED', 'CANCELLED'));

-- Optional: Update any existing IN_PROGRESS statuses to IN_SERVICE (if any exist)
UPDATE appointments SET status = 'IN_SERVICE' WHERE status = 'IN_PROGRESS';
