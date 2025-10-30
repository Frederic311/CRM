import { Component,OnInit } from '@angular/core';
import { RouterOutlet,Router,NavigationEnd } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { NavbarComponent } from './components/navbar/navbar.component';
import { HeaderComponent } from './components/header/header.component';
import { filter } from 'rxjs/operators';
import { AuthService } from './services/auth/auth.service';


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, LoginComponent,NavbarComponent, HeaderComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {

  currentComponent: string = "";

  constructor(private router: Router,private authService: AuthService){}

  ngOnInit() {
    // Clear token on app reload
     this.authService.clearTokenOnReload();

    // Clear token on navigation to login page
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(() => {
      const currentRoute = this.router.routerState.root.firstChild;

      if (currentRoute && currentRoute.component) {
        this.currentComponent = currentRoute.component?.name || ' ';
      }

      if (this.router.url === '/login') {
        this.authService.clearTokenOnReload();
      }
    });
  }
  title = 'CRM';
}
