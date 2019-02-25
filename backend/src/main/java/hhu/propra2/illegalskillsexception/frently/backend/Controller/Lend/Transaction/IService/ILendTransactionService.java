package hhu.propra2.illegalskillsexception.frently.backend.Controller.Lend.Transaction.IService;

import hhu.propra2.illegalskillsexception.frently.backend.Controller.Lend.Transaction.DTOs.TransactionUpdateRequestDTO;
import hhu.propra2.illegalskillsexception.frently.backend.Controller.Lend.Transaction.Exceptions.NoSuchTransactionException;
import hhu.propra2.illegalskillsexception.frently.backend.Data.Models.Inquiry;
import hhu.propra2.illegalskillsexception.frently.backend.Data.Models.Transaction;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ILendTransactionService {
    Transaction createTransaction(Inquiry inquiry); //TODO: move method from LendInquiryProcessingService

    List<Transaction> retrieveAllOfCurrentUser(Authentication authentication);

    Transaction updateTransaction(TransactionUpdateRequestDTO update)
            throws NoSuchTransactionException;
}
