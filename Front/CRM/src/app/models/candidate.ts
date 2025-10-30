export interface Candidate {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  applicationDate: string;  // Assuming it's a string in ISO format, otherwise, adjust the type
  applicationSource: string;
  status: string;  // You might want to create an enum for Status if needed
  imagePath?: string;  // Optional field for image path
}
