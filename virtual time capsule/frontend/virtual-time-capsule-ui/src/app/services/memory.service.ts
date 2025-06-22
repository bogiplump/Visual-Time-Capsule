import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MemoryCreateDto } from '../dtos/memory-create.dto';

@Injectable({
  providedIn: 'root'
})
export class MemoryService {
  // Note: The Java controller used @RestController("api/v1/memories") which is unusual.
  // Assuming the actual base path is /api/v1/memories.
  private apiUrl = 'http://localhost:8080/api/v1/memories'; // Adjust to your backend URL

  constructor(private http: HttpClient) { }

  /**
   * Adds a new memory to a capsule.
   * Handles file upload via FormData.
   * POST /api/v1/memories
   */
  addNewMemoryToCapsule(capsuleId: number, memoryDto: MemoryCreateDto): Observable<any> {
    const formData = new FormData();
    formData.append('name', memoryDto.name);
    formData.append('description', memoryDto.description);
    formData.append('type', memoryDto.type);
    formData.append('content', memoryDto.content); // memoryDto.content is a File

    let params = new HttpParams().set('capsuleId', capsuleId.toString());
    // HttpClient will automatically set Content-Type to multipart/form-data
    // when sending FormData, no need to set manually.
    return this.http.post<any>(`${this.apiUrl}`, formData, { params });
  }

  /**
   * Adds an existing memory to a capsule.
   * PUT /api/v1/memories/{id}/addMemory
   */
  addExistingMemoryToCapsule(memoryId: number, capsuleId: number): Observable<any> {
    let params = new HttpParams().set('capsuleId', capsuleId.toString());
    return this.http.put<any>(`${this.apiUrl}/${memoryId}/addMemory`, null, { params });
  }

  /**
   * Removes a memory from a capsule (deletes the association).
   * DELETE /api/v1/memories/{id}/removeMemory
   */
  removeMemoryFromCapsule(memoryId: number, capsuleId: number): Observable<void> {
    let params = new HttpParams().set('capsuleId', capsuleId.toString());
    return this.http.delete<void>(`${this.apiUrl}/${memoryId}/removeMemory`, { params });
  }
}
