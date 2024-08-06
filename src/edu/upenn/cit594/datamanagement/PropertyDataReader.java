package edu.upenn.cit594.datamanagement;

import edu.upenn.cit594.util.PropertyData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PropertyDataReader {
    private String filename;

    public PropertyDataReader(String filename) {
        this.filename = filename;
    }

    public List<PropertyData> readPropertyData() throws IOException {
        List<PropertyData> propertyDataList = new ArrayList<>();
        CSVReader csvReader = new CSVReader(filename);
        List<String[]> records = csvReader.readCSV();

        if (records.isEmpty()) {
            return propertyDataList;
        }

        String[] headers = records.get(0);
        int zipCodeIndex = findColumnIndex(headers, "zip_code");
        int marketValueIndex = findColumnIndex(headers, "market_value");
        int livableAreaIndex = findColumnIndex(headers, "total_livable_area");

        for (int i = 1; i < records.size(); i++) {
            String[] values = records.get(i);
            
            if (values.length > Math.max(zipCodeIndex, Math.max(marketValueIndex, livableAreaIndex))) {
                String zipCode = parseZipCode(values[zipCodeIndex]);
                double marketValue = parseDoubleOrZero(values[marketValueIndex]);
                double livableArea = parseDoubleOrZero(values[livableAreaIndex]);

                if (zipCode != null) {
                	if (marketValue != -1 || livableArea != -1) {
                		propertyDataList.add(new PropertyData(zipCode, marketValue, livableArea));
                    
                }
                }
            }
        }

        return propertyDataList;
    }

    public int findColumnIndex(String[] headers, String columnName) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].trim().equalsIgnoreCase(columnName)) {
                return i;
            }
        }
        return -1; // Column not found
    }

    public String parseZipCode(String zipCode) {
        if (zipCode == null) return null;
        String fiveDigitZip = zipCode.length() > 5 ? zipCode.substring(0, 5) : zipCode;
        return fiveDigitZip.matches("\\d{5}") ? fiveDigitZip : null;
    }

    public double parseDoubleOrZero(String value) {
        try {
            // Handling null values
            if (value == null) {
                return -1.0;
            }
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return -1.0;
        }
    }
}