package com.example.investment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.investment.dao.UserDao;
import com.example.investment.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    /** ユーザ作成 */
    public Boolean create(String userName, String mailaddress, String password){
        if (userRepository.findByMailaddressInt(mailaddress) != null) {
            return false;
        }
        UserDao userDao = new UserDao();
        userDao.setUserName(userName);
        userDao.setMailaddress(mailaddress);
        userDao.setPassword(new Pbkdf2PasswordEncoder().encode(password));
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
