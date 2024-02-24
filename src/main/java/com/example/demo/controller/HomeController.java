package com.example.demo.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.dao.UserRepository;
import com.example.demo.entity.User;

@Controller
public class HomeController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder; 
	
	@Autowired
	private UserRepository userRepository;
	@RequestMapping("/")
	public String home(Model m) {
		m.addAttribute("title","this is a title page");
		return "home";
	}
	@RequestMapping("/about")
	public String about(Model m) {
	m.addAttribute("title","this is about page");
		return "about";
	}
	@RequestMapping("/signup")
	public String register(Model m) {
	m.addAttribute("title","this is register page");
	m.addAttribute("user",new User());
		return "signup";
	}
	@RequestMapping(value = "/do_register",method=RequestMethod.POST)
	public String registerUser( @ModelAttribute("user")User user,@RequestParam(value="agreement",defaultValue="false")boolean agreement,Model model,HttpSession session) {
		try {
			if(!agreement) {
				throw new Exception("You have not agreed the teram and condition");
			}
			
			user.setRole("ROLE_USER");
			user.setEnable(true);
			user.setImageUrl("url");
			user.setPassword(passwordEncoder.encode(user.getPassword()));

			
			User result=this.userRepository.save(user);
			model.addAttribute("user",new User());

	session.setAttribute("message",new com.example.demo.message.Message("Registration sucessfully!!", "alert message"));
			System.out.println(result);
			return "signup";
		}
		catch(Exception e){
			e.printStackTrace();
			model.addAttribute("user",user);
		
			session.setAttribute("message", new com.example.demo.message.Message("something whent wrong!!"+e.getMessage(),"alert message"));
			return "signup";
		}
		
	}
	@RequestMapping("/signin")
	public String customLogin() {
		return "login";
	}
	}

