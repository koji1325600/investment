package com.example.investment.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.investment.dto.UserDto;
import com.example.investment.repository.UserRepository;

@Controller
public class LoginController {
	@Autowired
    HttpServletRequest httpServletRequest;

	@Autowired
    UserRepository userRepository;
    
    /** ログイン画面遷移 */
	@GetMapping(path="login")
	String login(Model model) {
		return "/Login";
	}

	/** ログイン処理を行う */
    @PostMapping(path="investLogin")
    String todoLogin(@RequestParam String mailaddress, String password, Pbkdf2PasswordEncoder passwordEncoder, Model model){
        UserDto userDto = userRepository.findByMailaddressDto(mailaddress);
        if (userDto == null) {
            //ログイン画面に戻る
            return "redirect:/login";
        }
        //パスワードがDBと一致しなかった場合
        if (!passwordEncoder.matches(password,userDto.getPassword())) {
            //ログイン画面に戻る
            return "redirect:/login";
        }
        //SessionにユーザIDを設定
        httpServletRequest.getSession().setAttribute("userId", userDto.getUserId());
        return "redirect:invest/home";
    }

    /** ログアウト */
	@GetMapping(path = "logout")
	String logout(HttpServletRequest httpServletRequest){
		httpServletRequest.getSession().invalidate();
		return "/Login";
	}
}
