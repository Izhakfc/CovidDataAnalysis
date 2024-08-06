package edu.upenn.cit594.processor;

import edu.upenn.cit594.util.PropertyData;

public interface AverageCalculationStrategy {
    double getValue(PropertyData propertyData);
}