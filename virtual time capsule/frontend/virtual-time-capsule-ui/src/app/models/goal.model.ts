import { UserModel } from './user.model';
import { Capsule } from './capsule.model'; // Assuming Capsule might refer to Goal

export interface Goal {
  id: number;
  isVisible: boolean;
  isAchieved: boolean;
  creationDate: string; // LocalDate in Java -> string (ISO 8601) in TypeScript
  content: string;
  creator: UserModel;
  capsule?: Capsule; // Optional, as Goal has a OneToOne relationship with Capsule, and Goal might exist without an associated Capsule initially. This also helps with potential circular dependencies.
}
