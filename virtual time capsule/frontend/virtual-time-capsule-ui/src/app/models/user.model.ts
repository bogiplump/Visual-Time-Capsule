export interface UserModel {
  id: number;
  username: string;
  email: string;
  firstName?: string; // Assuming these might be optional or only set after initial registration
  lastName?: string;  // Assuming these might be optional or only set after initial registration
  // Add other properties from your actual UserModel if you have them (e.g., roles, enabled status)
}
