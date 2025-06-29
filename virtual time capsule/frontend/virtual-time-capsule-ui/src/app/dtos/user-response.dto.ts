import {Friendship} from '../models/friendship.model';

export interface UserResponseDto {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  friendships: Friendship[];
}
