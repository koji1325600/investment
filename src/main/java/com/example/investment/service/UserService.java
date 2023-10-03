package com.example.investment.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.investment.dto.UserDto;
import com.example.investment.form.UserFrom;
import com.example.investment.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    /** ユーザ作成 */
    public Boolean create(UserFrom userFrom){
        if (userRepository.findByMailaddressInt(userFrom.getMailaddress()) != null) {
            return false;
        }
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userFrom, userDto);
        userDto.setPassword(new Pbkdf2PasswordEncoder().encode(userFrom.getPassword()));
        userDto.addTodoDto();
        userRepository.save(userDto);
        return true;
    }

    /** ユーザ削除処理 */
    public void removeUser(String id){
        userRepository.deleteById(id);
    }

    /** ユーザ更新処理 */
    public void updateUser(String id, UserDto userDto){
        //removeUser(id);
        userRepository.save(userDto);
    }
    
}
