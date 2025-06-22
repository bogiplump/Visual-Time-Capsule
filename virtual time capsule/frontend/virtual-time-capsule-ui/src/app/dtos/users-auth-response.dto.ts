import {UserResponseDto} from './user-response.dto';

export interface UsersAuthResponse {
    user: UserResponseDto;
    accessToken: string;
    refreshToken: string;
}
