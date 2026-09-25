package com.vidyasahay.vidyasahay.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.vidyasahay.vidyasahay.enums.Gender;

@Entity
@Table(name = "students")
public class Student extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false, updatable = false)
	private UUID id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false, unique = true)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "institute_id", nullable = true, unique = false)
	private Institute institute;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "address_id", nullable = false)
	private Address address;

	@Column(name = "location", length = 255)
	private String location;

	@Column(name = "pincode", nullable = false)
	private Integer pincode;

	@Column(name = "aadhar_number", nullable = false, unique = true, length = 12)
	private String aadharNumber;

	@Enumerated(EnumType.STRING)
	@Column(name = "gender", nullable = false, length = 30)
	private Gender gender;

	@Column(name = "date_of_birth", nullable = false)
	private LocalDate dateOfBirth;

	@Column(name = "father_name", nullable = false, length = 150)
	private String fatherName;

	@Column(name = "mother_name", nullable = false, length = 150)
	private String motherName;

	@Column(name = "fees_paid", nullable = true, precision = 15, scale = 2)
	private BigDecimal feesPaid;

	@Column(name = "fees_pending", nullable = true, precision = 15, scale = 2)
	private BigDecimal feesPending;

	@Column(name = "annual_family_income", nullable = false, precision = 15, scale = 2)
	private BigDecimal annualFamilyIncome;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "category_id", nullable = false, unique = false)
	private Category category;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "course_id", nullable = false, unique = false)
	private Course course;

	public Student() {
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Institute getInstitute() {
		return institute;
	}

	public void setInstitute(Institute institute) {
		this.institute = institute;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Integer getPincode() {
		return pincode;
	}

	public void setPincode(Integer pincode) {
		this.pincode = pincode;
	}

	public String getAadharNumber() {
		return aadharNumber;
	}

	public void setAadharNumber(String aadharNumber) {
		this.aadharNumber = aadharNumber;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getFatherName() {
		return fatherName;
	}

	public void setFatherName(String fatherName) {
		this.fatherName = fatherName;
	}

	public String getMotherName() {
		return motherName;
	}

	public void setMotherName(String motherName) {
		this.motherName = motherName;
	}

	public BigDecimal getFeesPaid() {
		return feesPaid;
	}

	public void setFeesPaid(BigDecimal feesPaid) {
		this.feesPaid = feesPaid;
	}

	public BigDecimal getFeesPending() {
		return feesPending;
	}

	public void setFeesPending(BigDecimal feesPending) {
		this.feesPending = feesPending;
	}

	public BigDecimal getAnnualFamilyIncome() {
		return annualFamilyIncome;
	}

	public void setAnnualFamilyIncome(BigDecimal annualFamilyIncome) {
		this.annualFamilyIncome = annualFamilyIncome;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}
}
