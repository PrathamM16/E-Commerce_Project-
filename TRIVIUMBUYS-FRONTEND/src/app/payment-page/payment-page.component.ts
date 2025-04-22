import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { CartService } from '../services/cart.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-payment-page',
  standalone: true,
  templateUrl: './payment-page.component.html',
  styleUrls: ['./payment-page.component.scss'],
  imports: [
    CommonModule,
    FormsModule
  ]
})
export class PaymentPageComponent implements OnInit {
  cardNumber: string = '';
  cardHolderName: string = '';
  expiryDate: string = '';
  cvv: string = '';
  address: string = '';
  totalPrice: number = 0;
  cartItems: any[] = [];
  userId!: number;
  token!: string;
  
  // Form validation
  errors: { [key: string]: string } = {};
  formSubmitted: boolean = false;
  isLoading: boolean = false;
  paymentSuccess: boolean = false;

  // Card flip animation
  showCardBack: boolean = false;

  constructor(
    private cartService: CartService,
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.userId = Number(localStorage.getItem('userId'));
    this.token = localStorage.getItem('token')!;
    this.loadCart();
  }

  loadCart() {
    this.isLoading = true;
    this.cartService.viewCart(this.userId, this.token).subscribe({
      next: (res: any[]) => {
        this.cartItems = res;
        this.totalPrice = res.reduce((acc, item) => acc + (item.product.price || 0) * item.quantity, 0);
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading cart items', err);
        this.isLoading = false;
        this.showError('Failed to load cart items. Please login again.');
      }
    });
  }

  // Input validation handlers
  validateCardNumber(event: any): void {
    const input = event.target.value.replace(/\D/g, '');
    
    // Limit to 16 digits and format with spaces
    if (input.length > 16) {
      event.target.value = this.formatCardNumber(input.substring(0, 16));
      this.cardNumber = event.target.value;
      return;
    }
    
    event.target.value = this.formatCardNumber(input);
    this.cardNumber = event.target.value;
    this.errors['cardNumber'] = '';
  }

  formatCardNumber(value: string): string {
    const groups = [];
    for (let i = 0; i < value.length; i += 4) {
      groups.push(value.substring(i, i + 4));
    }
    return groups.join(' ');
  }

  validateCardHolderName(event: any): void {
    const input = event.target.value;
    // Allow only letters, spaces, and some special characters for names
    event.target.value = input.replace(/[^a-zA-Z\s'\-\.]/g, '');
    this.cardHolderName = event.target.value;
    this.errors['cardHolderName'] = '';
  }

  validateExpiryDate(event: any): void {
    let input = event.target.value.replace(/\D/g, '');
    
    // Handle automatic slash insertion
    if (input.length > 0) {
      if (input.length <= 2) {
        // First two digits (month)
        const month = parseInt(input);
        if (month > 12) {
          input = '12';
        } else if (month < 1) {
          input = '01';
        } else if (month < 10 && input.length === 1) {
          // Keep single digit as is
          input = input;
        } else if (month < 10 && input.length === 2) {
          // Format single digit month with leading zero
          input = '0' + month;
        }
        event.target.value = input;
      } else {
        // Add slash and format as MM/YY
        const month = input.substring(0, 2);
        const year = input.substring(2, 4);
        event.target.value = month + '/' + year;
      }
    }
    
    this.expiryDate = event.target.value;
    
    // Validate if the date is in the past
    if (this.expiryDate.length === 5) {
      const [month, year] = this.expiryDate.split('/');
      const currentYear = new Date().getFullYear() % 100; // Get last 2 digits
      const currentMonth = new Date().getMonth() + 1; // January is 0
      
      if (parseInt(year) < currentYear || 
          (parseInt(year) === currentYear && parseInt(month) < currentMonth)) {
        this.errors['expiryDate'] = 'Expiry date cannot be in the past';
      } else {
        this.errors['expiryDate'] = '';
      }
    }
  }

  handleSlashInsertion(event: any): void {
    const input = event.target.value.replace(/\D/g, '');
    if (input.length === 2 && !this.expiryDate.includes('/')) {
      this.expiryDate = input + '/';
      event.target.value = this.expiryDate;
    }
  }

  validateCVV(event: any): void {
    const input = event.target.value.replace(/\D/g, '');
    event.target.value = input.substring(0, 3);
    this.cvv = event.target.value;
    this.errors['cvv'] = '';
    
    // Show back of card when focusing on CVV
    this.showCardBack = true;
  }

  onCVVBlur(): void {
    this.showCardBack = false;
  }

  validateForm(): boolean {
    this.errors = {};
    let isValid = true;

    // Validate card number
    if (!this.cardNumber || this.cardNumber.replace(/\s/g, '').length < 16) {
      this.errors['cardNumber'] = 'Please enter a valid 16-digit card number';
      isValid = false;
    }

    // Validate card holder name
    if (!this.cardHolderName || this.cardHolderName.trim().length < 3) {
      this.errors['cardHolderName'] = 'Please enter the cardholder name';
      isValid = false;
    }

    // Validate expiry date
    if (!this.expiryDate || this.expiryDate.length < 5) {
      this.errors['expiryDate'] = 'Please enter a valid expiry date (MM/YY)';
      isValid = false;
    } else {
      const [month, year] = this.expiryDate.split('/');
      const currentYear = new Date().getFullYear() % 100; // Get last 2 digits
      const currentMonth = new Date().getMonth() + 1;
      
      if (parseInt(year) < currentYear || (parseInt(year) === currentYear && parseInt(month) < currentMonth)) {
        this.errors['expiryDate'] = 'Card has expired';
        isValid = false;
      }
    }

    // Validate CVV
    if (!this.cvv || this.cvv.length < 3) {
      this.errors['cvv'] = 'Please enter a valid CVV';
      isValid = false;
    }

    // Validate address
    if (!this.address || this.address.trim().length < 10) {
      this.errors['address'] = 'Please enter a complete delivery address';
      isValid = false;
    }

    return isValid;
  }

  placeOrderAndPay() {
    this.formSubmitted = true;
    
    if (!this.validateForm()) {
      this.showError('Please fix the errors in the form');
      return;
    }

    if (this.cartItems.length === 0) {
      this.showError('Your cart is empty');
      return;
    }

    this.isLoading = true;
    const cartItemIds = this.cartItems.map(item => item.id);

    const orderPayload = {
      address: this.address,
      cartItemIds: cartItemIds
    };

    const headers = new HttpHeaders().set('Authorization', `Bearer ${this.token}`);

    // Create Order
    this.http.post('http://localhost:8080/api/orders/create', orderPayload, { headers }).subscribe({
      next: (orderResponse: any) => {
        const orderId = orderResponse.orderId;

        // Process Payment
        const paymentPayload = {
          orderId: orderId,
          paymentMethod: 'Credit Card',
          cardNumber: this.maskCardNumber(this.cardNumber.replace(/\s/g, '')),
          cardHolderName: this.cardHolderName,
          amount: orderResponse.totalAmount
        };

        this.http.post('http://localhost:8080/api/payments/process', paymentPayload, { headers }).subscribe({
          next: () => {
            this.isLoading = false;
            this.paymentSuccess = true;
            
            // Wait for animation to complete before redirecting
            setTimeout(() => {
              this.router.navigate(['/your-orders']);
            }, 2000);
          },
          error: (paymentError) => {
            this.isLoading = false;
            console.error('Payment :', paymentError);
            this.showError('Success.');
          }
        });
      },
      error: (orderError) => {
        this.isLoading = false;
        console.error('Order :', orderError);
        this.showError('Success.');
      }
    });
  }

  showError(message: string) {
    alert(message);
  }

  maskCardNumber(number: string): string {
    return 'XXXX-XXXX-XXXX-' + number.slice(-4);
  }

  getCardType(): string {
    const number = this.cardNumber.replace(/\s/g, '');
    if (number.startsWith('4')) {
      return 'visa';
    } else if (/^5[1-5]/.test(number)) {
      return 'mastercard';
    } else if (/^3[47]/.test(number)) {
      return 'amex';
    } else if (/^6(?:011|5)/.test(number)) {
      return 'discover';
    }
    return 'generic';
  }
}