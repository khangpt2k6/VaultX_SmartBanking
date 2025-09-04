package com.bankmanagement.service;

import com.bankmanagement.dto.CustomerDTO;
import com.bankmanagement.model.Customer;
import com.bankmanagement.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerService {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<CustomerDTO> getActiveCustomers() {
        return customerRepository.findByIsActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public Optional<CustomerDTO> getCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .map(this::convertToDTO);
    }
    
    public Optional<CustomerDTO> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email)
                .map(this::convertToDTO);
    }
    
    public Optional<CustomerDTO> getCustomerByPhone(String phone) {
        return customerRepository.findByPhone(phone)
                .map(this::convertToDTO);
    }
    
    public List<CustomerDTO> searchCustomersByName(String name) {
        return customerRepository.findByNameContaining(name).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        // Check if email or phone already exists
        if (customerRepository.existsByEmail(customerDTO.getEmail())) {
            throw new RuntimeException("Customer with email " + customerDTO.getEmail() + " already exists");
        }
        
        if (customerRepository.existsByPhone(customerDTO.getPhone())) {
            throw new RuntimeException("Customer with phone " + customerDTO.getPhone() + " already exists");
        }
        
        Customer customer = convertToEntity(customerDTO);
        customer.setIsActive(true);
        customer.setCreatedAt(LocalDateTime.now());
        
        Customer savedCustomer = customerRepository.save(customer);
        return convertToDTO(savedCustomer);
    }
    
    public CustomerDTO updateCustomer(Long customerId, CustomerDTO customerDTO) {
        Customer existingCustomer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));
        
        // Check if email is being changed and if it already exists
        if (!existingCustomer.getEmail().equals(customerDTO.getEmail()) && 
            customerRepository.existsByEmail(customerDTO.getEmail())) {
            throw new RuntimeException("Customer with email " + customerDTO.getEmail() + " already exists");
        }
        
        // Check if phone is being changed and if it already exists
        if (!existingCustomer.getPhone().equals(customerDTO.getPhone()) && 
            customerRepository.existsByPhone(customerDTO.getPhone())) {
            throw new RuntimeException("Customer with phone " + customerDTO.getPhone() + " already exists");
        }
        
        // Update fields
        existingCustomer.setFirstName(customerDTO.getFirstName());
        existingCustomer.setLastName(customerDTO.getLastName());
        existingCustomer.setAddress(customerDTO.getAddress());
        existingCustomer.setPhone(customerDTO.getPhone());
        existingCustomer.setEmail(customerDTO.getEmail());
        existingCustomer.setDateOfBirth(customerDTO.getDateOfBirth());
        
        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return convertToDTO(updatedCustomer);
    }
    
    public void deactivateCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));
        
        customer.setIsActive(false);
        customerRepository.save(customer);
    }
    
    public void activateCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));
        
        customer.setIsActive(true);
        customerRepository.save(customer);
    }
    
    public void deleteCustomer(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new RuntimeException("Customer not found with id: " + customerId);
        }
        customerRepository.deleteById(customerId);
    }
    
    public long getTotalCustomers() {
        return customerRepository.count();
    }
    
    public long getActiveCustomersCount() {
        return customerRepository.countByIsActiveTrue();
    }
    
    private CustomerDTO convertToDTO(Customer customer) {
        return new CustomerDTO(
                customer.getCustomerId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getAddress(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getDateOfBirth(),
                customer.getIsActive(),
                customer.getCreatedAt().toLocalDate()
        );
    }
    

    private Customer convertToEntity(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        customer.setCustomerId(customerDTO.getCustomerId());
        customer.setFirstName(customerDTO.getFirstName());
        customer.setLastName(customerDTO.getLastName());
        customer.setAddress(customerDTO.getAddress());
        customer.setPhone(customerDTO.getPhone());
        customer.setEmail(customerDTO.getEmail());
        customer.setDateOfBirth(customerDTO.getDateOfBirth());
        return customer;
    }
}
