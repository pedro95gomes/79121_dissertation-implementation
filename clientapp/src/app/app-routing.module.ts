import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';
import { AuthGuardService } from './services/auth-guard.service';

const routes: Routes = [
  { path: '', loadChildren: () => import('./auth/login/login.module').then(m => m.LoginPageModule) },
  { path: 'register', loadChildren: () => import('./auth/register/register.module').then(m => m.RegisterPageModule) },
  { path: 'tabs', loadChildren: () => import('./tabs/tabs.module').then(m => m.TabsPageModule), canActivate: [AuthGuardService] },
  { path: 'serviceregister', loadChildren: './auth/serviceregister/serviceregister.module#ServiceregisterPageModule' },
  { path: 'service', loadChildren: './tabservice/tabservice.module#TabservicePageModule', canActivate: [AuthGuardService]  }
];
@NgModule({
  imports: [
    RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule {}
