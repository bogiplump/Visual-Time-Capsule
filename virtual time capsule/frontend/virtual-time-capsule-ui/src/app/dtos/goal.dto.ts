import {UserModel} from '../models/user.model';
import {Capsule} from '../models/capsule.model';

export interface GoalDto {
  id: number | null;
  isVisible: boolean;
  isAchieved: boolean;
  creationDate: string;
  content: string;
  creator: number | null;
  capsuleId: number | null;
}
