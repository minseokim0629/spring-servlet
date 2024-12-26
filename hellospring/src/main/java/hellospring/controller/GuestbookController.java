package hellospring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/*
 * @RequestMapping 클래스 단독 매핑
 * 
 * Spring MVC 4.x 까지 지원
 */
@RequestMapping("/guestbook/*")
@Controller

public class GuestbookController {
	
	@ResponseBody
	public String list() {
		return "GustcookController:list()";
	}
}
