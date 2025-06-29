import { UserModel } from './user.model';
import {FriendshipStatus} from '../enums/friendship-status.enum';

export interface Friendship {
  id: number;
  requester: UserModel;
  responder: UserModel;
  status: FriendshipStatus;
  lastUpdate: string; // LocalDate in Java is commonly mapped to string (ISO 8601) in TypeScript
}
