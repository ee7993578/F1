package com.edufox.schoolerp.student;

public final class HouseMasterDtos {
    private HouseMasterDtos() {}
    public record HouseRequest(Integer id, String houseName, Integer inchargeId, String captain, String viceCaptain, String prefect, String color) {}
    public record HouseResponse(Integer id, String message) {}
}