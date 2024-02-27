package com.rh4.entities;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "weekly_report")
public class WeeklyReport {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "weekly_report")
	private long weeklyReportId;
	
	@JoinColumn(name = "intern_id")
	@ManyToOne
	private Intern intern;
	
	@JoinColumn(name = "group_id")
	@ManyToOne
	private GroupEntity group;
	
	private Date reportSubmittedDate;
	
	private int weekNo;
	
	private String submittedPdf;
	
	@JoinColumn(name = "guide_id")
	@ManyToOne
	private Guide guide;
	
	private String status;

	public WeeklyReport() {
		super();
		// TODO Auto-generated constructor stub
	}

	public WeeklyReport(long weeklyReportId, Intern intern, GroupEntity group, Date reportSubmittedDate, int weekNo,
			String submittedPdf, Guide guide, String status) {
		super();
		this.weeklyReportId = weeklyReportId;
		this.intern = intern;
		this.group = group;
		this.reportSubmittedDate = reportSubmittedDate;
		this.weekNo = weekNo;
		this.submittedPdf = submittedPdf;
		this.guide = guide;
		this.status = status;
	}
	public long getWeeklyReportId() {
		return weeklyReportId;
	}

	public void setWeeklyReportId(long weeklyReportId) {
		this.weeklyReportId = weeklyReportId;
	}

	public Intern getIntern() {
		return intern;
	}

	public void setIntern(Intern intern) {
		this.intern = intern;
	}

	public GroupEntity getGroup() {
		return group;
	}

	public void setGroup(GroupEntity group) {
		this.group = group;
	}

	public Date getReportSubmittedDate() {
		return reportSubmittedDate;
	}

	public void setReportSubmittedDate(Date reportSubmittedDate) {
		this.reportSubmittedDate = reportSubmittedDate;
	}

	public int getWeekNo() {
		return weekNo;
	}

	public void setWeekNo(int weekNo) {
		this.weekNo = weekNo;
	}

	public String getSubmittedPdf() {
		return submittedPdf;
	}

	public void setSubmittedPdf(String submittedPdf) {
		this.submittedPdf = submittedPdf;
	}

	public Guide getGuide() {
		return guide;
	}

	public void setGuide(Guide guide) {
		this.guide = guide;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
  
	
}