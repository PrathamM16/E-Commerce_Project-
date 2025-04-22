package com.triviumbuys.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;
import com.triviumbuys.entity.UploadedCustomer;
import com.triviumbuys.entity.UploadedOrder;
import com.triviumbuys.entity.UploadedProduct;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileHelper {

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        try {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue().toString();
                    } else {
                        // Format numeric values to avoid decimal places if it's a whole number
                        double numValue = cell.getNumericCellValue();
                        if (numValue == Math.floor(numValue)) {
                            return String.valueOf((long)numValue);
                        } else {
                            return String.valueOf(numValue);
                        }
                    }
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:
                    try {
                        return String.valueOf(cell.getNumericCellValue());
                    } catch (Exception e) {
                        try {
                            return cell.getStringCellValue();
                        } catch (Exception ex) {
                            return "";
                        }
                    }
                case BLANK:
                    return "";
                default:
                    return "";
            }
        } catch (Exception e) {
            System.err.println("Error getting cell value: " + e.getMessage());
            return "";
        }
    }

    public static List<UploadedOrder> parseOrdersFile(MultipartFile file) {
        List<UploadedOrder> orders = new ArrayList<>();
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            
            // Skip header row if it exists
            if (rows.hasNext()) {
                rows.next();
            }

            while (rows.hasNext()) {
                Row row = rows.next();
                if (isEmptyRow(row)) continue;
                
                UploadedOrder order = new UploadedOrder();

                try {
                    // Try different column positions for Order ID
                    String orderIdStr = getCellValueAsString(row.getCell(0));
                    if (orderIdStr.isEmpty()) {
                        // Try a different column
                        orderIdStr = getCellValueAsString(row.getCell(1));
                    }
                    
                    if (!orderIdStr.isEmpty()) {
                        try {
                            order.setOrderId(Long.parseLong(orderIdStr));
                        } catch (NumberFormatException e) {
                            // Try parsing as double first then converting to long
                            order.setOrderId((long) Double.parseDouble(orderIdStr));
                        }
                    } else {
                        continue; // Skip row if no order ID
                    }

                    // Try different patterns for customer name, status, date, amount, and product details
                    // by checking multiple potential columns
                    
                    // Customer Name - try columns 1, 2, 3
                    for (int i = 1; i <= 3; i++) {
                        String customerName = getCellValueAsString(row.getCell(i));
                        if (!customerName.isEmpty()) {
                            order.setCustomerName(customerName);
                            break;
                        }
                    }

                    // Status - try columns 2, 3, 4
                    for (int i = 2; i <= 4; i++) {
                        String status = getCellValueAsString(row.getCell(i));
                        if (!status.isEmpty() && isLikelyStatus(status)) {
                            order.setStatus(status);
                            break;
                        }
                    }

                    // Date - try columns 3, 4, 5
                    for (int i = 3; i <= 5; i++) {
                        String date = getCellValueAsString(row.getCell(i));
                        if (!date.isEmpty() && isLikelyDate(date)) {
                            order.setDate(formatDate(date));
                            break;
                        }
                    }

                    // Total Amount - try columns 4, 5, 6
                    for (int i = 4; i <= 6; i++) {
                        String amountStr = getCellValueAsString(row.getCell(i));
                        if (!amountStr.isEmpty() && isLikelyAmount(amountStr)) {
                            try {
                                order.setTotalAmount(Double.parseDouble(amountStr));
                                break;
                            } catch (NumberFormatException e) {
                                // Try removing currency symbols and commas
                                amountStr = amountStr.replaceAll("[^0-9.]", "");
                                try {
                                    order.setTotalAmount(Double.parseDouble(amountStr));
                                    break;
                                } catch (NumberFormatException ex) {
                                    // Continue to next column
                                }
                            }
                        }
                    }
                    
                    // Set default total amount if still not set
                    if (order.getTotalAmount() == null) {
                        order.setTotalAmount(0.0);
                    }

                    // Product Details - try columns 5, 6, 7
                    for (int i = 5; i <= 7; i++) {
                        String productDetails = getCellValueAsString(row.getCell(i));
                        if (!productDetails.isEmpty() && isLikelyProductDetails(productDetails)) {
                            order.setProductDetails(productDetails);
                            break;
                        }
                    }

                    orders.add(order);
                } catch (Exception e) {
                    // Log the error and continue with the next row
                    System.err.println("Error parsing row: " + e.getMessage());
                    continue;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to parse orders file: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to parse orders file: " + e.getMessage(), e);
        }
        return orders;
    }

    public static List<UploadedProduct> parseProductsFile(MultipartFile file) {
        List<UploadedProduct> products = new ArrayList<>();
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Find column indices
            Map<String, Integer> columnIndices = findColumnIndices(sheet,
                    Arrays.asList("id", "product id", "productid", "name", "product name", "productname",
                            "category", "price", "stock", "quantity"));

            // Skip header
            if (rows.hasNext()) rows.next();

            while (rows.hasNext()) {
                Row row = rows.next();
                if (isEmptyRow(row)) continue;

                try {
                    UploadedProduct product = new UploadedProduct();

                    Integer idColIndex = getFirstMatchingIndex(columnIndices, Arrays.asList("id", "product id", "productid"));
                    if (idColIndex == null) idColIndex = 0;

                    String productIdStr = getCellValueAsString(row.getCell(idColIndex));
                    if (!productIdStr.isEmpty() && isNumeric(productIdStr)) {
                        product.setId(Long.parseLong(productIdStr)); // 💥 Now setting product ID
                    } else {
                        continue; // Skip row if no ID
                    }

                    // Name
                    Integer nameColIndex = getFirstMatchingIndex(columnIndices, Arrays.asList("name", "product name", "productname"));
                    if (nameColIndex != null) {
                        product.setName(getCellValueAsString(row.getCell(nameColIndex)));
                    }

                    // Category
                    Integer categoryColIndex = getFirstMatchingIndex(columnIndices, Arrays.asList("category"));
                    if (categoryColIndex != null) {
                        product.setCategory(getCellValueAsString(row.getCell(categoryColIndex)));
                    }

                    // Price
                    Integer priceColIndex = getFirstMatchingIndex(columnIndices, Arrays.asList("price"));
                    if (priceColIndex != null) {
                        String priceStr = getCellValueAsString(row.getCell(priceColIndex)).replaceAll("[^0-9.]", "");
                        if (!priceStr.isEmpty()) {
                            product.setPrice(Double.parseDouble(priceStr));
                        }
                    }

                    // Stock
                    Integer stockColIndex = getFirstMatchingIndex(columnIndices, Arrays.asList("stock", "quantity"));
                    if (stockColIndex != null) {
                        String stockStr = getCellValueAsString(row.getCell(stockColIndex));
                        if (!stockStr.isEmpty()) {
                            product.setStock(Integer.parseInt(stockStr));
                        }
                    }

                    products.add(product);

                } catch (Exception e) {
                    System.err.println("Error parsing product row: " + e.getMessage());
                    continue;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to parse products file: " + e.getMessage());
            throw new RuntimeException("Failed to parse products file: " + e.getMessage(), e);
        }
        return products;
    }


    public static List<UploadedCustomer> parseCustomersFile(MultipartFile file) {
        List<UploadedCustomer> customers = new ArrayList<>();
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            
            // Find column indices by checking header row
            Map<String, Integer> columnIndices = findColumnIndices(sheet, 
                    Arrays.asList("id", "customer id", "customerid", "name", "customer name", "customername", 
                            "email", "phone", "address"));
            
            // Skip header row if it exists
            if (rows.hasNext()) {
                rows.next();
            }

            while (rows.hasNext()) {
                Row row = rows.next();
                if (isEmptyRow(row)) continue;
                
                try {
                    UploadedCustomer customer = new UploadedCustomer();

                    // Find customer ID (mandatory)
                    Integer idColIndex = getFirstMatchingIndex(columnIndices, Arrays.asList("id", "customer id", "customerid"));
                    if (idColIndex == null) idColIndex = 0; // Default to first column
                    
                    String customerIdStr = getCellValueAsString(row.getCell(idColIndex));
                    if (customerIdStr.isEmpty()) {
                        // Try scanning first few columns
                        for (int i = 0; i <= 2; i++) {
                            customerIdStr = getCellValueAsString(row.getCell(i));
                            if (!customerIdStr.isEmpty() && isNumeric(customerIdStr)) break;
                        }
                    }
                    
                    if (customerIdStr.isEmpty()) {
                        continue; // Skip row if no customer ID
                    }
                    
                    try {
                        customer.setCustomerId(Long.parseLong(customerIdStr));
                    } catch (NumberFormatException e) {
                        try {
                            // Try as double then convert to long
                            customer.setCustomerId((long) Double.parseDouble(customerIdStr));
                        } catch (NumberFormatException ex) {
                            continue; // Skip if ID can't be parsed
                        }
                    }
                    
                    // Name
                    Integer nameColIndex = getFirstMatchingIndex(columnIndices, Arrays.asList("name", "customer name", "customername"));
                    if (nameColIndex == null) nameColIndex = idColIndex + 1; // Default to column after ID
                    
                    customer.setName(getCellValueAsString(row.getCell(nameColIndex)));
                    
                    // Email
                    Integer emailColIndex = getFirstMatchingIndex(columnIndices, Arrays.asList("email"));
                    if (emailColIndex == null) emailColIndex = nameColIndex + 1; // Default to column after name
                    
                    customer.setEmail(getCellValueAsString(row.getCell(emailColIndex)));
                    
                    // Phone
                    Integer phoneColIndex = getFirstMatchingIndex(columnIndices, Arrays.asList("phone"));
                    if (phoneColIndex == null) phoneColIndex = emailColIndex + 1; // Default to column after email
                    
                    customer.setPhone(getCellValueAsString(row.getCell(phoneColIndex)));
                    
                    // Address
                    Integer addressColIndex = getFirstMatchingIndex(columnIndices, Arrays.asList("address"));
                    if (addressColIndex == null) addressColIndex = phoneColIndex + 1; // Default to column after phone
                    
                    customer.setAddress(getCellValueAsString(row.getCell(addressColIndex)));

                    customers.add(customer);
                } catch (Exception e) {
                    // Log the error and continue with the next row
                    System.err.println("Error parsing customer row: " + e.getMessage());
                    continue;
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to parse customers file: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to parse customers file: " + e.getMessage(), e);
        }
        return customers;
    }
    
    // Helper method to find column indices by header names
    private static Map<String, Integer> findColumnIndices(Sheet sheet, List<String> possibleHeaders) {
        Map<String, Integer> indices = new HashMap<>();
        
        // Try first 3 rows for headers
        for (int rowIndex = 0; rowIndex <= 2; rowIndex++) {
            Row headerRow = sheet.getRow(rowIndex);
            if (headerRow == null) continue;
            
            // Check each cell in the row
            for (int i = 0; i < 20; i++) { // Check first 20 columns
                Cell cell = headerRow.getCell(i);
                if (cell == null) continue;
                
                String cellValue = getCellValueAsString(cell).toLowerCase().trim();
                
                // Check if this cell matches any of our possible headers
                for (String header : possibleHeaders) {
                    if (cellValue.equals(header.toLowerCase()) || 
                            cellValue.replace(" ", "").equals(header.toLowerCase().replace(" ", ""))) {
                        indices.put(header.toLowerCase(), i);
                        break;
                    }
                }
            }
            
            // If we found at least some headers, stop looking
            if (!indices.isEmpty()) {
                break;
            }
        }
        
        return indices;
    }
    
    // Get the first matching column index from a list of possible headers
    private static Integer getFirstMatchingIndex(Map<String, Integer> indices, List<String> possibleHeaders) {
        for (String header : possibleHeaders) {
            Integer index = indices.get(header.toLowerCase());
            if (index != null) {
                return index;
            }
        }
        return null;
    }
    
    // Check if a row is empty
    private static boolean isEmptyRow(Row row) {
        if (row == null) return true;
        
        boolean isEmpty = true;
        for (int i = 0; i < 10; i++) { // Check first 10 columns
            Cell cell = row.getCell(i);
            if (cell != null && !getCellValueAsString(cell).trim().isEmpty()) {
                isEmpty = false;
                break;
            }
        }
        return isEmpty;
    }
    
    // Check if a string looks like a date
    private static boolean isLikelyDate(String str) {
        str = str.trim();
        // Check for common date formats
        return str.matches("\\d{4}-\\d{1,2}-\\d{1,2}") || // yyyy-MM-dd
               str.matches("\\d{1,2}-\\d{1,2}-\\d{4}") || // dd-MM-yyyy or MM-dd-yyyy
               str.matches("\\d{1,2}/\\d{1,2}/\\d{4}") || // dd/MM/yyyy or MM/dd/yyyy
               str.contains("Jan") || str.contains("Feb") || str.contains("Mar") || 
               str.contains("Apr") || str.contains("May") || str.contains("Jun") || 
               str.contains("Jul") || str.contains("Aug") || str.contains("Sep") || 
               str.contains("Oct") || str.contains("Nov") || str.contains("Dec");
    }
    
    // Format a date to yyyy-MM-dd
    private static String formatDate(String dateStr) {
        dateStr = dateStr.trim();
        // This is a simplified date formatter
        // For a production app, consider using a proper date parsing library
        
        // If already in yyyy-MM-dd format, return as is
        if (dateStr.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
            return dateStr;
        }
        
        // Very basic implementation - in a real app you'd use a more robust approach
        try {
            // Try to just extract year, month, day from various formats
            Pattern pattern = Pattern.compile("(\\d{1,2})[/-](\\d{1,2})[/-](\\d{4})");
            Matcher matcher = pattern.matcher(dateStr);
            if (matcher.find()) {
                // Assume MM/dd/yyyy format
                String month = matcher.group(1);
                String day = matcher.group(2);
                String year = matcher.group(3);
                return year + "-" + 
                       (month.length() == 1 ? "0" + month : month) + "-" + 
                       (day.length() == 1 ? "0" + day : day);
            }
            
            // Just return as-is for now
            return dateStr;
        } catch (Exception e) {
            return dateStr;
        }
    }
    
    // Check if a string looks like a monetary amount
    private static boolean isLikelyAmount(String str) {
        str = str.trim();
        // Remove currency symbols and commas for the check
        String cleaned = str.replaceAll("[^0-9.]", "");
        return !cleaned.isEmpty() && cleaned.matches("[0-9]+(\\.[0-9]+)?");
    }
    
    // Check if a string looks like a valid status
    private static boolean isLikelyStatus(String str) {
        str = str.trim().toLowerCase();
        return str.equals("pending") || str.equals("delivered") || 
               str.equals("shipped") || str.equals("canceled") || 
               str.equals("processing") || str.equals("completed") ||
               str.equals("returned");
    }
    
    // Check if a string looks like product details
    private static boolean isLikelyProductDetails(String str) {
        str = str.trim();
        return str.contains("ProductID:") || str.contains("Product ID:") || 
               str.contains("Qty:") || str.contains("Quantity:") ||
               str.contains("|");
    }
    
    // Check if a string is numeric
    private static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) return false;
        
        str = str.trim();
        if (str.startsWith("-")) str = str.substring(1);
        
        boolean hasDecimal = false;
        for (char c : str.toCharArray()) {
            if (c == '.') {
                if (hasDecimal) return false;
                hasDecimal = true;
            } else if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
    
    // Utility method to extract product IDs and quantities from the complex string format
    public static List<Map<String, Object>> parseProductDetails(String productDetailsStr) {
        List<Map<String, Object>> productsList = new ArrayList<>();
        
        if (productDetailsStr == null || productDetailsStr.isEmpty()) {
            return productsList;
        }
        
        try {
            // Split by pipe character to get individual product entries
            String[] productEntries = productDetailsStr.split("\\|");
            
            Pattern pattern = Pattern.compile("ProductID:\\s*(\\d+|null),\\s*Qty:\\s*(\\d+)");
            
            for (String entry : productEntries) {
                if (entry.trim().isEmpty()) continue;
                
                Matcher matcher = pattern.matcher(entry);
                if (matcher.find()) {
                    String productIdStr = matcher.group(1);
                    String quantityStr = matcher.group(2);
                    
                    if (productIdStr.equals("null")) {
                        // Handle null productId differently if needed
                        continue;
                    }
                    
                    try {
                        Map<String, Object> product = new HashMap<>();
                        product.put("productId", Long.parseLong(productIdStr));
                        product.put("quantity", Integer.parseInt(quantityStr));
                        productsList.add(product);
                    } catch (NumberFormatException e) {
                        // Skip this product if parsing fails
                        continue;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing product details: " + e.getMessage());
        }
        
        return productsList;
    }
}