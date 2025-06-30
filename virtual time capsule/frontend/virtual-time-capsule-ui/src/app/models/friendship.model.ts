import { UserModel } from './user.model';
import { FriendShipStatus } from './friendship-status.enum';

export interface Friendship {
  id: number;
  requester: UserModel;
  responder: UserModel;
  status: FriendShipStatus;
  lastUpdate: string; // LocalDate in Java is commonly mapped to string (ISO 8601) in TypeScript
}
