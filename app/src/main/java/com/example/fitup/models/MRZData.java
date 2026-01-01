package com.example.fitup.models;

import java.io.Serializable;

public class MRZData implements Serializable {

    private String documentType;
    private String documentNumber;
    private String nationality;
    private String dateOfBirth;
    private String gender;
    private String dateOfExpiry;
    private String personalNumber;
    private String name;

    public MRZData() {
    }

    public MRZData(String documentNumber, String dateOfBirth, String dateOfExpiry) {
        this.documentNumber = documentNumber;
        this.dateOfBirth = dateOfBirth;
        this.dateOfExpiry = dateOfExpiry;
    }

    // Getters and Setters
    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDateOfExpiry() {
        return dateOfExpiry;
    }

    public void setDateOfExpiry(String dateOfExpiry) {
        this.dateOfExpiry = dateOfExpiry;
    }

    public String getPersonalNumber() {
        return personalNumber;
    }

    public void setPersonalNumber(String personalNumber) {
        this.personalNumber = personalNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "MRZData{" +
                "documentType='" + documentType + '\'' +
                ", documentNumber='" + documentNumber + '\'' +
                ", nationality='" + nationality + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", gender='" + gender + '\'' +
                ", dateOfExpiry='" + dateOfExpiry + '\'' +
                ", personalNumber='" + personalNumber + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}