import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { CapsuleGroup } from '../models/group.model';
import { CreateGroupDto } from '../dtos/create-group.dto';
import { GroupUpdateDto } from '../dtos/group-update.dto';
import { Capsule } from '../models/capsule.model';

@Injectable({
providedIn: 'root'
})
export class CapsuleGroupService {
private apiUrl = `${environment.backendUrl}/api/v1/groups`;

constructor(private http: HttpClient) {}

  getAllByUserId(userId: number): Observable<CapsuleGroup[]> {
    return this.http.get<CapsuleGroup[]>(`${this.apiUrl}/user/${userId}`);
  }

  createGroup(dto: CreateGroupDto): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/`, dto); // trailing slash is optional
  }

  updateGroup(dto: GroupUpdateDto): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/update`, dto);
  }

  deleteGroup(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}/delete`);
  }

  getCapsulesInGroup(groupId: number): Observable<Capsule[]> {
    return this.http.get<Capsule[]>(`${this.apiUrl}/group/${groupId}`);
  }
}
