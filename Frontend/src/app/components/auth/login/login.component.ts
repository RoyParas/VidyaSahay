import { Component } from '@angular/core';
import { RouterLink } from "@angular/router";

@Component({
  selector: 'app-login',
  imports: [RouterLink],
  templateUrl: './login.component.html',
  styleUrls: ['../auth.component.css','./login.component.css']
})
export class LoginComponent {
  logoPath = "VidyaSahay_Logo.png"
}
