import {MemoryType} from '../enums/memory-type.enum';


export interface MemoryCreateDto {
  description: string;
  type: MemoryType;
  content: File;
}
