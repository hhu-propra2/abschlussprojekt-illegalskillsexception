package hhu.propra2.illegalskillsexception.frently.backend.ProPay.Services;

import hhu.propra2.illegalskillsexception.frently.backend.Controller.Lend.Transaction.Exceptions.InsufficientFundsException;
import hhu.propra2.illegalskillsexception.frently.backend.Controller.User.Exceptions.UserNotFoundException;
import hhu.propra2.illegalskillsexception.frently.backend.Data.Models.Transaction;
import hhu.propra2.illegalskillsexception.frently.backend.ProPay.Exceptions.ProPayConnectionException;
import hhu.propra2.illegalskillsexception.frently.backend.ProPay.IServices.IMoneyTransferService;
import hhu.propra2.illegalskillsexception.frently.backend.ProPay.IServices.IProPayService;
import hhu.propra2.illegalskillsexception.frently.backend.ProPay.Models.MoneyTransfer;
import hhu.propra2.illegalskillsexception.frently.backend.ProPay.Models.ProPayAccount;
import hhu.propra2.illegalskillsexception.frently.backend.ProPay.Models.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.ExhaustedRetryException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ProPayService implements IProPayService {
    private String BASE_URL;
    private IMoneyTransferService moneyTransferService;
    private RestTemplate restTemplate = new RestTemplate();

    //service.url takes the url from the application.properties based on the spring-configuration
    @Autowired
    public ProPayService(IMoneyTransferService moneyTransferService, @Value("${service.url}") String BASE_URL) {
        this.moneyTransferService = moneyTransferService;
        this.BASE_URL = BASE_URL;
    }

    @Override
    @Retryable(value = {ProPayConnectionException.class}, maxAttempts = 2, backoff = @Backoff(delay = 1000))
    public ProPayAccount createAccount(String userName, double amount)
            throws ProPayConnectionException, ExhaustedRetryException, UserNotFoundException {
        String url = BASE_URL + "account/" + userName + "?amount=" + amount;
        ProPayAccount newAccount;
        try {
            newAccount = restTemplate.postForObject(url, null, ProPayAccount.class);
        } catch (Exception e) {
            throw new ProPayConnectionException();
        }
        return newAccount;
    }

    @Override
    public double getAccountBalance(String username) throws ProPayConnectionException, ExhaustedRetryException {
        ProPayAccount proPayAccount = getProPayAccount(username);
        return proPayAccount.getAmount();
    }

    @Override
    public void payInMoney(Authentication auth, double amount) throws ProPayConnectionException, ExhaustedRetryException, UserNotFoundException {
        String userName = (String) auth.getPrincipal();
        createAccount(userName, amount);
        moneyTransferService.createMoneyTransfer(userName, userName, amount);
    }

    @Override
    @Retryable(value = {ProPayConnectionException.class}, maxAttempts = 2, backoff = @Backoff(delay = 1000))
    public void transferMoney(String borrower, String lender, double amount)
            throws InsufficientFundsException, ProPayConnectionException, ExhaustedRetryException, UserNotFoundException {
        final String url = BASE_URL + "account/" + borrower + "/transfer/" + lender + "?amount=" + amount;

        try {
            checkFunds(borrower, amount);
        } catch (ExhaustedRetryException e) {
            throw new ProPayConnectionException();
        }

        try {
            restTemplate.postForLocation(url, null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ProPayConnectionException();
        }

        moneyTransferService.createMoneyTransfer(borrower, lender, amount);
    }

    @Override
    public List<MoneyTransfer> getAllMoneyTransfers(String userName) {
        return moneyTransferService.getAll(userName);
    }

    @Override
    @Retryable(value = {ProPayConnectionException.class}, maxAttempts = 2, backoff = @Backoff(delay = 1000))
    public ProPayAccount getProPayAccount(String username) throws ProPayConnectionException, ExhaustedRetryException {
        final String url = BASE_URL + "account/" + username;
        ProPayAccount account;
        try {
            account = restTemplate.getForObject(url, ProPayAccount.class);
        } catch (Exception e) {
            throw new ProPayConnectionException();
        }
        return account;
    }

    @Override
    public boolean amountGreaterThanReservation(List<Reservation> reservations, double amount, double accountBalance) {
        double alreadyReserved = 0;
        for (Reservation reservation : reservations) {
            alreadyReserved += reservation.getAmount();
        }
        return accountBalance >= (amount + alreadyReserved);
    }

    @Override
    public Long blockDeposit(String borrower, String lender, double amount) throws ProPayConnectionException, InsufficientFundsException {
        final String url = BASE_URL + "reservation/reserve/" + borrower + "/" + lender + "?amount=" + amount;
        Reservation reservation;
        try {
            checkFunds(borrower, amount);
            reservation = postForReservation(url);
        } catch (ExhaustedRetryException e) {
            throw new ProPayConnectionException();
        }
        return reservation.getId();
    }

    @Retryable(value = {ProPayConnectionException.class}, maxAttempts = 2, backoff = @Backoff(delay = 1000))
    private Reservation postForReservation(String url) throws ProPayConnectionException, ExhaustedRetryException {
        Reservation reservation;
        try {
            reservation = restTemplate.postForObject(url, null, Reservation.class);
        } catch (Exception e) {
            throw new ProPayConnectionException();
        }
        return reservation;
    }

    @Retryable(value = {ProPayConnectionException.class}, maxAttempts = 2, backoff = @Backoff(delay = 1000))
    private ProPayAccount postForProPayAccount(String url) throws ProPayConnectionException, ExhaustedRetryException {
        ProPayAccount proPayAccount;
        try {
            proPayAccount = restTemplate.postForObject(url, null, ProPayAccount.class);
        } catch (Exception e) {
            throw new ProPayConnectionException();
        }
        return proPayAccount;
    }

    @Override
    public void freeDeposit(String borrower, Transaction transaction) throws ProPayConnectionException {
        long reservationId = transaction.getReservationId();
        final String url = BASE_URL + "reservation/release/" + borrower + "?reservationId=" + reservationId;
        try {
            postForProPayAccount(url);
        } catch (ExhaustedRetryException e) {
            throw new ProPayConnectionException();
        }
    }

    @Override
    public void punishUser(String borrower, Transaction transaction)
            throws ProPayConnectionException, UserNotFoundException {
        long reservationId = transaction.getReservationId();
        final String url = BASE_URL + "reservation/punish/" + borrower + "?reservationId=" + reservationId;
        try {
            postForReservation(url);
        } catch (ExhaustedRetryException e) {
            throw new ProPayConnectionException();
        }
        moneyTransferService.createMoneyTransfer(borrower, transaction.getInquiry().getLender().getUsername(), transaction.getInquiry().getBorrowArticle().getDeposit());
    }

    @Retryable(value = {ProPayConnectionException.class}, maxAttempts = 2, backoff = @Backoff(delay = 1000))
    private void checkFunds(String userName, double amount) throws InsufficientFundsException, ProPayConnectionException {
        ProPayAccount proPayAccount;
        try {
            proPayAccount = getProPayAccount(userName);
        } catch (ExhaustedRetryException e) {
            throw new ProPayConnectionException();
        }
        List<Reservation> reservations = proPayAccount.getReservations();
        double accountBalance = proPayAccount.getAmount();
        if (!amountGreaterThanReservation(reservations, amount, accountBalance)) throw new InsufficientFundsException();
    }
}
