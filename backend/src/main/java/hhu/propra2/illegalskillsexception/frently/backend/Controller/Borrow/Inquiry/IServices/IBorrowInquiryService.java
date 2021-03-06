package hhu.propra2.illegalskillsexception.frently.backend.Controller.Borrow.Inquiry.IServices;

import hhu.propra2.illegalskillsexception.frently.backend.Controller.Borrow.Inquiry.DTOs.BorrowInquiryRequestDTO;
import hhu.propra2.illegalskillsexception.frently.backend.Controller.Borrow.Inquiry.DTOs.BorrowInquiryResponseDTO;
import hhu.propra2.illegalskillsexception.frently.backend.Controller.Borrow.Inquiry.Exceptions.ArticleNotAvailableException;
import hhu.propra2.illegalskillsexception.frently.backend.Controller.Borrow.Inquiry.Exceptions.InvalidLendingPeriodException;
import hhu.propra2.illegalskillsexception.frently.backend.Data.Exceptions.NoSuchArticleException;
import hhu.propra2.illegalskillsexception.frently.backend.Data.Models.ApplicationUser;
import hhu.propra2.illegalskillsexception.frently.backend.Data.Models.Inquiry;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface IBorrowInquiryService {
    Inquiry createInquiry(Authentication auth, BorrowInquiryRequestDTO inquiryDTO)
            throws ArticleNotAvailableException, NoSuchArticleException, InvalidLendingPeriodException;

    List<BorrowInquiryResponseDTO> retrieveAllInquiriesByUser(ApplicationUser user);

    List<BorrowInquiryResponseDTO> retrieveAllUnacceptedInquiriesByUser(ApplicationUser user);

    List<Inquiry> getOpenAndAcceptedInquiries(List<Inquiry> inquiryList);
}
