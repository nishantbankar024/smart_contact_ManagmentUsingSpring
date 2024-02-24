package com.example.demo.dao;



import java.awt.print.Pageable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entity.Contact;



public interface ContactRepositorye extends JpaRepository<Contact, Integer> {
	
	@Query("from Contact as c where c.user.id=:userId")
	public Page<Contact> findContactsByUser(@Param("userId")int userId,org.springframework.data.domain.Pageable pageable);
}
