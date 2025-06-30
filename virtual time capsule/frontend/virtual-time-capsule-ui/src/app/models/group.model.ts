import { Capsule } from './capsule.model';
import { UserModel } from './user.model';

export interface CapsuleGroup {
 id: number;
 name: string;
 theme: string;
 capsules: Capsule[];
 timeBetweenCapsules: string; // ISO date string
 openTime: string;            // ISO date string
 timeOfCreation: string;      // ISO date string
 creator: UserModel;
}
