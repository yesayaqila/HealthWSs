package org.cal;

import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.xml.ws.WebServiceException;

@WebService(serviceName = "HealthService")
public class CaloriesWS {

    // 1. CALORIES CALCULATION
    @WebMethod(operationName = "calculateCalories")
    public double calculateCalories(
        @WebParam(name = "gender") String gender,
        @WebParam(name = "age") int age,
        @WebParam(name = "weightKg") double weightKg,
        @WebParam(name = "heightCm") double heightCm,
        @WebParam(name = "activityLevel") String activityLevel,
        @WebParam(name = "goal") String goal) {

        // Validation
        if (gender == null || (!gender.equalsIgnoreCase("male") && !gender.equalsIgnoreCase("female"))) {
            throw new WebServiceException("Invalid gender. Use 'male' or 'female'.");
        }
        if (age <= 0 || weightKg <= 0 || heightCm <= 0) {
            throw new WebServiceException("Age, weight, and height must be positive.");
        }
        if (activityLevel == null || !isValidActivityLevel(activityLevel.toLowerCase())) {
            throw new WebServiceException("Invalid activity level. Use: sedentary, light, moderate, active, very active.");
        }
        if (goal == null || !isValidGoal(goal.toLowerCase())) {
            throw new WebServiceException("Invalid goal. Use: maintain, lose, gain.");
        }

        // BMR calculation
        double bmr;
        if (gender.equalsIgnoreCase("male")) {
            bmr = (10 * weightKg) + (6.25 * heightCm) - (5 * age) + 5;
        } else {
            bmr = (10 * weightKg) + (6.25 * heightCm) - (5 * age) - 161;
        }

        // Activity multiplier
        double multiplier = getActivityMultiplier(activityLevel.toLowerCase());
        double tdee = bmr * multiplier;

        // Goal adjustment
        switch (goal.toLowerCase()) {
            case "lose": return tdee - 500;
            case "gain": return tdee + 300;
            default: return tdee; // maintain
        }
    }

    private boolean isValidGoal(String goal) {
        return goal.equals("maintain") || goal.equals("lose") || goal.equals("gain");
    }

    private boolean isValidActivityLevel(String level) {
        return level.equals("sedentary") || level.equals("light") || level.equals("moderate")
            || level.equals("active") || level.equals("very active");
    }

    private double getActivityMultiplier(String level) {
        switch (level) {
            case "sedentary": return 1.2;
            case "light": return 1.375;
            case "moderate": return 1.55;
            case "active": return 1.725;
            case "very active": return 1.9;
            default: return 1.2;
        }
    }

    // 2. BODY FAT PERCENTAGE CALCULATION
    @WebMethod(operationName = "calculateBodyFatPercentage")
    public double calculateBodyFatPercentage(
        @WebParam(name = "gender") String gender,
        @WebParam(name = "age") int age,
        @WebParam(name = "weight") double weightKg, // kg
        @WebParam(name = "height") double heightM  // meters
    ) {

        // Validation
        if (weightKg <= 0 || heightM <= 0 || age <= 0
                || !(gender.equalsIgnoreCase("male") || gender.equalsIgnoreCase("female"))) {
            throw new WebServiceException("Invalid input: check gender (male/female), age (>0), weight (>0), height (>0).");
        }

        double bmi = weightKg / (heightM * heightM);
        double bodyFatPercentage;

        if (gender.equalsIgnoreCase("male")) {
            bodyFatPercentage = 1.20 * bmi + 0.23 * age - 16.8;
        } else {
            bodyFatPercentage = 1.20 * bmi + 0.23 * age - 5.4;
        }

        return Math.round(bodyFatPercentage * 100.0) / 100.0; // rounded to 2 decimals
    }
}
