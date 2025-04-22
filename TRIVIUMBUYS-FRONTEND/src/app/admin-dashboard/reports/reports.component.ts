import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-reports',
  standalone: true,
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.scss'],
  imports: [CommonModule, FormsModule]
})
export class ReportsComponent {

  selectedManagement: string = 'orders';
  selectedDuration: string = 'today';
  selectedFormat: string = 'pdf';

  constructor(private http: HttpClient) {}

  exportReport() {
    const token = localStorage.getItem('token');

    if (!token) {
      console.error('No token found. Please login.');
      alert('Session expired. Please login again.');
      return;
    }

    const headers = new HttpHeaders({
      Authorization: `Bearer ${token}`
    });

    const url = `http://localhost:8080/api/reports/${this.selectedManagement}?format=${this.selectedFormat}&duration=${this.selectedDuration}`;

    this.http.get(url, { headers, responseType: 'blob' }).pipe(
      catchError(error => {
        console.error('Error downloading report:', error);
        alert('Failed to download report. Please try again.');
        return of(null);
      })
    ).subscribe(blob => {
      if (blob) {
        const downloadLink = document.createElement('a');
        const blobUrl = window.URL.createObjectURL(blob);
        const fileExtension = this.getFileExtension(this.selectedFormat);
        
        downloadLink.href = blobUrl;
        downloadLink.download = `${this.selectedManagement}-${this.selectedDuration}-report.${fileExtension}`;
        downloadLink.click();
        window.URL.revokeObjectURL(blobUrl);
      }
    });
  }

  // Helper method to map format to correct file extension
  private getFileExtension(format: string): string {
    switch (format.toLowerCase()) {
      case 'pdf':
        return 'pdf';
      case 'excel':
        return 'xlsx';
      case 'word':
        return 'docx';
      case 'html':
        return 'html';
      default:
        return format;
    }
  }
}