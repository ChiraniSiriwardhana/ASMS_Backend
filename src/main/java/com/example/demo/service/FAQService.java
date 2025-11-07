package com.example.demo.service;

import com.example.demo.dto.FAQRequest;
import com.example.demo.model.FAQ;
import com.example.demo.repository.FAQRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FAQService {

    @Autowired
    private FAQRepository faqRepository;

    // Get all active FAQs for customers
    public List<FAQ> getAllActiveFAQs() {
        return faqRepository.findByIsActiveTrueOrderByCreatedAtDesc();
    }

    // Get all FAQs for admin (including inactive)
    public List<FAQ> getAllFAQs() {
        return faqRepository.findAllByOrderByCreatedAtDesc();
    }

    // Get FAQ by ID
    public Optional<FAQ> getFAQById(Long id) {
        return faqRepository.findById(id);
    }

    // Create new FAQ
    @Transactional
    public FAQ createFAQ(FAQRequest request) {
        FAQ faq = new FAQ();
        faq.setCategory(request.getCategory());
        faq.setQuestion(request.getQuestion());
        faq.setAnswer(request.getAnswer());
        faq.setIsActive(true);
        faq.setDisplayOrder(request.getDisplayOrder());
        return faqRepository.save(faq);
    }

    // Update FAQ
    @Transactional
    public FAQ updateFAQ(Long id, FAQRequest request) {
        FAQ faq = faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FAQ not found with id: " + id));
        
        faq.setCategory(request.getCategory());
        faq.setQuestion(request.getQuestion());
        faq.setAnswer(request.getAnswer());
        if (request.getDisplayOrder() != null) {
            faq.setDisplayOrder(request.getDisplayOrder());
        }
        
        return faqRepository.save(faq);
    }

    // Toggle FAQ active status
    @Transactional
    public FAQ toggleFAQStatus(Long id) {
        FAQ faq = faqRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("FAQ not found with id: " + id));
        
        faq.setIsActive(!faq.getIsActive());
        return faqRepository.save(faq);
    }

    // Delete FAQ
    @Transactional
    public void deleteFAQ(Long id) {
        if (!faqRepository.existsById(id)) {
            throw new RuntimeException("FAQ not found with id: " + id);
        }
        faqRepository.deleteById(id);
    }
}
