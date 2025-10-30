import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'fieldFilter',
  standalone: true
})
export class FieldFilterPipe implements PipeTransform {
  transform(users: any[], fieldName: string | null = null, filterValue: string | null = null, isAscending: boolean = true): any[] {
    console.log("fieldName: "+fieldName);
    console.log("filterValue: "+filterValue);
    console.log("isAscending: "+isAscending);

    let filteredUsers: any[] = users;

    if (fieldName && !filterValue) {
      let sortableField: string = fieldName.toLowerCase();
    
      if (sortableField === 'status') {
        sortableField = 'enabled';
      }
      if (sortableField === 'recent') {
        sortableField = 'createdDate';
      }
      if (sortableField === 'last modified') {
        sortableField = 'lastModifiedDate';
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
        if (a.fullName < b.fullName) {
          return isAscending ? -1 : 1;
        }
          if (a.fullName > b.fullName) {
          return isAscending ? 1 : -1;
        }
          return 0; // When values are equal
      });
      console.log("sorted values", filteredUsers);
    }

    if (fieldName && filterValue) {
      if (fieldName === 'roles') {
        filteredUsers = users.filter(user =>
          user.roles[0].name.toLowerCase().includes(filterValue.toLowerCase())
        );
        // Sort users based on the isAscending boolean
        filteredUsers.sort((a, b) => {
          if (a['fullName'] < b['fullName']) {
            return isAscending ? -1 : 1;
          }
            if (a['fullName'] > b['fullName']) {
            return isAscending ? 1 : -1;
          }
            return 0; // When values are equal
        });
        console.log("sorted values ",filteredUsers)
      } else {
        if (filterValue) {
          filteredUsers = users.filter(user =>
            user[fieldName]?.toLowerCase().includes(filterValue.toLowerCase())
          );
        }
        // Sort users based on the isAscending boolean
        filteredUsers.sort((a, b) => {
          if (a[fieldName] < b[fieldName]) {
            return isAscending ? -1 : 1;
          }
            if (a[fieldName] > b[fieldName]) {
            return isAscending ? 1 : -1;
          }
            return 0; // When values are equal
        });
        console.log("sorted values ",filteredUsers)
      }
    }
    return filteredUsers;
  }
}
