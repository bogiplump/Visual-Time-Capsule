import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MemoryCreateDto } from '../dtos/memory-create.dto';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MemoryService {
  private apiUrl = `${environment.backendUrl}/api/v1/memories`;

  constructor(private http: HttpClient) { }

  addNewMemoryToCapsule(capsuleId: number, memoryDto: MemoryCreateDto): Observable<any> {
      const formData = new FormData();

      formData.append('description', memoryDto.description);
      formData.append('type', memoryDto.type);

      if (memoryDto.content) {
        formData.append('content', memoryDto.content, memoryDto.content.name);
      }

      let params = new HttpParams().set('capsuleId', capsuleId.toString());

      return this.http.post<any>(`${this.apiUrl}`, formData, { params });
  }

  removeMemoryFromCapsule(memoryId: number, capsuleId: number): Observable<void> {
    let params = new HttpParams().set('capsuleId', capsuleId.toString());
    return this.http.delete<void>(`${this.apiUrl}/${memoryId}/removeMemory`, { params });
  }

  getMemoriesForCapsule(capsuleId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${capsuleId}/memories`);
  }

  getMemoryContent(filename: string): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/content/${filename}`, { responseType: 'blob' });
  }
}
