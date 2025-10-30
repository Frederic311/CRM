import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'candidateFilter',
  standalone: true
})
export class CandidateFilterPipe implements PipeTransform {
  transform(users: any[], fieldName: string | null = null, filterValue: string | null = null, isAscending: boolean = true): any[] {
    console.log("fieldName: "+fieldName);
    console.log("filterValue: "+filterValue);
    console.log("isAscending: "+isAscending);

    let filteredUsers: any[] = users;

    if (fieldName && !filterValue) {
      let sortableField: string = fieldName.toLowerCase();
    
      if (sortableField === 'recent') {
        sortableField = 'applicationDate';
      }
    
      // Sort users based on the isAscending boolean
      filteredUsers.sort((a, b) => {
        if (a[sortableField] < b[sortableField]) {
          return isAscending ? -1 : 1;
        }
        if (a[sortableField] > b[sortableField]) {
          return isAscending ? 1 : -1;
        }
        return 0; // When values are equal
      });
    
      console.log("sorted values", filteredUsers);
    }
    
    if (!fieldName && !filterValue) {
      // Sort users based on the isAscending boolean
      filteredUsers.sort((a, b) => {
        if (a.firstName < b.firstName) {
          return isAscending ? -1 : 1;
        }
          if (a.firstName > b.firstName) {
          return isAscending ? 1 : -1;
        }
          return 0; // When values are equal
      });
      console.log("sorted values", filteredUsers);
    }
    return filteredUsers;
  }
}
