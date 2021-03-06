package database;

import java.time.LocalDateTime;

public class TimeReport {
	private int timeReportId;
	private int projectUserId;
	private int signedById;
	private LocalDateTime signedAt;
	private int year;
	private int week;
	private LocalDateTime updatedAt;
	private boolean finished;
	
	public TimeReport(int projectUserId, int year, int week) {
		super();
		this.projectUserId = projectUserId;
		this.year = year;
		this.week = week;
	}

	public TimeReport(int timeReportId, int projectUserId, int signedById, LocalDateTime signedAt, int year, int week,
			LocalDateTime updatedAt, boolean finished) {
		super();
		this.timeReportId = timeReportId;
		this.projectUserId = projectUserId;
		this.signedById = signedById;
		this.signedAt = signedAt;
		this.year = year;
		this.week = week;
		this.updatedAt = updatedAt;
		this.finished = finished;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}
	
	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	public boolean isFinished() {
		return finished;
	}
	
	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	
	public int getTimeReportId() {
		return timeReportId;
	}
	
	public int getProjectUserId() {
		return projectUserId;
	}
	
	public int getSignedById() {
		return signedById;
	}
	
	public boolean isSigned() {
		return signedById != 0;
	}
	
	public void sign(int userId) {
		signedById = userId;
		signedAt = LocalDateTime.now();
	}
	 
	public void unsign() {
		signedById = 0;
		signedAt = null;
	}
	
	public LocalDateTime getSignedAt() {
		return signedAt;
	}
	
	public int getYear() {
		return year;
	}
	
	public int getWeek() {
		return week;
	}
}
