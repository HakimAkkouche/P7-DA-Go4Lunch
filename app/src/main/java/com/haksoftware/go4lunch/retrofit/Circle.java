package com.haksoftware.go4lunch.retrofit;

public class Circle {

    private Center center;
    private double radius;

    public Circle(Center center, double radius) {
        this.center = center;
        this.radius = radius;
    }

    // Getters and setters

    public Center getCenter() {
        return center;
    }

    public void setCenter(Center center) {
        this.center = center;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}