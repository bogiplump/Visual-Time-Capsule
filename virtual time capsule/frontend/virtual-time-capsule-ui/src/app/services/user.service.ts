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

// Define a placeholder for UserUpdateDto if not yet created.
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

  /**
   * Retrieves a user's profile by ID.
   * GET /api/v1/users/{id} (inferred)
   */
  getUserProfile(id: number): Observable<UserResponseDto> {
    return this.http.get<UserResponseDto>(`${this.apiUrl}/${id}`);
  }

  /**
   * Retrieves all capsules for a specific user ID.
   * GET /api/v1/users/{id}/capsules
   */
  getUserCapsules(userId: number): Observable<CapsuleResponseDto[]> {
    return this.http.get<CapsuleResponseDto[]>(`${this.apiUrl}/${userId}/capsules`);
  }

  /**
   * Updates a user's profile.
   * PUT /api/v1/users/{id} (inferred)
   */
  updateUser(id: number, userUpdateDto: UserUpdateDto): Observable<string> {
    return this.http.put<string>(`${this.apiUrl}/${id}`, userUpdateDto);
  }

  /**
   * Deletes a user by ID.
   * DELETE /api/v1/users/{id} (inferred)
   */
  deleteUser(id: number): Observable<string> {
    return this.http.delete<string>(`${this.apiUrl}/${id}`);
  }

  /**
   * Sends a friendship invitation to another user.
   * Corresponds to backend: POST /api/v1/users/friendship/{id} where {id} is responderId.
   * @param responderId The ID of the user to send a friend request to.
   */
  sendFriendRequest(responderId: number): Observable<string> {
    return this.http.post(`${this.apiUrl}/friendship/${responderId}`, null, { responseType: 'text' });
  }

  /**
   * Answers a friendship invitation (accept/decline).
   * Corresponds to backend: PUT /api/v1/users/friendship/{id} where {id} is the friendshipId
   * and the body contains the FriendshipDto with the updated status.
   * @param friendshipId The ID of the friendship record to update.
   * @param friendshipDto The FriendshipDto containing the updated status (e.g., ACCEPTED, DECLINED).
   */
  answerFriendshipInvitation(friendshipId: number, friendshipDto: FriendshipDto): Observable<string> {
    console.log(friendshipId);
    return this.http.put(`${this.apiUrl}/friendship/${friendshipId}`, friendshipDto, { responseType: 'text' });
  }

  /**
   * Retrieves a user's goals by user ID.
   * GET /api/v1/users/{id}/goals
   */
  getUserGoals(userId: number): Observable<GoalDto[]> {
    return this.http.get<GoalDto[]>(`${this.apiUrl}/${userId}/goals`);
  }

  /**
   * Retrieves all users (excluding the current one) for display.
   * GET /api/v1/users
   */
  getAllUsers(): Observable<UserProfileDto[]> {
    console.log('UserService: Making HTTP GET request to:', this.apiUrl);
    return this.http.get<any[]>(this.apiUrl).pipe(
      map(rawUsersData => {
        return rawUsersData.map(rawUser => {
          const clonedRawUser = JSON.parse(JSON.stringify(rawUser));

          const userProfile: UserProfileDto = {
            id: clonedRawUser.id,
            username: clonedRawUser.username,
            friendshipStatus: clonedRawUser.friendshipStatus as FriendshipStatus, // Explicitly cast the string to enum type
            associatedFriendshipId: clonedRawUser.associatedFriendshipId,
            isRequestFromCurrentUser: clonedRawUser.isRequestFromCurrentUser,
            isRequestToCurrentUser: clonedRawUser.isRequestToCurrentUser
          };
          return userProfile;
        });
      })
    );
  }

  /**
   * Retrieves public capsules for a user.
   * GET /api/v1/users/{userId}/capsules/public
   */
  getPublicCapsulesByUserId(userId: number): Observable<CapsuleResponseDto[]> {
    console.log(`UserService: Making HTTP GET request to public capsules for user ${userId}:`, `${this.apiUrl}/${userId}/capsules`);
    return this.http.get<CapsuleResponseDto[]>(`${this.apiUrl}/${userId}/capsules`);
  }

  /**
   * Retrieves all friendships for a given user.
   * Corresponds to backend: GET /api/v1/users/{id}/friendships
   * @param userId The ID of the user whose friendships to retrieve.
   */
  getFriendships(userId: number | null): Observable<FriendshipDto[]> {
    return this.http.get<FriendshipDto[]>(`${this.apiUrl}/${userId}/friendships`);
  }
}
