package com.example.investment.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.investment.dao.UserDao;
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
        UserDao userDao = new UserDao();
        BeanUtils.copyProperties(userFrom, userDao);
        userDao.setPassword(new Pbkdf2PasswordEncoder().encode(userFrom.getPassword()));
        userDao.addTodoDao();
        userRepository.save(userDao);
        return true;
    }

    /** ユーザ削除処理 */
    public void removeUser(String id){
        userRepository.deleteById(id);
    }

    /** ユーザ更新処理 */
    public void updateUser(String id, UserDao userDao){
        //removeUser(id);
        userRepository.save(userDao);
    }
    
}
