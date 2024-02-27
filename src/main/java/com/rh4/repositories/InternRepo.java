package com.rh4.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.rh4.entities.*;
@Repository
public interface InternRepo extends JpaRepository<Intern, String> {

	Intern findTopByOrderByInternIdDesc();
	
	Intern findByEmail(String username);

	List<Intern> findByGroupId(long groupId);
}