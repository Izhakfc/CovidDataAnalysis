package edu.upenn.cit594.processor;

import edu.upenn.cit594.util.CovidData;
import edu.upenn.cit594.util.PropertyData;
import edu.upenn.cit594.util.PopulationData;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
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

    public TreeMap<String, Double> getVaccinationsPerCapita(String date, boolean isPartial) {
        TreeMap<String, Double> result = new TreeMap<>();
       
        for (CovidData data : covidDataList) {
        	String timestamp = data.getDateTime();
        	String extractedDate = extractDate(timestamp);
            if (extractedDate != null && extractedDate.equals(date)) {
                String zipCode = data.getZipCode();
                int population = getPopulation(zipCode);
                if (population > 0) {
                    double vaccinations = isPartial ? data.getPartialVaccinations() : data.getFullVaccinations();
                    result.put(zipCode, vaccinations / population);
                }
            }
        }
        return result;
    }
    
    
    
       // calc vaccination rate (so number of vaccinations per capita for that zip code)
    private Map<String, Double> getAllVaccinationRates(boolean isPartial) {
    	Map<String, Double> ratesByZipCode = new HashMap<String, Double>();
        
        for (CovidData data : covidDataList) {
                String zipCode = data.getZipCode();
            	int population = getPopulation(zipCode);
                if (population > 0) {
                    double vaccinationSum = isPartial ? data.getPartialVaccinations() : data.getFullVaccinations();     
                    ratesByZipCode.put(zipCode, vaccinationSum / population);
            }
          }
        return ratesByZipCode;
        }
    
    private Map<String, Double> getAllPropertyValues() {
    	Map<String, Double> propertyValuesByZipCode = new HashMap<String, Double>();
        
        for (PropertyData data : propertyDataList) {
              propertyValuesByZipCode.put(data.getZipCode(), data.getMarketValue());
            }
        return propertyValuesByZipCode;
          }
      
    
    
  public double getCorrelation(List<CovidData> covidData, boolean isPartial) {
	  
	Map<String, Double> vaccinationRates = getAllVaccinationRates(isPartial);
	Map<String, Double> marketValues = getAllPropertyValues();
	
	ArrayList<Double> rates = new ArrayList<>();
	ArrayList<Double> values = new ArrayList<>();
	
	
	for (String zipCode : vaccinationRates.keySet()) {
		if (marketValues.containsKey(zipCode)) {
			rates.add(vaccinationRates.get(zipCode));
			values.add(marketValues.get(zipCode));
		}
	}

	// can't calc pearson correlation with little data
	if (rates.size() < 2) {
		System.out.println("There is not enough data in order for Pearson calculation to be calculated");
		return 0;
	}
	
    return calculatePearsonCorrelation(rates, values);
	
}


  private double calculatePearsonCorrelation(ArrayList<Double> vaccinationRates, ArrayList<Double> marketValues) {
	
    int n = vaccinationRates.size();
	
	double meanVac = calculateMean(vaccinationRates);
	double meanMarket = calculateMean(marketValues);
	
	double covariance = 0; //numerator
	double varianceVac = 0;
	double varianceMarket = 0;
	
	for (int i = 0; i < n; i++) {
		double diffVacc = vaccinationRates.get(i) - meanVac;
		double diffMarket = marketValues.get(i) - meanMarket;
		
		covariance += diffVacc * diffMarket;
		varianceVac += diffVacc * diffVacc;
		varianceMarket += diffMarket * diffMarket;
	}
	
	double sqrt = Math.sqrt(varianceVac * varianceMarket);
	if (sqrt == 0) {
		return 0;
	}
	return covariance / sqrt;
	
}

    private double calculateMean(ArrayList<Double> values) {
    	
    	double sum = 0.0;
    	for (Double value : values) {
    		sum += value;
    	}
	
    	return sum / values.size();
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
                .filter(value -> value != -1.0) // Filter out invalid values
                .sum();
        int population = getPopulation(zipCode);
        return population > 0 ? (int) (totalMarketValue / population) : 0;
    }

    private int calculateAverage(String zipCode, AverageCalculationStrategy strategy) {
        return (int) propertyDataList.stream()
                .filter(p -> p.getZipCode().equals(zipCode))
                .mapToDouble(strategy::getValue)
                .filter(value -> value != -1.0) // Filter out invalid values
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
