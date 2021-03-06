package hhu.propra2.illegalskillsexception.frently.backend.Controller.Services;

import hhu.propra2.illegalskillsexception.frently.backend.Controller.User.Exceptions.UserAlreadyExistsAuthenticationException;
import hhu.propra2.illegalskillsexception.frently.backend.Controller.User.Services.ApplicationUserService;
import hhu.propra2.illegalskillsexception.frently.backend.Data.Models.ApplicationUser;
import hhu.propra2.illegalskillsexception.frently.backend.Data.Models.Role;
import hhu.propra2.illegalskillsexception.frently.backend.Data.Repositories.IApplicationUserRepository;
import hhu.propra2.illegalskillsexception.frently.backend.Data.Repositories.IRoleRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

public class ApplicationUserServiceTest {

    private IApplicationUserRepository applicationUserRepository;
    private IRoleRepository roleRepository;
    private hhu.propra2.illegalskillsexception.frently.backend.Controller.User.IServices.IApplicationUserService IApplicationUserService;
    private ArrayList<Optional> userList;
    private List<ApplicationUser> userList2;

    @Before
    public void setUp() {
        Optional<ApplicationUser> user0 = Optional.empty();
        Optional<ApplicationUser> user1 = Optional.of(new ApplicationUser());
        Optional<ApplicationUser> user2 = Optional.of(new ApplicationUser());
        userList = new ArrayList<>();
        userList.addAll(Arrays.asList(user0, user1, user2));
        userList2 = new ArrayList<>();

        ApplicationUser user3 = new ApplicationUser();
        user3.setUsername("ExampleUser");
        user3.setPassword("ExamplePassword");

        roleRepository = mock(IRoleRepository.class);
        when(roleRepository.findById(2L)).thenReturn(Optional.of(new Role()));

        userList2.addAll(Arrays.asList(new ApplicationUser(), new ApplicationUser(), new ApplicationUser(), user3));
        applicationUserRepository = mock(IApplicationUserRepository.class);
        when(applicationUserRepository.findById(0L)).thenReturn(userList.get(0));
        when(applicationUserRepository.findById(1L)).thenReturn(userList.get(1));
        when(applicationUserRepository.findAll()).thenReturn(userList2);
        when(applicationUserRepository.existsByUsername("ExampleUser")).thenReturn(true);
        IApplicationUserService = new ApplicationUserService(applicationUserRepository, new BCryptPasswordEncoder(), roleRepository);
    }

    @Test
    public void noUserPresent() {
        ApplicationUser temp = IApplicationUserService.getUserById(0L);
        verify(applicationUserRepository).findById(0L);
        assertNull(temp);
    }

    @Test
    public void userIsPresent() {
        ApplicationUser temp = IApplicationUserService.getUserById(1L);
        verify(applicationUserRepository).findById(1L);
        assertNotNull(temp);
    }

    @Test
    public void createValidUser() {
        ApplicationUser temp = new ApplicationUser();
        temp.setUsername("TestUser");
        temp.setPassword("TestPassword");
        IApplicationUserService.createUser(temp);

        verify(applicationUserRepository).existsByUsername("TestUser");
        verify(applicationUserRepository).save(temp);
    }

    @Test(expected = UserAlreadyExistsAuthenticationException.class)
    public void createInvalidUser() {
        ApplicationUser temp = new ApplicationUser();
        temp.setUsername("ExampleUser");
        temp.setPassword("ExamplePassword");
        IApplicationUserService.createUser(temp);

        verify(applicationUserRepository).existsByUsername("ExampleUser");

    }
}
