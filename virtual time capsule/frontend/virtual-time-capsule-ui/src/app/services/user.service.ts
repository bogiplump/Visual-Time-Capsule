import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {UserResponseDto} from '../dtos/user-response.dto';
import {GoalDto} from '../dtos/goal.dto';
import {FriendshipDto} from '../dtos/friendship.dto';
import {CapsuleResponseDto} from '../dtos/capsule-response.dto';
import {environment} from '../../environments/environment';
import {map} from 'rxjs/operators';
import {UserProfileDto} from '../dtos/user-profile.dto';
import {FriendshipStatus} from '../enums/friendship-status.enum';


export interface UserUpdateDto {
  firstName?: string;
  lastName?: string;
  email?: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = `${environment.backendUrl}/api/v1/users`;

  constructor(private http: HttpClient) { }

  sendFriendRequest(responderId: number): Observable<string> {
    return this.http.post(`${this.apiUrl}/friendship/${responderId}`, null, { responseType: 'text' });
  }

  answerFriendshipInvitation(friendshipId: number, friendshipDto: FriendshipDto): Observable<string> {
    console.log(friendshipId);
    return this.http.put(`${this.apiUrl}/friendship/${friendshipId}`, friendshipDto, { responseType: 'text' });
  }

  getAllUsers(): Observable<UserProfileDto[]> {
    console.log('UserService: Making HTTP GET request to:', this.apiUrl);
    return this.http.get<any[]>(this.apiUrl).pipe(
      map(rawUsersData => {
        return rawUsersData.map(rawUser => {
          const clonedRawUser = JSON.parse(JSON.stringify(rawUser));

          const userProfile: UserProfileDto = {
            id: clonedRawUser.id,
            username: clonedRawUser.username,
            friendshipStatus: clonedRawUser.friendshipStatus as FriendshipStatus,
            associatedFriendshipId: clonedRawUser.associatedFriendshipId,
            isRequestFromCurrentUser: clonedRawUser.isRequestFromCurrentUser,
            isRequestToCurrentUser: clonedRawUser.isRequestToCurrentUser
          };
          return userProfile;
        });
      })
    );
  }

  getPublicCapsulesByUserId(userId: number): Observable<CapsuleResponseDto[]> {
    console.log(`UserService: Making HTTP GET request to public capsules for user ${userId}:`, `${this.apiUrl}/${userId}/capsules`);
    return this.http.get<CapsuleResponseDto[]>(`${this.apiUrl}/${userId}/capsules`);
  }

  getFriendships(userId: number | null): Observable<FriendshipDto[]> {
    return this.http.get<FriendshipDto[]>(`${this.apiUrl}/${userId}/friendships`);
  }
}
