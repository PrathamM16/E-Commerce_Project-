import { ApplicationConfig, importProvidersFrom, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { routes } from './app.routes';
import { MaterialModule } from './material.module';
import { CustomerService } from './services/customer.service';
import { provideLottieOptions } from 'ngx-lottie';
import player from 'lottie-web';

// Factory function for Lottie player
export function playerFactory() {
  return player;
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(),
    CustomerService,
    importProvidersFrom(MaterialModule),
    provideLottieOptions({
      player: playerFactory
    })
  ]
};