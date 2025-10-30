import { Routes } from '@angular/router';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { UserComponent } from './pages/user/user.component';
import { LoginComponent } from './pages/login/login.component';
import {ForgotPasswordComponent} from "./pages/forgot-password/forgot-password.component";
import { UserdetailsComponent } from './components/userdetails/userdetails.component';
import { CandidatesComponent } from './pages/candidates/candidates.component';
import { authGuard} from './guards/auth/auth.guard';
import { PipelineComponent } from './pages/pipeline/pipeline.component';
import { CandidateFormComponent } from './components/candidate-form/candidate-form.component';
import {CalenderComponent} from "./pages/calender/calender.component";
import {TaskComponent} from "./pages/task/task.component";
import {ContactComponent} from "./pages/contact/contact.component";
import { CandidateDetailComponent } from './pages/candidate-detail/candidate-detail.component';
import {VenueComponent} from "./pages/venue/venue.component";
import {TypeComponent} from "./pages/type/type.component";
 import {EventComponent} from "./pages/event/event.component";
 import { UpdateCandidateComponent } from './pages/update-candidate/update-candidate.component';
import { SelfCreationnComponent } from './pages/self-creationn/self-creationn.component';


export const routes: Routes = [
    {path: 'dashboard', component: DashboardComponent,canActivate: [authGuard]},
    {path: 'users', component: UserComponent,canActivate: [authGuard]},
    {path: "", component: LoginComponent},
    {path: 'forget-password', component: ForgotPasswordComponent},
    {path: "login", component: LoginComponent},
    {path: "pipeline", component: PipelineComponent,canActivate: [authGuard]},
    {path: 'users/:id', component: UserdetailsComponent,canActivate: [authGuard] },
    {path: 'candidates', component: CandidatesComponent,canActivate: [authGuard]},
    {path: 'candidates/new', component: CandidateFormComponent, canActivate: [authGuard]},
    {path: "calendar", component:CalenderComponent,canActivate: [authGuard]},
{path: "event", component:EventComponent,canActivate: [authGuard]},

  {path: "venue", component:VenueComponent,canActivate: [authGuard]},
 {path: "type", component:TypeComponent,canActivate: [authGuard]},
  {path: "task", component:TaskComponent,canActivate: [authGuard]},
  {path:"contact", component:ContactComponent,canActivate:[authGuard]},
  { path: 'candidate/:id', component: CandidateDetailComponent, canActivate: [authGuard] },
  { path:"candidate-detail", component:CandidateDetailComponent,canActivate: [authGuard]},
{ path: 'update-candidate/:id', component: UpdateCandidateComponent, canActivate: [authGuard] },
{ path: 'selfCreation', component: SelfCreationnComponent}

];

