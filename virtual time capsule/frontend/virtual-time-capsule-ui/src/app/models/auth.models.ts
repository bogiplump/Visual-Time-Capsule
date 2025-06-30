export interface UserCreateDto {
  username: string;
  password: string; // Changed from passwordHash to password
  firstName: string; // Added firstName
  lastName: string;  // Added lastName
  email: string;
}

export interface UserLoginDto {
  username: string;
  password: string;
}

export interface UsersAuthResponse {
  accessToken: string;
  refreshToken: string;
}
