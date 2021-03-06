package hhu.propra2.illegalskillsexception.frently.backend.Controller.User.IServices;

import hhu.propra2.illegalskillsexception.frently.backend.Data.Models.ApplicationUser;
import org.springframework.security.core.Authentication;

public interface IApplicationUserService {
    ApplicationUser getApplicationUserByUsername(String userName);

    void createUser(ApplicationUser user);

    ApplicationUser getUserById(Long userId);

    void encryptPassword(ApplicationUser user);

    ApplicationUser getCurrentUser(Authentication authentication);
}
