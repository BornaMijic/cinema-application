import {Directive, HostBinding, HostListener} from '@angular/core';

@Directive({
  selector: '[appMenuDirective]'
})
export class MenuDirective {

  @HostBinding('class.open') isOpen = false;

  @HostListener('click') open() {
    this.isOpen = !this.isOpen;
  }

  @HostListener('mouseleave') close() {
    this.isOpen = false;
  }
}
