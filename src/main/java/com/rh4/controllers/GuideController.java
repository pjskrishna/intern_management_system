package com.rh4.controllers;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.rh4.entities.GroupEntity;
import com.rh4.entities.Guide;
import com.rh4.entities.Intern;
import com.rh4.entities.InternApplication;
import com.rh4.models.ProjectDefinition;
import com.rh4.repositories.GroupRepo;
import com.rh4.repositories.GuideRepo;
import com.rh4.services.EmailSenderService;
import com.rh4.services.GroupService;
import com.rh4.services.GuideService;
import com.rh4.services.InternService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/bisag/guide")
public class GuideController {
	
	@Autowired
	HttpSession session;
	@Autowired
	private GuideService guideService;
	@Autowired
	private InternService internService;
	@Autowired
	private GroupService groupService;
	@Autowired
	private GroupRepo groupRepo;
	private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
	
	public Guide getSignedInGuide()
	{
		String username = (String) session.getAttribute("username");
		Guide guide = guideService.getGuideByUsername(username);
		return guide;
	}
	
	public String getUsername()
	{
		String username = (String) session.getAttribute("username");
		return username;
	}
	@GetMapping("/guide_dashboard")
	public ModelAndView guide_dashboard(HttpSession session, Model model) {

		ModelAndView mv = new ModelAndView("guide/guide_dashboard");

		Guide guide = getSignedInGuide();
		String username = getUsername();

		long gPendingCount = groupService.countGPendingGroups();
		mv.addObject("gPendingCount", gPendingCount);
		
		// Set the "id" and "username" attributes in the session
		session.setAttribute("id", guide.getGuideId());
		session.setAttribute("username", username);

		// Add the username to the ModelAndView
		mv.addObject("username", username);

		// Add intern details to the ModelAndView
		mv.addObject("guide", guide);
		
		return mv;
	}
	
	//Intern Groups
		@GetMapping("/intern_groups")
		public ModelAndView internGroups(HttpSession session, Model model) {
			
			ModelAndView mv = new ModelAndView("/guide/intern_groups");
			Guide guide = getSignedInGuide();
			List<GroupEntity> internGroups = guideService.getInternGroups(guide);
			List<Intern> interns = internService.getInterns();
			mv.addObject("internGroups", internGroups);
			mv.addObject("intern",interns);
			return mv;
		}
		@GetMapping("/intern_groups/{id}")
		public ModelAndView internGroups(@PathVariable("id") String id) {
			
			ModelAndView mv = new ModelAndView("/guide/intern_groups_detail");
			Guide guide = getSignedInGuide();
			List<GroupEntity> internGroups = guideService.getInternGroups(guide);
			mv.addObject("internGroups", internGroups);
			return mv;
			
		}
		@GetMapping("/intern/{id}")
		public ModelAndView internDetails(@PathVariable("id") String id)
		{
			ModelAndView mv = new ModelAndView();
			Optional<Intern> intern = internService.getIntern(id);
			mv.addObject("intern", intern);
			mv.setViewName("guide/intern_detail");
			return mv;
		}
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

				// Save the updated admin entity
				guideService.updateGuide(updatedGuide, existingGuide);

			}
			return "redirect:/logout";
		}
		@GetMapping("/guide_pending_def_approvals")
		public ModelAndView pendingFromGuide(HttpSession session, Model model)
		{
			ModelAndView mv = new ModelAndView("/guide/guide_pending_def_approvals");
			List<GroupEntity> groups = groupService.getGPendingGroups();
			mv.addObject("groups", groups);
			return mv;
		}
		@PostMapping("/guide_pending_def_approvals/ans")
		public String pendingFromGuide(@RequestParam("gpendingAns") String gpendingAns,@RequestParam("groupId") String groupId)
		{
			ModelAndView mv = new ModelAndView("/guide/guide_pending_def_approvals");
			GroupEntity group = groupService.getGroup(groupId);
			if(gpendingAns.equals("approve"))
			{
				group.setProjectDefinitionStatus("gapproved");
			}
			else
			{
				group.setProjectDefinitionStatus("pending");
			}
			groupRepo.save(group);
			return "redirect:/bisag/guide/guide_pending_def_approvals";
		}
		@GetMapping("/admin_pending_def_approvals")
		public ModelAndView pendingFromAdmin()
		{
			ModelAndView mv = new ModelAndView();
			List<GroupEntity> groups = groupService.getAPendingGroups();
			mv.addObject("groups", groups);
			return mv;
		}
		
		@GetMapping("weekly_report")
		public String weeklyReport(Model model)
		{
			return "guide/weekly_report";
		}
}