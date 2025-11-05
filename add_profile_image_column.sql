-- Add profile_image column to users table
ALTER TABLE users ADD COLUMN IF NOT EXISTS profile_image VARCHAR(500);

-- Add comment to the column
COMMENT ON COLUMN users.profile_image IS 'URL of the user profile image stored in Cloudinary';
