import { Component } from '@angular/core';
import { Router, Event, NavigationEnd } from '@angular/router';
import { RouterOutlet } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { HeaderComponent } from './shared/header/header.component';
import { FooterComponent } from './shared/footer/footer.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, HttpClientModule, HeaderComponent, FooterComponent, CommonModule],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'triviumbuys-frontend';
  showHeaderFooter = true;

  constructor(private router: Router) {
    // Listen to router events to determine when to show/hide header and footer
    this.router.events.subscribe((event: Event) => {
      if (event instanceof NavigationEnd) {
        // List of routes where header and footer should be hidden
        const hiddenHeaderFooterRoutes = [
          '/login', 
          '/signup', 
          '/register',
          '/admin-dashboard'
        ];
        
        // Check if current URL starts with any of the routes where header/footer should be hidden
        this.showHeaderFooter = !hiddenHeaderFooterRoutes.some(route => 
          event.urlAfterRedirects.startsWith(route)
        );
      }
    });
  }
}