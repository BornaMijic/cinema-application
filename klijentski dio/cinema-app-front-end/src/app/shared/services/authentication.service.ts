import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject, Observable} from "rxjs";
import {User} from "../models/user";
import {environment} from "../../../environments/environment";
import {Router} from "@angular/router";
import {DataStorageService} from "./data-storage.service";

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  constructor(private http: HttpClient, private router: Router, private dataStorageService: DataStorageService) {
  }

  currentUser: BehaviorSubject<User | null> = new BehaviorSubject<User | null>(null)
  user: User | null = null;
  jwtToken: string = '';

  getUserById(id: string) {
    return this.http.get<{ id: string, name: string, surname: string }>(`${environment.backend}api/user/${id}`);
  }

  login(user: { username: string, password: string }): Observable<{ username: string, email: string, token: string, verified: boolean, codeToVerifyEmail: string, active: boolean }> {
    return this.dataStorageService.login(user);
  }

  register(registrationUser: { username: string, password: string, email: string, name: string, surname: string }): Observable<string> {
    return this.dataStorageService.register(registrationUser)
  }

  setCurrentUser(user: User | null) {
    if (user != null && user.token && !this.router.url.includes("/verify-code")) {
      this.currentUser.next(user);
      this.jwtToken = user.token;
      this.user = user;
    }
  }

  logout() {
    sessionStorage.removeItem("token");
    this.currentUser.next(null)
    this.jwtToken = '';
    this.user = null;
  }

  validateToken(token: String) {
    return this.dataStorageService.validateToken(token)
  }

  verifyAccount(verificationInfo: { username: String, codeToVerifyAccount: String }): Observable<{ text: string }> {
    return this.dataStorageService.verifyAccount(verificationInfo);
  }

  sendVerificationLink(userForVerifying: { email: String, username: String, link: String, codeToVerifyEmail: String }) {
    return this.dataStorageService.sendVerificationLink(userForVerifying);
  }

  changeEmailAndSendVerificationCode(userForVerifying: { email: String, username: String, link: String, codeToVerifyEmail: String }) {
    return this.dataStorageService.changeEmailAndSendVerificationCode(userForVerifying);
  }

  findTenUsersPerPageAdmin(pageNumber: number): Observable<{ usersAmount: number, usersList: User[] }> {
    return this.dataStorageService.findTenUsersPerPageAdmin(pageNumber)
  }

  findTenUsersPerPageSearchUsernameAdmin(pageNumber: number, searchUsername: string): Observable<{ usersAmount: number, usersList: User[] }> {
    return this.dataStorageService.findTenUsersPerPageSearchUsernameAdmin(pageNumber, searchUsername);
  }


  changeActive(id: string, username: string, active: boolean) {
    return this.dataStorageService.changeActive(id, username, active);

  }

  changeVerified(id: string, username: string, verified: boolean) {
    return this.dataStorageService.changeVerified(id, username, verified);

  }
}
