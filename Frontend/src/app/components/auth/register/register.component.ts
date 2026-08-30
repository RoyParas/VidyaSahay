import { Component } from '@angular/core';
import { RouterLink } from "@angular/router";

@Component({
  selector: 'app-register',
  imports: [RouterLink],
  templateUrl: './register.component.html',
  styleUrls: ['../auth.component.css','./register.component.css']
})
export class RegisterComponent {
    logoPath = "VidyaSahay_Logo.png"
}
