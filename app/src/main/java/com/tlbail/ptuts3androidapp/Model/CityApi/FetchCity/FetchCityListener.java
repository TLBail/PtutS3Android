package com.tlbail.ptuts3androidapp.Model.CityApi.FetchCity;

import com.tlbail.ptuts3androidapp.Model.City.CityData;

import java.util.List;

public interface FetchCityListener {

    public void onDataQueryComplete(List<CityData> cityData);

}