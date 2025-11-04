package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "team_members")
public class TeamMember {

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

    @Enumerated(EnumType.STRING)
    @Column(name = "working_hours_per_day", nullable = false, length = 2)
    private WorkingHours workingHoursPerDay;

    @Column(name = "team_id", nullable = false, length = 50)
    private String teamId;

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

    public enum WorkingHours {
        FOUR("4"), SIX("6"), EIGHT("8"), TEN("10"), TWELVE("12");

        private final String hours;

        WorkingHours(String hours) {
            this.hours = hours;
        }

        public String getHours() {
            return hours;
        }

        public static WorkingHours fromString(String hours) {
            for (WorkingHours wh : WorkingHours.values()) {
                if (wh.hours.equals(hours)) {
                    return wh;
                }
            }
            throw new IllegalArgumentException("Invalid working hours: " + hours);
        }
    }

    // Constructors
    public TeamMember() {}

    public TeamMember(String fullName, String nic, String contactNo, LocalDate birthDate,
                      String address, District city, Specialization specialization,
                      LocalDate joinedDate, WorkingHours workingHoursPerDay,
                      String teamId, User supervisor) {
        this.fullName = fullName;
        this.nic = nic;
        this.contactNo = contactNo;
        this.birthDate = birthDate;
        this.address = address;
        this.city = city;
        this.specialization = specialization;
        this.joinedDate = joinedDate;
        this.workingHoursPerDay = workingHoursPerDay;
        this.teamId = teamId;
        this.supervisor = supervisor;
        calculateAge();
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
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
        calculateAge();
    }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public District getCity() { return city; }
    public void setCity(District city) { this.city = city; }

    public Specialization getSpecialization() { return specialization; }
    public void setSpecialization(Specialization specialization) { this.specialization = specialization; }

    public LocalDate getJoinedDate() { return joinedDate; }
    public void setJoinedDate(LocalDate joinedDate) { this.joinedDate = joinedDate; }

    public WorkingHours getWorkingHoursPerDay() { return workingHoursPerDay; }
    public void setWorkingHoursPerDay(WorkingHours workingHoursPerDay) { this.workingHoursPerDay = workingHoursPerDay; }

    public String getTeamId() { return teamId; }
    public void setTeamId(String teamId) { this.teamId = teamId; }

    public User getSupervisor() { return supervisor; }
    public void setSupervisor(User supervisor) { this.supervisor = supervisor; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Utility methods
    public Long getSupervisorId() {
        return supervisor != null ? supervisor.getId() : null;
    }

    public String getSupervisorName() {
        return supervisor != null ?
                (supervisor.getFirstName() + " " + supervisor.getLastName()).trim() : null;
    }
}