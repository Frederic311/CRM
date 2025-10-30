import { Component, QueryList } from '@angular/core';
import { MatIconModule, MatIconRegistry } from '@angular/material/icon';
import { DomSanitizer } from '@angular/platform-browser';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../services/auth/auth.service';
import { MatMenuModule } from '@angular/material/menu';
import { MatButtonModule } from '@angular/material/button';
@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterModule, MatIconModule,MatMenuModule,MatButtonModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'

})
export class NavbarComponent {
  min: boolean = false;
  constructor(
    private matIconRegistry: MatIconRegistry,
    private domSanitizer: DomSanitizer,
    private router: Router,
    private authService: AuthService
  ) {
    this.matIconRegistry.addSvgIcon(
      'home',
      this.domSanitizer.bypassSecurityTrustResourceUrl('/assets/icons/home-1-svgrepo-com.svg')
    );
    this.matIconRegistry.addSvgIcon(
      'users',
      this.domSanitizer.bypassSecurityTrustResourceUrl('/assets/icons/users-svgrepo-com.svg')
    );
    this.matIconRegistry.addSvgIcon(
      'pipeline',
      this.domSanitizer.bypassSecurityTrustResourceUrl('/assets/icons/pipeline-svgrepo-com (1).svg')
    );
    this.matIconRegistry.addSvgIcon(
      'logout',
      this.domSanitizer.bypassSecurityTrustResourceUrl('/assets/icons/logout-2-svgrepo-com.svg')
    );
    this.matIconRegistry.addSvgIcon(
      'candidate',
      this.domSanitizer.bypassSecurityTrustResourceUrl('/assets/icons/users-more-svgrepo-com.svg')
    );
    this.matIconRegistry.addSvgIcon(
      'task',
      this.domSanitizer.bypassSecurityTrustResourceUrl('/assets/icons/task-svgrepo-com.svg')
    );
    this.matIconRegistry.addSvgIcon(
      'calendar',
      this.domSanitizer.bypassSecurityTrustResourceUrl('/assets/icons/calendar-svgrepo-com.svg')
    );
  }

  isActiveRoute(route: string): boolean {
    return this.router.url === route;
  }

  navlogout() {
    this.authService.logout();
}

  isActive(route: string): boolean {
    // Check if the current URL starts with the given route or if the link has the 'active-tab' class
    // @ts-ignore
    return this.router.url.startsWith(route) || document.querySelector(`a[routerLink="${route}"]`)?.classList.contains('active-tab');
  }


  minimize(){
    this.min = !this.min;
    console.log(this.min);
    if (!this.min) {
      const sides = document.querySelectorAll<HTMLDivElement>('.side');
      sides.forEach((side) => {
        side.style.display = 'block';
        side.classList.add('animated');
        side.addEventListener('animationend', () => {
          side.classList.remove('animated');
        })
      })
    } 
    else {
      setTimeout(() => {
        const sides = document.querySelectorAll<HTMLDivElement>('.side');
        sides.forEach((side) => {
          side.style.display = 'none';
        })
      }, 1000)
    }
  }
}
