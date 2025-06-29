import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { CapsuleResponseDto } from '../dtos/capsule-response.dto';
import { MemoryDto } from '../dtos/memory.dto';
import { CapsuleCreateDto } from '../dtos/capsule-create.dto';
import { CapsuleUpdateDto } from '../dtos/capsule-update.dto';

@Injectable({
  providedIn: 'root'
})
export class CapsuleService {
  private apiUrl = `${environment.backendUrl}/api/v1/capsules`;

  constructor(private http: HttpClient) { }

  createCapsule(capsuleDto: CapsuleCreateDto): Observable<CapsuleResponseDto> {
    return this.http.post<CapsuleResponseDto>(this.apiUrl, capsuleDto);
  }

  getAllMyCapsules(): Observable<CapsuleResponseDto[]> {
    return this.http.get<CapsuleResponseDto[]>(`${this.apiUrl}/all`);
  }

  getCapsule(capsuleId: number): Observable<CapsuleResponseDto> {
    return this.http.get<CapsuleResponseDto>(`${this.apiUrl}/${capsuleId}`);
  }

  updateCapsule(id: number, capsuleDto: CapsuleUpdateDto): Observable<CapsuleResponseDto> {
    console.log("Sending request" +`${this.apiUrl}/${id}`);
    return this.http.put<CapsuleResponseDto>(`${this.apiUrl}/${id}`, capsuleDto);
  }

  deleteCapsule(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  lockCapsule(id: number, openDateTime: string): Observable<CapsuleResponseDto> {
    let params = new HttpParams().set('openDateTime', openDateTime); // Changed to openDateTime
    return this.http.put<CapsuleResponseDto>(`${this.apiUrl}/lock/${id}`, {}, { params });
  }

  openCapsule(id: number): Observable<CapsuleResponseDto> {
    return this.http.put<CapsuleResponseDto>(`${this.apiUrl}/${id}/open`, {});
  }


  markReadyToClose(capsuleId: number): Observable<CapsuleResponseDto> {
    return this.http.put<CapsuleResponseDto>(`${this.apiUrl}/${capsuleId}/ready-to-close`, {});
  }
}
