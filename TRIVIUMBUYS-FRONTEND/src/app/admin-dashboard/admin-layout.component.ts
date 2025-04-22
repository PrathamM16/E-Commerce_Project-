import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { RouterLink, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-admin-layout',
  standalone: true,
  imports: [RouterLink, RouterOutlet],
  templateUrl: './admin-layout.component.html',
  styleUrls: ['./admin-layout.component.scss']
})
export class AdminLayoutComponent implements OnInit {

  accessRights: string[] = [];
  adminName: string = 'Admin User';
  isSuperAdmin: boolean = false;

  constructor(private router: Router) {}

  ngOnInit(): void {
    const rights = localStorage.getItem('accessRights');
    const name = localStorage.getItem('adminName');

    if (rights) {
      this.accessRights = JSON.parse(rights);
    }
    if (name) {
      this.adminName = name;
      if (name.trim().toLowerCase() === 'pratham') { 
        this.isSuperAdmin = true;
      }
    }
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('accessRights');
    localStorage.removeItem('adminName');
    this.router.navigate(['/login']);
  }

  hasAccess(right: string): boolean {
    if (this.isSuperAdmin) {
      return true;
    }
    return this.accessRights.includes(right);
  }
}
