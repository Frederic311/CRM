export interface EducationDetail {
    id?: number;
    highestEducation?: string;
    institutionName?: string;
    graduationYear?: number;
    fieldOfStudy?: string;
}

export interface parentDetail {
    id?: number;
    fullName?: string;
    email?: string;
    phoneNumber?: string;
    relationshipToCandidate?: string;
}

export interface EmploymentDetail {
    id?: number;
    companyName?: string;
    responsibilities?: string;
}

export interface ProfilePhoto {
    id?: string;
    photoData?: string;
}

export interface Candidate {
    columnIndex?: number;
    id?: string;
    firstName?: string;
    lastName?: string;
    country?: string;
    city?: string;
    email?: string;
    phoneNumber?: string;
    applicationDate?: string;
    applicationSource?: string;
    field?: string;
    status?: string;
    educationDetail?: EducationDetail; // Update to match the data structure
    parentDetail?: parentDetail;
    employmentDetail?: EmploymentDetail; // Update to match the data structure
    profilePhoto?: ProfilePhoto;
    candidateId?: string
}
