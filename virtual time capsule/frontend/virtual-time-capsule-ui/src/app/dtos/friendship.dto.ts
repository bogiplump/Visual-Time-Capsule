import { FriendShipStatus } from '../models/friendship-status.enum'; // Assuming you have this enum defined

export interface FriendshipDto {
  receiverId: number;
  status: FriendShipStatus;
}
