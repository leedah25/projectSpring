package com.project.spring.login;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.spring.vo.EmailDto;
import com.project.spring.vo.MemberVo;
import com.project.spring.vo.ProductVo;
import com.project.spring.vo.ReviewVo;

@Controller
@RequestMapping("/member/*")
public class MemberController {
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	private JavaMailSenderImpl mailSender;
	
	//濡쒓렇�씤 �솕硫� �쓣�슦湲�
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String loginForm() {
		
		return "member/login";
	}
	
	//濡쒓렇�씤 �떆
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String loginRun(String member_id, String password,HttpSession session
							,RedirectAttributes rttr,String saveId,HttpServletResponse response) {
		
		MemberVo memberVo=memberService.loginRun(member_id, password);
		String page="";
		
		
		if(memberVo ==null){
			//濡쒓렇�씤 �떎�뙣�떆
			rttr.addFlashAttribute("isLogin", "fail");
			page="redirect:/member/login";
		}else {
			//濡쒓렇�씤 �꽦怨듭떆
			//濡쒓렇�씤 �꽭�뀡�뿉 �꽔�뼱�몺  
			session.setAttribute("loginmember", memberVo);
			
			
			//荑좏궎�꽔湲�
			Cookie cookie=new Cookie("member_id",member_id);
			if(saveId!=null) {
				cookie.setMaxAge(60*60*24*7);
			}else{
				cookie.setMaxAge(0);
			}
			response.addCookie(cookie);
			page="index/main";
		}
		return page; 
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpSession session) {
		session.invalidate(); // 현재 세션 무효화
		return "redirect:/member/login";
	}
	
	//�쉶�썝媛��엯 �솕硫� �쓣�슦湲�
	@RequestMapping(value = "/registerForm", method = RequestMethod.GET)
	public String showRegister() {
		
		return "member/register";
	}
	
	
	
	//�벑濡� �떎�뻾
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public String registerRun(MemberVo memberVo, MultipartFile file, RedirectAttributes rttr) {
		
		String page="";
		String originalFilename=file.getOriginalFilename();
		
		//�씠誘몄� �뙆�씪�뾽濡쒕뱶
		
//		"//192.168.0.233/userpics/"
		try {
		 String member_pic=MyFileUploader.uploadfile("C:/userpics/", originalFilename, file.getBytes());
		 System.out.println("member_pic:"+member_pic);
		 memberVo.setMember_pic(member_pic);
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//�벑濡앹떎�뻾
		boolean result=memberService.registerRun(memberVo);
		System.out.println("controller memberVo:"+memberVo);
		if(result) {
			rttr.addFlashAttribute("register_result","true");
			page="redirect:/member/login";

		}else {
			rttr.addFlashAttribute("register_result","false");
			
			page="redirect:/member/registerForm";
		}
			return page;
	}
	
	//鍮꾨�踰덊샇 李얘린 �쓣�슦湲�
	@RequestMapping(value = "/forgot-passwordForm", method = RequestMethod.GET)
	public String showforgotpassword() {
		
		return "member/forgot-password";
	}
	
	//鍮꾨�踰덊샇 李얘린 �떎�뻾
	@RequestMapping(value = "/forgot-password", method = RequestMethod.POST)
	public String snedPassword(EmailDto emailDto, RedirectAttributes rttr) {
		
		//李얘린�븯�뒗�뜲 �젣��濡� �꽔吏� �븡�� 寃쎌슦
		if(emailDto.getMember_id()==null ||
		   emailDto.getMember_id().equals("") ||
		   emailDto.getTo()==null ||
		   emailDto.getTo().equals("")){
			rttr.addFlashAttribute("isEmpty", "true");
			return "redirect:/member/forgot-passwordForm";
		}
		
		//�븘�씠�뵒�븯怨� �씠硫붿씪�씠 �젣��濡� �엳�뒗吏�
		if(!memberService.isExist(emailDto.getMember_id(),emailDto.getTo())) {
			rttr.addFlashAttribute("isExist","false");
			return "redirect:/member/forgot-passwordForm";
			
		}
		
		//�엫�떆鍮꾨�踰덊샇 �깮�꽦 諛� 諛쒖넚
		String uuid=UUID.randomUUID().toString();
		String uuidsub = uuid.substring(0, uuid.indexOf("-"));
		
		if(memberService.updatePassword(emailDto.getTo(),uuidsub)) {
			
			MimeMessagePreparator preparator=new MimeMessagePreparator() {
				
				@Override
				public void prepare(MimeMessage mimeMessage) throws Exception {
					MimeMessageHelper helper=new MimeMessageHelper(
					mimeMessage,
					false,
					"utf-8");
					helper.setFrom(emailDto.getFrom());
					helper.setTo(emailDto.getTo());
					helper.setSubject("�엫�떆鍮꾨�踰덊샇 諛쒖넚�븞�궡");
					helper.setText("�깉濡쒖슫 �엫�떆 鍮꾨�踰덊샇�뒗 "+uuidsub+"�엯�땲�떎");
				}
			};
			mailSender.send(preparator);
		}
		return "redirect:/member/forgot-passwordForm";
		
	}
	
	//�븘�씠�뵒 泥댄겕
	@RequestMapping(value = "/idcheck", method = RequestMethod.POST)
	@ResponseBody
	public boolean idCheck(String member_id) {
		boolean result= memberService.idCheck(member_id);
		
		return result;
	}
	

	
}
