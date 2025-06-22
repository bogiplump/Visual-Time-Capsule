import { MemoryType } from './memory-type.enum';
import { Capsule } from './capsule.model'; // For the ManyToOne relationship
import { UserModel } from './user.model';

export interface Memory {
  id: number;
  memoryType: MemoryType;
  path: string; // content_path in Java
  creationDate: string; // LocalDate in Java -> string (ISO 8601) in TypeScript
  description: string;
  capsule: Capsule; // ManyToOne relationship
  creator: UserModel;
}
