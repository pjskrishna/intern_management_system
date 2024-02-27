package com.rh4.services;
import com.rh4.entities.Domain;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.rh4.repositories.DomainRepo;
import com.rh4.entities.Branch;
import com.rh4.entities.College;
import com.rh4.repositories.BranchRepo;
import com.rh4.repositories.CollegeRepo;

@Service
public class FieldService {
	@Autowired
	private CollegeRepo collegeRepo;
	@Autowired
	private DomainRepo domainRepo;
	@Autowired
	private BranchRepo branchRepo;
	
	public void addCollege(College college) {
		collegeRepo.save(college);
	}
	public void addDomain(Domain domain) {
		domainRepo.save(domain);
	}
	public void addBranch(Branch branch) {
		branchRepo.save(branch);
	}
	public List<College> getColleges()
	{
		return collegeRepo.findAll();
	}
	public List<Branch> getBranches()
	{
		return branchRepo.findAll();
	}
	public List<Domain> getDomains()
	{
		return domainRepo.findAll();
	}
	public void deleteBranch(long id)
	{
		branchRepo.deleteById(id);
	}
	public void deleteDomain(long id)
	{
		domainRepo.deleteById(id);
	}
	public void deleteCollege(long id)
	{
		collegeRepo.deleteById(id);
	}
	public Optional<College> getCollege(long id)
	{
		return collegeRepo.findById(id);
	}
	public void updateCollege(College college)
	{
		collegeRepo.save(college);
	}
}