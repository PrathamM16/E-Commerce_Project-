import { Component, OnInit, AfterViewInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { CommonModule, NgIf } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { jwtDecode } from 'jwt-decode';
import gsap from 'gsap'; // ✅ Install if not installed

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [NgIf, ReactiveFormsModule, HttpClientModule, RouterLink, CommonModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit, AfterViewInit {

  loginForm!: FormGroup;
  loginError: string = '';

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  ngAfterViewInit(): void {
    this.initializeYetiAnimation();
  }

  onSubmit() {
    if (this.loginForm.invalid) {
      return;
    }
  
    this.authService.login(this.loginForm.value).subscribe({
      next: (res) => {
        localStorage.setItem('token', res.token);
        localStorage.setItem('role', res.role);
  
        const decodedToken: any = jwtDecode(res.token);
        console.log('Logged-in user:', decodedToken.sub);
  
        // ✅ SAVE adminName and accessRights from backend
        localStorage.setItem('adminName', decodedToken.sub); // Save username
        if (res.accessRights) { 
          localStorage.setItem('accessRights', JSON.stringify(res.accessRights)); 
        }
  
        if (res.role === 'ADMIN') {
          this.router.navigate(['/admin-dashboard']);
        } else if (res.role === 'CUSTOMER') {
          this.router.navigate(['/customer-dashboard']);
        } else if (res.role === 'DELIVERY') {
          this.router.navigate(['/delivery-dashboard']);
        } else {
          this.loginError = 'Unknown role!';
        }
      },
      error: (err) => {
        console.error(err);
        this.loginError = 'Invalid Username or Password';
      }
    });
  }
  
  

  initializeYetiAnimation() {
    const emailInput = document.querySelector<HTMLInputElement>('#username');
    const passwordInput = document.querySelector<HTMLInputElement>('#password');
    const armL = document.querySelector<SVGElement>('.armL');
    const armR = document.querySelector<SVGElement>('.armR');

    if (!emailInput || !passwordInput || !armL || !armR) {
      console.error('Yeti Animation elements not found.');
      return;
    }

    // Cover eyes on password focus
    passwordInput.addEventListener('focus', () => {
      gsap.to(armL, { x: -93, y: 2, rotation: 0, duration: 0.45 });
      gsap.to(armR, { x: -93, y: 2, rotation: 0, duration: 0.45, delay: 0.1 });
    });

    // Uncover eyes on password blur
    passwordInput.addEventListener('blur', () => {
      gsap.to(armL, { y: 220, rotation: 105, duration: 1.35 });
      gsap.to(armR, { y: 220, rotation: -105, duration: 1.35, delay: 0.1 });
    });

    // Initialize arm position
    gsap.set(armL, { x: -93, y: 220, rotation: 105, transformOrigin: "top left" });
    gsap.set(armR, { x: -93, y: 220, rotation: -105, transformOrigin: "top right" });
  }
}
