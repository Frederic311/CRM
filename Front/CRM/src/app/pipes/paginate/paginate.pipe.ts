import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'paginate',
  standalone: true
})
export class PaginatePipe implements PipeTransform {
  transform(value: any[], page: number, size: number): any[] {
    if (!value || value.length === 0) return [];

    const startIndex = page * size;
    const endIndex = startIndex + size;
    return value.slice(startIndex, endIndex);
  }
}
