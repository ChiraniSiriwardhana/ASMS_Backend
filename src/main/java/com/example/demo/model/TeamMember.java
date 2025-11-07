package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "team_members")
public class TeamMember {

    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @NotBlank(message = "NIC is required")
    @Pattern(regexp = "^[0-9]{12}$", message = "NIC must be exactly 12 digits")
    @Column(name = "nic", nullable = false, unique = true, length = 12)
    private String nic;

    @NotBlank(message = "Contact number is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Contact number must be 10-15 digits")
    @Column(name = "contact_no", nullable = false, length = 15)
    private String contactNo;

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "age", nullable = false)
    private Integer age;

    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 200, message = "Address must be between 5 and 200 characters")
    @Column(name = "address", nullable = false, length = 200)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "city", nullable = false, length = 50)
    private District city;

    @Enumerated(EnumType.STRING)
    @Column(name = "specialization", nullable = false, length = 50)
    private Specialization specialization;

    @NotNull(message = "Joined date is required")
    @PastOrPresent(message = "Joined date cannot be in the future")
    @Column(name = "joined_date", nullable = false)
    private LocalDate joinedDate;

    @Column(name = "working_hours_per_day", nullable = false, length = 2)
    private String workingHoursPerDay; // Store "4", "6", "8", etc.

    // Foreign key relationship with Team table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", referencedColumnName = "id")
    private Team team;

    // Foreign key relationship with User table for supervisor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id", referencedColumnName = "id")
    private User supervisor;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateAge();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateAge();
    }

    private void calculateAge() {
        if (birthDate != null) {
            this.age = java.time.Period.between(birthDate, LocalDate.now()).getYears();
        }
    }

    // Enums
    public enum District {
        AMPARA, ANURADHAPURA, BADULLA, BATTICALOA, COLOMBO, GALLE, GAMPAHA,
        HAMBANTOTA, JAFFNA, KALUTARA, KANDY, KEGALLE, KILINOCHCHI, KURUNEGALA,
        MANNAR, MATALE, MATARA, MONERAGALA, MULLAITIVU, NUWARA_ELIYA, POLONNARUWA,
        PUTTALAM, RATNAPURA, TRINCOMALEE, VAVUNIYA
    }

    public enum Specialization {
        ENGINE, TRANSMISSION, SUSPENSION, BRAKES, ELECTRICAL,
        BODYWORK, INTERIOR, DIAGNOSTICS
    }

    // Constructors
    public TeamMember() {}

    public void setId(Long id) { this.id = id; }

    public void setFullName(String fullName) { this.fullName = fullName; }

    public void setNic(String nic) { this.nic = nic; }

    public void setContactNo(String contactNo) { this.contactNo = contactNo; }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
        calculateAge();
    }

    public void setAge(Integer age) { this.age = age; }

    public void setAddress(String address) { this.address = address; }

    public void setCity(District city) { this.city = city; }

    public void setSpecialization(Specialization specialization) { this.specialization = specialization; }

    public void setJoinedDate(LocalDate joinedDate) { this.joinedDate = joinedDate; }

    public void setWorkingHoursPerDay(String workingHoursPerDay) { this.workingHoursPerDay = workingHoursPerDay; }

    public void setTeam(Team team) { this.team = team; }

    public void setSupervisor(User supervisor) { this.supervisor = supervisor; }

    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Utility methods
    public Long getTeamId() {
        return team != null ? team.getId() : null;
    }

    public String getTeamName() {
        return team != null ? team.getName() : null;
    }

    public Long getSupervisorId() {
        return supervisor != null ? supervisor.getId() : null;
    }

    public String getSupervisorName() {
        return supervisor != null ?
                (supervisor.getFirstName() + " " + supervisor.getLastName()).trim() : null;
    }
}