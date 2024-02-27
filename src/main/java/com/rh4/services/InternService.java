package com.rh4.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rh4.entities.*;
import com.rh4.repositories.*;
import java.util.*;
@Service
public class InternService {

	@Autowired
	private InternRepo internRepo;
	@Autowired
	private InternApplicationRepo internApplicationRepo;
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private CancelledRepo cancelledRepo;
	@Autowired
	private Cancelled cancelled;

    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	public void registerIntern(Intern intern)
	{
		internRepo.save(intern);
	}
	
	public String getMostRecentInternId()
	{
		Intern mostRecentIntern = internRepo.findTopByOrderByInternIdDesc();
        return mostRecentIntern != null ? mostRecentIntern.getInternId() : null;
	}
	public List<InternApplication> getInternApplication()
	{
		return internApplicationRepo.findAll();
	}
	public Optional<InternApplication> getInternApplication(long id)
	{
		return internApplicationRepo.findById(id);
	}
	public void addInternApplication(InternApplication intern)
	{
		internApplicationRepo.save(intern);
	}
	public List<InternApplication> getApprovedInterns()
	{
		return internApplicationRepo.getInternApprovedStatus();
	}
	public List<Intern> getInterns()
	{
		return internRepo.findAll();
	}
	public void addIntern(Intern intern)
	{
		String encryptedPassword = passwordEncoder().encode(intern.getPassword());
		intern.setPassword(encryptedPassword);
		internRepo.save(intern);		
		//save to user table
		String email = intern.getEmail();
		String role = "UNDERPROCESSINTERN";
		userRepo.deleteByUsername(email, role);
		MyUser user = new MyUser();
		user.setUsername(intern.getEmail());
		//encrypt password
		user.setPassword(encryptedPassword);
		user.setRole("INTERN");
		user.setEnabled(true);
		//from long to string
		String userId = intern.getInternId();
		user.setUserId(userId);
		userRepo.save(user);
	}

	public long countPendingApplications() {
        return internApplicationRepo.countByStatus("pending");
    }
	public long countInterviewApplications() {
		return internApplicationRepo.countRemainingInterview();
        //return internApplicationRepo.countByFinalStatus("pending");
    }
	public long countInterns() {
        return internRepo.count();
    }

	public Optional<Intern> getIntern(String id) {
		return internRepo.findById(id);
	}

	public Intern getInternByUsername(String username) {
		return internRepo.findByEmail(username);
	}

	public InternApplication getInternApplicationByUsername(String username) {
		return internApplicationRepo.findByEmail(username);
	}

	public void cancelInternApplication(Optional<InternApplication> intern) {
		
		intern.get().setIsActive(false);
		cancelled.setCancelId(Long.toString(intern.get().getId()));
		cancelled.setTableName("internApplication");
		cancelledRepo.save(cancelled);
		
	}
	public List<Intern> getInternsByGroupId(long groupId) {
		return internRepo.findByGroupId(groupId);
	}
	
}
