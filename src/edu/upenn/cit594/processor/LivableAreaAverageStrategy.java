package edu.upenn.cit594.processor;

import edu.upenn.cit594.util.PropertyData;

public class LivableAreaAverageStrategy implements AverageCalculationStrategy {
    @Override
    public double getValue(PropertyData propertyData) {
        return propertyData.getTotalLivableArea();
    }
}