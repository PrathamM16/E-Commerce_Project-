package com.triviumbuys.services;

import com.triviumbuys.entity.UploadedCustomer;
import com.triviumbuys.entity.UploadedOrder;
import com.triviumbuys.entity.UploadedProduct;
import com.triviumbuys.utils.FileHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class FileUploadService {

    private List<UploadedOrder> uploadedOrders;
    private List<UploadedProduct> uploadedProducts;
    private List<UploadedCustomer> uploadedCustomers;

    public void uploadOrders(MultipartFile file) {
        List<UploadedOrder> parsedOrders = FileHelper.parseOrdersFile(file);
        if (parsedOrders != null && !parsedOrders.isEmpty()) {
            this.uploadedOrders = parsedOrders;
        } else {
            throw new RuntimeException("Uploaded Orders file is empty or invalid!");
        }
    }

    public void uploadProducts(MultipartFile file) {
        List<UploadedProduct> parsedProducts = FileHelper.parseProductsFile(file);
        if (parsedProducts != null && !parsedProducts.isEmpty()) {
            this.uploadedProducts = parsedProducts;
        } else {
            throw new RuntimeException("Uploaded Products file is empty or invalid!");
        }
    }

    public void uploadCustomers(MultipartFile file) {
        List<UploadedCustomer> parsedCustomers = FileHelper.parseCustomersFile(file);
        if (parsedCustomers != null && !parsedCustomers.isEmpty()) {
            this.uploadedCustomers = parsedCustomers;
        } else {
            throw new RuntimeException("Uploaded Customers file is empty or invalid!");
        }
    }

    public List<UploadedOrder> getUploadedOrders() {
        return uploadedOrders;
    }

    public List<UploadedProduct> getUploadedProducts() {
        return uploadedProducts;
    }

    public List<UploadedCustomer> getUploadedCustomers() {
        return uploadedCustomers;
    }
}
