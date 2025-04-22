import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { NgIf } from '@angular/common';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [NgIf, ReactiveFormsModule, RouterLink],  // Added RouterLink import
  templateUrl: './signup.component.html',
  styleUrls: ['./signup.component.scss']
})
export class SignupComponent implements OnInit {

  signupForm!: FormGroup;
  passwordMismatch: boolean = false;
  signupError: string = '';

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.signupForm = this.fb.group({
      name: ['', [Validators.required]],
      username: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
      password: ['', [
        Validators.required,
        Validators.minLength(8),
        Validators.pattern('^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$')
      ]],
      confirmPassword: ['', Validators.required],
      address: ['', [Validators.required]]
    });
  }

  onSubmit() {
    if (this.signupForm.invalid) {
      console.log("Form is invalid");
      return;
    }

    const { password, confirmPassword } = this.signupForm.value;
    if (password !== confirmPassword) {
      console.log("Passwords do not match");
      this.passwordMismatch = true;
      return;
    }

    this.passwordMismatch = false;

    const signupData = {
      name: this.signupForm.value.name,
      username: this.signupForm.value.username,
      email: this.signupForm.value.email,
      phone: this.signupForm.value.phone,
      password: this.signupForm.value.password,
      address: this.signupForm.value.address,
    };

    this.authService.signup(signupData).subscribe({
      next: (res) => {
        alert(res.message || 'Signup successful! Now login.');
        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.error(err);
        this.signupError = err.error?.error || 'Signup failed. Please try again.';
      }
    });
  }
}