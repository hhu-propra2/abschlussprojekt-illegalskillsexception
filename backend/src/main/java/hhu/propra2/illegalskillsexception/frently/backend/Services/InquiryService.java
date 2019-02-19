package hhu.propra2.illegalskillsexception.frently.backend.Services;

import hhu.propra2.illegalskillsexception.frently.backend.Models.*;
import hhu.propra2.illegalskillsexception.frently.backend.Repositories.InquiryRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class InquiryService implements IInquiryService{

    private final InquiryRepository inquiryRepository;

    public InquiryService(InquiryRepository pInquiryRepository) {
        this.inquiryRepository = pInquiryRepository;
    }

    public Long createInquiry(Article article, ApplicationUser borrower,
                              LendingPeriod lendingPeriod, Inquiry.Status status) {
        final Inquiry temp = setInquiry(new Inquiry(), article, borrower, article.getOwner(), lendingPeriod, status);
        inquiryRepository.save(temp);
        return temp.getId();
    }

    public Inquiry updateInquiry(Inquiry inquiry) {
        if (inquiryRepository.existsById(inquiry.getId())) {
            inquiryRepository.save(inquiry);
        }
        return inquiry;
    }

    public Inquiry getInquiry(Long id) {
        if (inquiryRepository.existsById(id)) {
            return inquiryRepository.findById(id).get();
        }
        return null;
    }

    public List<Inquiry> getAllInquirys() {
        return inquiryRepository.findAll();
    }

    private Inquiry setInquiry(Inquiry inquiry, Article article, ApplicationUser borrower,
                               ApplicationUser lender, LendingPeriod lendingPeriod, Inquiry.Status status) {
        inquiry.setArticle(article);
        inquiry.setBorrower(borrower);
        inquiry.setLender(lender);
        inquiry.setDuration(lendingPeriod);
        inquiry.setStatus(status);
        return inquiry;
    }

    public List<Inquiry> getAllInquiries(Long id) {
        List<Inquiry> list = inquiryRepository.findAllByBorrower_Id(id);
        list.addAll(inquiryRepository.findAllByLender_Id(id));

        return list;
    }

    // TODO
    public Long accept(ApplicationUser borrower, Long inquiryId) {
        Inquiry inquiry = getInquiry(inquiryId);

        Double prize = calculatePrize(inquiry);
        ProPayAccount proPayAccount = getProPayAccount(borrower.getBankAccount());

        if(hasEnoughMoney(proPayAccount, prize)) {
            // TODO create reservation and create Transaction
        }

        return 0L; // TODO alter return value
    }

    public void decline(ApplicationUser borrower, Long inquiryId) {
        Inquiry inquiry = getInquiry(inquiryId);
        inquiry.setStatus(Inquiry.Status.declined);
    }

    Boolean hasEnoughMoney(ProPayAccount account, Double requestedMoney) {
        return account.getAmount() >= requestedMoney;
    }

    public ProPayAccount getProPayAccount(String account) {
        final String url = "http://localhost:8080/ProPay/account/" + account;
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.getForObject(url, ProPayAccount.class);
    }

    Double calculatePrize(Inquiry inquiry) {
        Article article  = inquiry.getArticle();
        Double deposit = article.getDeposit();
        Double dailyRate = article.getDailyRate();
        LendingPeriod lendingPeriod = inquiry.getDuration();
        Long length = lendingPeriod.getLengthInDays();

        return deposit + dailyRate * length;
    }
}
