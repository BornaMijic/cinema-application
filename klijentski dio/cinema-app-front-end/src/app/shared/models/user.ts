export class User {
  id?: string;
  username?: string;
  password?: string;
  email?: string;
  token?: string;
  role?: string;

  constructor(id?: string, username?: string, password?: string, email?: string, token?: string, role?: string) {
    if (id) {
      this.id = id;
    }
    if (username) {
      this.username = username;
    }
    if (password) {
      this.password = password;
    }
    if (email) {
      this.password = password;
    }
    if (token) {
      this.token = token
    }

    if (role) {
      this.role = role;
    }
  }
}
