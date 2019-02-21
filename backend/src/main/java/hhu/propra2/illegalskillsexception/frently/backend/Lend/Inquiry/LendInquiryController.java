package hhu.propra2.illegalskillsexception.frently.backend.Lend.Inquiry;

import hhu.propra2.illegalskillsexception.frently.backend.Controllers.Response.FrentlyError;
import hhu.propra2.illegalskillsexception.frently.backend.Controllers.Response.FrentlyResponse;
import hhu.propra2.illegalskillsexception.frently.backend.Exceptions.FrentlyException;
import hhu.propra2.illegalskillsexception.frently.backend.Lend.Inquiry.Services.LendInquiryProcessingService;
import hhu.propra2.illegalskillsexception.frently.backend.Lend.Inquiry.Services.LendInquiryService;
import hhu.propra2.illegalskillsexception.frently.backend.Models.ApplicationUser;
import hhu.propra2.illegalskillsexception.frently.backend.Models.Inquiry;
import hhu.propra2.illegalskillsexception.frently.backend.Models.Transaction;
import hhu.propra2.illegalskillsexception.frently.backend.Services.ApplicationUserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lend/inquiry")
@AllArgsConstructor
public class LendInquiryController {

    private final LendInquiryService lendInquiryService;
    private final LendInquiryProcessingService lendInquiryProcessingService;
    private final ApplicationUserService userService;

    @GetMapping("/")
    public FrentlyResponse retrieveLendInquiry(Authentication authentication) {

        ApplicationUser user = userService.getCurrentUser(authentication);
        FrentlyResponse response = new FrentlyResponse();

        try {
            final List<Inquiry> inquiryList = lendInquiryService.retrieveInquiriesFromUser(user);
            response.setData(inquiryList);
        } catch (Exception e) {
            response.setError(new FrentlyError(e));
        }
        return response;
    }

    @PostMapping("/decline")
    public FrentlyResponse declineLendInquiry(@RequestBody Long inquiryId) {

        FrentlyResponse response = new FrentlyResponse();

        try {
            final Inquiry inquiry = lendInquiryProcessingService.declineInquiry(inquiryId);
            response.setData(inquiry);
        } catch (Exception e) {
            response.setError(new FrentlyError(e));
        }
        return response;
    }

    @PostMapping("/accept")
    public FrentlyResponse accept(@RequestBody Long inquiryId) {
        FrentlyResponse response = new FrentlyResponse();

        try {
            final Transaction transaction = lendInquiryProcessingService.acceptInquiry(inquiryId);
            response.setData(transaction);
        } catch (FrentlyException fe) {
            response.setError(new FrentlyError(fe));
        } catch (Exception e) {
            response.setError(new FrentlyError(e));
        }
        return response;
    }
}
