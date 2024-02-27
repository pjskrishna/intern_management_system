package com.rh4.entities;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

//updated at

@Entity
@Table(name="intern")
public class Intern {
	 	@Id
	    //@GeneratedValue(strategy = GenerationType.IDENTITY)
	    @Column(name = "intern_id")
	    private String internId;

	 	@Column(name = "first_name")
	    private String firstName;

	    @Column(name = "last_name")
	    private String lastName;
	    
	    @Column(name = "contact_no", unique = true)
	    private String contactNo;
	    
	    @Column(name = "email_id", unique = true)
	    private String email;

	    @Column(name = "college_name")
	    private String collegeName;

	    @Column(name = "branch_name")
	    private String branch;
	    
	    @Column(name = "icard_image", length = 1000)
	    private String icardImage;
	    
		@Column(name = "noc_pdf", length = 1000)
	    private String nocPdf;
		
		@Column(name = "resume_pdf", length = 1000)
		private String resumePdf;
		
	    @Column(name = "semester")
	    private int semester;
			    
	    @Column(name = "programming_lang_name")
	    private String programmingLangName;
	    
	    @Column(name = "password")
	    private String password;
	    
	    ///////////////////////////////////////////////
	    
	    @Column(name = "permanent_address")
	    private String permanentAddress;

	    @Column(name = "date_of_birth")
	    private Date dateOfBirth;

	    @Column(name = "gender")
	    private String gender;

	    @Column(name = "college_guide_hod_name")
	    private String collegeGuideHodName;

	    @Column(name = "university_name")
	    private String universityName;

	    @Column(name = "degree")
	    private String degree;

	    @Column(name = "aggregate_percentage")
	    private Double aggregatePercentage;

		@Column(name = "project_definition_name")
	    private String projectDefinitionName;
		
	    @ManyToOne
	    private Guide guide;

	    @Column(name = "joining_date")
	    private Date joiningDate;

	    @Column(name = "completion_date")
	    private Date completionDate;

	    @Column(name = "used_resource", columnDefinition = "TEXT")
	    private String usedResource;

	    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", nullable = true)
	    private Date createdAt;

	    @Column(name = "updated_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP", nullable = true)
	    private Date updatedAt;

	    @ManyToOne
	    private GroupEntity group;
	    	

	    public Intern()
	    {
	    	super();
	    }
		public Intern(String internId, String firstName, String lastName, String contactNo, String email,
				String collegeName, String branch, String icardImage, String nocPdf, String resumePdf, int semester,
				String permanentAddress, Date dateOfBirth, String gender, String collegeGuideHodName,
				String universityName, String degree, Double aggregatePercentage, String projectDefinitionName,
				Guide guide, String programmingLangName, Date joiningDate, Date completionDate,String password,
				String usedResource, Date createdAt, Date updatedAt,GroupEntity group) {
			super();
			this.internId = internId;
			this.firstName = firstName;
			this.lastName = lastName;
			this.contactNo = contactNo;
			this.email = email;
			this.collegeName = collegeName;
			this.branch = branch;
			this.icardImage = icardImage;
			this.nocPdf = nocPdf;
			this.resumePdf = resumePdf;
			this.semester = semester;
			this.permanentAddress = permanentAddress;
			this.dateOfBirth = dateOfBirth;
			this.gender = gender;
			this.password = password;
			this.collegeGuideHodName = collegeGuideHodName;
			this.universityName = universityName;
			this.degree = degree;
			this.aggregatePercentage = aggregatePercentage;
			this.projectDefinitionName = projectDefinitionName;
			this.guide = guide;
			this.programmingLangName = programmingLangName;
			this.joiningDate = joiningDate;
			this.completionDate = completionDate;
			this.usedResource = usedResource;
			this.createdAt = createdAt;
			this.updatedAt = updatedAt;
			this.group = group;
		}
		
		public Intern(String firstName, String lastName, String contactNo, String email, String collegeName,Date joiningDate, Date completionDate,
				String branch, String password, String icardImage, String nocPdf, String resumePdf, int semester, String programmingLangName, String universityName, GroupEntity group) {
			super();
			this.firstName = firstName;
			this.lastName = lastName;
			this.contactNo = contactNo;
			this.email = email;
			this.collegeName = collegeName;
			this.joiningDate = joiningDate;
			this.completionDate = completionDate;
			this.branch = branch;
			this.icardImage = icardImage;
			this.nocPdf = nocPdf;
			this.resumePdf = resumePdf;
			this.semester = semester;
			this.password = password;
			this.programmingLangName = programmingLangName;
			this.universityName = universityName;
			this.group = group;
		}

		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getBranch() {
			return branch;
		}

		public void setBranch(String branch) {
			this.branch = branch;
		}

		public String getIcardImage() {
			return icardImage;
		}

		public void setIcardImage(String icardImage) {
			this.icardImage = icardImage;
		}

		public String getNocPdf() {
			return nocPdf;
		}

		public void setNocPdf(String nocPdf) {
			this.nocPdf = nocPdf;
		}

		public String getResumePdf() {
			return resumePdf;
		}

		public void setResumePdf(String resumePdf) {
			this.resumePdf = resumePdf;
		}

		public int getSemester() {
			return semester;
		}

		public void setSemester(int semester) {
			this.semester = semester;
		}

		public String getInternId() {
			return internId;
		}

		public void setInternId(String internId) {
			this.internId = internId;
		}


		public String getPermanentAddress() {
			return permanentAddress;
		}

		public void setPermanentAddress(String permanentAddress) {
			this.permanentAddress = permanentAddress;
		}

		public Date getDateOfBirth() {
			return dateOfBirth;
		}

		public void setDateOfBirth(Date dateOfBirth) {
			this.dateOfBirth = dateOfBirth;
		}

		public String getGender() {
			return gender;
		}

		public void setGender(String gender) {
			this.gender = gender;
		}

		public String getContactNo() {
			return contactNo;
		}

		public void setContactNo(String contactNo) {
			this.contactNo = contactNo;
		}

		public String getCollegeName() {
			return collegeName;
		}

		public void setCollegeName(String collegeName) {
			this.collegeName = collegeName;
		}

		public String getCollegeGuideHodName() {
			return collegeGuideHodName;
		}

		public void setCollegeGuideHodName(String collegeGuideHodName) {
			this.collegeGuideHodName = collegeGuideHodName;
		}

		public String getUniversityName() {
			return universityName;
		}

		public void setUniversityName(String universityName) {
			this.universityName = universityName;
		}

		public String getDegree() {
			return degree;
		}

		public void setDegree(String degree) {
			this.degree = degree;
		}

		public Double getAggregatePercentage() {
			return aggregatePercentage;
		}

		public void setAggregatePercentage(Double aggregatePercentage) {
			this.aggregatePercentage = aggregatePercentage;
		}

		public String getProjectDefinitionName() {
			return projectDefinitionName;
		}

		public void setProjectDefinitionName(String projectDefinitionName) {
			this.projectDefinitionName = projectDefinitionName;
		}

		public Guide getGuide() {
			return guide;
		}

		public void setGuide(Guide guide) {
			this.guide = guide;
		}

		public String getProgrammingLangName() {
			return programmingLangName;
		}

		public void setProgrammingLangName(String programmingLangName) {
			this.programmingLangName = programmingLangName;
		}

		public Date getJoiningDate() {
			return joiningDate;
		}

		public void setJoiningDate(Date joiningDate) {
			this.joiningDate = joiningDate;
		}

		public Date getCompletionDate() {
			return completionDate;
		}

		public void setCompletionDate(Date completionDate) {
			this.completionDate = completionDate;
		}

		public String getUsedResource() {
			return usedResource;
		}

		public void setUsedResource(String usedResource) {
			this.usedResource = usedResource;
		}

		public Date getCreatedAt() {
			return createdAt;
		}

		public void setCreatedAt(Date createdAt) {
			this.createdAt = createdAt;
		}
		
	
		public GroupEntity getGroup() {
			return group;
		}

		public void setGroup(GroupEntity group) {
			this.group = group;
		}

		public Date getUpdatedAt() {
			return updatedAt;
		}

		public void setUpdatedAt(Date updatedAt) {
			this.updatedAt = updatedAt;
		}
		 @PrePersist
		    protected void onCreate() {
		       createdAt = new Date();
		    }
		public Object getGroupEntity() {
			// TODO Auto-generated method stub
			return group;
		}
}