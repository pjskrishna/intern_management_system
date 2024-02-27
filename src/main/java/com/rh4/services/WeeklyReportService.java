package com.rh4.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.rh4.entities.GroupEntity;
import com.rh4.entities.WeeklyReport;
import com.rh4.repositories.WeeklyReportRepo;

@Service
public class WeeklyReportService {

	@Autowired
	private WeeklyReportRepo weeklyReportRepo;

	public int getRecentWeekNo(GroupEntity group) {
	    List<WeeklyReport> reports = weeklyReportRepo.getRecentWeekNo(group, PageRequest.of(0, 1));

	    if (!reports.isEmpty()) {
	        return reports.get(0).getWeekNo();
	    } else {
	        // Handle the case where no reports are found
	        return 1; // Or any other appropriate value
	    }
	}

	
	
}