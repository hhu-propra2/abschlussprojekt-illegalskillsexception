package hhu.propra2.illegalskillsexception.frently.backend.Borrow.Inquiry;

import hhu.propra2.illegalskillsexception.frently.backend.Borrow.Inquiry.DTOs.BorrowInquiryDTO;
import hhu.propra2.illegalskillsexception.frently.backend.Borrow.Inquiry.Services.IBorrowInquiryService;
import hhu.propra2.illegalskillsexception.frently.backend.Controllers.Response.FrentlyError;
import hhu.propra2.illegalskillsexception.frently.backend.Controllers.Response.FrentlyErrorType;
import hhu.propra2.illegalskillsexception.frently.backend.Controllers.Response.FrentlyResponse;
import hhu.propra2.illegalskillsexception.frently.backend.Exceptions.ArticleNotAvailableException;
import hhu.propra2.illegalskillsexception.frently.backend.Models.Inquiry;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/borrow/inquiry")
public class BorrowInquiryController {
    private final IBorrowInquiryService inquiryService;

    @GetMapping("/")
    public FrentlyResponse retrieveAllMyInquiries(Authentication auth) {
        FrentlyResponse response = new FrentlyResponse();
        List<Inquiry> inquiries = inquiryService.retrieveAllInquiriesByUser(auth);
        response.setData(inquiries);
        return response;
    }

    @PostMapping("/create")
    public FrentlyResponse createInquiry(Authentication auth, @RequestBody BorrowInquiryDTO borrowInquiryDTO) {
        FrentlyResponse response = new FrentlyResponse();
        try {
            long inquiryId = inquiryService.createInquiry(auth, borrowInquiryDTO);
            response.setData(inquiryId);
        } catch (ArticleNotAvailableException e) {
            response.setError(new FrentlyError(e.getMessage(), FrentlyErrorType.ARTICLE_NOT_AVAILABLE));
        }
        return response;
    }
}