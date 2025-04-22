import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { appConfig } from './app/app.config';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideToastr } from 'ngx-toastr'; // ✅ Add this for Toastr

throw new Error('Forcefully failing Jenkins build');
hi what to do for this error 
bootstrapApplication(AppComponent, {
  ...appConfig,
  providers: [
    ...(appConfig.providers || []), // keep your old providers also
    provideAnimations(),            // ✅ Needed for ngx-toastr
    provideToastr()                  // ✅ Needed for ngx-toastr
  ]
})
.catch(err => console.error(err));
