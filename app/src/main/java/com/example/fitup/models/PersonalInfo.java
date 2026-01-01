package com.example.fitup.models;

import java.io.Serializable;

public class PersonalInfo implements Serializable {

    private String documentNumber;
    private String name;
    private String nationality;
    private String dateOfBirth;
    private String dateOfExpiry;
    private String gender;
    private byte[] photoData;

    public PersonalInfo() {
    }

    // Getters and Setters
    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDateOfExpiry() {
        return dateOfExpiry;
    }

    public void setDateOfExpiry(String dateOfExpiry) {
        this.dateOfExpiry = dateOfExpiry;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public byte[] getPhotoData() {
        return photoData;
    }

    public void setPhotoData(byte[] photoData) {
        this.photoData = photoData;
    }

    public String getFormattedDateOfBirth() {
        if (dateOfBirth != null && dateOfBirth.length() == 6) {
            // Format YYMMDD to DD/MM/YY
            String day = dateOfBirth.substring(4, 6);
            String month = dateOfBirth.substring(2, 4);
            String year = dateOfBirth.substring(0, 2);
            return day + "/" + month + "/" + year;
        }
        return dateOfBirth;
    }

    public String getFormattedDateOfExpiry() {
        if (dateOfExpiry != null && dateOfExpiry.length() == 6) {
            // Format YYMMDD to DD/MM/YY
            String day = dateOfExpiry.substring(4, 6);
            String month = dateOfExpiry.substring(2, 4);
            String year = dateOfExpiry.substring(0, 2);
            return day + "/" + month + "/" + year;
        }
        return dateOfExpiry;
    }

    @Override
    public String toString() {
        return "PersonalInfo{" +
                "documentNumber='" + documentNumber + '\'' +
                ", name='" + name + '\'' +
                ", nationality='" + nationality + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", dateOfExpiry='" + dateOfExpiry + '\'' +
                ", gender='" + gender + '\'' +
                ", hasPhoto=" + (photoData != null) +
                '}';
    }
}