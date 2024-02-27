package com.rh4.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.rh4.entities.Admin;
import com.rh4.entities.GroupEntity;
import com.rh4.entities.Intern;
import com.rh4.entities.InternApplication;
import com.rh4.entities.WeeklyReport;
import com.rh4.models.ProjectDefinition;
import com.rh4.repositories.GroupRepo;
import com.rh4.services.InternService;
import com.rh4.services.WeeklyReportService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/bisag/intern")
public class InternController {

	@org.springframework.beans.factory.annotation.Value("${projectDefinitionDocument.filepath}")
	private String projectDefinitionDocument;
	@org.springframework.beans.factory.annotation.Value("${weeklyReportSubmission.filepath}")
	private String weeklyReportSubmission;
	@Autowired
	private InternService internService;
	@Autowired
	private WeeklyReportService weeklyReportService;
	@Autowired
	HttpSession session;
	@Autowired
	GroupRepo groupRepo;

	public Intern getSignedInIntern() {
		String username = (String) session.getAttribute("username");
		Intern intern = internService.getInternByUsername(username);
		return intern;
	}

	public String getUsername() {
		String username = (String) session.getAttribute("username");
		return username;
	}

	public Date getNextSubmissionDate() {
        Intern intern = getSignedInIntern();
        GroupEntity group = intern.getGroup();
        List<Intern> interns = internService.getInternsByGroupId(group.getId());
        Date oldestJoiningDate = null;

        for (Intern i : interns) {
            Date joiningDate = i.getJoiningDate();

            // Check if the oldestJoiningDate is null or if the current intern's joining date is older
            if (oldestJoiningDate == null || joiningDate.before(oldestJoiningDate)) {
                oldestJoiningDate = joiningDate;
            }
        }

        System.out.println("Oldest joining date from each intern: " + oldestJoiningDate);

        Integer recentWeekNo = (Integer) weeklyReportService.getRecentWeekNo(group);
        System.out.println(recentWeekNo);

        // Calculate next submission date based on recentWeekNo and oldestJoiningDate
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(oldestJoiningDate);
        calendar.add(Calendar.DAY_OF_MONTH, (recentWeekNo) * 7 ); // Add 6 weeks for each week number

        return calendar.getTime();
	}

	@GetMapping("/intern_dashboard")
	public ModelAndView intern_dashboard(HttpSession session, Model model) {

		ModelAndView mv = new ModelAndView("intern/intern_dashboard");
		Intern intern = getSignedInIntern();
		String username = getUsername();
		InternApplication internApplication = internService.getInternApplicationByUsername(username);
		// Add group details to the ModelAndView
		if (intern.getGroupEntity() != null) {
			mv.addObject("group", intern.getGroupEntity());
		} else {
			mv.addObject("group", null); // Handle the case when no group is assigned
		}

		// Set the "id" and "username" attributes in the session
		session.setAttribute("id", intern.getInternId());
		session.setAttribute("username", username);

		// Add the username to the ModelAndView
		mv.addObject("username", username);

		// Add intern details to the ModelAndView
		mv.addObject("intern", intern);
		mv.addObject("internApplication", internApplication);

		return mv;
	}

	// project def approval
	@GetMapping("/project_definition")
	public ModelAndView project_definition(HttpSession session, Model model) {

		ModelAndView mv = new ModelAndView("/intern/project_definition");
		String username = getUsername();
		Intern intern = internService.getInternByUsername(username);
		if (intern.getGroupEntity() != null) {
			mv.addObject("group", intern.getGroupEntity());
		} else {
			mv.addObject("group", null); // Handle the case when no group is assigned
		}
		return mv;

	}

	@PostMapping("/project_definition_submition")
	public String approveProjectDefinition(MultipartHttpServletRequest req,
			@ModelAttribute("ProjectDefinition") ProjectDefinition projectDefinition, BindingResult bindingresult)
			throws IllegalStateException, Exception {
		Intern intern = getSignedInIntern();
		GroupEntity group = intern.getGroup();
		group.setProjectDefinition(projectDefinition.getProjectDefinition());
		group.setDescription(projectDefinition.getDescription());
		group.setProjectDefinitionDocument(projectDefinition.getProjectDefinitionDocument());
		group.setProjectDefinitionDocument(uploadfile(req.getFile("projectDefinitionDocument"), "projectDefinitionDocument"));
		group.setProjectDefinitionStatus("gpending");
		groupRepo.save(group);
		return "redirect:/bisag/intern/project_definition";
	}

	@GetMapping("/weekly_report_submission")
	public ModelAndView weeklyReportSubmission() {
		ModelAndView mv = new ModelAndView("intern/weekly_report_submission");
		Date nextSubmissionDate = getNextSubmissionDate();
		Intern intern = getSignedInIntern();
        GroupEntity group = intern.getGroup();
		Integer nextSubmissionWeekNo = (Integer) weeklyReportService.getRecentWeekNo(group);
		List<WeeklyReport> weeklyReports = weeklyReportService.getReports(group.getId());
		mv.addObject("nextSubmissionDate", nextSubmissionDate);
		mv.addObject("nextSubmissionWeekNo", nextSubmissionWeekNo);
		mv.addObject("weeklyReports", weeklyReports);
		mv.addObject("intern", intern);
		mv.addObject("group", group);
		return mv;
	}
	
	@PostMapping("/weekly_report_submission")
	public String weeklyReportSubmission(@RequestParam("currentWeekNo") int currentWeekNo, MultipartHttpServletRequest req) throws IllegalStateException, IOException, Exception
	{
		Intern intern = getSignedInIntern();
        GroupEntity group = intern.getGroup();
        Date currentDate = new Date();
		WeeklyReport weeklyReport = new WeeklyReport();
		weeklyReport.setGroup(group);
		weeklyReport.setGuide(group.getGuide());
		weeklyReport.setIntern(intern);
		weeklyReport.setReportSubmittedDate(currentDate);
		weeklyReport.setSubmittedPdf(uploadfile(req.getFile("weeklyReportSubmission"), "weeklyReportSubmission"));
		weeklyReport.setWeekNo(currentWeekNo);
		weeklyReport.setDeadline(getNextSubmissionDate());
		// Check if the deadline is greater than or equal to the reportSubmittedDate
	    if (weeklyReport.getDeadline().compareTo(currentDate) >= 0) {
	        // If the deadline is greater than or equal to the reportSubmittedDate, set the status to "submitted"
	        weeklyReport.setStatus("submitted");
	    } else {
	        // If the deadline is less than the reportSubmittedDate, set the status to "late submitted"
	        weeklyReport.setStatus("late submitted");
	    }
		weeklyReportService.addReport(weeklyReport);
		return "redirect:/bisag/intern/weekly_report_submission";
	}
	public String uploadfile(MultipartFile file, String object) throws Exception, IllegalStateException, IOException {
		try {
			if (object == "projectDefinitionDocument") {
				File myDir = new File(projectDefinitionDocument);
				if (!myDir.exists())
					myDir.mkdirs();
				long timeadd = System.currentTimeMillis();

				if (!file.isEmpty()) {
					file.transferTo(Paths.get(myDir.getAbsolutePath(), timeadd + "_" + file.getOriginalFilename()));
					return timeadd + "_" + file.getOriginalFilename();
				} else {
					return null;
				}
			}
			else if (object == "weeklyReportSubmission") {
				Intern intern = getSignedInIntern();
		        GroupEntity group = intern.getGroup();
				File myDir = new File(weeklyReportSubmission + "/"+ group.getGroupId());
				if (!myDir.exists())
					myDir.mkdirs();
				long timeadd = System.currentTimeMillis();

				if (!file.isEmpty()) {
					file.transferTo(Paths.get(myDir.getAbsolutePath(), timeadd + "_" + file.getOriginalFilename()));
					return timeadd + "_" + file.getOriginalFilename();
				} else {
					return null;
				}
			}
			else {
				System.out.println("nothing is true");
				return "redirect:/";
			}

		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/";
		}

	} 

}
