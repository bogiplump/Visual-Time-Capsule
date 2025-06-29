import { CapsuleStatus } from '../enums/capsule-status.enum';
import { UserModel } from './user.model';
import { Goal } from './goal.model';
import { Memory } from './memory.model';

export interface Capsule {
  id: number;
  capsuleName: string;
  status: CapsuleStatus;
  creationDate: string; // LocalDateTime in Java -> string (ISO 8601) in TypeScript
  lockDate?: string;    // LocalDateTime in Java -> string (ISO 8601) in TypeScript (optional)
  openDate?: string;    // LocalDateTime in Java -> string (ISO 8601) in TypeScript (optional)
  creator: UserModel;
  goal: Goal;
  memoryEntries?: Memory[]; // Set<Memory> in Java -> Array<Memory> in TypeScript (optional, or depending on DTO structure)
}
