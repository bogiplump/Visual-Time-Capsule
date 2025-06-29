import {MemoryType} from '../enums/memory-type.enum';


export interface MemoryCreateDto {
  description: string;
  type: MemoryType;
  content: File; // MultipartFile in Java maps to File or Blob in TypeScript for file uploads
}
