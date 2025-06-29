import {FriendshipStatus} from '../enums/friendship-status.enum';

export interface UserProfileDto {
  id: number;
  username: string;
  friendshipStatus?: FriendshipStatus;
  associatedFriendshipId?: number;
  isRequestFromCurrentUser?: boolean;
  isRequestToCurrentUser?: boolean;
}
