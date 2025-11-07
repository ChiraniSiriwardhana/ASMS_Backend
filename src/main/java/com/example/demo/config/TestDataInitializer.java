package com.example.demo.config;

import com.example.demo.model.FAQ;
import com.example.demo.model.CustomerQuestion;
import com.example.demo.repository.FAQRepository;
import com.example.demo.repository.CustomerQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@Order(2) // Run after DatabaseInitializer
public class TestDataInitializer implements CommandLineRunner {

    @Autowired
    private FAQRepository faqRepository;

    @Autowired
    private CustomerQuestionRepository customerQuestionRepository;

    @Override
    public void run(String... args) throws Exception {
        // Only add test data if tables are empty
        if (faqRepository.count() == 0) {
            System.out.println("=== Initializing Test FAQ Data ===");
            
            List<FAQ> testFAQs = Arrays.asList(
                createFAQ("General", "What are your business hours?", 
                    "We are open Monday to Friday from 8:00 AM to 6:00 PM, and Saturday from 9:00 AM to 4:00 PM. We are closed on Sundays and public holidays.", true, 1),
                
                createFAQ("Services", "What services do you offer?", 
                    "We offer a wide range of automotive services including routine maintenance, repairs, diagnostics, tire services, brake services, and more. Please visit our Services page for a complete list.", true, 2),
                
                createFAQ("Appointments", "How do I schedule an appointment?", 
                    "You can schedule an appointment online through our booking system, call us directly, or visit our location. We recommend booking in advance to ensure availability.", true, 3),
                
                createFAQ("Pricing", "Do you provide estimates before service?", 
                    "Yes, we provide free estimates for all services. Our technicians will assess your vehicle and provide a detailed quote before beginning any work.", true, 4),
                
                createFAQ("Payment", "What payment methods do you accept?", 
                    "We accept cash, credit cards (Visa, MasterCard, American Express), debit cards, and online payments. Payment is due upon completion of service.", true, 5),
                
                createFAQ("Warranty", "Do you offer warranty on your services?", 
                    "Yes, we offer a 6-month or 10,000 km warranty on all our services and parts, whichever comes first. Terms and conditions apply.", true, 6),
                
                createFAQ("General", "Where are you located?", 
                    "We are located at 123 Main Street, City Center. You can find detailed directions on our Contact page or use GPS navigation.", true, 7),
                
                createFAQ("Appointments", "Can I cancel or reschedule my appointment?", 
                    "Yes, you can cancel or reschedule your appointment up to 24 hours before the scheduled time. Please contact us as soon as possible to make changes.", true, 8),
                
                createFAQ("Services", "How long does a typical service take?", 
                    "Service duration varies depending on the type of work needed. Routine maintenance typically takes 1-2 hours, while more complex repairs may take longer. We'll provide an estimated completion time when you book.", true, 9),
                
                createFAQ("General", "Do you provide pickup and drop-off service?", 
                    "Yes, we offer complimentary pickup and drop-off service within a 5km radius for major services. Please inquire when booking your appointment.", false, 10)
            );
            
            faqRepository.saveAll(testFAQs);
            System.out.println("✅ Added " + testFAQs.size() + " test FAQs");
        } else {
            System.out.println("FAQ data already exists. Skipping test data initialization.");
        }

        // Add sample customer questions if none exist
        if (customerQuestionRepository.count() == 0) {
            System.out.println("=== Initializing Test Customer Questions ===");
            
            List<CustomerQuestion> testQuestions = Arrays.asList(
                createQuestion("John Smith", "john.smith@email.com", "Services", "Do you offer electric vehicle servicing?", false),
                createQuestion("Sarah Johnson", "sarah.j@email.com", "Pricing", "Can I get a quote for brake replacement?", false),
                createQuestion("Mike Chen", "mike.chen@email.com", "Services", "Do you provide roadside assistance?", true),
                createQuestion("Emily Davis", "emily.d@email.com", "General", "What is your policy on parts replacement?", true)
            );
            
            customerQuestionRepository.saveAll(testQuestions);
            System.out.println("✅ Added " + testQuestions.size() + " test customer questions");
        } else {
            System.out.println("Customer questions already exist. Skipping test data initialization.");
        }
        
        System.out.println("=== Test Data Initialization Complete ===");
    }

    private FAQ createFAQ(String category, String question, String answer, boolean isActive, int displayOrder) {
        FAQ faq = new FAQ();
        faq.setCategory(category);
        faq.setQuestion(question);
        faq.setAnswer(answer);
        faq.setIsActive(isActive);
        faq.setDisplayOrder(displayOrder);
        return faq;
    }

    private CustomerQuestion createQuestion(String fullName, String email, String category, String question, boolean isResolved) {
        CustomerQuestion cq = new CustomerQuestion();
        cq.setFullName(fullName);
        cq.setEmail(email);
        cq.setCategory(category);
        cq.setQuestion(question);
        cq.setIsResolved(isResolved);
        return cq;
    }
}
