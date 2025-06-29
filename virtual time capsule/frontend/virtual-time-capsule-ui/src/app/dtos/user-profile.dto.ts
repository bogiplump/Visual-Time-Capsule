import {FriendshipStatus} from '../enums/friendship-status.enum';

export interface UserProfileDto {
  id: number;
  username: string;
  friendshipStatus?: FriendshipStatus; // PENDING, ACCEPTED, DECLINED (from backend)
  associatedFriendshipId?: number; // Store the ID of the friendship entity if one exists
  isRequestFromCurrentUser?: boolean; // True if current user sent a PENDING request to this user
  isRequestToCurrentUser?: boolean;   // True if this user sent a PENDING request to current user (current user needs to accept)
}
