package com.example.demo.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class TeamMemberDTO {

    private Long id;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "NIC is required")
    @Pattern(regexp = "^[0-9]{12}$", message = "NIC must be exactly 12 digits")
    private String nic;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Contact number must be 10-15 digits")
    private String contactNo;

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    private Integer age;

    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 200, message = "Address must be between 5 and 200 characters")
    private String address;

    @NotNull(message = "City is required")
    private String city;

    @NotNull(message = "Specialization is required")
    private String specialization;

    @NotNull(message = "Joined date is required")
    @PastOrPresent(message = "Joined date cannot be in the future")
    private LocalDate joinedDate;

    @NotNull(message = "Working hours are required")
    private String workingHoursPerDay;

    // Team fields (replacing teamId String with team relationship)
    private Long teamId;
    private String teamName;

    // Supervisor fields
    private Long supervisorId;
    private String supervisorName;

    // Constructors
    public TeamMemberDTO() {}

    public TeamMemberDTO(Long id, String fullName, String nic, String contactNo,
                         LocalDate birthDate, Integer age, String address,
                         String city, String specialization, LocalDate joinedDate,
                         String workingHoursPerDay, Long teamId, String teamName,
                         Long supervisorId, String supervisorName) {
        this.id = id;
        this.fullName = fullName;
        this.nic = nic;
        this.contactNo = contactNo;
        this.birthDate = birthDate;
        this.age = age;
        this.address = address;
        this.city = city;
        this.specialization = specialization;
        this.joinedDate = joinedDate;
        this.workingHoursPerDay = workingHoursPerDay;
        this.teamId = teamId;
        this.teamName = teamName;
        this.supervisorId = supervisorId;
        this.supervisorName = supervisorName;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getNic() { return nic; }
    public void setNic(String nic) { this.nic = nic; }

    public String getContactNo() { return contactNo; }
    public void setContactNo(String contactNo) { this.contactNo = contactNo; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getWorkingHoursPerDay() { return workingHoursPerDay; }
    public void setWorkingHoursPerDay(String workingHoursPerDay) { this.workingHoursPerDay = workingHoursPerDay; }

    public LocalDate getJoinedDate() { return joinedDate; }
    public void setJoinedDate(LocalDate joinedDate) { this.joinedDate = joinedDate; }

    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }

    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }

    public Long getSupervisorId() { return supervisorId; }
    public void setSupervisorId(Long supervisorId) { this.supervisorId = supervisorId; }

    public String getSupervisorName() { return supervisorName; }
    public void setSupervisorName(String supervisorName) { this.supervisorName = supervisorName; }
}