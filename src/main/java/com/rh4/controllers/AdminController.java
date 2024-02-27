package com.rh4.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;
import org.springframework.http.HttpHeaders;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.rh4.entities.*;
import com.rh4.repositories.AdminRepo;
import com.rh4.repositories.GroupRepo;
import com.rh4.repositories.InternRepo;
import com.rh4.services.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/bisag/admin")
public class AdminController {

	@Autowired
	private InternService internService;
	@Autowired
	private EmailSenderService emailService;
	@Autowired
	private GroupService groupService;

	@Autowired
	private AdminService adminService;
	@Autowired
	private FieldService fieldService;
	@Autowired
	private GroupRepo groupRepo;
	@Autowired
	private InternRepo internRepo;
	@Autowired
	private GuideService guideService;
	private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public static String encodePassword(String rawPassword) {
		return passwordEncoder.encode(rawPassword);
	}

	public static boolean matches(String rawPassword, String encodedPassword) {
		return passwordEncoder.matches(rawPassword, encodedPassword);
	}
	// generate internid//////////////////////////////////////

	public String generateInternId() {
		// Generate custom internId using current year and serial number
		SimpleDateFormat yearFormat = new SimpleDateFormat("yy");
		String currentYear = yearFormat.format(new Date());

		// Assuming you have a method to get the next serial number
		int serialNumber = generateSerialNumber();
		++serialNumber;
		// Combine the parts to form the custom internId
		String sno = String.valueOf(serialNumber);
		String formattedSerialNumber = String.format("%04d", Integer.parseInt(sno));
		System.out.println("serialNumber..." + serialNumber);
		System.out.println("formated..." + formattedSerialNumber);
		String internId = currentYear + "BISAG" + formattedSerialNumber;
		return internId;
	}

	public int generateSerialNumber() {

		String id = internService.getMostRecentInternId();
		if (id == null)
			return 0;
		String serialNumber = id.substring(id.length() - 4);
		int lastFourDigits = Integer.parseInt(serialNumber);
		return lastFourDigits;
	}

	// generate groupid////////////////////////////////////////////////////////////

	public String generateGroupId() {
		// Generate custom groupId using current year and serial number
		SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
		String currentYear = yearFormat.format(new Date());

		// Assuming you have a method to get the next serial number
		int serialNumber = generateSerialNumberForGroup();
		++serialNumber;
		// Combine the parts to form the custom internId
		String sno = String.valueOf(serialNumber);
		String formattedSerialNumber = String.format("%03d", Integer.parseInt(sno));
		System.out.println("serialNumber..." + serialNumber);
		System.out.println("formated..." + formattedSerialNumber);
		String groupId = currentYear + "G" + formattedSerialNumber;
		return groupId;
	}

	public int generateSerialNumberForGroup() {

		String id = groupService.getMostRecentGroupId();
		if (id == null)
			return 0;
		String serialNumber = id.substring(id.length() - 3);
		int lastThreeDigits = Integer.parseInt(serialNumber);
		return lastThreeDigits;
	}

	// Admin Dashboard

	@GetMapping("/admin_dashboard")
	public ModelAndView admin_dashboard(HttpSession session, Model model) {

		ModelAndView mv = new ModelAndView("admin/admin_dashboard");

		// Retrieve the username from the session
		String username = (String) session.getAttribute("username");

		// Use the adminService to get the Admin object based on the username
		Admin admin = adminService.getAdminByUsername(username);

		// Set the "id" and "username" attributes in the session
		session.setAttribute("id", admin.getAdminId());
		session.setAttribute("username", username);

		// count
		long pendingApplicationsCount = internService.countPendingApplications();
		model.addAttribute("pendingApplicationsCount", pendingApplicationsCount);

		long countInterviewApplications = internService.countInterviewApplications();
		model.addAttribute("countInterviewApplications", countInterviewApplications);

		long aPendingCount = groupService.countAPendingGroups();
		mv.addObject("aPendingCount", aPendingCount);

		long countGuide = guideService.countGuides();
		model.addAttribute("countGuide", countGuide);

		long countInterns = internService.countInterns();
		model.addAttribute("countInterns", countInterns);
		// Add the username to the ModelAndView
		mv.addObject("username", username);
		mv.addObject("admin", admin);

		return mv;
	}

	// Group Manage

	// Intern Management///////////////////////////////////////////////

	@GetMapping("/register_intern")
	public String registerIntern() {
		return "admin/InternRegistration";
	}

	@PostMapping("/register_intern")
	public String registerIntern(@ModelAttribute("intern") Intern intern) {
		intern.setInternId(generateInternId());
		internService.registerIntern(intern);
		return "redirect:/bisag";
	}

	@GetMapping("/intern/{id}")
	public ModelAndView internDetails(@PathVariable("id") String id) {
		ModelAndView mv = new ModelAndView();
		Optional<Intern> intern = internService.getIntern(id);
		mv.addObject("intern", intern);
		mv.setViewName("admin/intern_detail");
		return mv;
	}

	@GetMapping("/update_admin/{id}")
	public ModelAndView updateAdmin(@PathVariable("id") long id) {
		ModelAndView mv = new ModelAndView("super_admin/update_admin");
		Optional<Admin> admin = adminService.getAdmin(id);
		mv.addObject("admin", admin.orElse(new Admin()));
		return mv;
	}

	@PostMapping("/update_admin/{id}")
	public String updateAdmin(@ModelAttribute("admin") Admin admin, @PathVariable("id") long id) {
		Optional<Admin> existingAdmin = adminService.getAdmin(admin.getAdminId());

		if (existingAdmin.isPresent()) {

			String currentPassword = existingAdmin.get().getPassword();
			Admin updatedAdmin = existingAdmin.get();
			updatedAdmin.setName(admin.getName());
			updatedAdmin.setLocation(admin.getLocation());
			updatedAdmin.setContactNo(admin.getContactNo());
			updatedAdmin.setEmailId(admin.getEmailId());

			// Save the updated admin entity
			adminService.updateAdmin(updatedAdmin, existingAdmin);
		}
		return "redirect:/logout";
	}

	// Manage intern application///////////////////////////////////
	@GetMapping("/intern_application")
	public ModelAndView internApplication() {
		ModelAndView mv = new ModelAndView("admin/intern_application");
		List<InternApplication> interns = internService.getInternApplication();
		mv.addObject("interns", interns);
		return mv;
	}

	@GetMapping("/intern_application/{id}")
	public ModelAndView internApplication(@PathVariable("id") long id) {
		System.out.println("id" + id);
		ModelAndView mv = new ModelAndView();
		Optional<InternApplication> intern = internService.getInternApplication(id);
		mv.addObject("intern", intern);
		mv.setViewName("admin/intern_application_detail");
		return mv;
	}

	@PostMapping("/intern_application/ans")
	public String internApplicationSubmission(@RequestParam String message, @RequestParam long id,
			@RequestParam String status) {
		System.out.println("iddd" + id + status);
		// Long ID = Long.parseLong(id);
		Optional<InternApplication> intern = internService.getInternApplication(id);
		intern.get().setStatus(status);
		internService.addInternApplication(intern.get());
		if (status.equals("rejected")) {
			// emailService.sendSimpleEmail(intern.get().getEmail(),"You are rejected",
			// "BISAG INTERNSHIP RESULT");
			emailService.sendSimpleEmail(intern.get().getEmail(),
					"Notification: Rejection of BISAG Internship Application\r\n" + "\r\n" + "Dear "
							+ intern.get().getFirstName() + ",\r\n" + "\r\n"
							+ "We appreciate your interest in the BISAG internship program and the effort you put into your application. After careful consideration, we regret to inform you that your application has not been successful on this occasion.\r\n"
							+ "\r\n"
							+ "Please know that the decision was a difficult one, and we had many qualified candidates. We want to thank you for your interest in joining our team and for taking the time to apply for the internship position.\r\n"
							+ "\r\n"
							+ "We encourage you to continue pursuing your goals, and we wish you the best in your future endeavors. If you have any feedback or questions about the decision, you may reach out to [Contact Person/Department].\r\n"
							+ "\r\n"
							+ "Thank you again for considering BISAG for your internship opportunity. We appreciate your understanding.\r\n"
							+ "\r\n" + "Best regards,\r\n" + "\r\n" + "Your Colleague,\r\n"
							+ "Internship Coordinator\r\n" + "BISAG INTERNSHIP PROGRAM\r\n" + "1231231231",
					"BISAG INTERNSHIP RESULT");
		} else
			emailService.sendSimpleEmail(intern.get().getEmail(), message + "your unique id is " + intern.get().getId(),
					"BISAG INTERNSHIP RESULT");
		return "redirect:/bisag/admin/intern_application";
	}

	@GetMapping("/intern_application/approved_interns")
	public ModelAndView approvedInterns() {
		ModelAndView mv = new ModelAndView();
		List<InternApplication> intern = internService.getApprovedInterns();
		mv.addObject("interns", intern);
		mv.setViewName("admin/approved_interns");
		return mv;
	}

	@GetMapping("/intern_application/approved_intern/{id}")
	public ModelAndView approvedInterns(@PathVariable("id") long id) {
		System.out.println("approved id" + id);
		ModelAndView mv = new ModelAndView();
		Optional<InternApplication> intern = internService.getInternApplication(id);
		mv.addObject("intern", intern);
		mv.setViewName("admin/approved_intern_application_detail");
		return mv;
	}

	@PostMapping("/intern_application/approved_intern/ans")
	public String approvedInterns(@RequestParam String message, @RequestParam long id,
			@RequestParam String finalStatus) {
		System.out.println("iddd" + id + finalStatus);
		// Long ID = Long.parseLong(id);
		Optional<InternApplication> intern = internService.getInternApplication(id);
		intern.get().setFinal_status(finalStatus);
		internService.addInternApplication(intern.get());

		if (finalStatus.equals("failed")) {
			emailService.sendSimpleEmail(intern.get().getEmail(), "You are Failed", "BISAG INTERNSHIP RESULT");
		} else {
			String finalmessage = message + "\n" + "username: " + intern.get().getFirstName()
					+ intern.get().getLastName() + "\n Password: " + intern.get().getFirstName() + "_"
					+ intern.get().getId();
			emailService.sendSimpleEmail(intern.get().getEmail(), finalmessage, "BISAG INTERNSHIP RESULT");
		}
		return "redirect:/bisag/admin/intern_application/approved_interns";
	}

	@GetMapping("/intern_application/new_interns")
	public ModelAndView newInterns() {
		ModelAndView mv = new ModelAndView();
		List<Intern> intern = internService.getInterns();
		mv.addObject("intern", intern);
		mv.setViewName("admin/new_interns");
		return mv;
	}

	// Group Creation//////////////////////////////////////////

	@GetMapping("/create_group")
	public ModelAndView groupCreation() {
		ModelAndView mv = new ModelAndView();
		List<InternApplication> intern = internService.getInternApplication();
		mv.addObject("interns", intern);
		mv.setViewName("admin/create_group");
		return mv;
	}

	@PostMapping("/create_group_details")
	public String createGroup(@RequestParam("selectedInterns") List<Long> selectedInterns) {

		System.out.println("Selected Intern IDs: " + selectedInterns);
		// generate group id
		String id = generateGroupId();
		GroupEntity group = new GroupEntity();
		group.setGroupId(id);
		groupService.registerGroup(group);

		// register those intern
		for (Long internId : selectedInterns) {
			Optional<InternApplication> internApplicationOptional = internService.getInternApplication(internId);

			if (internApplicationOptional.isPresent()) {
				InternApplication internApplication = internApplicationOptional.get();
				internApplication.setGroupCreated(true);
				internService.addInternApplication(internApplication);

				// Create an Intern object using a constructor or a factory method
				Intern intern = new Intern(internApplication.getFirstName(), internApplication.getLastName(),
						internApplication.getContactNo(), internApplication.getEmail(),
						internApplication.getCollegeName(), internApplication.getJoiningDate(),
						internApplication.getCompletionDate(), internApplication.getBranch(),
						internApplication.getPassword(), internApplication.getIcardImage(),
						internApplication.getNocPdf(), internApplication.getResumePdf(),
						internApplication.getSemester(), internApplication.getProgrammingLangName(),
						internApplication.getCollegeName(), group);

				intern.setInternId(generateInternId());
				internService.addIntern(intern);
			}

		}
		return "redirect:/bisag/admin/create_group";
	}

	// Add dynamic fields(college, branch)

	@GetMapping("/add_fields")
	public ModelAndView addFields() {
		ModelAndView mv = new ModelAndView();
		List<College> colleges = fieldService.getColleges();
		List<Branch> branches = fieldService.getBranches();
		List<Domain> domains = fieldService.getDomains();
		mv.addObject("colleges", colleges);
		mv.addObject("branches", branches);
		mv.addObject("domains", domains);
		mv.setViewName("admin/add_fields");
		return mv;
	}

	@PostMapping("/add_college")
	public String addCollege(College college, Model model) {
		fieldService.addCollege(college);
		return "redirect:/bisag/admin/add_fields";
	}

	@PostMapping("/add_domain")
	public String addDomain(Domain domain, Model model) {
		fieldService.addDomain(domain);
		return "redirect:/bisag/admin/add_fields";
	}

	@PostMapping("/add_branch")
	public String addBranch(Branch branch, Model model) {
		fieldService.addBranch(branch);
		return "redirect:/bisag/admin/add_fields";
	}

	// Guide Allocation///////////////////////////
	@GetMapping("/allocate_guide")
	public ModelAndView allocateGuide() {
		ModelAndView mv = new ModelAndView("admin/allocate_guide");
		List<GroupEntity> group = groupService.getGuideNotAllocatedGroup();
		mv.addObject("groups", group);
		return mv;
	}

	@GetMapping("/delete_college/{id}")
	public String deleteCollege(@PathVariable("id") long id) {
		fieldService.deleteCollege(id);
		return "redirect:/bisag/admin/add_fields";
	}

	@GetMapping("/delete_branch/{id}")
	public String deleteBranch(@PathVariable("id") long id, Model model) {
		fieldService.deleteBranch(id);
		return "redirect:/bisag/admin/add_fields";
	}

	@GetMapping("/delete_domain/{id}")
	public String deleteDomain(@PathVariable("id") long id, Model model) {
		fieldService.deleteDomain(id);
		return "redirect:/bisag/admin/add_fields";
	}

	@PostMapping("/update_college/{id}")
	public String updateCollege(@ModelAttribute("college") College college, @PathVariable("id") long id) {
		Optional<College> existingCollege = fieldService.getCollege(college.getCollegeId());

		if (existingCollege.isPresent()) {
			// If the college exists, update its properties
			College updatedCollege = existingCollege.get();
			updatedCollege.setName(college.getName());
			updatedCollege.setLocation(college.getLocation());
			// Save the updated College entity
			fieldService.updateCollege(updatedCollege);
		}
		return "redirect:/bisag/admin/add_fields";
	}

	// ----------------------------------- Guide registration
	// ---------------------------------------//

	@GetMapping("/register_guide")
	public String registerGuide(Model model) {
		return "admin/guide_registration";
	}

	@PostMapping("/register_guide")
	public String registerGuide(@ModelAttribute("guide") Guide guide) {
		guideService.registerGuide(guide);
		emailService.sendSimpleEmail(guide.getEmailId(), "Notification: Appointment as Administrator\r\n" + "\r\n"
				+ "Dear " + guide.getName() + "\r\n" + "\r\n"
				+ "I trust this email finds you well. We are pleased to inform you that you have been appointed as an administrator within our organization, effective immediately. Your dedication and contributions to the team have not gone unnoticed, and we believe that your new role will bring value to our operations.\r\n"
				+ "\r\n"
				+ "As an administrator, you now hold a position of responsibility within the organization. We trust that you will approach your duties with diligence, professionalism, and a commitment to upholding the values of our organization.\r\n"
				+ "\r\n"
				+ "It is imperative to recognize the importance of your role and the impact it may have on the functioning of our team. We have confidence in your ability to handle the responsibilities that come with this position and to contribute positively to the continued success of our organization.\r\n"
				+ "\r\n"
				+ "We would like to emphasize the importance of maintaining the highest standards of integrity and ethics in your role. It is expected that you will use your administrative privileges responsibly and refrain from any misuse.\r\n"
				+ "\r\n"
				+ "Should you have any questions or require further clarification regarding your new responsibilities, please do not hesitate to reach out to [Contact Person/Department].\r\n"
				+ "\r\n"
				+ "Once again, congratulations on your appointment as an administrator. We look forward to your continued contributions and success in this elevated role.\r\n"
				+ "\r\n" + "Best regards,\r\n" + "\r\n" + "Your Colleague,\r\n" + "Administrator\r\n" + "1231231231",
				"BISAG ADMINISTRATIVE OFFICE");

		return "redirect:/bisag/admin/guide_list";
	}

	// --------------------------------------- Guide List
	// -------------------------------------------//

	@GetMapping("/guide_list")
	public ModelAndView guideList() {
		ModelAndView mv = new ModelAndView("admin/guide_list");
		List<Guide> guides = guideService.getGuide();
		mv.addObject("guides", guides);
		return mv;
	}

	@GetMapping("/guide_list/{id}")
	public ModelAndView guideList(@PathVariable("id") long id) {
		System.out.println("id" + id);
		ModelAndView mv = new ModelAndView();
		Optional<Guide> guide = guideService.getGuide(id);
		mv.addObject("guide", guide);
		mv.setViewName("admin/guide_list_detail");
		return mv;
	}

	// -------------------------------------- Guide Update
	// ------------------------------------------//

	@GetMapping("/update_guide/{id}")
	public ModelAndView updateGuide(@PathVariable("id") long id) {
		ModelAndView mv = new ModelAndView("admin/update_guide");
		Optional<Guide> guide = guideService.getGuide(id);
		mv.addObject("guide", guide.orElse(new Guide()));
		return mv;
	}

	@PostMapping("/update_guide/{id}")
	public String updateGuide(@ModelAttribute("guide") Guide guide, @PathVariable("id") long id) {
		Optional<Guide> existingGuide = guideService.getGuide(guide.getGuideId());

		if (existingGuide.isPresent()) {

			String currentPassword = existingGuide.get().getPassword();
			Guide updatedGuide = existingGuide.get();
			updatedGuide.setName(guide.getName());
			updatedGuide.setLocation(guide.getLocation());
			updatedGuide.setFloor(guide.getFloor());
			updatedGuide.setLabNo(guide.getLabNo());
			updatedGuide.setContactNo(guide.getContactNo());
			updatedGuide.setEmailId(guide.getEmailId());
			if (!currentPassword.equals(encodePassword(guide.getPassword())) && guide.getPassword() != "") {
				updatedGuide.setPassword(encodePassword(guide.getPassword()));
			}
			// Save the updated admin entity
			guideService.updateGuide(updatedGuide, existingGuide);

		}
		return "redirect:/bisag/admin/guide_list";
	}

	// Delete Guide
	@PostMapping("/guide_list/delete/{id}")
	public String deleteAdmin(@PathVariable("id") long id) {
		guideService.deleteGuide(id);
		return "redirect:/bisag/admin/guide_list";
	}

	// Manage group
	@GetMapping("/manage_group")
	public ModelAndView manageGroup() {
		ModelAndView mv = new ModelAndView();
		List<GroupEntity> allocatedGroups = groupService.getAllocatedGroups();
		List<GroupEntity> notAllocatedGroups = groupService.getNotAllocatedGroups();
		List<Intern> interns = internService.getInterns();
		List<Guide> guides = guideService.getGuide();
		mv.setViewName("/admin/manage_group");
		mv.addObject("alloactedGroups", allocatedGroups);
		mv.addObject("notAllocatedGroups", notAllocatedGroups);
		mv.addObject("guides", guides);
		mv.addObject("interns", interns);
		return mv;
	}

	// Manage group details
	@GetMapping("/manage_group/{id}")
	public ModelAndView manageGroup(@PathVariable("id") String id) {
		ModelAndView mv = new ModelAndView();
		GroupEntity group = groupService.getGroup(id);
		List<Intern> interns = internService.getInterns();
		List<Guide> guides = guideService.getGuide();
		mv.setViewName("/admin/manage_group_detail");
		mv.addObject("groups", group);
		mv.addObject("guides", guides);
		mv.addObject("interns", interns);
		return mv;
	}

	@PostMapping("/manage_group/assign_guide")
	public String assignGuide(@RequestParam("guideid") long guideid, @RequestParam("groupId") String groupId) {
		System.out.println("guide id: " + guideid);
		groupService.assignGuide(groupId, guideid);
		return "redirect:/bisag/admin/manage_group";
	}

	// Project Definition Approvals
	@GetMapping("/admin_pending_def_approvals")
	public ModelAndView pendingFromGuide() {
		ModelAndView mv = new ModelAndView("/admin/admin_pending_def_approvals");
		List<GroupEntity> groups = groupService.getAPendingGroups();
		mv.addObject("groups", groups);
		return mv;
	}

	@PostMapping("/admin_pending_def_approvals/ans")
	public String pendingFromAdmin(@RequestParam("apendingAns") String apendingAns,
			@RequestParam("groupId") String groupId) {
		ModelAndView mv = new ModelAndView();
		GroupEntity group = groupService.getGroup(groupId);
		if (apendingAns.equals("approve")) {
			group.setProjectDefinitionStatus("approved");
			List<Intern> interns = internService.getInternsByGroupId(group.getId());
			for (Intern intern : interns) {
				intern.setProjectDefinitionName(group.getProjectDefinition());
				internRepo.save(intern);
			}
		} else {
			group.setProjectDefinitionStatus("pending");
		}
		groupRepo.save(group);
		return "redirect:/bisag/admin/admin_pending_def_approvals";
	}

}
