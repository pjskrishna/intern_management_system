package com.rh4.repositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.rh4.entities.Cancelled;


@Repository
public interface CancelledRepo extends CrudRepository<Cancelled,Long>{
	
	
}

