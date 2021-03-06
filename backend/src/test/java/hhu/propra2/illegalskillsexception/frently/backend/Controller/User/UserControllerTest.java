package hhu.propra2.illegalskillsexception.frently.backend.Controller.User;

import com.fasterxml.jackson.databind.ObjectMapper;
import hhu.propra2.illegalskillsexception.frently.backend.Controller.Security.ApplicationUserDTO;
import hhu.propra2.illegalskillsexception.frently.backend.Controller.User.DTOs.*;
import hhu.propra2.illegalskillsexception.frently.backend.Controller.User.Exceptions.UserAlreadyExistsAuthenticationException;
import hhu.propra2.illegalskillsexception.frently.backend.Controller.User.IServices.IApplicationUserService;
import hhu.propra2.illegalskillsexception.frently.backend.Controller.User.IServices.IUserDetailService;
import hhu.propra2.illegalskillsexception.frently.backend.Controller.User.IServices.IUserTransactionService;
import hhu.propra2.illegalskillsexception.frently.backend.Data.Models.ApplicationUser;
import hhu.propra2.illegalskillsexception.frently.backend.Data.Models.Transaction;
import hhu.propra2.illegalskillsexception.frently.backend.ProPay.Exceptions.ProPayConnectionException;
import hhu.propra2.illegalskillsexception.frently.backend.ProPay.IServices.IProPayService;
import org.hamcrest.core.IsNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.retry.ExhaustedRetryException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
public class UserControllerTest {
    @InjectMocks
    private UserController userController;
    private MockMvc mockMvc;
    @MockBean
    private IApplicationUserService mockUserService;
    @MockBean
    private IUserDetailService mockUserDetailService;
    @MockBean
    private IUserTransactionService mockTransactionService;
    @MockBean
    private IProPayService mockProPayService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void signHannesUpSuccessfully() throws Exception {
        ApplicationUserDTO applicationUserDTO = new ApplicationUserDTO();
        applicationUserDTO.setUsername("Hannes");
        applicationUserDTO.setPassword("123");
        applicationUserDTO.setEmail("HannesSupercool@yahoo.com");
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(applicationUserDTO);

        this.mockMvc.perform(post("/user/sign-up").contentType(APPLICATION_JSON_UTF8)
                .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.error").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.data[0].email").value("HannesSupercool@yahoo.com"))
                .andExpect(jsonPath("$.data[0].username").value("Hannes"))
                .andExpect(jsonPath("$.data[0].roles").value(IsNull.nullValue()));


        verify(mockUserService, times(1)).encryptPassword(any());
        verify(mockUserService, times(1)).createUser(any());
        verify(mockProPayService, times(1)).createAccount(any(), eq(0.0));
    }

    @Test
    public void signHannesUpFailsBecauseProPayIsNotReachable() throws Exception {
        ApplicationUserDTO applicationUserDTO = new ApplicationUserDTO();
        applicationUserDTO.setUsername("Hannes");
        applicationUserDTO.setPassword("123");
        applicationUserDTO.setEmail("HannesSupercool@yahoo.com");
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(applicationUserDTO);

        when(mockProPayService.createAccount(anyString(), anyDouble())).thenThrow(ExhaustedRetryException.class);
        this.mockMvc.perform(post("/user/sign-up").contentType(APPLICATION_JSON_UTF8)
                .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.error.errorType").value("PROPAY_CONNECTION"))
                .andExpect(jsonPath("$.data").value(IsNull.nullValue()));


        verify(mockUserService, times(0)).createUser(any());
        verify(mockProPayService, times(1)).createAccount(any(), eq(0.0));
    }

    @Test
    public void signHannesUpFailsBecauseTheUsernameIsAlreadyTaken() throws Exception {
        ApplicationUserDTO applicationUserDTO = new ApplicationUserDTO();
        applicationUserDTO.setUsername("Hannes");
        applicationUserDTO.setPassword("123");
        applicationUserDTO.setEmail("HannesSupercool@yahoo.com");
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(applicationUserDTO);
        doThrow(UserAlreadyExistsAuthenticationException.class).when(mockUserService).createUser(any());

        this.mockMvc.perform(post("/user/sign-up").contentType(APPLICATION_JSON_UTF8)
                .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.error.errorType").value("MISC"))
                .andExpect(jsonPath("$.data").value(IsNull.nullValue()));


        verify(mockUserService, times(1)).createUser(any());
        verify(mockProPayService, times(1)).createAccount(any(), eq(0.0));
    }

    @Test
    public void getHannesUserDetailsSuccessfully() throws Exception {
        UserDetailResponse hannesUserDetails = new UserDetailResponse();
        hannesUserDetails.setAccountBalance(300.0);
        hannesUserDetails.setEmail("HannesSupercool@yahoo.com");
        hannesUserDetails.setUsername("Hannes");
        hannesUserDetails.setPropayUsername("Hannes");

        MoneyTransferDTO transferDTO = new MoneyTransferDTO();
        transferDTO.setAmount(300);
        transferDTO.setReceiver("Hannes");
        transferDTO.setSender("Hannes");
        ArrayList<MoneyTransferDTO> moneyTransferDTOList = new ArrayList<>();
        moneyTransferDTOList.add(transferDTO);

        hannesUserDetails.setCompletedTransactions(moneyTransferDTOList);

        when(mockUserDetailService.getUserDetails(any())).thenReturn(hannesUserDetails);

        this.mockMvc.perform(get("/user/").contentType(APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.error").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.data.username").value("Hannes"))
                .andExpect(jsonPath("$.data.email").value("HannesSupercool@yahoo.com"))
                .andExpect(jsonPath("$.data.propayUsername").value("Hannes"))
                .andExpect(jsonPath("$.data.accountBalance").value("300.0"))
                .andExpect(jsonPath("$.data.completedTransactions[0].sender").value("Hannes"))
                .andExpect(jsonPath("$.data.completedTransactions[0].receiver").value("Hannes"))
                .andExpect(jsonPath("$.data.completedTransactions[0].amount").value("300.0"));

        verify(mockUserDetailService, times(1)).getUserDetails(any());
    }

    @Test
    public void getHannesUserDetailsFailsBecauseProPayDoesNotAnswer() throws Exception {
        when(mockUserDetailService.getUserDetails(any())).thenThrow(ProPayConnectionException.class);

        this.mockMvc.perform(get("/user/").contentType(APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.error.errorType").value("PROPAY_CONNECTION"))
                .andExpect(jsonPath("$.data").value(IsNull.nullValue()));

        verify(mockUserDetailService, times(1)).getUserDetails(any());
    }

    @Test
    public void getHannesUserDetailsWithoutBeingHannes() throws Exception {

        ForeignUserDetailRequest foreignUserDetailRequest = new ForeignUserDetailRequest();
        foreignUserDetailRequest.setUsername("Hannes");
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(foreignUserDetailRequest);

        MoneyTransferDTO transferDTO = new MoneyTransferDTO();
        transferDTO.setReceiver("Hannes");
        transferDTO.setSender("Max");
        transferDTO.setAmount(300.0);
        ArrayList<MoneyTransferDTO> moneyTransferDTOList = new ArrayList<>();
        moneyTransferDTOList.add(transferDTO);

        ForeignUserDetailResponse foreignUserDetailResponse = new ForeignUserDetailResponse();
        foreignUserDetailResponse.setUsername("Hannes");
        foreignUserDetailResponse.setCompletedTransactions(moneyTransferDTOList);

        when(mockUserDetailService.getForeignUserDetails(any())).thenReturn(foreignUserDetailResponse);

        this.mockMvc.perform(post("/user/").contentType(APPLICATION_JSON_UTF8)
                .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.error").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.data.username").value("Hannes"))
                .andExpect(jsonPath("$.data.completedTransactions[0].sender").value("Max"))
                .andExpect(jsonPath("$.data.completedTransactions[0].receiver").value("Hannes"))
                .andExpect(jsonPath("$.data.completedTransactions[0].amount").value("300.0"));

        verify(mockUserDetailService, times(1)).getForeignUserDetails(any());
    }

    @Test
    public void getHannesUserDetailsWithoutBeingHannesFailsBecauseProPayDoesNotAnswer() throws Exception {

        ForeignUserDetailRequest foreignUserDetailRequest = new ForeignUserDetailRequest();
        foreignUserDetailRequest.setUsername("Hannes");
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(foreignUserDetailRequest);

        when(mockUserDetailService.getForeignUserDetails(any())).thenThrow(ProPayConnectionException.class);

        this.mockMvc.perform(post("/user/").contentType(APPLICATION_JSON_UTF8)
                .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.error.errorType").value("PROPAY_CONNECTION"))
                .andExpect(jsonPath("$.data").value(IsNull.nullValue()));

        verify(mockUserDetailService, times(1)).getForeignUserDetails(any());
    }

    @Test
    public void chargeUpHannesAccountWith300Euro() throws Exception {
        ChargeAmountDTO chargeAmountDTO = new ChargeAmountDTO();
        chargeAmountDTO.setAmount(300L);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(chargeAmountDTO);

        this.mockMvc.perform(post("/user/charge").contentType(APPLICATION_JSON_UTF8)
                .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.error").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.data").value(IsNull.nullValue()));

        verify(mockProPayService, times(1)).payInMoney(any(), eq(300.0));
    }

    @Test
    public void chargeUpHannesAccountWith300EuroFailsBecauseProPayDoesNotRespond() throws Exception {
        ChargeAmountDTO chargeAmountDTO = new ChargeAmountDTO();
        chargeAmountDTO.setAmount(300L);

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(chargeAmountDTO);

        doThrow(ExhaustedRetryException.class).when(mockProPayService).payInMoney(any(), eq(300.0));

        this.mockMvc.perform(post("/user/charge").contentType(APPLICATION_JSON_UTF8)
                .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.error.errorType").value("PROPAY_CONNECTION"))
                .andExpect(jsonPath("$.data").value(IsNull.nullValue()));

        verify(mockProPayService, times(1)).payInMoney(any(), eq(300.0));
    }

    @Test
    public void notificationsOneOverdueTransaction() throws Exception {
        ApplicationUser applicationUser = new ApplicationUser();
        applicationUser.setUsername("Hans");
        applicationUser.setEmail("HansSupercool@yahoo.com");
        applicationUser.setPassword("123");
        applicationUser.setId(1);

        Transaction transaction = Mockito.mock(Transaction.class);
        ArrayList<Transaction> transactionArrayList = new ArrayList<>();
        transactionArrayList.add(transaction);

        when(mockUserService.getCurrentUser(any())).thenReturn(applicationUser);
        when(mockTransactionService.allOverdueTransactions(eq(1))).thenReturn(transactionArrayList);

        this.mockMvc.perform(get("/user/notifications").contentType(APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.error").value(IsNull.nullValue()))
                .andExpect(jsonPath("$.data", hasSize(0)));
    }

    @Test
    public void notificationsNoOverdueTransactions() throws Exception {
        when(mockTransactionService.allOverdueTransactions(eq(1))).thenReturn(null);

        this.mockMvc.perform(get("/user/notifications").contentType(APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.error.errorType").value("NO_SUCH_TRANSACTION"))
                .andExpect(jsonPath("$.data").value(IsNull.nullValue()));

    }

}