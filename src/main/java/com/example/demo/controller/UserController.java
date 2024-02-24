package com.example.demo.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.Path;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dao.ContactRepositorye;
import com.example.demo.dao.UserRepository;
import com.example.demo.entity.Contact;
import com.example.demo.entity.User;
import com.example.demo.message.Message;

@Controller
@RequestMapping("/user")

public class UserController {
	@Autowired
private UserRepository userRepository;
	
	@Autowired
private ContactRepositorye contactRepository;	
	
	@ModelAttribute
	public void addCommonData(Model model,Principal principal) {
		String userName=principal.getName();
		com.example.demo.entity.User user=userRepository.getUserByUserName(userName);
		model.addAttribute("user", user);
	}
	
	
	@RequestMapping("/index")
	public String dashboard(Model model,Principal principal) {
		String userName=principal.getName();
		com.example.demo.entity.User user=userRepository.getUserByUserName(userName);
		model.addAttribute("user", user);
		return "normal/user_dashboard";
	}
	
	//Show Add contact Page
	
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		
	model.addAttribute("title","addcontact");
	model.addAttribute("contact",new Contact());
		return "normal/contact";
	}
	
	//Add Contact Section
	
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact, 
			@RequestParam("image") MultipartFile file,
			Principal principal,HttpSession session)
	{
		try {
		String name=principal.getName();
		com.example.demo.entity.User user=this.userRepository.getUserByUserName(name);
		contact.setUser(user);
		
		if(file.isEmpty()) {
			contact.setimageFile("img/user.png");
		}
		else {
			contact.setimageFile(file.getOriginalFilename());
			File saveFile=new ClassPathResource("static/img").getFile();
			java.nio.file.Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());	
			Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			
			}
		
		user.getContact().add(contact);
		this.userRepository.save(user);
		System.out.println("Data"+contact);
		System.out.println("data is added");
		session.setAttribute("message",new Message("Your cxontact is added || Add more..","sucess"));
		
		}
		catch(Exception e) {
			e.printStackTrace();
			session.setAttribute("message",new Message("Something went Wrong || Tray Again...","danger"));
		}
		return "normal/contact";
	}	
	
	//Contact Show logic 
	
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model m,Principal principal) {
		m.addAttribute("title","show user contacts");
		//contact fetching
		String userName=principal.getName();
		User user=this.userRepository.getUserByUserName(userName);
		Pageable pageable=PageRequest.of(page, 3);
		Page<Contact> contact=this.contactRepository.findContactsByUser(user.getId(),pageable);
		m.addAttribute("contacts",contact);
		m.addAttribute("currentPage",page);
	 m.addAttribute("totalPages",contact.getTotalPages());
		return "normal/show_contacts";
	}
	
	@RequestMapping("/{cId}/contact")
	public String showContactDetails(@PathVariable("cId") Integer cId,Model model,Principal  principal) {
		System.out.println("cid"+cId);
		
		Optional<Contact> contactOptional=this.contactRepository.findById(cId);
		Contact contact=contactOptional.get();
		
		String userName=principal.getName();
		User user=this.userRepository.getUserByUserName(userName);
		if(user.getId()==contact.getUser().getId())
		{
			model.addAttribute("contact",contact);
		}
		
		
		return "normal/contact_detail";
	}
	@GetMapping("/delete/{cid}")
@org.springframework.transaction.annotation.Transactional
	public String deleteContact(@PathVariable("cid") Integer cId,Model model,HttpSession session,Principal principle) {
		
		Contact contact=this.contactRepository.findById(cId).get();
		
		User user=this.userRepository.getUserByUserName(principle.getName());
		user.getContact().remove(contact);
		this.userRepository.save(user);
	session.setAttribute("message",new Message("contact deleted","success"));
	
		return "redirect:/user/show-contacts/0";
	}
	
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cId,Model model) {
		model.addAttribute("title","update contact");
		Contact contact=this.contactRepository.findById(cId).get();
		model.addAttribute("contact",contact);
		
		
		return "normal/update_form";
	}
	@RequestMapping(value="/process-update",method=RequestMethod.POST)
	public String updateHandeler(@ModelAttribute Contact contact,@RequestParam("image")
	MultipartFile file,Model m,HttpSession session,Principal principle) {
		
		try {
			//old contact deleted
			Contact oldcontact=this.contactRepository.findById(contact.getcId()).get();
			
			if(!file.isEmpty()) {
				//deleted old photo
				
				File deleteFile=new ClassPathResource("static/img").getFile();
				File file1=new File(deleteFile,oldcontact.getImageFile());
				file1.delete();
				
				//update file
				File saveFile=new ClassPathResource("static/img").getFile();
				java.nio.file.Path path=Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
				contact.setimageFile(file.getOriginalFilename());
			}
			else {
				contact.setImageFile(oldcontact.getImageFile());
			}
			User user=this.userRepository.getUserByUserName(principle.getName());
			contact.setUser(user);
	this.contactRepository.save(contact);
	session.setAttribute("message",new Message("your contact isa updated","success"));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return "redirect:/user/"+contact.getcId()+"/contact";
	}
	@GetMapping("/profile")
	public String yourProfile(Model model) {
		model.addAttribute("tittle","profile page");
		return "normal/profile";
	}

}

