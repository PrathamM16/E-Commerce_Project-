import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { NgIf } from '@angular/common';
import { DeliveryService } from '../services/delivery.service';

@Component({
  selector: 'app-delivery-signup',
  standalone: true,
  imports: [NgIf, ReactiveFormsModule],
  templateUrl: './delivery-signup.component.html',
  styleUrls: ['./delivery-signup.component.scss']
})
export class DeliverySignupComponent implements OnInit {

  deliverySignupForm!: FormGroup;
  passwordMismatch: boolean = false;
  bankAccountMismatch: boolean = false;
  signupError: string = '';

  constructor(private fb: FormBuilder, private deliveryService: DeliveryService, private router: Router) {}

  ngOnInit(): void {
    this.deliverySignupForm = this.fb.group({
      name: ['', Validators.required],
      username: ['', Validators.required],
      aadharNumber: ['', Validators.required],
      panNumber: ['', Validators.required],
      drivingLicense: ['', Validators.required],
      phoneNumber: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
      address: ['', Validators.required],
      bankAccountNumber: ['', Validators.required],
      confirmBankAccountNumber: ['', Validators.required],
      password: ['', Validators.required],
      confirmPassword: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.deliverySignupForm.invalid) {
      return;
    }

    const { password, confirmPassword, bankAccountNumber, confirmBankAccountNumber } = this.deliverySignupForm.value;

    if (password !== confirmPassword) {
      this.passwordMismatch = true;
      return;
    } else {
      this.passwordMismatch = false;
    }

    if (bankAccountNumber !== confirmBankAccountNumber) {
      this.bankAccountMismatch = true;
      return;
    } else {
      this.bankAccountMismatch = false;
    }

    const signupData = {
      name: this.deliverySignupForm.value.name,
      username: this.deliverySignupForm.value.username,
      aadharNumber: this.deliverySignupForm.value.aadharNumber,
      panNumber: this.deliverySignupForm.value.panNumber,
      drivingLicense: this.deliverySignupForm.value.drivingLicense,
      phoneNumber: this.deliverySignupForm.value.phoneNumber,
      address: this.deliverySignupForm.value.address,
      bankAccountNumber: this.deliverySignupForm.value.bankAccountNumber,
      password: this.deliverySignupForm.value.password
    };

    this.deliveryService.registerDeliveryPerson(signupData).subscribe({
      next: (res: any) => {
        alert('Delivery Person Registered Successfully!');
        this.router.navigate(['/login']);
      },
      error: (err: { error: { message: string; }; }) => {
        console.error(err);
        this.signupError = err.error?.message || 'Signup failed. Please try again.';
      }
    });
  }
}
