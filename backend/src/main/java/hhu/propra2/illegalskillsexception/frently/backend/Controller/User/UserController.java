package hhu.propra2.illegalskillsexception.frently.backend.Controller.User;


import hhu.propra2.illegalskillsexception.frently.backend.Controller.Response.FrentlyError;
import hhu.propra2.illegalskillsexception.frently.backend.Controller.Response.FrentlyErrorType;
import hhu.propra2.illegalskillsexception.frently.backend.Controller.Response.FrentlyException;
import hhu.propra2.illegalskillsexception.frently.backend.Controller.Response.FrentlyResponse;
import hhu.propra2.illegalskillsexception.frently.backend.Controller.Security.ApplicationUserDTO;
import hhu.propra2.illegalskillsexception.frently.backend.Controller.User.DTOs.ChargeAmountDTO;
import hhu.propra2.illegalskillsexception.frently.backend.Controller.User.DTOs.ForeignUserDetailRequest;
import hhu.propra2.illegalskillsexception.frently.backend.Controller.User.DTOs.ForeignUserDetailResponse;
import hhu.propra2.illegalskillsexception.frently.backend.Controller.User.DTOs.UserDetailResponse;
import hhu.propra2.illegalskillsexception.frently.backend.Controller.User.IServices.IApplicationUserService;
import hhu.propra2.illegalskillsexception.frently.backend.Controller.User.IServices.IUserDetailService;
import hhu.propra2.illegalskillsexception.frently.backend.Controller.User.IServices.IUserTransactionService;
import hhu.propra2.illegalskillsexception.frently.backend.Data.Models.ApplicationUser;
import hhu.propra2.illegalskillsexception.frently.backend.ProPay.Exceptions.ProPayConnectionException;
import hhu.propra2.illegalskillsexception.frently.backend.ProPay.IServices.IProPayService;
import lombok.AllArgsConstructor;
import org.springframework.retry.ExhaustedRetryException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@AllArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private IApplicationUserService userService;
    private IUserDetailService userDetailService;
    private IUserTransactionService transactionService;
    private IProPayService proPayService;

    @PostMapping("/sign-up")
    public FrentlyResponse signUp(@RequestBody ApplicationUserDTO dtoUser) {
        FrentlyResponse response = new FrentlyResponse();
        ApplicationUser user = new ApplicationUser();
        user.setPassword(dtoUser.getPassword());
        user.setUsername(dtoUser.getUsername());
        user.setEmail(dtoUser.getEmail());
        userService.encryptPassword(user);
        try {
            proPayService.createAccount(user.getUsername(), 0);
            userService.createUser(user);
            response.setData(Collections.singletonList(user));
        } catch (ExhaustedRetryException e) {
            response.setError(new FrentlyError(new ProPayConnectionException()));
        } catch (FrentlyException fe) {
            response.setError(new FrentlyError(fe));
        } catch (Exception e) {
            response.setError(new FrentlyError(e));
        }
        return response;
    }

    @GetMapping("/")
    public FrentlyResponse getUserDetails(Authentication auth) {
        FrentlyResponse response = new FrentlyResponse();

        UserDetailResponse userDetailResponse;
        try {
            userDetailResponse = userDetailService.getUserDetails(auth);
            response.setData(userDetailResponse);
        } catch (FrentlyException exc) {
            userDetailResponse = userDetailService.getUserDetailsWithoutPropay(auth);
            response.setData(userDetailResponse);
            response.setError(new FrentlyError(new ProPayConnectionException()));
        } catch (Exception e) {
            response.setError(new FrentlyError(e));
        }
        return response;
    }

    @PostMapping("/")
    public FrentlyResponse getUserDetails(@RequestBody ForeignUserDetailRequest request) {
        FrentlyResponse response = new FrentlyResponse();
        try {
            ForeignUserDetailResponse foreignUserDetailResponse = userDetailService.getForeignUserDetails(request.getUsername());
            response.setData(foreignUserDetailResponse);
        } catch (FrentlyException exc) {
            response.setError(new FrentlyError(new ProPayConnectionException()));
        } catch (Exception e) {
            response.setError(new FrentlyError(e));
        }
        return response;
    }

    @PostMapping("/charge")
    public FrentlyResponse chargeCredit(Authentication auth, @RequestBody ChargeAmountDTO amount) {
        FrentlyResponse response = new FrentlyResponse();
        try {
            proPayService.payInMoney(auth, amount.getAmount());
        } catch (ExhaustedRetryException e) {
            response.setError(new FrentlyError(new ProPayConnectionException()));
        } catch (FrentlyException fe) {
            response.setError(new FrentlyError(fe));
        } catch (Exception e) {
            response.setError(new FrentlyError(e));
        }
        return response;
    }

    @GetMapping("/notifications")
    public FrentlyResponse notifications(Authentication auth) {
        FrentlyResponse response = new FrentlyResponse();
        ApplicationUser currentUser = userService.getCurrentUser(auth);
        try {
            response.setData(transactionService.allOverdueTransactions(currentUser.getId()));
        } catch (Exception e) {
            response.setError(new FrentlyError("Couldn't find any Transactions overdue for owner", FrentlyErrorType.NO_SUCH_TRANSACTION));
        }
        return response;
    }
}