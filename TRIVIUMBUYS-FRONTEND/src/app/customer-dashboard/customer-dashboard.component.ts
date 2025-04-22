import { Component, OnInit, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CartService } from '../services/cart.service';
import { CustomerService } from '../services/customer.service';

@Component({
  selector: 'app-customer-dashboard',
  standalone: true,
  templateUrl: './customer-dashboard.component.html',
  styleUrls: ['./customer-dashboard.component.scss'],
  imports: [CommonModule, FormsModule]
})
export class CustomerDashboardComponent implements OnInit, AfterViewInit {

  products: any[] = [];
  filteredProducts: any[] = [];
  paginatedProducts: any[] = [];
  categories: any[] = [];
  selectedCategory: string = '';
  searchQuery: string = '';
  sortOption: string = '';
  cartCount: number = 0;
  userId!: number;

  itemsPerPage: number = 12;
  currentPage: number = 1;
  totalPages: number = 1;

  bannerImages = [
    'https://i.ytimg.com/vi/suEUu3CYoxs/maxresdefault.jpg',
    'https://th.bing.com/th/id/OIP.OnISiU3lKrSTpQFYuEpATQHaEB',
    'https://m.media-amazon.com/images/G/31/img2021/Sportswear_21/SW_22/Dec/Eoss/ANUSHKA-LAUNCH-1440X500.jpg',
    'data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAs...'
  ];

  currentSlide = 0;

  // --- Animation related ---
  showCartoon: boolean = false;
  cartoonImageUrl: string = 'https://img.freepik.com/premium-vector/cartoon-skater-boy-pushing-shopping-cart_9645-2209.jpg?ga=GA1.1.1814408975.1744862421&semt=ais_hybrid&w=740';
  fullText: string = "Buy the products! It's on discount.";
  typedText: string = '';

  constructor(
    private customerService: CustomerService,
    private cartService: CartService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.userId = Number(localStorage.getItem('userId'));
    this.fetchProducts();
    this.fetchCategories();
    this.getCartCount();
    setInterval(() => {
      this.currentSlide = (this.currentSlide + 1) % this.bannerImages.length;
    }, 3000);
  }

  ngAfterViewInit(): void {
    // After 20 seconds show animation
    setTimeout(() => {
      this.showCartoon = true;
      this.startTyping();
    }, 20000);

    setTimeout(() => {
      const cards = document.querySelectorAll('.product-card');
      cards.forEach((card: Element) => {
        cards.forEach((card: Element) => {
          card.addEventListener('mousemove', function (e) {
            const element = card as HTMLElement;
            const rect = element.getBoundingClientRect();
            const x = (e as MouseEvent).clientX - rect.left;
            const y = (e as MouseEvent).clientY - rect.top;
        
            element.style.setProperty('--mouse-x-pos', `${(x / rect.width) * 100}%`);
            element.style.setProperty('--mouse-y-pos', `${(y / rect.height) * 100}%`);
            element.style.setProperty('--mouse-x', ((x / rect.width) - 0.5).toFixed(2));
            element.style.setProperty('--mouse-y', ((y / rect.height) - 0.5).toFixed(2));
          });
        
          card.addEventListener('mouseleave', function () {
            const element = card as HTMLElement;
            element.style.transition = 'transform 0.5s ease';
            element.style.transform = 'translateY(-5px) translateX(0) rotateX(0) rotateY(0)';
            setTimeout(() => { element.style.transition = ''; }, 500);
          });
        });
      });
    }, 500);
  }

  startTyping(): void {
    let index = 0;
    const interval = setInterval(() => {
      if (index < this.fullText.length) {
        this.typedText += this.fullText.charAt(index);
        index++;
      } else {
        clearInterval(interval);
      }
    }, 100);
  }

  fetchProducts(): void {
    const token = localStorage.getItem('token')!;
    this.customerService.getAllProducts(token).subscribe({
      next: (res: any[]) => {
        this.products = res.map(product => ({ ...product, quantity: 1 }));
        this.filteredProducts = [...this.products];
        this.calculateTotalPages();
        this.updatePaginatedProducts();
      },
      error: (err) => console.error('Error fetching products', err)
    });
  }

  fetchCategories(): void {
    const token = localStorage.getItem('token')!;
    this.customerService.getAllCategories(token).subscribe({
      next: (res: any[]) => this.categories = res,
      error: (err) => console.error('Error fetching categories', err)
    });
  }

  searchProducts(): void {
    const query = this.searchQuery.trim().toLowerCase();
    this.filteredProducts = this.products.filter(product =>
      product.name.toLowerCase().includes(query)
    );
    this.resetPagination();
  }

  filterByCategory(categoryId: string): void {
    if (categoryId === '') {
      this.filteredProducts = [...this.products];
    } else {
      this.filteredProducts = this.products.filter(product =>
        product.category?.id == categoryId
      );
    }
    this.resetPagination();
  }

  sortProducts(): void {
    if (this.sortOption === 'lowToHigh') {
      this.filteredProducts.sort((a, b) => a.price - b.price);
    } else if (this.sortOption === 'highToLow') {
      this.filteredProducts.sort((a, b) => b.price - a.price);
    }
    this.resetPagination();
  }

  increaseQuantity(product: any): void {
    product.quantity = (product.quantity || 1) + 1;
  }

  decreaseQuantity(product: any): void {
    if ((product.quantity || 1) > 1) {
      product.quantity--;
    }
  }

  addToCart(product: any): void {
    const token = localStorage.getItem('token')!;
    const cartDto = { productId: product.id, userId: this.userId, quantity: product.quantity || 1 };
    this.cartService.addToCart(cartDto, token).subscribe({
      next: () => {
        this.cartCount++;
        alert('Product added to cart successfully!');
        this.navigateToCart();
      },
      error: (err) => {
        console.error('Error adding to cart', err);
        alert('Failed to add product to cart. Please try again.');
      }
    });
  }

  openProductDetails(productId: number): void {
    this.router.navigate(['/customer-dashboard/product', productId]);
  }

  navigateToCart(): void {
    this.router.navigate(['/cart']).then(() => {
      window.location.reload();
    });
  }

  getCartCount(): void {
    const token = localStorage.getItem('token')!;
    this.cartService.viewCart(this.userId, token).subscribe({
      next: (res: any[]) => this.cartCount = res.length,
      error: (err) => console.error('Error fetching cart count', err)
    });
  }

  navigateToOrders(): void {
    this.router.navigate(['/your-orders']);
  }

  onQuantityManualInput(product: any): void {
    if (product.quantity < 1) {
      product.quantity = 1;
    } else if (product.quantity > product.stock) {
      product.quantity = product.stock;
    }
  }

  calculateTotalPages(): void {
    this.totalPages = Math.ceil(this.filteredProducts.length / this.itemsPerPage);
  }

  updatePaginatedProducts(): void {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = Math.min(startIndex + this.itemsPerPage, this.filteredProducts.length);
    this.paginatedProducts = this.filteredProducts.slice(startIndex, endIndex);
  }

  changePage(page: number): void {
    if (page < 1 || page > this.totalPages || page === this.currentPage) {
      return;
    }
    this.currentPage = page;
    this.updatePaginatedProducts();
    document.querySelector('.products-grid')?.scrollIntoView({ behavior: 'smooth' });
  }

  resetPagination(): void {
    this.currentPage = 1;
    this.calculateTotalPages();
    this.updatePaginatedProducts();
  }

  visiblePageNumbers(): number[] {
    const visiblePages: number[] = [];
    if (this.totalPages <= 5) {
      for (let i = 1; i <= this.totalPages; i++) {
        visiblePages.push(i);
      }
    } else {
      const startPage = Math.max(1, this.currentPage - 1);
      const endPage = Math.min(this.totalPages, this.currentPage + 1);
      if (startPage > 1) visiblePages.push(1);
      if (startPage > 2) visiblePages.push(-1);
      for (let i = startPage; i <= endPage; i++) {
        visiblePages.push(i);
      }
    }
    return visiblePages.filter(page => page > 0);
  }

  showEndEllipsis(): boolean {
    return this.totalPages > 5 && this.currentPage < this.totalPages - 1;
  }

  showLastPageButton(): boolean {
    return this.totalPages > 2 && this.currentPage < this.totalPages;
  }

  nextSlide(): void {
    this.currentSlide = (this.currentSlide + 1) % this.bannerImages.length;
  }

  prevSlide(): void {
    this.currentSlide = this.currentSlide === 0 ? this.bannerImages.length - 1 : this.currentSlide - 1;
  }

  goToSlide(index: number): void {
    if (index >= 0 && index < this.bannerImages.length) {
      this.currentSlide = index;
    }
  }
}
