package edu.upenn.cit594.processor;

// public class DataProcessor {

//    public int getTotalPopulation(ArrayList populationData) {
// 	int totalSum = 0;
// 	for (PopulationData data: populationData) {
// 		totalSum += data.getPopulation();}
// 	return totalSum;
// 	}






  
  
// }

import edu.upenn.cit594.util.CovidData;
import edu.upenn.cit594.util.PropertyData;
import edu.upenn.cit594.util.PopulationData;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;

public class DataProcessor {
    private List<CovidData> covidDataList;
    private List<PropertyData> propertyDataList;
    private List<PopulationData> populationDataList;
    private Map<String, Integer> populationCache;
    private Map<String, Integer> averageMarketValueCache;
    private Map<String, Integer> averageLivableAreaCache;

    public DataProcessor(List<CovidData> covidDataList, List<PropertyData> propertyDataList, List<PopulationData> populationDataList) {
        this.covidDataList = covidDataList;
        this.propertyDataList = propertyDataList;
        this.populationDataList = populationDataList;
        this.populationCache = new HashMap<>();
        this.averageMarketValueCache = new HashMap<>();
        this.averageLivableAreaCache = new HashMap<>();
    }

    public int getTotalPopulation() {
        return populationDataList.stream()
                .mapToInt(PopulationData::getPopulation)
                .sum();
    }

    public Map<String, Double> getVaccinationsPerCapita(String date, boolean isPartial) {
        Map<String, Double> result = new HashMap<>();
        for (CovidData data : covidDataList) {
        	String timestamp = data.getTimestamp();
        	String extractedDate = extractDate(timestamp);
            if (extractedDate != null && extractedDate.equals(date)) {
                String zipCode = data.getZipCode();
                System.out.println(zipCode);
                int population = getPopulation(zipCode);
                if (population > 0) {
                    double vaccinations = isPartial ? data.getPartiallyVaccinated() : data.getFullyVaccinated();
                    result.put(zipCode, vaccinations / population);
                }
            }
        }
        return result;
    }

    public int getAverageMarketValue(String zipCode) {
        return averageMarketValueCache.computeIfAbsent(zipCode, 
            k -> calculateAverage(zipCode, new MarketValueAverageStrategy()));
    }

    public int getAverageLivableArea(String zipCode) {
        return averageLivableAreaCache.computeIfAbsent(zipCode, 
            k -> calculateAverage(zipCode, new LivableAreaAverageStrategy()));
    }

    public int getTotalMarketValuePerCapita(String zipCode) {
        double totalMarketValue = propertyDataList.stream()
                .filter(p -> p.getZipCode().equals(zipCode))
                .mapToDouble(PropertyData::getMarketValue)
                .sum();
        int population = getPopulation(zipCode);
        return population > 0 ? (int) totalMarketValue / population : 0;
    }

    private int calculateAverage(String zipCode, AverageCalculationStrategy strategy) {
        return (int) propertyDataList.stream()
                .filter(p -> p.getZipCode().equals(zipCode))
                .mapToDouble(strategy::getValue)
                .average()
                .orElse(0.0);
    }

    public int getPopulation(String zipCode) {
        return populationCache.computeIfAbsent(zipCode, k -> 
            populationDataList.stream()
                .filter(p -> p.getZipCode().equals(zipCode))
                .mapToInt(PopulationData::getPopulation)
                .findFirst()
                .orElse(0)
        );
    }
    
    public static String extractDate(String timestamp) {
        // Regex pattern for YYYY-MM-DD
        String regex = "\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])";
        Pattern datePattern = Pattern.compile(regex);
        Matcher matcher = datePattern.matcher(timestamp);
        
        if (matcher.find()) {
            return matcher.group(); // Return the matched date
        } else {
            // Handle case where date is not found
            System.err.println("Date not found or invalid in timestamp.");
            return null;
        }
    }
}

