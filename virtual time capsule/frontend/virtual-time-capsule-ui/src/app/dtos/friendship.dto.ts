import { FriendshipStatus } from '../enums/friendship-status.enum'; // Ensure this path is correct

export interface FriendshipDto {
  id: number;
  requesterId: number;
  requesterUsername: string;
  responderId: number;
  responderUsername: string;
  status: FriendshipStatus;
  lastUpdate: string; // ISO string
}
